package cn.niven.rpc.client;

public abstract class IClient {
	String serverAddr;
	String interfaceTypeName;

	public Object _niven_rpc_invoke(String methodName, Class<?> returnType,
			Object... arguments) {
		return Integer.valueOf(((Integer) arguments[0]).intValue()
				+ ((Integer) arguments[1]).intValue());
	}
}
