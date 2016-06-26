package org.gradle;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import com.testxyz.report.MultiReportController;
import com.testxyz.report.ReportEvent;
import com.testxyz.report.ReportException;
import com.testxyz.report.ReportResult;
import com.testxyz.report.test.MockRptDao;
import com.testxyz.report.test.MockRptEntity;

@Service
public class TestBizServiceImpl implements TestBizService {
	private MockRptDao rptDao;

	private static String strHtml;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

	private String convert2Html(List<ReportResult> result) {
		StringBuilder sb = new StringBuilder();
		if (result != null && !result.isEmpty()) {
			sb.append("<table border='1'><tr><td>Hash Code</td><td>Result Code</td><td>Start Time</td><td>End Time</td></tr>");
			for (ReportResult res : result) {
				sb.append("<tr>");
				sb.append(String.format("<td>%d</td>", res.hashCode()));
				sb.append(String.format("<td>%s</td>", res.getResultCode() == ReportResult.SUCC ? "SUCC" : res.getResultCode() + ""));
				sb.append(String.format("<td>%s</td>", sdf.format(res.getStartTime())));
				sb.append(String.format("<td>%s</td>", sdf.format(res.getEndTime())));
				sb.append("</tr>");
			}
			sb.append("</table>");
		} else {
			sb.append("<table border='1'><tr><td>Hash Code</td><td>Result Code</td><td>Start Time</td><td>End Time</td></tr>");
			sb.append("<tr>");
			sb.append(String.format("<td colspan='4'>%s</td>", "NO RESULT."));
			sb.append("</tr>");
			sb.append("</table>");
		}
		return sb.toString();
	}

	public String runMultiThread() {
		strHtml = "";
		MultiReportController<MockRptEntity> mrc = null;

		try {
			mrc = new MultiReportController<MockRptEntity>("RPT_XX_01");
			rptDao = new MockRptDao();
			mrc.setRptDao(rptDao);
			mrc.attachInitializeEvent(new ReportEvent() {
				public void onEvent(Object sender, Object arg) {
					System.out.println("Controller init. hashCode:" + sender.hashCode());
				}
			});

			mrc.attachFinalizeEvent(new ReportEvent() {
				public void onEvent(Object sender, Object arg) {
					List<ReportResult> result = (List<ReportResult>) arg;
					if (result != null && !result.isEmpty()) {
						System.out.println("result getted. count:" + result.size());
						for (ReportResult res : result) {
							System.out.println(String.format("hashcode:[%d], result code:[%d]", res.hashCode(), res.getResultCode()));
						}
					}
					strHtml = convert2Html(result);
					System.out.println("html:" + strHtml);
				}
			});

			mrc.executeAndWait();
//			mrc.execute();
		} catch (ReportException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return strHtml;
//		return "";
	}

}
