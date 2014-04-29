package cn.niven.rpc.types;

public class RPCException extends Exception {

	private static final long serialVersionUID = 1L;

	public final Throwable cause;

	public RPCException(Throwable cause) {
		super("Exception occured during RPC");
		this.cause = cause;
	}

	public RPCException(String errorMessage) {
		super(errorMessage);
		cause = this;
	}
}
