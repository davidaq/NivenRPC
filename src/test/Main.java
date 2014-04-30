package test;

import java.io.IOException;

import cn.niven.rpc.client.CachedSocket;
import cn.niven.rpc.client.RPCClient;
import cn.niven.rpc.client.SocketPool;

public class Main {
	public static void main(String args[]) throws Exception, IOException {
		TestInterface client = RPCClient.getClient(TestInterface.class,
				"127.0.0.1:5097");
		CachedSocket socket = SocketPool.getSocket("niven.cn:80");
		socket.getOutputStream().write(
				"GET / HTTP/1.1\r\n\r\n\r\n".getBytes("UTF-8"));
		Thread.sleep(1000);
		byte buff[] = new byte[1500];
		socket.getInputStream().read(buff);
		System.out.println(new String(buff, "UTF-8"));
		socket.close();
		Thread.sleep(1500);
		socket = SocketPool.getSocket("niven.cn:80");
		socket.getOutputStream().write(
				"GET / HTTP/1.1\r\n\r\n\r\n".getBytes("UTF-8"));
		Thread.sleep(1000);
		buff = new byte[1500];
		socket.getInputStream().read(buff);
		System.out.println(new String(buff, "UTF-8"));
		socket.close();

		System.out.println(client.sum(2, 4));
	}
}
