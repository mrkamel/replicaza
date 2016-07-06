
package com.github.mrkamel.replicaza;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducer extends Thread {
	private Producer<String, String> producer;
	private BlockingQueue<Message> queue;
	private GtidSync gtidSync;
	
	public KafkaProducer(String brokers, Integer requiredAcks, GtidSync gtidSync) {
		this.queue = new BlockingQueue<Message>(100);
		this.gtidSync = gtidSync;
		
		Properties properties = new Properties();
		 
		properties.put("metadata.broker.list", brokers);
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("partitioner.class", "kafka.producer.DefaultPartitioner");
		properties.put("request.required.acks", requiredAcks.toString());
		 
		ProducerConfig config = new ProducerConfig(properties);
		
		this.producer = new Producer<String, String>(config);
	}
	
	public void send(String topic, String gtidSet, String operation, String id, String time) {
		queue.put(new Message(topic, gtidSet, operation, id, time));
	}
	
	public void flush() {
		List<Message> messages = new ArrayList<Message>();
		
		Message message = queue.poll();
		
		while(message != null) {
			messages.add(message);

			if(messages.size() > 100) {
				send(messages);
				
				messages.clear();
			}

			message = queue.poll();
		}
		
		if(messages.size() > 0)
			send(messages);
	}
	
	private void send(List<Message> messages) {
		while(true) {
			try {
				sendUnsafe(messages);
				
				return;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendUnsafe(List<Message> messages) {
		List<KeyedMessage<String, String>> keyedMessages = new ArrayList<KeyedMessage<String, String>>();
		
		for(Message message : messages) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("operation", message.getOperation()).put("id", message.getId()).put("time", message.getTime());
			
			keyedMessages.add(new KeyedMessage<String, String>(message.getTopic(), message.getId(), jsonObject.toString()));
		}
		
		producer.send(keyedMessages);
		
		gtidSync.setGtidSet(messages.get(messages.size() - 1).getGtidSet());
	}
	
	public void run() {
		while(true) {
			queue.waitIfNeccessary();
			
			flush();
		}
	}
}
