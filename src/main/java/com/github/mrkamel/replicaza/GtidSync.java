
package com.github.mrkamel.replicaza;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class GtidSync extends Thread {	
	private String gtidSet = "";
	private CuratorZookeeperClient curatorZookeeperClient;
	private String zkPath;
	private boolean closed = false;
	
	public GtidSync(String zkPath, CuratorZookeeperClient curatorZookeeperClient) {
		this.curatorZookeeperClient = curatorZookeeperClient;
		this.zkPath = zkPath;
		
		loadGtidSet();
	}
	
	public synchronized void setGtidSet(String gtidSet) {
		this.gtidSet = gtidSet;
	}
	
	public synchronized String getGtidSet() {
		return gtidSet;
	}
	
	private void loadGtidSet() {
		while(true) {
			try {
				loadGtidSetUnsafe();
				
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadGtidSetUnsafe() throws Exception {
		try {
			byte data[] = curatorZookeeperClient.getZooKeeper().getData(zkPath, false, null);
			
			setGtidSet(new String(data));
		} catch(KeeperException.NoNodeException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		String lastGtidSet = getGtidSet();
		
		while(!isClosed()) {
			String currentGtidSet = getGtidSet();
			
			try {
				if(!currentGtidSet.equals(lastGtidSet))
					lastGtidSet = saveGtidSet();
			} catch(Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String saveGtidSet() throws Exception {	
		String currentGtidSet = getGtidSet();
		
		try {
			curatorZookeeperClient.getZooKeeper().create(zkPath, currentGtidSet.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch(KeeperException.NodeExistsException e) {
			curatorZookeeperClient.getZooKeeper().setData(zkPath, currentGtidSet.getBytes(), -1);
		}
		
		return currentGtidSet;
	}
	
	private synchronized boolean isClosed() {
		return closed;
	}
	
	public synchronized void close() {
		closed = true;
		
		interrupt();
	}
}
