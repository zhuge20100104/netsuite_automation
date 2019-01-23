package com.netsuite.chinalocalization.cashflow.CONSTANTS;

enum RecordErrorMsgEnum {
	RCRD_DSNT_EXIST("That record does not exist.", "该记录不存在。"),
	RCRD_COMPLETE("Complete", "完成"),
	RCRD_PERCENT_COMPLETE("100.0%", "100.0%"),
	RCRD_0ERROE_MSG("0 Errors", "0错误"),
	RCRD_1ERROE_MSG("1 Error", "1个错误"),
	RCRD_FAIL_MSG("Failure", "失败"),
	RCRD_SUCCESS_MSG("Success", "成功");;

	private RecordErrorMsgEnum(String enLabel, String cnLabel) {
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

	private String enLabel;
	private String cnLabel;

}
