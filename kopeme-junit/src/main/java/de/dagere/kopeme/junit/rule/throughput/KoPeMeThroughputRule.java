package de.dagere.kopeme.junit.rule.throughput;

import java.lang.reflect.Method;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.junit.rule.TestRunnables;

/**
 * This rule enables measuring, how often a method can be called meeting certain performance requirements. The method is called as often as given by the
 * parameters, where the execution times increase every call. The first test execution, where the *sum* of a performance measurement does not meet one
 * assertion, is marked as failed.
 * 
 * @author reichelt
 *
 */
public class KoPeMeThroughputRule implements TestRule {

	private final int maxsize, stepsize;

	private Object testObject;

	public KoPeMeThroughputRule(int stepsize, int maxsize, Object testObject) {
		this.stepsize = stepsize;
		this.maxsize = maxsize;
		this.testObject = testObject;
	}

	@Override
	public Statement apply(final Statement stmt, Description descr) {
		if (descr.isTest()) {
			Method testMethod = null;
			Class<?> testClass = null;
			try {
				// testClass = Class.forName(descr.getClassName());
				testClass = testObject.getClass();
				testMethod = testClass.getMethod(descr.getMethodName());
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			TestRunnables runnables = new TestRunnables(new Runnable() {

				@Override
				public void run() {
					try {
						stmt.evaluate();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, testClass, testObject);

			return new ThroughputStatement(runnables, testMethod, testClass.getName() + ".yaml", stepsize, maxsize);
		} else {
			return stmt;
		}
	}

}
