package org.wilson.world.js;

import java.util.ArrayList;
import java.util.List;

public class TestResult {
	private List<TestFailure> failures = new ArrayList<TestFailure>();
	
	public TestResult() {
		
	}
	
	public List<TestFailure> getFailures() {
		return this.failures;
	}
	
	public void addFailure(Object expected, Object real, String message) {
		this.failures.add(new TestFailure(expected, real, message));
	}
	
	public void assertTrue(boolean condition, String message) {
		if(!condition) {
			this.addFailure(true, false, message);
		}
	}
	
	public void assertEquals(Object expected, Object real, String message) {
		if((expected == null && real != null) || !expected.equals(real)) {
			this.addFailure(expected, real, message);
		}
	}
}
