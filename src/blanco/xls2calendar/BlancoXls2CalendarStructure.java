package blanco.xls2calendar;

import java.util.ArrayList;
import java.util.List;

public class BlancoXls2CalendarStructure {
    private String emailaddr;
    private String uid;
    private List<BlancoXls2CalendarEventStructure> eventList = new ArrayList<BlancoXls2CalendarEventStructure>();

    public String getEmailaddr() {
        return emailaddr;
    }

    public void setEmailaddr(String emailaddr) {
        this.emailaddr = emailaddr;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<BlancoXls2CalendarEventStructure> getEventList() {
        return eventList;
    }
}
