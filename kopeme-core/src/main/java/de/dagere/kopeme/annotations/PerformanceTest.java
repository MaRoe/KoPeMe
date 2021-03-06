package de.dagere.kopeme.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.dagere.kopeme.MaximalRelativeStandardDeviation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface PerformanceTest {
	/**
	 * Marks the counts of executions, that should be executed and measured for
	 * real data.
	 * 
	 * @return
	 */
	public int executionTimes() default 10;

	public int timeout() default 100000;

	public boolean failAfterTimeout() default false;

	public boolean logFullData() default false;

	// public float maximalRelativeStandardDeviation() default 0;

	/**
	 * Marks the count of executions, that should be executed before the
	 * measuring begins.
	 * 
	 * @return
	 */
	public int warmupExecutions() default 1;

	public Assertion[] assertions() default {};

	public int minEarlyStopExecutions() default 10;

	public MaximalRelativeStandardDeviation[] deviations() default {};
}