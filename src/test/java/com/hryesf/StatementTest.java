package com.hryesf;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatementTest {

    private final Statement statement = new Statement();

    @Test
    void shouldCalculateTragedyAmountUnder30Audience() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("hamlet", 20))
        );

        Map<String, Play> plays = Map.of(
                "hamlet", new Play("Hamlet", "tragedy")
        );

        String result = statement.statement(invoice, plays);

        assertThat(result).contains("$400.00");
    }

    @Test
    void shouldCalculateTragedyAmountAbove30Audience() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("hamlet", 40))
        );

        Map<String, Play> plays = Map.of(
                "hamlet", new Play("Hamlet", "tragedy")
        );

        String result = statement.statement(invoice, plays);

        int expected = 40000 + (1000 * (40 - 30)); // = 50000
        String expectedFormatted = NumberFormat.getCurrencyInstance(Locale.US)
                .format(expected / 100.0);

        assertThat(result).contains(expectedFormatted);
    }

    @Test
    void shouldCalculateComedyAmountUnder20Audience() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("aslike", 10))
        );

        Map<String, Play> plays = Map.of(
                "aslike", new Play("As You Like It", "comedy")
        );

        String result = statement.statement(invoice, plays);

        int expected = 30000 + (300 * 10); // 33000
        String expectedFormatted = NumberFormat.getCurrencyInstance(Locale.US)
                .format(expected / 100.0);

        assertThat(result).contains(expectedFormatted);
    }

    @Test
    void shouldCalculateComedyAmountAbove20Audience() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("aslike", 30))
        );

        Map<String, Play> plays = Map.of(
                "aslike", new Play("As You Like It", "comedy")
        );

        String result = statement.statement(invoice, plays);

        int expected = 30000 + 10000 + (500 * (30 - 20)) + (300 * 30);
        String expectedFormatted = NumberFormat.getCurrencyInstance(Locale.US)
                .format(expected / 100.0);

        assertThat(result).contains(expectedFormatted);
    }

    @Test
    void shouldCalculateVolumeCreditsForTragedy() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("hamlet", 40)) // 40-30 = 10
        );

        Map<String, Play> plays = Map.of(
                "hamlet", new Play("Hamlet", "tragedy")
        );

        String result = statement.statement(invoice, plays);

        assertThat(result).contains("10 credits");
    }

    @Test
    void shouldCalculateVolumeCreditsForComedy() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("aslike", 25)) // base: max(25-30=0) + floor(25/5=5)
        );

        Map<String, Play> plays = Map.of(
                "aslike", new Play("As You Like It", "comedy")
        );

        String result = statement.statement(invoice, plays);

        assertThat(result).contains("5 credits");
    }

    @Test
    void shouldHandleMultiplePerformances() {
        Invoice invoice = new Invoice(
                "BigCo",
                List.of(
                        new Performance("hamlet", 55),
                        new Performance("aslike", 35)
                )
        );

        Map<String, Play> plays = Map.of(
                "hamlet", new Play("Hamlet", "tragedy"),
                "aslike", new Play("As You Like It", "comedy")
        );

        String result = statement.statement(invoice, plays);

        assertThat(result).contains("Statement for BigCo");
        assertThat(result).contains("Hamlet");
        assertThat(result).contains("As You Like It");
        assertThat(result).contains("Amount owed is");
        assertThat(result).contains("credits");
    }

    @Test
    void shouldThrowExceptionForUnknownType() {
        Invoice invoice = new Invoice(
                "Test",
                List.of(new Performance("othello", 20))
        );

        Map<String, Play> plays = Map.of(
                "othello", new Play("Othello", "mystery")
        );

        assertThatThrownBy(() -> statement.statement(invoice, plays))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("unknown type");
    }
}
