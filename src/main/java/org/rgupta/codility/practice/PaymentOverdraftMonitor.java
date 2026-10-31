package org.rgupta.codility.practice;

import java.util.*;

public class PaymentOverdraftMonitor {

    public String[] solution(String[] events, long overdraftLimitCents) {

        Set<String> consumedEventIds = new HashSet<>();
        Map<String, Long> accountBalances = new HashMap<>();
        Set<String> flaggedAccounts = new HashSet<>();
        for (String event : events) {
            String[] parts = event.split("\\|");
            String eventId = parts[0];
            String accountId = parts[1];
            String action = parts[2];
            long amountCents = Long.parseLong(parts[3]);
            String status = parts[4];
            if (!consumedEventIds.add(eventId)) {
                continue;
            }
            if (!"SETTLED".equals(status)) {
                continue;
            }
            long currAccountBalance = accountBalances.getOrDefault(accountId, 0L);
            if ("CREDIT".equals(action)) {
                currAccountBalance = currAccountBalance + amountCents;
            } else {
                currAccountBalance = currAccountBalance - amountCents;
            }
            accountBalances.put(accountId, currAccountBalance);
            if (currAccountBalance < -overdraftLimitCents) {
                flaggedAccounts.add(accountId);
            }
        }
        String[] flaggedAccountsArr = flaggedAccounts.toArray(new String[0]);
        Arrays.sort(flaggedAccountsArr);
        return flaggedAccountsArr;
    }


    public static void main(String[] args) {
        String[] events1 = {
                "p1|ACC1|DEBIT|500|SETTLED",
                "p2|ACC2|CREDIT|200|SETTLED",
                "p3|ACC2|DEBIT|600|SETTLED",
                "p4|ACC1|CREDIT|500|SETTLED",
                "p1|ACC1|DEBIT|500|SETTLED",
                "p5|ACC3|DEBIT|1000|PENDING"
        };

        long overdraftLimitCents = 300;

        PaymentOverdraftMonitor sol = new PaymentOverdraftMonitor();

        System.out.println(
                Arrays.toString(sol.solution(events1, overdraftLimitCents))
        );
    }
}
