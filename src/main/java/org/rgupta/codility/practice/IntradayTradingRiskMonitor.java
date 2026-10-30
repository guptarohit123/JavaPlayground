package org.rgupta.codility.practice;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;

public class IntradayTradingRiskMonitor {

	enum Side {
		BUY, SELL
	}

	enum Status {
		EXECUTED, CANCELLED, PENDING
	}

	private record Trade(String tradeId, Side side, BigDecimal amountCents, Status status) {
	}

	private record Desk(
			String name,
			BigDecimal limitCents,
			List<Trade> trades) {
	}

	private record RiskInput(List<Desk> desks) {
	}

	public String[] solution(String json) {
		// Your implementation
		Gson gson = new Gson();
		final RiskInput riskInput = gson.fromJson(json, RiskInput.class);
		final Set<String> breachedDesks = new HashSet<>();
		riskInput.desks.forEach(desk -> {
			BigDecimal currentExposure = BigDecimal.ZERO;
			for (Trade trade : desk.trades()) {
				if (trade.status() != Status.EXECUTED) {
					continue;
				}
				if (trade.side() == Side.BUY) {
					currentExposure = currentExposure.add(trade.amountCents());
				} else {
					currentExposure = currentExposure.subtract(trade.amountCents());
				}
				if (currentExposure.abs().compareTo(desk.limitCents()) > 0) {
					breachedDesks.add(desk.name());
				}
			}
		});
		String[] brachedDesksArr = breachedDesks.toArray(new String[0]);
		Arrays.sort(brachedDesksArr);
		return brachedDesksArr;
	}

	public static void main(String[] args) {
		String jsonInput = "{\n"
				+ "  \"desks\": [\n"
				+ "    {\n"
				+ "      \"name\": \"FX\",\n"
				+ "      \"limitCents\": 1000,\n"
				+ "      \"trades\": [\n"
				+ "        {\"tradeId\": \"T1\", \"side\": \"BUY\", \"amountCents\": 800, \"status\": \"EXECUTED\"},\n"
				+ "        {\"tradeId\": \"T2\", \"side\": \"BUY\", \"amountCents\": 350, \"status\": \"EXECUTED\"},\n"
				+ "        {\"tradeId\": \"T3\", \"side\": \"SELL\", \"amountCents\": 500, \"status\": \"EXECUTED\"}\n"
				+ "      ]\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"name\": \"RATES\",\n"
				+ "      \"limitCents\": 500,\n"
				+ "      \"trades\": [\n"
				+ "        {\"tradeId\": \"T4\", \"side\": \"SELL\", \"amountCents\": 400, \"status\": \"EXECUTED\"},\n"
				+ "        {\"tradeId\": \"T5\", \"side\": \"SELL\", \"amountCents\": 150, \"status\": \"EXECUTED\"},\n"
				+ "        {\"tradeId\": \"T6\", \"side\": \"BUY\", \"amountCents\": 300, \"status\": \"EXECUTED\"}\n"
				+ "      ]\n"
				+ "    },\n"
				+ "    {\n"
				+ "      \"name\": \"CREDIT\",\n"
				+ "      \"limitCents\": 600,\n"
				+ "      \"trades\": [\n"
				+ "        {\"tradeId\": \"T7\", \"side\": \"BUY\", \"amountCents\": 1000, \"status\": \"CANCELLED\"},\n"
				+ "        {\"tradeId\": \"T8\", \"side\": \"BUY\", \"amountCents\": 600, \"status\": \"EXECUTED\"}\n"
				+ "      ]\n"
				+ "    }\n"
				+ "  ]\n"
				+ "}";
		String[] result = new IntradayTradingRiskMonitor().solution(jsonInput);
		System.out.println("result = " + Arrays.toString(result));
	}
}
