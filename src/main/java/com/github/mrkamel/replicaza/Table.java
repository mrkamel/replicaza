
package com.github.mrkamel.replicaza;

public class Table {
	private String database;
	private String table;
	
	public Table(String database, String table) {
		this.database = database;
		this.table = table;
	}
	
	public String getDatabase() {
		return database;
	}
	
	public String getTable() {
		return table;
	}
}