package cn.niven.rpc.server.option;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import cn.niven.rpc.server.abstraction.IDeserializer;
import cn.niven.rpc.server.abstraction.ISerializer;
import cn.niven.rpc.server.abstraction.ISerializerFactory;

public class PlainJavaSerializerFactory implements ISerializerFactory {

	@Override
	public ISerializer getSerializer(OutputStream outStream) throws IOException {
		return new PlainJavaSerializer(new ObjectOutputStream(outStream));
	}

	@Override
	public IDeserializer getDeserializer(InputStream inStream)
			throws IOException {
		return new PlainJavaSerializer(new ObjectInputStream(inStream));
	}

}

class PlainJavaSerializer implements ISerializer, IDeserializer {
	private Object stream;

	public PlainJavaSerializer(Object stream) {
		this.stream = stream;
	}

	@Override
	public Object readObject() throws ClassNotFoundException, IOException {
		return ((ObjectInputStream) stream).readObject();
	}

	@Override
	public void writeObject(Object obj) throws IOException {
		((ObjectOutputStream) stream).writeObject(obj);
	}

}