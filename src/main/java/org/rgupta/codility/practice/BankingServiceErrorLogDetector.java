package org.rgupta.codility.practice;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BankingServiceErrorLogDetector {

	public String[] solution(String logs, int threshold) {
		// Implement
		record ErrorBurstKey(String service, String minute) {
		}
		Map<ErrorBurstKey, Integer> errorCounts = new HashMap<>();
		Set<String> servicesInError = new HashSet<>();
		logs.lines()
				.filter(line -> !line.isBlank())
				.forEach(line -> {
			String[] log = line.split("\\|");
					final String status = log[2];
					if ("ERROR".equals(status) | "TIMEOUT".equals(status)) {
						String minute = log[0].substring(0, 5);
						final String service = log[1];
						final ErrorBurstKey key = new ErrorBurstKey(service, minute);
						int count = errorCounts.merge(key, 1, Integer::sum);
						if (count >= threshold) {
							servicesInError.add(service);
						}
			}
		});

		final String[] servicesInErrArr = servicesInError.toArray(new String[0]);
		Arrays.sort(servicesInErrArr);
		return servicesInErrArr;
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
		// Below is scratchpad
		String input = "2026-10-30 11:43:07.123";
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime localDateTime = LocalDateTime.parse(input, format);
		System.out.println(" = " + localDateTime.truncatedTo(ChronoUnit.MINUTES));
		String normalInput = "09:34:32.234";
		LocalTime localTime = LocalTime.parse(normalInput);
		System.out.println("localTime.truncatedTo(ChronoUnit.MINUTES) = " + localTime.truncatedTo(ChronoUnit.MINUTES));

	}
}
