package cn.niven.rpc.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import cn.niven.rpc.Common;
import cn.niven.rpc.types.RPCException;

public class ServiceMap {

	private HashMap<String, ServiceItem> serviceMap = new HashMap<String, ServiceItem>();

	public void put(Class<?> serviceInterface, Object serviceHandler) {
		serviceMap.put(serviceInterface.getName(), new ServiceItem(
				serviceInterface, serviceHandler));
	}

	public Object invoke(String serviceName, String methodName,
			Object[] parameters) {
		ServiceItem item = serviceMap.get(serviceName);
		if (item == null)
			throw new RPCException("Service not found on server");
		return item.invoke(methodName, parameters);
	}
}

class ServiceItem {
	private final Object serviceHandler;
	private static final HashMap<String, Method> methodMap = new HashMap<String, Method>();

	ServiceItem(Class<?> serviceInterface, Object serviceHandler) {
		this.serviceHandler = serviceHandler;
		for (Method method : serviceInterface.getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())) {
				methodMap.put(Common.methodFullName(method), method);
			}
		}
	}

	Object invoke(String methodName, Object[] parameters) {
		try {
			Method m = methodMap.get(methodName);
			if (m == null)
				throw new RPCException("No such method in service");
			return m.invoke(serviceHandler, parameters);
		} catch (Throwable e) {
			if (null != e.getCause())
				e = e.getCause();
			throw new RPCException(e);
		}
	}
}