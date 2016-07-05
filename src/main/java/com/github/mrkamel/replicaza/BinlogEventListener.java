
package com.github.mrkamel.replicaza;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

public class BinlogEventListener implements EventListener {
	private BinaryLogClient client;
	private Map<Long, Table> tables;
	private KafkaProducer producer;
	private GtidSet gtidSet;

	public BinlogEventListener(BinaryLogClient client, GtidSet gtidSet, KafkaProducer kafkaProducer) {
		this.client = client;
		this.tables = new HashMap<Long, Table>();
		this.producer = kafkaProducer;
		this.gtidSet = gtidSet;
	}

	public void onEvent(Event event) {
		String eventName = event.getHeader().getEventType().name();

    	String time = new SimpleDateFormat("YYYY-MM-YY HH:mm:ss").format(Calendar.getInstance().getTime());
		
		if(eventName.equals("EXT_WRITE_ROWS")) {
			WriteRowsEventData data = event.getData();
			Table table = tables.get(data.getTableId());
			
			for(Object[] row: data.getRows())
				producer.send(table.getDatabase() + "." + table.getTable(), row[0].toString(), new JSONObject().put("op", "insert").put("id", row[0]).put("time", time).toString());

			gtidSet.setGtidSet(client.getGtidSet());
		} else if(eventName.equals("EXT_UPDATE_ROWS")) {
			UpdateRowsEventData data = event.getData();
			Table table = tables.get(data.getTableId());
			
			for(Map.Entry<Serializable[], Serializable[]> row: data.getRows())
				producer.send(table.getDatabase() + "." + table.getTable(), row.getKey()[0].toString(), new JSONObject().put("op", "update").put("id", row.getKey()[0]).put("time", time).toString());

			gtidSet.setGtidSet(client.getGtidSet());
		} else if(eventName.equals("EXT_DELETE_ROWS")) {
			DeleteRowsEventData data = event.getData();
			Table table = tables.get(data.getTableId());
			
			for(Object[] row: data.getRows())
				producer.send(table.getDatabase() + "." + table.getTable(), row[0].toString(), new JSONObject().put("op", "delete").put("id", row[0]).put("time", time).toString());

			gtidSet.setGtidSet(client.getGtidSet());	
		} else if(eventName.equals("TABLE_MAP")) {
			TableMapEventData data = event.getData();
				
			tables.put(data.getTableId(), new Table(data.getDatabase(), data.getTable()));
		}
	}
}