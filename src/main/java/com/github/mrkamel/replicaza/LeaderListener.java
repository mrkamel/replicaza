
package com.github.mrkamel.replicaza;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import com.github.shyiko.mysql.binlog.BinaryLogClient;

class LeaderListener extends LeaderSelectorListenerAdapter {
	private GtidSet gtidSet;
	
	public LeaderListener(GtidSet gtidSet) {
		this.gtidSet = gtidSet;
	}
	
    public void takeLeadership(CuratorFramework curatorFramework) throws InterruptedException {		    	
		try {
		   	BinaryLogClient binaryLogClient = new BinaryLogClient(
		   		Config.getProperty("mysql.host"),
		   		new Integer(Config.getProperty("mysql.port")),
		   		Config.getProperty("mysql.user"),
		   		Config.getProperty("mysql.pass")
		   	);
		   	
		   	binaryLogClient.setKeepAlive(false);
		   	binaryLogClient.setGtidSet(gtidSet.getGtidSet());
		   	
		   	KafkaProducer kafkaProducer = new KafkaProducer(
		   		Config.getProperty("kafka.brokers"),
		   		new Integer(Config.getProperty("kafka.requiredAcks"))
		   	);

		   	binaryLogClient.registerEventListener(new BinlogEventListener(binaryLogClient, gtidSet, kafkaProducer));
		   	binaryLogClient.connect();
		} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		// Sleep some time, but then give someone else a chance to become leader,
    		// because someone else could have better connectivity
    		
		   	Thread.sleep(5000);
    	}
    }
}
