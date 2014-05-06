package cn.niven.rpc.client;

import java.lang.reflect.Method;
import java.util.HashMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import cn.niven.rpc.Common;
import cn.niven.rpc.types.RPCException;

public class RPCClient {
	private static final ClassPool classPool = ClassPool.getDefault();
	private static final CtClass iClientCtClass;
	static {
		CtClass _iClientCtClass;
		try {
			_iClientCtClass = classPool.get(IClient.class.getName());
		} catch (NotFoundException e) {
			_iClientCtClass = null;
			e.printStackTrace();
			System.exit(-1);
		}
		iClientCtClass = _iClientCtClass;
	}

	public static <InterfaceType> InterfaceType getClient(
			Class<InterfaceType> interfaceType, String serverAddr) {
		try {
			String className = "Impl" + interfaceType.getSimpleName();
			CtClass proxy = classPool.getOrNull(className);
			if (proxy == null) {
				proxy = classPool.makeClass(className);
				CtClass interfaceCtClass = classPool.get(interfaceType
						.getName());
				proxy.setInterfaces(new CtClass[] { interfaceCtClass });
				proxy.setSuperclass(iClientCtClass);
				HashMap<String, Method> methods = new HashMap<String, Method>();
				for (Method method : interfaceType.getDeclaredMethods()) {
					methods.put(method.getName(), method);
				}
				for (CtMethod method : interfaceCtClass.getMethods()) {
					Method oMethod = methods.get(method.getName());
					if (null != oMethod) {
						CtMethod proxyMethod = CtNewMethod.copy(method, proxy,
								null);
						String methodBody = "_niven_rpc_invoke(\""
								+ Common.methodFullName(oMethod) + "\"";
						if (method.getParameterTypes().length >= 0) {
							String params = "";
							for (int i = 0; i < method.getParameterTypes().length; i++) {
								params += ","
										+ wrapPrimitiveToObjectCast(
												oMethod.getParameterTypes()[i],
												"$" + (i + 1));
							}
							methodBody += ", new Object[]{"
									+ params.substring(1) + "}";
						}
						methodBody += ")";
						Class<?> returnType = oMethod.getReturnType();
						if (!Void.TYPE.equals(returnType)) {
							methodBody = "Object ret = "
									+ methodBody
									+ "; return "
									+ wrapObjectToPrimitiveCast(returnType,
											"ret");
						}
						methodBody = "{" + methodBody + ";}";
						System.out.println(methodBody);
						proxyMethod.setBody(methodBody);
						proxy.addMethod(proxyMethod);
					}
				}
			}
			IClient iClient = (IClient) proxy.toClass().newInstance();
			iClient.serverAddr = serverAddr;
			iClient.interfaceTypeName = interfaceType.getName();
			return interfaceType.cast(iClient);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RPCException(
					"Failed to make client proxy from interface", e);
		}
	}

	public static String getPrimitiveClassName(Class<?> target) {
		for (int i = 0; i < Common.primitiveTypes.length; i += 4) {
			if (Common.primitiveTypes[i + 1].equals(target)) {
				return ((Class<?>) Common.primitiveTypes[i]).getSimpleName()
						+ ".TYPE";
			}
		}
		return null;
	}

	public static String wrapObjectToPrimitiveCast(Class<?> target,
			String content) {
		if (target.isPrimitive()) {
			for (int i = 0; i < Common.primitiveTypes.length; i += 4) {
				if (Common.primitiveTypes[i + 1].equals(target)) {
					String typeName = ((Class<?>) Common.primitiveTypes[i])
							.getSimpleName();
					return content + "==null?" + Common.primitiveTypes[i + 2]
							+ ":((" + typeName + ")" + content + ")."
							+ Common.primitiveTypes[i + 3] + "()";
				}
			}
		}
		return "((" + target.getName() + ")(" + content + "))";
	}

	public static String wrapPrimitiveToObjectCast(Class<?> target,
			String content) {
		if (target.isPrimitive()) {
			for (int i = 0; i < Common.primitiveTypes.length; i += 4) {
				if (Common.primitiveTypes[i + 1].equals(target)) {
					return ((Class<?>) Common.primitiveTypes[i]).getName()
							+ ".valueOf(" + content + ")";
				}
			}
		}
		return content;
	}
}
