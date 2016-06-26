package com.testxyz.report;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class MultiReportController<E> {
	private Constructor<?> rptGenConst;
	private Thread monitor;
	private boolean isMonitoring = true;

	private ReportDao<E> rptDao;
	private Vector<ReportEvent> initializeEvents;
	private Vector<ReportEvent> finalizeEvents;
	private Vector<ReportEvent> faultEvents;
	private Vector<ReportEvent> generatorInitializeEvents;
	private Vector<ReportEvent> generatorFinializeEvents;
	private Vector<ReportEvent> generatorFaultEvents;

	private Vector<ReportResult> result;

	private Hashtable<Integer, ReportThread> threadPool;

	private int threadPoolMaxSize = 10;
	private int threadGeneratedCount = 0;
	private int threadCount = 0;

	private static final Hashtable<String, String> REPORT_MAP;
	static {
		REPORT_MAP = new Hashtable<String, String>();
		REPORT_MAP.put("RPT_XX_01", "com.testxyz.report.test.MockRptGen");
	}

	private static final Object[] CONST_ARGS = new Object[] {};

	private MultiReportController() {
		initializeEvents = new Vector<ReportEvent>();
		finalizeEvents = new Vector<ReportEvent>();
		faultEvents = new Vector<ReportEvent>();
		generatorInitializeEvents = new Vector<ReportEvent>();
		generatorFinializeEvents = new Vector<ReportEvent>();
		generatorFaultEvents = new Vector<ReportEvent>();
		result = new Vector<ReportResult>();
		threadPool = new Hashtable<Integer, MultiReportController<E>.ReportThread>();
		monitor = new Thread(new Runnable() {
			public void run() {
				while (isMonitoring) {
					System.out.println(threadPool.size());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public MultiReportController(String reportId) throws ReportException {
		this();
		try {
			rptGenConst = ((Class<?>) Class.forName(REPORT_MAP.get(reportId))).getConstructor();
		} catch (NoSuchMethodException e) {
			throw new ReportException(e);
		} catch (SecurityException e) {
			throw new ReportException(e);
		} catch (ClassNotFoundException e) {
			throw new ReportException(e);
		}
	}

	public void setRptDao(ReportDao<E> rptDao) {
		this.rptDao = rptDao;
	}

	public void attachInitializeEvent(ReportEvent e) {
		this.initializeEvents.add(e);
	}

	public void attachFinalizeEvent(ReportEvent e) {
		this.finalizeEvents.add(e);
	}

	public void attachFaultEvent(ReportEvent e) {
		this.faultEvents.add(e);
	}

	public void attachGeneratorInitializeEvent(ReportEvent e) {
		this.generatorInitializeEvents.add(e);
	}

	public void attachGeneratorFinalizeEvent(ReportEvent e) {
		this.generatorFinializeEvents.add(e);
	}

	public void attachGeneratorFaultEvent(ReportEvent e) {
		this.generatorFaultEvents.add(e);
	}

	public void execute() {
		execute0();
	}

	public List<ReportResult> executeAndWait() throws InterruptedException {
		execute0();

		while (isMonitoring) {
			System.out.println(threadPool.size());
			if (isAllThreadCreated() && threadPool.isEmpty()) {
				onFinal();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.result;
	}

	private void execute0() {
		try {
			List<E> rptData = rptDao.getData();
			threadCount = rptData.size();
			monitor.start();
			onInit(rptData);
			for (E data : rptData) {
				while (threadPool.size() >= this.threadPoolMaxSize) {
					Thread.sleep(500);
				}
				@SuppressWarnings("unchecked")
				ReportGenerator<E> rptGen = (ReportGenerator<E>) this.rptGenConst.newInstance(CONST_ARGS);

				onRptGenInit(rptGen, data);
				rptGen.setData(data);
				ReportThread rptThd = new ReportThread(rptGen);
				this.threadPool.put(rptGen.hashCode(), new ReportThread(rptGen));
				threadGeneratedCount++;

				rptThd.start();
			}
		} catch (Exception e) {
			onFault(e);
		}
	}

	private void onInit(List<E> data) {
		doEvents(initializeEvents, this, data);
	}

	private void onFinal() {
		System.out.println("onFinal");
		doEvents(finalizeEvents, this, this.result.clone());
		this.isMonitoring = false;
	}

	private void onFault(Exception e) {
		doEvents(faultEvents, this, e);
	}

	private void onRptGenInit(ReportGenerator<E> rptGen, E data) {
		doEvents(generatorInitializeEvents, rptGen, data);
	}

	private void onRptGenFinal(ReportGenerator<E> rptGen, ReportResult rptRes) {
		try {
			doEvents(generatorFinializeEvents, rptGen, rptRes);
		} finally {
			result.add(rptRes);
			this.threadPool.remove(rptGen.hashCode());
		}
	}

	private boolean isAllThreadCreated() {
		return threadGeneratedCount == threadCount;
	}

	private void onRptGenFault(ReportGenerator<E> rptGen, ReportException e) {
		doEvents(generatorFaultEvents, rptGen, e);
	}

	private void doEvents(Vector<ReportEvent> events, Object sender, Object arg) {
		if (events != null && !events.isEmpty()) {
			for (ReportEvent e : events) {
				e.onEvent(sender, arg);
			}
		}
	}

	private class ReportThread extends Thread {
		public ReportThread(final ReportGenerator<E> rptGen) {
			super(new Runnable() {
				public void run() {
					ReportResult rptRes = new ReportResult();
					try {
						rptRes.setResultCode(rptGen.generate());
					} catch (ReportException ex) {
						// TODO to specific the Exception type.
						// TO create a ReportException type is a good idea. 
						rptRes.setResultCode(ReportResult.FAULT);
						MultiReportController.this.onRptGenFault(rptGen, ex);
					} finally {
						rptRes.setEndTime(new Date());
						MultiReportController.this.onRptGenFinal(rptGen, rptRes);
					}
				}
			});
		}
	}
}
