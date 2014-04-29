package cn.niven.rpc.types;

public class ResponseVO {
	private final Object result;

	public ResponseVO(Object result) {
		this.result = result;
	}

	public <ResultType> ResultType getResult(Class<ResultType> resultType)
			throws RPCException {
		if (result instanceof RPCException)
			throw (RPCException) result;
		if (resultType.isInstance(result))
			return resultType.cast(result);
		throw new RPCException("Unexpected result type");
	}
}
