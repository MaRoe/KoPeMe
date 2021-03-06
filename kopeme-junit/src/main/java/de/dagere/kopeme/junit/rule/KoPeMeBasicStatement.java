package de.dagere.kopeme.junit.rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;

/**
 * Should once become base class of several TestExecutingStatements - isn't yet.
 * 
 * @author reichelt
 *
 */
public abstract class KoPeMeBasicStatement extends Statement {

	private static final Logger log = LogManager.getLogger(KoPeMeBasicStatement.class);

	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;
	protected Method method;
	protected TestRunnables runnables;

	protected int executionTimes, warmupExecutions, minEarlyStopExecutions, timeout;

	public KoPeMeBasicStatement(TestRunnables runnables, Method method, String filename) {
		super();
		this.runnables = runnables;
		this.filename = filename;
		this.method = method;

		PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();
			assertationvalues = new HashMap<>();
			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		} else {
			log.error("No @PerformanceTest-Annotation present!");
		}
	}

	/**
	 * Saves the measured data
	 */
	/**
	 * Tests weather the collectors given in the assertions and the maximale relative standard deviations are correct
	 * 
	 * @param tr
	 * @return
	 */
	protected boolean checkCollectorValidity(TestResult tr) {
		return PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation);
	}

	protected void runMainExecution(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {

			log.debug("--- Starting execution " + executions + "/" + executionTimes + " ---");
			runnables.getBeforeRunnable().run();
			tr.startCollection();
			runnables.getTestRunnable().run();
			tr.stopCollection();
			runnables.getAfterRunnable().run();

			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				log.trace("Entry: {} {}", entry.getKey(), entry.getValue());
			}
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + (executions - 1));
		tr.setRealExecutions(executions - 1);
	}

	protected void runWarmup(String methodString) {
		for (int i = 1; i <= warmupExecutions; i++) {
			runnables.getBeforeRunnable().run();
			log.info("--- Starting warmup execution " + methodString + " " + i + "/" + warmupExecutions + " ---");
			runnables.getTestRunnable().run();
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
			runnables.getAfterRunnable().run();
		}
	}
}