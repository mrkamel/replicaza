
package com.github.mrkamel.replicaza;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import com.github.shyiko.mysql.binlog.BinaryLogClient;

class LeaderListener extends LeaderSelectorListenerAdapter {
	public void takeLeadership(CuratorFramework curatorFramework) throws InterruptedException {		    	
		try {
			BinaryLogClient binaryLogClient = new BinaryLogClient(
		   		App.config.getProperty("mysql.host"),
		   		new Integer(App.config.getProperty("mysql.port")),
		   		App.config.getProperty("mysql.user"),
		   		App.config.getProperty("mysql.pass")
		   	);

	    	GtidSync gtidSync = new GtidSync("/replicaza_gtid", curatorFramework.getZookeeperClient());
			gtidSync.start();
			
		   	binaryLogClient.setKeepAlive(false);
		   	binaryLogClient.setGtidSet(gtidSync.getGtidSet());
		   	
		   	KafkaProducer kafkaProducer = new KafkaProducer(
		   		App.config.getProperty("kafka.brokers"),
		   		new Integer(App.config.getProperty("kafka.requiredAcks")),
		   		gtidSync
		   	);
		   	
		   	kafkaProducer.start();

		   	binaryLogClient.registerEventListener(new BinlogEventListener(binaryLogClient, kafkaProducer));
		   	binaryLogClient.connect();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {			
			// Sleep some time, but then give someone else a chance to become leader,
			// because someone else could have better connectivity
    		
			Thread.sleep(5000);
			
			System.exit(-1);
		}
	}
}
