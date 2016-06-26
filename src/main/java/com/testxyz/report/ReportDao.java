package com.testxyz.report;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface ReportDao<E> {
	List<E> getData();
}
