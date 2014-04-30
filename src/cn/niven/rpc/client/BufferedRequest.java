package cn.niven.rpc.client;

import java.io.InputStream;

import cn.niven.rpc.types.RPCException;

public class BufferedRequest {

	public static InputStream request(byte[] sendData, String serverAddr) {
		BufferedRequest request = new BufferedRequest(sendData, serverAddr);
		synchronized (request) {
			for (int i = 0; i < 100; i++) {
				try {
					request.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (request.inStream != null)
					return request.inStream;
			}
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
}
