package blanco.xls2calendar;

import java.util.Calendar;

public class BlancoXls2CalendarEventStructure {
	private String no;
	private String title;
	private String description;
	private String location;
	private Calendar dateBegin;
	private Calendar dateEnd;

	public Calendar getDateBegin() {
		return dateBegin;
	}

	public void setDateBegin(Calendar dateBegin) {
		this.dateBegin = dateBegin;
	}

	public Calendar getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Calendar dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
