package org.rgupta.codility.practice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PaymentSettlementReconciliation {

	public String[] solution_chatGpt(String[] ledger, String[] bank) {

		Map<String, Long> expected = new HashMap<>();
		Map<String, Long> confirmed = new HashMap<>();
		Set<String> discrepancies = new HashSet<>();

		// Load expected settlements.
		for (String record : ledger) {
			String[] fields = record.split(",");
			expected.put(fields[0], Long.parseLong(fields[1]));
		}

		// Process external bank confirmations.
		for (String record : bank) {
			String[] fields = record.split(",");

			String id = fields[0];
			long amount = Long.parseLong(fields[1]);

			Long previous = confirmed.putIfAbsent(id, amount);

			// Conflicting bank confirmations.
			if (previous != null && previous.longValue() != amount) {
				discrepancies.add(id);
			}

			// Unknown payment or amount mismatch.
			Long expectedAmount = expected.get(id);

			if (expectedAmount == null
					|| expectedAmount.longValue() != amount) {
				discrepancies.add(id);
			}
		}

		// Payments with no bank confirmation.
		for (String id : expected.keySet()) {
			if (!confirmed.containsKey(id)) {
				discrepancies.add(id);
			}
		}

		String[] result = discrepancies.toArray(new String[0]);
		Arrays.sort(result);

		return result;
	}

	public String[] solution(String[] ledger, String[] bank) {
		Set<String> paymentsInError = new HashSet<>();
		Map<String, Long> ledgerMap = new HashMap<>();
		Map<String, Long> bankMap = new HashMap<>();

		populateMap(ledger, ledgerMap, paymentsInError);
		populateMap(bank, bankMap, paymentsInError);

		for (Map.Entry<String, Long> ledgerEntry : ledgerMap.entrySet()) {
			final Long bankAmount = bankMap.get(ledgerEntry.getKey());
			if (bankAmount == null || !bankAmount.equals(ledgerEntry.getValue())) {
				paymentsInError.add(ledgerEntry.getKey());
			}
		}
		final Set<String> bankSet = bankMap.keySet();
		bankSet.removeAll(ledgerMap.keySet());
		paymentsInError.addAll(bankSet);

		final String[] result = paymentsInError.toArray(new String[0]);
		Arrays.sort(result);
		return result;
	}

	private void populateMap(String[] entries, Map<String, Long> map, Set<String> errorContainer) {
		for (String ledgerEntry : entries) {
			final String[] entry = ledgerEntry.split(",");
			String paymentId = entry[0];
			long amount = Long.parseLong(entry[1]);
			final Long prevEntry = map.get(paymentId);
			if (prevEntry != null && prevEntry != amount) {
				errorContainer.add(paymentId);
			} else {
				map.put(paymentId, amount);
			}
		}
	}

	public static void main(String[] args) {
		String[] ledger = {
				"P01,1000",
				"P02,2500",
				"P03,800"
		};

		String[] bank = {
				"P01,1000",
				"P01,1000",
				"P02,2000",
				"P04,100"
		};

		String[] result = new PaymentSettlementReconciliation().solution(ledger, bank);
		System.out.println("result = " + Arrays.toString(result));

	}

}
