package com.netsuite.chinalocalization.cashflow.CONSTANTS;

enum CashFlowEnum {

	//INFLOW:
	CASH_RECEIVED_FROM_SALES_AND_SERVICES("Cash received from sales and services", "销售商品、提供劳务收到的现金"),
	TAXES_AND_SURCHARGES_REFUNDS("Taxes and surcharges refunds", "收到的税费返还"),
	OTHER_CASH_RECEIPTS_RELATED_TO_OPERATING_ACTIVITIES("Other cash receipts related to operating activities", "收到其他与经营活动有关的现金"),
	CASH_RECEIVED_FROM_WITHDRAW_OF_INVESTMENTS("Cash received from withdraw of investments", "收回投资收到的现金"),
	CASH_RECEIVED_FROM_INVESTMENT_INCOME("Cash received from investment income", "取得投资收益收到的现金"),
	NET_CASH_RECEIVED_FROM_DISPOSAL_OF_FIXED_ASSETS("Net cash received from disposal of fixed assets, intangible assets and other long-term assets","处置固定资产、无形资产和其他长期资产收回的现金净额"),
	NET_CASH_RECEIVED_FROM_DISPOSAL_OF_SUBSIDIARIES_AND_OTHER_BUSINESS_EXPENSES("Net cash received from disposal of subsidiaries and other business units", "处置子公司及其他营业单位收到的现金净额"),
	OTHER_CASH_RECEIPTS_RELATED_TO_INVESTING_ACTIVITIES("Other cash receipts related to investing activities", "收到其他与投资活动有关的现金"),
	CASH_RECEIVED_FROM_INVESTMENTS_BY_OTHERS("Cash received from investments by others", "吸收投资收到的现金"),
	CASH_RECEIVED_FROM_BORROWINGS("Cash received from borrowings", "取得借款收到的现金"),
	OTHER_CASH_RECEIPTS_RELATED_TO_OTHER_FINANCING_ACTIVITIES("Other cash receipts related to other financing activities", "收到其他与筹资活动有关的现金"),

	//OutFlow:
	CASH_PAID_FOR_GOODS_AND_SERVICES("Cash paid for goods and services", "购买商品、接受劳务支付的现金"),
	CASH_PAID_TO_AND_FOR_EMPLOYEES("Cash paid to and for employees", "支付给职工以及为职工支付的现金"),
	TAXES_AND_SURCHARGES_CASH_PAYMENTS("Taxes and surcharges cash payments", "支付的各项税费"),
	OTHER_CASH_PAYMENTS_RELATED_TO_OPERATING_ACTIVITIES("Other cash payments related to operating activities", "支付其他与经营活动有关的现金"),
	CASH_PAID_FOR_FIXED_ASSETS("Cash paid for fixed assets, intangible assets and other long-term assets", "购建固定资产、无形资产和其他长期资产支付的现金"),
	CASH_PAYMENTS_FOR_INVESTMENTS("Cash payments for investments", "投资支付的现金"),
	NET_CASH_PAID_FOR_ACUIRING_SUBSIDIARIES_AND_OTHER_BUSINESS_UNITS("Net cash paid for acquiring subsidiaries and other business units", "取得子公司及其他营业单位支付的现金净额"),
	OTHER_CASH_PAYMENTS_RELATED_TO_INVESTING_ACTIVITIES("Other cash payments related to investing activities", "支付其他与投资活动有关的现金"),
	CASH_REPAYMENTS_FOR_DEBTS(" Cash repayments for debts", "偿还债务支付的现金"),
	CASH_PAYMENTS_FOR_DISTRIBUTION_FOR_DIVIDENDS_PROFIT_AND_INTEREST_EXPENSES("Cash payments for distribution of dividends, profit and interest expenses", " 分配股利、利润或偿付利息支付的现金"),
	OTHER_CASH_PAYMENTS_RELATED_TO_FINANCING_ACTIVITIES("Other cash payments related to financing activities", "支付其他与筹资活动有关的现金"),

	LOCATION("Location", "地点"),
	DEPARTMENT("Department", "部门"),
	CLASS("Class", "类别"),

	NEED_DEFINE_PERIOD("Accounting Period is not defined for this fiscal calendar. Go to Setup > Accounting > Manage Accounting Periods and set up or assign the period you need.", "未对此财政日历定义期间。请依次转到“设置>会计>管理会计期间”，然后设置或指定所需期间。"),

	private String enLabel;
	private String cnLabel;

	private CashFlowEnum(String enLabel, String cnLabel) {
		this.enLabel = enLabel;
		this.cnLabel = cnLabel;
	}

	static String getCnLabel(String enLabel) {
		for (CashFlowEnum enumItem : CashFlowEnum.values()) {
			if (enumItem.enLabel.equals(enLabel))
				return enumItem.cnLabel;
		}
		return enLabel;
	}

//	static String getEnLabel() {
//		return enLabel;
//	}

	static String getEnLabel(String enLabel) {
		for (CashFlowEnum enumItem : CashFlowEnum.values()) {
			if (enumItem.enLabel==enLabel)
				return enumItem.enLabel;
		}
		println "No matched label for:"+enLabel
	}

	void setEnLabel(String enLabel) {
		this.enLabel = enLabel;
	}

	String getCnLabel() {
		return cnLabel;
	}

	void setCnLabel(String cnLabel) {
		this.cnLabel = cnLabel;
	}
}
