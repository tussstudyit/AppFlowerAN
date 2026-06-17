package com.example.appshopbanhang.core.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyFormatter {
    private static final Locale VIETNAM = new Locale("vi", "VN");

    private MoneyFormatter() {
    }

    public static String format(Number amount) {
        double value = amount == null ? 0D : amount.doubleValue();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(VIETNAM);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(Math.round(value));
    }

    public static String toPlainVnd(Number amount) {
        double value = amount == null ? 0D : amount.doubleValue();
        return String.valueOf(Math.round(value));
    }

    public static float parseVndInput(String input) {
        if (input == null) {
            throw new NumberFormatException("Empty money value");
        }

        String normalized = input.trim()
                .replace("₫", "")
                .replace("đ", "")
                .replace("VND", "")
                .replace("vnd", "")
                .replace(" ", "");
        if (normalized.matches("\\d+[\\.,]0+")) {
            normalized = normalized.substring(0, Math.max(normalized.indexOf('.'), normalized.indexOf(',')));
        } else {
            normalized = normalized.replace(".", "").replace(",", "");
        }

        float value = Float.parseFloat(normalized);
        if (value > 0 && value < 1000) {
            return value * 1000;
        }
        return value;
    }
}
