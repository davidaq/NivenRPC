package cn.niven.rpc.server.abstraction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISerializerFactory {
	public ISerializer getSerializer(OutputStream outStream) throws IOException;

	public IDeserializer getDeserializer(InputStream inStream)
			throws IOException;
}
