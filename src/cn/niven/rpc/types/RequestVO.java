package cn.niven.rpc.types;

import java.io.Serializable;

public final class RequestVO implements Serializable {
	private static final long serialVersionUID = -5394921948255857993L;
	public String serviceName;
	public String methodName;
	public Object[] parameters;
}
