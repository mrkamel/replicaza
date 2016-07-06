package com.github.mrkamel.replicaza;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventHeaderV4;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BinlogEventListenerTest extends TestCase {
    public BinlogEventListenerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BinlogEventListenerTest.class);
    }
    
    public void setUp() {
    	App.config = new Config();
    	App.config.setProperty("replicateTables", "");
    }
    
    private BinaryLogClient createBinaryLogClient() {
    	return new BinaryLogClient("127.0.0.1", 3306, "root", "");
    }
    
    private KafkaProducer createKafkaProducer() {
    	return new KafkaProducer("127.0.0.1:9092", 1);
    }

    private GtidSet createGtidSet() {
    	CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(1000, 3));
    	curatorFramework.start();
    	
    	return new GtidSet("/replicaza_test_gtid", curatorFramework.getZookeeperClient());
    }
    
    public void testBinlogEventListener() {
    	new BinlogEventListener(createBinaryLogClient(), createGtidSet(), createKafkaProducer());
    }

    public Event createTableMapEvent(int tableId, String database, String table) {
    	EventHeaderV4 eventHeader = new EventHeaderV4();
    	eventHeader.setEventType(EventType.TABLE_MAP);
    	
    	TableMapEventData eventData = new TableMapEventData();
    	eventData.setTableId(tableId);
    	eventData.setDatabase(database);
    	eventData.setTable(table);
    	
    	return new Event(eventHeader, eventData);
    }
    
    public void testOnEventWriteRows() {
    	BinlogEventListener binlogEventListener = new BinlogEventListener(createBinaryLogClient(), createGtidSet(), createKafkaProducer());
    	
    	binlogEventListener.onEvent(createTableMapEvent(1, "database", "table"));
    	
    	EventHeaderV4 eventHeader = new EventHeaderV4();
    	eventHeader.setEventType(EventType.EXT_WRITE_ROWS);
    	
    	WriteRowsEventData eventData = new WriteRowsEventData();
    	eventData.setTableId(1);
    	
    	List<Serializable[]> rows = new ArrayList<Serializable[]>();
    	
    	Serializable[] row = { "1" };
    	
    	rows.add(row);
    	
    	eventData.setRows(rows);
    	
    	binlogEventListener.onEvent(new Event(eventHeader, eventData));
    }
    
    public void testOnEventDeleteRows() {
    	BinlogEventListener binlogEventListener = new BinlogEventListener(createBinaryLogClient(), createGtidSet(), createKafkaProducer());
    	
    	binlogEventListener.onEvent(createTableMapEvent(1, "database", "table"));
    	
    	EventHeaderV4 eventHeader = new EventHeaderV4();
    	eventHeader.setEventType(EventType.EXT_DELETE_ROWS);
    	
    	DeleteRowsEventData eventData = new DeleteRowsEventData();
    	eventData.setTableId(1);
    	
    	List<Serializable[]> rows = new ArrayList<Serializable[]>();
    	
    	Serializable[] row = { "1" };
    	
    	rows.add(row);
    	
    	eventData.setRows(rows);
    	
    	binlogEventListener.onEvent(new Event(eventHeader, eventData));
    }
    
    public void testOnEventUpdateRows() {
    	BinlogEventListener binlogEventListener = new BinlogEventListener(createBinaryLogClient(), createGtidSet(), createKafkaProducer());
    	
    	binlogEventListener.onEvent(createTableMapEvent(1, "database", "table"));
    	
    	EventHeaderV4 eventHeader = new EventHeaderV4();
    	eventHeader.setEventType(EventType.EXT_UPDATE_ROWS);
    	
    	UpdateRowsEventData eventData = new UpdateRowsEventData();
    	eventData.setTableId(1);
    	
    	List<Map.Entry<Serializable[], Serializable[]>> rows = new ArrayList<Map.Entry<Serializable[], Serializable[]>>();
    	
    	Serializable[] row = { "1" };
    	
    	rows.add(new AbstractMap.SimpleEntry<Serializable[], Serializable[]>(row, row));
    	
    	eventData.setRows(rows);
    	
    	binlogEventListener.onEvent(new Event(eventHeader, eventData));
    }
}
