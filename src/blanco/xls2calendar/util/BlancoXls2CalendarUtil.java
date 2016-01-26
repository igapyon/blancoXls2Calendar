package blanco.xls2calendar.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

public class BlancoXls2CalendarUtil {
	/**
	 * TODO replace as your product id.
	 */
	public static final String PROD_ID = "-//Toshiki Iga//Simple iCal Library 1.0//EN";

	/**
	 * TODO change timezone if you needed.
	 */
	public static final String TIMEZONE = "Asia/Tokyo";

	/**
	 * Create Event.
	 * 
	 * @param emailaddr
	 *            like "igapyon@igapyon.com"
	 * @param summary
	 * @param description
	 * @param location
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public static VEvent createEvent(final String emailaddr, final String summary, final String description,
			final String location, final Calendar start, final Calendar end, final String uid) {
		final VEvent event = new VEvent();
		event.getProperties().add(new Uid(uid));

		final TimeZoneRegistry tzReg = TimeZoneRegistryFactory.getInstance().createRegistry();
		net.fortuna.ical4j.model.TimeZone icalTimeZone = tzReg.getTimeZone(TIMEZONE);
		final VTimeZone vtz = icalTimeZone.getVTimeZone();
		event.getProperties().add(vtz.getTimeZoneId());
		event.getProperties().add(new Summary(summary));
		event.getProperties().add(new Description(description));
		event.getProperties().add(new Location(location));
		event.getProperties().add(new LastModified(new DateTime(System.currentTimeMillis())));

		try {
			event.getProperties().add(new Organizer("MAILTO:" + emailaddr));
		} catch (URISyntaxException ex) {
			System.out.println(ex.toString());
		}

		event.getProperties().add(new DtStart(new DateTime(start.getTime().getTime())));
		event.getProperties().add(new DtEnd(new DateTime(end.getTime().getTime())));

		return event;
	}

	/**
	 * VEvent to iCal String.
	 * 
	 * @param event
	 * @return
	 * @throws IOException
	 */
	public static String vevent2CalString(final VEvent event) throws IOException {
		net.fortuna.ical4j.model.Calendar icalCalendar = new net.fortuna.ical4j.model.Calendar();
		icalCalendar.getProperties().add(new ProdId(PROD_ID));
		icalCalendar.getProperties().add(CalScale.GREGORIAN);
		icalCalendar.getProperties().add(Version.VERSION_2_0);

		// Treat as single event calendar.
		icalCalendar.getComponents().add(event);

		// ical to string.
		final ByteArrayOutputStream bytearrayOutputStream = new ByteArrayOutputStream();
		try {
			new CalendarOutputter().output(icalCalendar, bytearrayOutputStream);
		} catch (ValidationException ex) {
			throw new IOException(ex);
		}

		return bytearrayOutputStream.toString("UTF-8");
	}

	/**
	 * new Date
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static Calendar newDate(final int year, final int month, final int date, final int hour, final int minute) {
		final java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.clear();
		cal.set(java.util.Calendar.YEAR, year);
		cal.set(java.util.Calendar.MONTH, month - 1);
		cal.set(java.util.Calendar.DAY_OF_MONTH, date);
		cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
		cal.set(java.util.Calendar.MINUTE, minute);
		return cal;
	}

	public static Calendar fromDate(final java.util.Date fromDate) {
		final java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.clear();
		cal.setTime(fromDate);
		return cal;
	}
}