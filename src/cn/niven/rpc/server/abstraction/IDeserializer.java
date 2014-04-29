package cn.niven.rpc.server.abstraction;

import java.io.IOException;

public interface IDeserializer {

	public Object readObject() throws ClassNotFoundException, IOException;

}
