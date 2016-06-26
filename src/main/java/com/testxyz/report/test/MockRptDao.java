package com.testxyz.report.test;

import java.util.ArrayList;
import java.util.List;

import com.testxyz.report.ReportDao;

public class MockRptDao implements ReportDao<MockRptEntity> {

	public List<MockRptEntity> getData() {
		List<MockRptEntity> entities = new ArrayList<MockRptEntity>();
		for (int i = 0; i < 50; i++) {
			MockRptEntity entity = new MockRptEntity();
			entity.setEntityId("entity id:" + i);
			entities.add(entity);
		}
		return entities;
	}

}
