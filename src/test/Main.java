package test;

import cn.niven.rpc.client.RPCClient;
import cn.niven.rpc.server.RPCServer;

public class Main implements TestInterface {
	public static void main(String args[]) throws Exception {
		new Thread(new Runnable() {

			@Override
			public void run() {
				RPCServer server = new RPCServer();
				server.exportService(TestInterface.class, new Main());
				server.serve(15301);
			}

		}).start();
		final TestInterface test = RPCClient.getClient(TestInterface.class,
				"127.0.0.1:15301");
		for (int i = 0; i < 20; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 19; i++) {
						System.out.println(test.sum(11 * i, 6));
						try {
							Thread.sleep((long) (Math.random() * 1000));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	@Override
	public int sum(int a, int b) {
		return a + b;
	}
}
