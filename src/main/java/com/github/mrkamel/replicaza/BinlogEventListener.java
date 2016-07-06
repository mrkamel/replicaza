
package com.github.mrkamel.replicaza;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.util.internal.StringUtil;
import org.json.JSONObject;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

public class BinlogEventListener implements EventListener {
	private BinaryLogClient binaryLogClient;
	private Map<Long, Table> tableMap;
	private KafkaProducer kafkaProducer;
	private Set<String> replicateTables;
	private GtidSet gtidSet;

	public BinlogEventListener(BinaryLogClient binaryLogClient, GtidSet gtidSet, KafkaProducer kafkaProducer) {
		this.binaryLogClient = binaryLogClient;
		this.tableMap = new HashMap<Long, Table>();
		this.kafkaProducer = kafkaProducer;
		this.gtidSet = gtidSet;
		
		this.replicateTables = new HashSet<String>();
		
		for(String fqTable: StringUtil.split(App.config.getProperty("replicateTables"), ',')) {
			String cleanFqTable = fqTable.trim();
			
			if(cleanFqTable.length() > 0)
				replicateTables.add(cleanFqTable);
		}
	}

	public void onEvent(Event event) {
		while(true) {
			try {
				onEventUnsafe(event);
				
				return;
			} catch(Exception e) {
				e.printStackTrace();
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		}
	}

	private void onEventUnsafe(Event event) {
		String eventName = event.getHeader().getEventType().name();
		String time = new SimpleDateFormat("YYYY-MM-YY HH:mm:ss").format(Calendar.getInstance().getTime());

		if(eventName.equals("EXT_WRITE_ROWS")) {
			WriteRowsEventData data = event.getData();
			Table table = tableMap.get(data.getTableId());
			String fqTable = table.getDatabase() + "." + table.getTable();
			
			if(replicateTables.size() > 0 && !replicateTables.contains(fqTable))
				return;
			
			for(Object[] row: data.getRows())
				kafkaProducer.send(fqTable, row[0].toString(), new JSONObject().put("op", "insert").put("id", row[0]).put("time", time).toString());
			
			gtidSet.setGtidSet(binaryLogClient.getGtidSet());
		} else if(eventName.equals("EXT_UPDATE_ROWS")) {
			UpdateRowsEventData data = event.getData();
			Table table = tableMap.get(data.getTableId());
			String fqTable = table.getDatabase() + "." + table.getTable();
			
			if(replicateTables.size() > 0 && !replicateTables.contains(fqTable))
				return;
			
			for(Map.Entry<Serializable[], Serializable[]> row: data.getRows())
				kafkaProducer.send(fqTable, row.getKey()[0].toString(), new JSONObject().put("op", "update").put("id", row.getKey()[0]).put("time", time).toString());

			gtidSet.setGtidSet(binaryLogClient.getGtidSet());
		} else if(eventName.equals("EXT_DELETE_ROWS")) {
			DeleteRowsEventData data = event.getData();
			Table table = tableMap.get(data.getTableId());
			String fqTable = table.getDatabase() + "." + table.getTable();
			
			if(replicateTables.size() > 0 && !replicateTables.contains(fqTable))
				return;
			
			for(Object[] row: data.getRows())
				kafkaProducer.send(fqTable, row[0].toString(), new JSONObject().put("op", "delete").put("id", row[0]).put("time", time).toString());

			gtidSet.setGtidSet(binaryLogClient.getGtidSet());	
		} else if(eventName.equals("TABLE_MAP")) {
			TableMapEventData data = event.getData();
				
			tableMap.put(data.getTableId(), new Table(data.getDatabase(), data.getTable()));
		}
	}
}