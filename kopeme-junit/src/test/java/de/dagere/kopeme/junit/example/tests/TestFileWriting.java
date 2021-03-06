package de.dagere.kopeme.junit.example.tests;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitMultiplicationTest;

public class TestFileWriting {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testNormalWriting() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);

		String name = PerformanceTestUtils.PERFORMANCEFOLDER + "/" + JUnitAdditionTest.class.getName() + ".testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		f.delete();
	}

	@Test
	public void testDoubleWriting() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);

		jc.run(JUnitAdditionTest.class);

		String name = PerformanceTestUtils.PERFORMANCEFOLDER + "/" + JUnitAdditionTest.class.getName() + "." + "testAddition" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		f.delete();
	}

	@Test
	public void testResults() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitMultiplicationTest.class);

		String name = PerformanceTestUtils.PERFORMANCEFOLDER + "/" + JUnitMultiplicationTest.class.getName() + ".testMultiplication" + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());

		try {
			Kopemedata kd = new XMLDataLoader(f).getFullData();
			Testcases tc = kd.getTestcases();
			Assert.assertEquals(JUnitMultiplicationTest.class.getCanonicalName(), tc.getClazz());

			TestcaseType tct = null;
			for (TestcaseType t : tc.getTestcase()) {
				if (t.getName().equals("testMultiplication")) {
					tct = t;
					break;
				}
			}
			Assert.assertNotNull(tct);

			Datacollector timeCollector = null;
			for (Datacollector dc : tct.getDatacollector()) {
				if (dc.getName().equals("de.dagere.kopeme.datacollection.TimeDataCollector")) {
					timeCollector = dc;
					break;
				}
			}
			Assert.assertNotNull(timeCollector);

			for (Result r : timeCollector.getResult()) {
				int val = new Integer(r.getValue());
				int min = (int) r.getMin();
				int max = (int) r.getMax();
				Assert.assertThat(val, Matchers.greaterThan(0));
				Assert.assertThat(max, Matchers.greaterThanOrEqualTo(val));
				Assert.assertThat(val, Matchers.greaterThanOrEqualTo(min));
			}

		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Test
	public void testExceptionWriting() {

	}
}
