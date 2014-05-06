package cn.niven.rpc.client;

import java.io.IOException;
import java.io.InputStream;

public class CachedSocketInputStream extends InputStream {
	private final InputStream in;
	private final CachedSocket socket;

	CachedSocketInputStream(CachedSocket socket) throws IOException {
		super();
		this.in = socket.getInputStream();
		this.socket = socket;
	}

	public int read() throws IOException {
		return in.read();
	}

	public int hashCode() {
		return in.hashCode();
	}

	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public boolean equals(Object obj) {
		return in.equals(obj);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public String toString() {
		return in.toString();
	}

	public int available() throws IOException {
		return in.available();
	}

	public void close() throws IOException {
		socket.close();
	}

	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	public void reset() throws IOException {
		in.reset();
	}

	public boolean markSupported() {
		return in.markSupported();
	}

}
