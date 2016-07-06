
package com.github.mrkamel.replicaza;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducer {
	private Producer<String, String> producer;
	
	public KafkaProducer(String brokers, Integer requiredAcks) {
		Properties properties = new Properties();
		 
		properties.put("metadata.broker.list", brokers);
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("partitioner.class", "kafka.producer.DefaultPartitioner");
		properties.put("request.required.acks", requiredAcks.toString());
		 
		ProducerConfig config = new ProducerConfig(properties);
		
		this.producer = new Producer<String, String>(config);
	}
	
	public void send(String topic, String partitionKey, String message) {
		producer.send(new KeyedMessage<String, String>(topic, partitionKey, message));
	}
}