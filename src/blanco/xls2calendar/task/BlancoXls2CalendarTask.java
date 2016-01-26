package blanco.xls2calendar.task;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.TransformerException;

import blanco.commons.calc.parser.BlancoCalcParser;
import blanco.xls2calendar.BlancoXls2CalendarEventStructure;
import blanco.xls2calendar.BlancoXls2CalendarStructure;
import blanco.xls2calendar.BlancoXls2CalendarXmlParser;
import blanco.xls2calendar.util.BlancoXls2CalendarUtil;
import net.fortuna.ical4j.model.component.VEvent;

public class BlancoXls2CalendarTask {
	public void process(final InputStream inStreamMetaSource, final OutputStream outStreamTarget) {
		try {
			// このクラス自身とおなじクラスローダからXML設定ファイルのロードをおこないます。
			final InputStream meta2xmlStream = getClass().getClassLoader()
					.getResourceAsStream("blanco/xls2calendar/BlancoXls2CalendarMeta2Xml.xml");
			if (meta2xmlStream == null) {
				throw new IllegalArgumentException("リソース[...Meta2Xml.xml]の取得に失敗しました.");
			}
			final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			final byte[] bufWrk = new byte[8192];
			for (;;) {
				final int readLength = meta2xmlStream.read(bufWrk);
				if (readLength <= 0) {
					break;
				}
				outStream.write(bufWrk, 0, readLength);
			}
			outStream.flush();
			meta2xmlStream.close();

			final InputStream inStreamDef = new ByteArrayInputStream(outStream.toByteArray());
			new BlancoCalcParser().process(inStreamDef, inStreamMetaSource, outStreamTarget);
		} catch (IOException | TransformerException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(final String[] args) throws IOException {
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		new BlancoXls2CalendarTask().process(new FileInputStream("meta/template/BlancoXls2CalendarTemplate.xls"),
				outStream);

		System.out.println(new String(outStream.toByteArray()));

		final BlancoXls2CalendarStructure[] result = new BlancoXls2CalendarXmlParser().parse(outStream.toByteArray());
		for (BlancoXls2CalendarStructure calendar : result) {
			for (BlancoXls2CalendarEventStructure eventStructure : calendar.getEventList()) {
				final String uid = "uid" + calendar.getUid() + "-" + eventStructure.getNo() + "-"
						+ calendar.getEmailaddr();
				final VEvent event = BlancoXls2CalendarUtil.createEvent(calendar.getEmailaddr(),
						eventStructure.getTitle(), eventStructure.getDescription(), eventStructure.getLocation(),
						eventStructure.getDateBegin(), eventStructure.getDateEnd(), uid);

				final String calendarAsString = BlancoXls2CalendarUtil.vevent2CalString(event);
				try {
					final File outFile = new File("./tmp/" + uid + ".ics");
					if (outFile.getParentFile().exists() == false) {
						outFile.getParentFile().mkdirs();
					}
					final BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
					writer.write(calendarAsString);
					writer.flush();
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
