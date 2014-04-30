package cn.niven.rpc.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import cn.niven.rpc.types.RPCException;

public class CachedSocket extends Socket {
	final String serverAddr;
	private Socket socket;
	long expire;

	CachedSocket(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	void connect() {
		if (socket != null) {
			if (!socket.isConnected()) {
				disconnect();
			}
		}
		if (socket == null) {
			System.out.println("connected");
			try {
				socket = new Socket();
				socket.setKeepAlive(true);
				socket.setSoTimeout(30000);
				String[] addr = serverAddr.split(":");
				int port;
				try {
					port = Integer.valueOf(addr[1]);
				} catch (Exception e) {
					port = 15301;
				}
				socket.connect(new InetSocketAddress(InetAddress
						.getByName(addr[0]), port));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RPCException(e);
			}
		}
	}

	void disconnect() {
		try {
			if (socket != null && socket.isConnected())
				socket.close();
			System.out.println("disconnected");
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket = null;
	}

	public void close() {
		if (socket != null)
			try {
				socket.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		SocketPool.release(this);
	}

	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	public InetAddress getLocalAddress() {
		return socket.getLocalAddress();
	}

	public int getPort() {
		return socket.getPort();
	}

	public int getLocalPort() {
		return socket.getLocalPort();
	}

	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	public SocketAddress getLocalSocketAddress() {
		return socket.getLocalSocketAddress();
	}

	public SocketChannel getChannel() {
		return socket.getChannel();
	}

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public void setTcpNoDelay(boolean on) throws SocketException {
		socket.setTcpNoDelay(on);
	}

	public boolean getTcpNoDelay() throws SocketException {
		return socket.getTcpNoDelay();
	}

	public void setSoLinger(boolean on, int linger) throws SocketException {
		socket.setSoLinger(on, linger);
	}

	public int getSoLinger() throws SocketException {
		return socket.getSoLinger();
	}

	public void sendUrgentData(int data) throws IOException {
		socket.sendUrgentData(data);
	}

	public void setOOBInline(boolean on) throws SocketException {
		socket.setOOBInline(on);
	}

	public boolean getOOBInline() throws SocketException {
		return socket.getOOBInline();
	}

	public void setSoTimeout(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
	}

	public int getSoTimeout() throws SocketException {
		return socket.getSoTimeout();
	}

	public void setSendBufferSize(int size) throws SocketException {
		socket.setSendBufferSize(size);
	}

	public int getSendBufferSize() throws SocketException {
		return socket.getSendBufferSize();
	}

	public void setReceiveBufferSize(int size) throws SocketException {
		socket.setReceiveBufferSize(size);
	}

	public int getReceiveBufferSize() throws SocketException {
		return socket.getReceiveBufferSize();
	}

	public void setKeepAlive(boolean on) throws SocketException {
		socket.setKeepAlive(on);
	}

	public boolean getKeepAlive() throws SocketException {
		return socket.getKeepAlive();
	}

	public void setTrafficClass(int tc) throws SocketException {
		socket.setTrafficClass(tc);
	}

	public int getTrafficClass() throws SocketException {
		return socket.getTrafficClass();
	}

	public void setReuseAddress(boolean on) throws SocketException {
		socket.setReuseAddress(on);
	}

	public boolean getReuseAddress() throws SocketException {
		return socket.getReuseAddress();
	}

	public void shutdownInput() throws IOException {
		socket.shutdownInput();
	}

	public void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}

	public boolean isConnected() {
		return socket.isConnected();
	}

	public boolean isBound() {
		return socket.isBound();
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	public boolean isInputShutdown() {
		return socket.isInputShutdown();
	}

	public boolean isOutputShutdown() {
		return socket.isOutputShutdown();
	}

	public void setPerformancePreferences(int connectionTime, int latency,
			int bandwidth) {
		socket.setPerformancePreferences(connectionTime, latency, bandwidth);
	}
}
