package com.utp.trabajo.services.util;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;

public class DateTableCellRenderer extends DefaultTableCellRenderer {
    
    final TableCellRenderer defaultTableCellRenderer= new JXTable().getDefaultRenderer(Object.class);

    DateFormat formatter;

    private boolean withTime;

    public DateTableCellRenderer(boolean withTime) {
        super();
        this.withTime = withTime;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (formatter == null) {
            if (withTime) {
                formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.forLanguageTag("es-PE"));
            } else {
                formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.forLanguageTag("es-PE"));
            }
        }
        value = ((value == null) ? "" : formatter.format(value));
        Component c = defaultTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        return c;
    }

}
