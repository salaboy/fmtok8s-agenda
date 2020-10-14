package com.salaboy.conferences.agenda.util;

import com.salaboy.conferences.agenda.model.AgendaItem;

public class AgendaItemCreator {

    public static final String DAY = "2020-10-05";
    public static final String OTHER_DAY = "2020-10-02";

    public static AgendaItem validWithDefaultDay() {
        return new AgendaItem("Title", "Author", DAY, "13:00");
    }

    public static AgendaItem otherValidWithDefaultDay() {
        return new AgendaItem("Other title", "Other Author", DAY, "13:00");
    }

    public static AgendaItem withFail() {
        return new AgendaItem("Title fail", "Author fail",  OTHER_DAY, "12:30");
    }
}
