package de.kopeme;
import java.lang.annotation.*;

import de.kopeme.datacollection.DataCollectorList;



import static java.lang.annotation.ElementType.*;



@Retention(RetentionPolicy.RUNTIME)
@Target( { METHOD } )
public @interface PerformanceTest{
	/**
	 * Marks the counts of executions, that should be executed and measured for real data.
	 * @return
	 */
	public int executionTimes() default 5;
	
//	public float maximalRelativeStandardDeviation() default 0;
	
	/**
	 * Marks the count of executions, that should be executed before the measuring begins. 
	 * @return
	 */
	public int warmupExecutions() default 1;
	
	public Assertion[] assertions() default {};
	
	public MaximalRelativeStandardDeviation[] deviations() default {};
}