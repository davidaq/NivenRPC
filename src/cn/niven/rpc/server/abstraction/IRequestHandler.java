package cn.niven.rpc.server.abstraction;

public interface IRequestHandler {
	public Object handleRequest(Object input) throws Exception;
}
