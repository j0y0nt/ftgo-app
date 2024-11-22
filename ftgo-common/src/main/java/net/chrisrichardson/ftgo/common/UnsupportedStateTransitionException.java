package net.chrisrichardson.ftgo.common;

public class UnsupportedStateTransitionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -420987691737856424L;

	public UnsupportedStateTransitionException(Enum state) {
	    super("current state: " + state);
	  }
}
