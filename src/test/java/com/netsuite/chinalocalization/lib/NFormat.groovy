package com.netsuite.chinalocalization.lib

import com.google.inject.Inject

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

class NFormat {

    @Inject
    NetSuiteAppCN context

    enum MinusFormat {
        SYMBOL(0), PARENTHESES(1)

        MinusFormat(int value) {
            this.value = value
        }
        private final int value

        int getValue() {
            value
        }
    }

    /**
     * @Description
     * Format the number accounting to the netsuite's number format
     * Example:
     * COMMA_POINT: 1,000,000.00
     * POINT_COMMA: 1.000.000,00
     * SPACE_POINT: 1 000 000.00
     * SPACE_COMMA: 1 000 000,00
     * CHINESE_COMMA_POINT: 1٬000٬000.00
     * CHINESE_COMMA_COMMA: 1٬000٬000,00
     * @author Bryan Chen
     *
     */
    enum NetSuiteNumberFormat {
        COMMA_POINT(0), POINT_COMMA(1), SPACE_POINT(2), SPACE_COMMA(3), CHINESE_COMMA_POINT(4), CHINESE_COMMA_COMMA(5)

        NetSuiteNumberFormat(int value) {
            this.value = value
        }
        private final int value

        int getValue() {
            value
        }
    }

    String formatDate(String date) {
        return context.executeScript("""
            var date = new Date('${date}');
            return nlapiDateToString(date, 'date');
        """)
    }

    String formatDateTime(String dateTime) {
        return context.executeScript("""
            var dateTime = new Date('${dateTime}');
            return nlapiDateToString(dateTime, 'datetime');
        """)
    }

    public static double parseMoney(String value) throws Exception {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return nf.parse(value).doubleValue();
    }

    public static String removeCurrencySymbol(String number) {
        return number.replace("\$", "").replace("¥", "");
    }

    public String formatCurrency(double number, int pattern, int minusFormat) {
        // The SS 1.0 API doesn't work well on currency format due to issue #299943(id=21096527).
        // We have to implement the same functionality by ourselves.
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        String positivePattern = "###,##0.00";
        String negativePattern = "###,##0.00";
        switch (minusFormat) {
            case MinusFormat.SYMBOL.getValue():
                negativePattern = "###,##0.00";
                break;
            case MinusFormat.PARENTHESES.getValue():
                negativePattern = "(###,##0.00)";
                break;
        }

        String numberPattern = positivePattern + ";" + negativePattern;

        switch (pattern) {
            case NetSuiteNumberFormat.COMMA_POINT.value:
                symbols.setGroupingSeparator(',' as char);
                symbols.setDecimalSeparator('.' as char);
                break;
            case NetSuiteNumberFormat.POINT_COMMA.value:
                symbols.setGroupingSeparator('.' as char);
                symbols.setDecimalSeparator(',' as char);
                break;
            case NetSuiteNumberFormat.SPACE_POINT.value:
                symbols.setGroupingSeparator(' ' as char);
                symbols.setDecimalSeparator('.' as char);
                break;
            case NetSuiteNumberFormat.SPACE_COMMA.value:
                symbols.setGroupingSeparator(' ' as char);
                symbols.setDecimalSeparator(',' as char);
                break;
            case NetSuiteNumberFormat.CHINESE_COMMA_POINT.value:
                symbols.setGroupingSeparator('٬' as char);
                symbols.setDecimalSeparator('.' as char);
                break;
            case NetSuiteNumberFormat.CHINESE_COMMA_COMMA.value:
                symbols.setGroupingSeparator('٬' as char);
                symbols.setDecimalSeparator(',' as char);
                break;
        }
        DecimalFormat df = new DecimalFormat(numberPattern, symbols);
        return df.format(number);
    }

    String formatString(String string) {
        return string?.trim()?.replaceAll("\\u00A0", "") // &nbsp;
    }
    String formatCharacter(String string) {
        return string.replaceAll("（", "(").replaceAll("）", ")").replaceAll("：", ":")
    }
}
