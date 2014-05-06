package cn.niven.rpc;

import java.lang.reflect.Method;

public class Common {
	public static String methodFullName(Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(method.getName());
		for (Class<?> type : method.getParameterTypes()) {
			builder.append('-').append(type.getName());
		}
		return builder.toString();
	}

	public static final Object primitiveTypes[] = new Object[] { Integer.class,
			Integer.TYPE, "0", "intValue", Short.class, Short.TYPE, "0",
			"shortValue", Long.class, Long.TYPE, "0", "longValue", Float.class,
			Float.TYPE, "0", "floatValue", Double.class, Double.TYPE, "0",
			"doubleValue", Boolean.class, Boolean.TYPE, "false",
			"booleanValue", Character.class, Character.TYPE, "0", "charValue",
			Byte.class, Byte.TYPE, "0", "byteValue" };
}
