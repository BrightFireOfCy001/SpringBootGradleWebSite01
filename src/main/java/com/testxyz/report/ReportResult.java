package com.testxyz.report;

import java.util.Date;

public class ReportResult {
	private Date startTime;
	private Date endTime;
	private int resultCode;
	private ReportException cause;
	
	public static final int INIT = 0;
	public static final int SUCC = 1;
	public static final int FAULT = -1;
	
	public ReportResult() {
		this.startTime = new Date();
		resultCode = 0;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getMsg() {
		String ttt = "";
		if (this.cause != null) {
			ttt = this.cause.getMessage();
		}
		return ttt;
	}

	public ReportException getCause() {
		return cause;
	}

	public void setCause(ReportException cause) {
		this.cause = cause;
	}
}
