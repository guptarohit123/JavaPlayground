package org.rgupta.codility.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankingServiceErrorLogDetector {

	public String[] solution(String logs, int threshold) {
		// Implement
		record ErrorBurstKey(String service, String hour, String minute) {
		}
		;
		Map<ErrorBurstKey, Integer> errorCounts = new HashMap<>();
		logs.lines().forEach(line -> {
			String[] log = line.split("\\|");
			if (log[2].equals("ERROR") | log[2].equals("TIMEOUT")) {
				String[] timeParts = log[0].split(":");
				errorCounts.merge(new ErrorBurstKey(log[1], timeParts[0], timeParts[1]), 1, (ignored, val) -> val + 1);
			}
		});
		List<String> servicesInError = new ArrayList<>();
		for (Map.Entry<ErrorBurstKey, Integer> entry : errorCounts.entrySet()) {
			if (entry.getValue() >= threshold) {
				servicesInError.add(entry.getKey().service);
			}
		}
		return servicesInError.toArray(new String[0]);
	}

	public static void main(String[] args) {
		String input1 = "09:41:01|pricing|OK\n"
				+ "09:41:07|orders|ERROR\n"
				+ "09:41:45|orders|TIMEOUT\n"
				+ "09:42:00|orders|ERROR\n"
				+ "09:42:01|pricing|TIMEOUT\n"
				+ "09:42:44|pricing|ERROR\n"
				+ "09:42:58|risk|ERROR";
		int threshold1 = 2;
		String[] result = new BankingServiceErrorLogDetector().solution(input1, threshold1);
		System.out.println("result = " + Arrays.toString(result));

	}
}
