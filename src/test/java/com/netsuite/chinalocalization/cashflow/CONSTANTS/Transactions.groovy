package com.netsuite.chinalocalization.cashflow.CONSTANTS;

enum Transactions {
	INVOICE("Invoice", "发票"),
	CREDITMEMO("Credit Memo", "贷项通知单"),
	CUSTOMERDEPOSIT("Customer Deposit", "客户存款");


	private String enLabel;
	private String cnLabel;

	private Transactions(String enLabel, String cnLabel) {
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
}
