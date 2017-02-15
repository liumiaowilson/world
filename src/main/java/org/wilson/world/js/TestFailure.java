package org.wilson.world.js;

public class TestFailure {
	private Object expected;
	private Object real;
	private String message;
	
	public TestFailure(Object expected, Object real, String message) {
		this.expected = expected;
		this.real = real;
		this.message = message;
	}
	
	public Object getExpected() {
		return this.expected;
	}
	
	public Object getReal() {
		return this.real;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("The expected was [").append(expected).append("], but actually it is [").append(real).append("]: ").append(message);
		
		return sb.toString();
	}
}
