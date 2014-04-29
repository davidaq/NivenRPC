package cn.niven.rpc.server;

import cn.niven.rpc.server.abstraction.IRequestHandler;
import cn.niven.rpc.types.RPCException;
import cn.niven.rpc.types.RequestVO;

public class RPCServer implements IRequestHandler {
	private final ServiceMap serviceMap = new ServiceMap();

	public void exportService(Class<?> serviceInterface, Object serviceHandler) {
		serviceMap.put(serviceInterface, serviceHandler);
	}

	public void serve(int port) {
		Composition.baseServer.serve(port, this);
	}

	@Override
	public Object handleRequest(Object input) throws Exception {
		if (input instanceof RequestVO) {
			RequestVO request = (RequestVO) input;
			return serviceMap.invoke(request.serviceName, request.methodName,
					request.parameters);
		} else {
			throw new RPCException("Invalid request format");
		}
	}
}
