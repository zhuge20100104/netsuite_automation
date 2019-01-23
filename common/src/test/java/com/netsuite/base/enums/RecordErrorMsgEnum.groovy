package com.netsuite.base.enums;

enum RecordErrorMsgEnum {
	RCRD_DSNT_EXIST("That record does not exist.", "该记录不存在。");

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
