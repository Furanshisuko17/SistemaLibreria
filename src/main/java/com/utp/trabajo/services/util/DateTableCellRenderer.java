package com.utp.trabajo.services.util;

import java.text.DateFormat;
import java.util.Locale;
import javax.swing.table.DefaultTableCellRenderer;

public class DateTableCellRenderer extends DefaultTableCellRenderer {

    DateFormat formatter;

    private boolean withTime;

    public DateTableCellRenderer(boolean withTime) {
        super();
        this.withTime = withTime;
    }

    @Override
    public void setValue(Object value) {
        if (formatter == null) {
            if (withTime) {
                formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.forLanguageTag("es-PE"));
            } else {
                formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.forLanguageTag("es-PE"));
            }
        }
        setText((value == null) ? "" : formatter.format(value));
    }

}
