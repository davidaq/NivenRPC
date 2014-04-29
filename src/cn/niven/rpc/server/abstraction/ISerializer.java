package cn.niven.rpc.server.abstraction;

import java.io.IOException;

public interface ISerializer {

	public void writeObject(Object obj) throws IOException;
}
