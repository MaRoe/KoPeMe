package de.dagere.kopeme.example.tests;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.Checker;
import de.dagere.kopeme.PerformanceTest;
import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datacollection.TimeDataCollector;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerJUnit;

@RunWith(PerformanceTestRunnerJUnit.class)
public class ExampleJUnitTests {

	@Test
	@PerformanceTest(executionTimes=5)
	public void testMoebelkauf(final TestResult tr) {
		tr.setCollectors(DataCollectorList.STANDARD);
		tr.startCollection();
		int anzahl = 1000 + (int) (Math.random() * 10);
		for (int i = 0; i < anzahl; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		tr.stopCollection();
		tr.addValue("Anzahl", (int)(1000 + Math.random()*100));

		tr.setChecker(new Checker() {

			@Override
			public void checkValues(TestResult tr) {
				MatcherAssert.assertThat(tr.getValue(TimeDataCollector.class
						.getName()), Matchers.lessThan((long) (tr
						.getLastRunsAverage(TimeDataCollector.class.getName(),
								5) * 1.10)));
			}
		});

	}

}