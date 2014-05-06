package cn.niven.rpc.server.option;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import cn.niven.rpc.Composition;
import cn.niven.rpc.server.abstraction.IRequestHandler;
import cn.niven.rpc.server.abstraction.ISerializer;
import cn.niven.rpc.server.abstraction.IServer;

public class NettyTcpServer implements IServer {

	@Override
	public void serve(int port, final IRequestHandler handler) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(
									new MessageBlockDivideHandler(),
									new MessageBlockHandler(handler));
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.option(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			try {
				bootstrap.bind(port).sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}

class MessageBlockDivideHandler extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < 4)
			return;
		int l = in.readInt();
		if (in.readableBytes() >= l) {
			byte inBuff[] = new byte[l];
			in.readBytes(inBuff);
			out.add(inBuff);
		} else {
			in.resetReaderIndex();
		}
	}
}

class MessageBlockHandler extends ChannelInboundHandlerAdapter {
	private final IRequestHandler handler;

	public MessageBlockHandler(IRequestHandler handler) {
		this.handler = handler;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		byte pack[] = (byte[]) msg;
		ByteArrayInputStream bStream = new ByteArrayInputStream(pack);
		try {
			ObjectInputStream oStream = new ObjectInputStream(bStream);
			Object result = handler.handleRequest(oStream.readObject());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ISerializer serializer = Composition.serializerFactory
					.getSerializer(bos);
			serializer.writeObject(result);
			ByteBuf outBuf = ctx.alloc().buffer(bos.size());
			outBuf.writeBytes(bos.toByteArray());
			ctx.writeAndFlush(outBuf);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}