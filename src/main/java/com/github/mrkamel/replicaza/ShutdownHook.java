
package com.github.mrkamel.replicaza;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;

public class ShutdownHook extends Thread {
	private LeaderSelector leaderSelector;
	private CuratorFramework curatorFramework;
	
	public ShutdownHook(LeaderSelector leaderSelector, CuratorFramework curatorFramework) {
		this.leaderSelector = leaderSelector;
		this.curatorFramework = curatorFramework;
	}

	public void run() {
		leaderSelector.close();
		curatorFramework.close();
	}
}