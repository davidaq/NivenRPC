package cn.niven.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import cn.niven.rpc.Composition;
import cn.niven.rpc.server.abstraction.ISerializer;
import cn.niven.rpc.types.RPCException;
import cn.niven.rpc.types.RequestVO;

public abstract class IClient {
	String serverAddr;
	String interfaceTypeName;

	public Object _niven_rpc_invoke(String methodName, Object... arguments) {
		byte[] sendData;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(new byte[4]);
			ISerializer serializer = Composition.serializerFactory
					.getSerializer(bos);
			RequestVO send = new RequestVO();
			send.serviceName = interfaceTypeName;
			send.methodName = methodName;
			send.parameters = arguments;
			serializer.writeObject(send);
			sendData = bos.toByteArray();
			int len = sendData.length - 4;
			if (len == 0)
				return null;
			sendData[0] = (byte) (len >> 24);
			sendData[1] = (byte) ((len >> 16) & 0xff);
			sendData[2] = (byte) ((len >> 8) & 0xff);
			sendData[3] = (byte) ((len) & 0xff);
		} catch (IOException e) {
			throw new RPCException("Unable to serialize request", e);
		}
		try {
			ObjectInputStream inStream = new ObjectInputStream(
					BufferedRequest.request(sendData, serverAddr));
			try {
				return inStream.readObject();
			} finally {
				inStream.close();
			}
		} catch (Exception e) {
			throw new RPCException("Unable to deserialize response", e);
		}
	}
}
