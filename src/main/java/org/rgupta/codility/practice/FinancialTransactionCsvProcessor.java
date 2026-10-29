package org.rgupta.codility.practice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class FinancialTransactionCsvProcessor {
	public String solution_chatgpt(String S) {

		Map<String, BigDecimal> totals = new HashMap<>();

		// Skip the header and process one line at a time.
		S.lines()
				.skip(1)
				.filter(line -> !line.isBlank())
				.forEach(line -> {

					String[] fields = line.split(",");

					if ("EUR".equals(fields[2])
							&& "SETTLED".equals(fields[4])) {

						String account = fields[1];
						BigDecimal amount = new BigDecimal(fields[3]);

						totals.merge(account, amount, BigDecimal::add);
					}
				});

		String winner = null;
		BigDecimal highestTotal = null;

		for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {

			String account = entry.getKey();
			BigDecimal total = entry.getValue();

			if (highestTotal == null
					|| total.compareTo(highestTotal) > 0
					|| (total.compareTo(highestTotal) == 0
							&& account.compareTo(winner) < 0)) {

				highestTotal = total;
				winner = account;
			}
		}

		return winner == null ? "NONE" : winner;
	}

	public String solution(String S) {
		// Your implementation
		final Map<String, BigDecimal> eurAccountBalances = new HashMap<>();
		final String[] split = S.split("\\R");
		boolean first = false;
		for (String line : split) {
			if (!first) {
				first = true;
				continue;
			}
			if (line.isBlank()) {
				continue;
			}
			String[] transaction = line.split(",");
			String accountId = transaction[1];
			String currency = transaction[2];
			String status = transaction[4];
			if (currency.equals("EUR") && status.equals("SETTLED")) {
				BigDecimal amount = new BigDecimal(transaction[3]);
				BigDecimal accountBalance = eurAccountBalances.getOrDefault(accountId, BigDecimal.ZERO);
				eurAccountBalances.put(accountId, accountBalance.add(amount));
			}
		}
		BigDecimal maxAccountTotal = BigDecimal.valueOf(Long.MIN_VALUE);
		String highestAccount = null;
		for (Map.Entry<String, BigDecimal> entry : eurAccountBalances.entrySet()) {
			final BigDecimal accBalance = entry.getValue();
			final String account = entry.getKey();
			if (accBalance.compareTo(maxAccountTotal) > 0) {
				maxAccountTotal = accBalance;
				highestAccount = account;
			} else if (accBalance.compareTo(maxAccountTotal) == 0 && account.compareTo(highestAccount) < 0) {
				highestAccount = account;
			}
		}
		return highestAccount == null ? "NONE" : highestAccount;
	}

	private static final String INPUT = "transaction_id,account_id,currency,amount,status\n"
			+ "T001,ACC7,EUR,120.50,SETTLED\n"
			+ "T002,ACC2,EUR,200.00,SETTLED\n"
			+ "T003,ACC7,EUR,-20.50,SETTLED\n"
			+ "T004,ACC2,USD,400.00,SETTLED\n"
			+ "T005,ACC2,EUR,-50.00,SETTLED\n"
			+ "T006,ACC1,EUR,150.00,SETTLED\n"
			+ "T007,ACC9,EUR,900.00,PENDING";

	public static void main(String[] args) {
		final String result = new FinancialTransactionCsvProcessor().solution(INPUT);
		System.out.println("result = " + result);
	}
}