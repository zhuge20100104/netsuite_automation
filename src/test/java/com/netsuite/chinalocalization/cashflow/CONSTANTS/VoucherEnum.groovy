package com.netsuite.chinalocalization.cashflow.CONSTANTS

import java.awt.Choice

enum VoucherEnum {
    VOUCHER_MULT_CURRENCIES_TEMPLATE("Multiple Currencies", "复币"),
    VOUCHER_SINGLE_CURRENCY_TEMPLATE("Single Currency","单币"),
    VOUCHER_CREDIT_LABEL("Credit","借方"),
    VOUCHER_DEBIT_LABEL("Debit","贷方"),
    VOUCHER_CFS_NEED_ENTER("Please enter value(s) for: China Cash Flow Item", "请输入值：中国现金流量表项目")

    private String enLabel;
    private String cnLabel;

    private VoucherEnum(String enLabel, String cnLabel) {
        this.enLabel = enLabel;
        this.cnLabel = cnLabel;
    }

    String getEnLabel() {
        return enLabel
    }

    void setEnLabel(String enLabel) {
        this.enLabel = enLabel
    }

    String getCnLabel() {
        return cnLabel
    }

    void setCnLabel(String cnLabel) {
        this.cnLabel = cnLabel
    }


}
