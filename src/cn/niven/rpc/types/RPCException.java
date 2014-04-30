package cn.niven.rpc.types;

public class RPCException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public final Throwable cause;

	public RPCException(String errorMessage) {
		super(errorMessage);
		cause = this;
	}

	public RPCException(Throwable cause) {
		this("Exception occured during RPC", cause);
	}

	public RPCException(String errorMessage, Throwable cause) {
		super(errorMessage);
		this.cause = cause;
	}
}
