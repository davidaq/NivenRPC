package cn.niven.rpc;

import cn.niven.rpc.server.abstraction.ISerializerFactory;
import cn.niven.rpc.server.abstraction.IServer;
import cn.niven.rpc.server.option.PlainJavaSerializerFactory;
import cn.niven.rpc.server.option.NettyTcpServer;

public final class Composition {
	public static final ISerializerFactory serializerFactory = new PlainJavaSerializerFactory();
	public static final IServer baseServer = new NettyTcpServer();
}
