package com.testxyz.report;

public interface ReportGenerator<E> {
	int generate() throws ReportException;
	void setData(E reportEntity);
}
