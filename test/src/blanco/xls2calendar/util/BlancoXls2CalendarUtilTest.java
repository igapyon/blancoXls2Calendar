package blanco.xls2calendar.util;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;

public class BlancoXls2CalendarUtilTest {

    @Test
    public void testCreateEvent() {
        fail("Not yet implemented");
    }

    @Test
    public void testVevent2CalString() {
        fail("Not yet implemented");
    }

    @Test
    public void testNewDate() {
        fail("Not yet implemented");
    }

    @Test
    public void testMain() throws IOException, ValidationException {
        final BlancoXls2CalendarUtil icallib = new BlancoXls2CalendarUtil();

        final VEvent event = icallib.createEvent("tosiki.iga@nifty.ne.jp", "弦楽四重奏の練習#1", "アメリカ第一楽章を練習", "いつもの練習場所",
                BlancoXls2CalendarUtil.newDate(2015, 12, 31, 17, 30),
                BlancoXls2CalendarUtil.newDate(2015, 12, 31, 19, 30));
        final String calendarAsString = BlancoXls2CalendarUtil.vevent2CalString(event);
        System.out.println(calendarAsString);
    }
}
