package com.hryesf;

import java.text.NumberFormat;
import java.util.*;

public class Statement {

    public String statement(Invoice invoice, Map<String, Play> plays) {

        int totalAmount = 0;
        int volumeCredits = 0;

        StringBuilder result = new StringBuilder();
        result.append("chapter_one.Statement for ").append(invoice.customer).append("\n");

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance perf : invoice.performances) {
            Play play = plays.get(perf.playID);
            int thisAmount = 0;

            switch (play.type) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.audience > 30) {
                        thisAmount += 1000 * (perf.audience - 30);
                    }
                    break;

                case "comedy":
                    thisAmount = 30000;
                    if (perf.audience > 20) {
                        thisAmount += 10000 + 500 * (perf.audience - 20);
                    }
                    thisAmount += 300 * perf.audience;
                    break;

                default:
                    throw new RuntimeException("unknown type: " + play.type);
            }

            // add volume credits
            volumeCredits += Math.max(perf.audience - 30, 0);

            // extra credit for every 5 comedy attendees
            if ("comedy".equals(play.type)) {
                volumeCredits += Math.floor(perf.audience / 5.0);
            }

            // print line for this order
            result.append("  ").append(play.name).append(": ")
                    .append(format.format(thisAmount / 100.0))
                    .append(" (").append(perf.audience).append(" seats)\n");

            totalAmount += thisAmount;
        }

        result.append("Amount owed is ").append(format.format(totalAmount / 100.0)).append("\n");
        result.append("You earned ").append(volumeCredits).append(" credits\n");

        return result.toString();
    }
}
