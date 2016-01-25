package blanco.xls2calendar.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            final BlancoXls2CalendarUtil icallib = new BlancoXls2CalendarUtil();

            for (BlancoXls2CalendarEventStructure eventStructure : calendar.getEventList()) {
                final VEvent event = icallib.createEvent(calendar.getEmailaddr(), eventStructure.getTitle(),
                        eventStructure.getDescription(), eventStructure.getLocation(),
                        BlancoXls2CalendarUtil.newDate(eventStructure.getDateBegin().getYear(),
                                eventStructure.getDateBegin().getMonth() + 1, eventStructure.getDateBegin().getDay(),
                                eventStructure.getDateBegin().getHours(), eventStructure.getDateBegin().getMinutes()),
                        BlancoXls2CalendarUtil.newDate(eventStructure.getDateEnd().getYear(),
                                eventStructure.getDateEnd().getMonth() + 1, eventStructure.getDateEnd().getDay(),
                                eventStructure.getDateEnd().getHours(), eventStructure.getDateEnd().getMinutes()));

                final String calendarAsString = BlancoXls2CalendarUtil.vevent2CalString(event);
                System.out.println(calendarAsString);
            }
        }
    }
}
