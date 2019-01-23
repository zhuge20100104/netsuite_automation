package com.netsuite.chinalocalization.voucher


enum VoucherMsgEnum {
	INVALID_DATE_RANGE("Select a start date that is earlier than the end date.", "起始日期须早于截至日期。"),
	INVALID_PERIOD_RANGE("Select a start period that is earlier than the end period.", "起始期间须早于截至期间。"),
	SINGLE_CURRENCY("Single Currency", "单币"),
	GREAT_THAN_50_RECORDS("This report may take some time to generate. You will be alerted once the report is generated and available for viewing.", "此报表生成可能要花一段时间。报表生成并且可以查看时即会通知您。"),
	MULTIPLE_CURRENCY("Multiple Currencies", "复币"),
	NO_ACCOUNTING_PERIOD("Accounting Period is not defined for this fiscal calendar. Go to “Setup > Accounting > Manage Accounting Periods” and set up or assign the period you need.", "未对此财政日历定义日期。请依次转到“设置>会计>管理会计期间”，然后设置或指定所需期间。"),
	NO_SEARCH_RESULT("No Search Results Match Your Criteria.","没有与搜索条件匹配的结果。"),
	QUARTER("Quarter","季度"),
	CHECK("Check","支票"),
	JOURNAL("Journal","日记账"),
	IS("is","为"),
	GL_IMPACT_ADJ("GL Impact Adjustment","GL Impact Adjustment"),
	BILL("Bill","账单"),
	INVOICE("Invoice","发票"),
	CUSTOMER_DEPOSIT("customerdeposit","客户存款"),
	OVERLAP_DATE_RANGE("Overlapping date range. To edit a Line with the same Type, Transaction Type, and User, you need to select an End Date on your current Line, then manage another user.","日期范围重叠。对于同一用户类型、交易类型和用户组合的行，需要在当前行输入截至日期，再编辑其他用户。")

	private VoucherMsgEnum(String enLabel, String cnLabel) {
		this.enLabel = enLabel;
		this.cnLabel = cnLabel;
	}

	String getEnLabel() {
		return enLabel;
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


	static String getCnLabel(String enLabel) {
		for (VoucherMsgEnum enumItem : VoucherMsgEnum.values()) {
			if (enumItem.enLabel.equals(enLabel))
				return enumItem.cnLabel;
		}
		return enLabel;
	}

	static String getEnLabel(String enLabel) {
		for (VoucherMsgEnum enumItem : VoucherMsgEnum.values()) {
			if (enumItem.enLabel==enLabel)
				return enumItem.enLabel;
		}
		println "No matched label for:"+enLabel
	}


	private String enLabel;
	private String cnLabel;
}