package de.dagere.kopeme;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.TemperatureCollector;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.DataStorer;
import de.dagere.kopeme.datastorage.PerformanceDataMeasure;
import de.dagere.kopeme.datastorage.XMLDataStorer;

public class PerformanceTestUtils {
	private static final Logger log = LogManager.getLogger(PerformanceTestUtils.class);

	/**
	 * Tests weather the collectors given in the assertions and the maximale
	 * relative standard deviations are correct
	 * 
	 * @param tr
	 *            Testresult, that should be tested
	 * @return Weather the collector is valid or not
	 */
	public static boolean checkCollectorValidity(TestResult tr, Map<String, Long> assertationvalues, Map<String, Double> maximalRelativeStandardDeviation) {
		log.info("Checking DataCollector validity...");
		boolean valid = true;
		for (String collectorName : assertationvalues.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for assertion: " + collectorName);
			}
		}
		String keys = "";
		for (String key : tr.getKeys()) {
			keys += key + " ";
		}
		for (String collectorName : maximalRelativeStandardDeviation.keySet()) {
			if (!tr.getKeys().contains(collectorName)) {
				valid = false;
				log.warn("Invalid Collector for maximale relative standard deviation: " + collectorName + " Available Keys: " + keys);
				for (String key : tr.getKeys()) {
					System.out.println(key + " - " + collectorName + ": " + key.equals(collectorName));
				}
			}
		}
		log.info("... " + valid);
		return valid;
	}

	public static void saveData(String testcasename, TestResult tr, int executions, boolean failure, boolean error, String filename, boolean saveValues) {
		try {
			DataStorer xds = new XMLDataStorer(filename, testcasename);
			for (String s : tr.getKeys()) {
				double relativeStandardDeviation = tr.getRelativeStandardDeviation(s);
				long value = tr.getValue(s);
				log.info("Ermittle Minimum");
				long min = tr.getMinumumCurrentValue(s);
				log.info("Min: " + min);
				long max = tr.getMaximumCurrentValue(s);
				double first10percentile = getPercentile(tr.getValues(s), 10);
				PerformanceDataMeasure performanceDataMeasure = new PerformanceDataMeasure(testcasename, s, value, relativeStandardDeviation, executions, min,
						max, first10percentile, TemperatureCollector.getTemperature());
				List<Long> values = saveValues ? tr.getValues(s) : null;
				xds.storeValue(performanceDataMeasure, values);
				// xds.storeValue(s, getValue(s));
				log.info("{}: {}, (rel. Standardabweichung: {})", s, value, relativeStandardDeviation);
			}
			xds.storeData();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public static double getPercentile(List<Long> values, int percentil) {
		double wertArray[] = new double[values.size()];
		int i = 0;
		for (Long l : values) {
			wertArray[i] = l;
			System.out.println("Wert: " + wertArray[i] + " " + l);
			i++;
		}

		Percentile p = new Percentile(percentil);
		double evaluate = p.evaluate(wertArray);
		log.debug("Perzentil: " + evaluate);
		return evaluate;
	}

}
