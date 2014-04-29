package cn.niven.rpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;

import cn.niven.rpc.types.RPCException;

public class ServiceMap {

	private HashMap<String, ServiceItem> serviceMap = new HashMap<String, ServiceItem>();

	public void put(Class<?> serviceInterface, Object serviceHandler) {
		serviceMap.put(serviceInterface.getName(), new ServiceItem(
				serviceInterface, serviceHandler));
	}

	public Object invoke(String serviceName, String methodName,
			Object[] parameters) throws RPCException {
		ServiceItem item = serviceMap.get(serviceName);
		if (item == null)
			throw new RPCException("Service not found on server");
		return item.invoke(methodName, parameters);
	}
}

class ServiceItem {
	private final Object serviceHandler;
	private final Class<?> serviceInterface;

	ServiceItem(Class<?> serviceInterface, Object serviceHandler) {
		this.serviceInterface = serviceInterface;
		this.serviceHandler = serviceHandler;
	}

	Object invoke(String methodName, Object[] parameters) throws RPCException {
		Class<?> parameterTypes[] = new Class<?>[parameters.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}
		try {
			Method m = serviceInterface.getMethod(methodName, parameterTypes);
			return m.invoke(serviceHandler, parameters);
		} catch (Throwable e) {
			if (null != e.getCause())
				e = e.getCause();
			throw new RPCException(e);
		}
	}
}