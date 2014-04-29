package cn.niven.rpc.server.abstraction;

public interface IServer {
	public void serve(int port, IRequestHandler handler);
}
