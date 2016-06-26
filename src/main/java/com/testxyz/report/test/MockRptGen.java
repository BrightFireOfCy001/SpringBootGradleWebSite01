package com.testxyz.report.test;

import com.testxyz.report.ReportException;
import com.testxyz.report.ReportGenerator;
import com.testxyz.report.ReportResult;

public class MockRptGen implements ReportGenerator<MockRptEntity> {
	private MockRptEntity data;

	public int generate() throws ReportException {
		System.out.println("MockRptGen start");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("MockRptGen end");
		return ReportResult.SUCC;
	}

	public void setData(MockRptEntity reportEntity) {
		data = reportEntity;
	}

}
