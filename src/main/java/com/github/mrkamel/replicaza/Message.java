
package com.github.mrkamel.replicaza;

public class Message {
	private String topic, gtidSet, operation, id, time;
	
	public Message(String topic, String gtidSet, String operation, String id, String time) {
		this.topic = topic;
		this.gtidSet = gtidSet;
		this.operation = operation;
		this.id = id;
		this.time = time;
	}

	public String getTopic() {
		return topic;
	}

	public String getGtidSet() {
		return gtidSet;
	}

	public String getOperation() {
		return operation;
	}

	public String getId() {
		return id;
	}

	public String getTime() {
		return time;
	}
}