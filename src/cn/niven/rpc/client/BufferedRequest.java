package cn.niven.rpc.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.niven.rpc.types.RPCException;

public class BufferedRequest implements Runnable {

	private static final ExecutorService threadPool = Executors
			.newCachedThreadPool();

	public static InputStream request(byte[] sendData, String serverAddr) {
		BufferedRequest request = new BufferedRequest(sendData, serverAddr);
		for (int i = 0; i < 100; i++) {
			synchronized (request) {
				try {
					threadPool.execute(request);
					request.wait();
				} catch (InterruptedException e) {
					break;
				}
			}
			if (request.inStream != null)
				return request.inStream;
		}
		throw new RPCException("Request interrupted");
	}

	private final byte[] sendData;
	private final String serverAddr;
	private InputStream inStream;

	private BufferedRequest(byte[] sendData, String serverAddr) {
		this.sendData = sendData;
		this.serverAddr = serverAddr;
	}

	@Override
	public void run() {
		CachedSocket socket = SocketPool.getSocket(serverAddr);
		for (int i = 0; i < 10; i++) {
			try {
				socket.getOutputStream().write(sendData);
				synchronized (this) {
					inStream = new CachedSocketInputStream(socket);
					notifyAll();
					return;
				}
			} catch (IOException e) {
				socket.disconnect();
				socket.connect();
				try {
					Thread.sleep(500 * i);
				} catch (InterruptedException e1) {
					break;
				}
				e.printStackTrace();
			}
		}
	}
}
