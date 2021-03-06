package de.dagere.kopeme.example.tests;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.exampletests.pure.ExamplePurePerformanceTests;
import de.dagere.kopeme.exampletests.pure.TestTimeTest;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.testrunner.PerformanceTestRunnerKoPeMe;

public class PureKoPeMeTest {

	private static final Logger log = LogManager.getLogger(PureKoPeMeTest.class);

	@Test
	public void testPureKoPeMeExecution() throws Throwable {
		String params[] = new String[] { ExamplePurePerformanceTests.class.getName() };
		PerformanceTestRunnerKoPeMe.main(params);
	}

	@Test
	public void testExecutionTimeMeasurement() {
		String params[] = new String[] { TestTimeTest.class.getName() };
		try {
			long start = System.currentTimeMillis();
			PerformanceTestRunnerKoPeMe.main(params);
			long duration = System.currentTimeMillis() - start;
			log.debug("Overall Duration: " + duration);
			XMLDataLoader xdl = new XMLDataLoader("performanceresults/" + TestTimeTest.class.getCanonicalName() + ".simpleTest.yaml");
			Kopemedata kd = xdl.getFullData();
			List<Datacollector> collector = null;
			for (TestcaseType tct : kd.getTestcases().getTestcase()) {
				if (tct.getName().contains("simpleTest")) {
					collector = tct.getDatacollector();
				}
			}
			Assert.assertNotNull(collector);

			double timeConsumption = 0.0;
			for (Datacollector c : collector) {
				if (c.getName().contains("TimeData")) {
					timeConsumption = Double.parseDouble(c.getResult().get(c.getResult().size() - 1).getValue());
				}
			}
			Assert.assertNotEquals(timeConsumption, 0.0);

			int milisecondTime = (int) ((timeConsumption * 40) / 1000);

			Assert.assertThat((long) milisecondTime, Matchers.lessThan(duration));

			// collector.get

		} catch (Throwable e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
