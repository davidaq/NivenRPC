package cn.niven.rpc.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.niven.rpc.types.RPCException;

public final class SocketPool {
	private static final int socketConnectionExpireTime = 10000;

	private static final HashMap<String, Set<CachedSocket>> available = new HashMap<String, Set<CachedSocket>>();
	private static final HashSet<CachedSocket> expireQueue = new HashSet<CachedSocket>();

	static {
		Timer cleanerTimer = new Timer();
		cleanerTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				long now = System.currentTimeMillis();
				synchronized (expireQueue) {
					Iterator<CachedSocket> iterator = expireQueue.iterator();
					while (iterator.hasNext()) {
						CachedSocket item = iterator.next();
						if (now > item.expire) {
							item.disconnect();
							iterator.remove();
						}
					}
				}
			}

		}, socketConnectionExpireTime, socketConnectionExpireTime);
	}

	private static Set<CachedSocket> getPool(String serverAddr) {
		Set<CachedSocket> pool;
		synchronized (available) {
			pool = available.get(serverAddr);
			if (pool == null) {
				pool = new HashSet<CachedSocket>();
				available.put(serverAddr, pool);
			}
		}
		return pool;
	}

	public static CachedSocket getSocket(String serverAddr) {
		for (int i = 0; i < 100; i++) {
			try {
				Set<CachedSocket> pool = getPool(serverAddr);
				CachedSocket socket = null;
				synchronized (pool) {
					Iterator<CachedSocket> iterator = pool.iterator();
					if (iterator.hasNext()) {
						socket = iterator.next();
						iterator.remove();
					}
				}
				if (socket == null) {
					socket = new CachedSocket(serverAddr);
				} else {
					synchronized (expireQueue) {
						expireQueue.remove(socket);
					}
				}
				socket.expire = 0;
				socket.connect();
				return socket;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		throw new RPCException("Can not get an working socket to " + serverAddr);
	}

	public static void release(CachedSocket socket) {
		socket.expire = System.currentTimeMillis() + socketConnectionExpireTime;
		synchronized (expireQueue) {
			expireQueue.add(socket);
		}
		Set<CachedSocket> pool = getPool(socket.serverAddr);
		synchronized (pool) {
			pool.add(socket);
		}
	}
}
