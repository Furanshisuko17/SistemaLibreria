package com.utp.trabajo.services.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.springframework.stereotype.Service;

@Service
public class OptionPaneService {

    public static final String EXCEPTION_TITLE = "Error...";
    public static final String INFORMATION_TITLE = "Información";
    public static final String WARNING_TITLE = "Atención!";
    public static final String QUESTION_TITLE = "¿Está seguro?";

    private static final Object[] SINGLE_OPTION = {"Aceptar"};
    private static final Object[] EXCEPTION_OPTIONS = {"Aceptar", "Mostrar stacktrace"};
    private static final Object[] QUESTION_OPTIONS = {"Sí", "No"};

    public static void exceptionMessage(String message) {
        exceptionMessage(message, EXCEPTION_TITLE);
    }

    public static void exceptionMessage(String message, String title) {
        exceptionMessage(null, title, message);
    }

    public static void exceptionMessage(Exception e) {
        exceptionMessage(e, EXCEPTION_TITLE);
    }

    public static void exceptionMessage(Exception e, String title) {
        exceptionMessage(e, title, e.getMessage());
    }

    //final exception handler
    public static void exceptionMessage(Exception e, String title, String message) {
        if (e == null) {
            mainHandler(message,
                title,
                JOptionPane.OK_OPTION,
                JOptionPane.ERROR_MESSAGE,
                SINGLE_OPTION,
                SINGLE_OPTION[0]);
        } else {
            int value = mainHandler(message,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                EXCEPTION_OPTIONS,
                EXCEPTION_OPTIONS[0]);

            if (value == 1) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println("Stacktrace: ");
                e.printStackTrace(pw);
                pw.flush();

                JTextArea errorText = new JTextArea(sw.toString(), 15, 70);
                JScrollPane sp = new JScrollPane(errorText);

                mainHandler(sp,
                    title,
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    SINGLE_OPTION,
                    SINGLE_OPTION[0]);
            }
        }

    }

    public static void informationMessage(String message) {
        informationMessage(message, "Información!");
    }

    public static void informationMessage(String message, String title) {
        mainHandler(message,
            title,
            JOptionPane.OK_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            SINGLE_OPTION,
            SINGLE_OPTION[0]);

    }

    public static void warningMessage(String message) {
        warningMessage(message, WARNING_TITLE);
    }

    public static void warningMessage(String message, String title) {
        mainHandler(message,
            title,
            JOptionPane.OK_OPTION,
            JOptionPane.WARNING_MESSAGE,
            SINGLE_OPTION,
            SINGLE_OPTION[0]);

    }

    public static int questionMessage(String message) {
        return questionMessage(message, QUESTION_TITLE);
    }

    public static int questionMessage(String message, String title) {
        return mainHandler(message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            QUESTION_OPTIONS,
            QUESTION_OPTIONS[0]);
    }

    private static int mainHandler(Object message, String title, int optionType,
        int messageType, Object[] options, Object selectedOption) {
        return JOptionPane.showOptionDialog(
            null, //parent container
            message,
            title,
            optionType, //Option type
            messageType, //MessageType
            null, //Icon
            options, //Buttons label
            selectedOption); //Preselected button
    }

}
