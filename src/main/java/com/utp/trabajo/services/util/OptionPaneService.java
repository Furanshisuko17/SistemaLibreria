package com.utp.trabajo.services.util;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// No necesita ser marcado con @Service
public class OptionPaneService {

    public static final String EXCEPTION_TITLE = "Error...";
    public static final String INFORMATION_TITLE = "Información";
    public static final String WARNING_TITLE = "Atención!";
    public static final String QUESTION_TITLE = "¿Está seguro?";

    private static final Object[] SINGLE_OPTION = {"Aceptar"};
    private static final Object[] EXCEPTION_OPTIONS = {"Aceptar", "Mostrar stacktrace"};
    private static final Object[] QUESTION_OPTIONS = {"Sí", "No"};

    public static void errorMessage(Component component, String message) {
        errorMessage(component, message, EXCEPTION_TITLE);
    }

    public static void errorMessage(Component component, String message, String title) {
        exceptionMessage(component, null, title, message);
    }

    public static void exceptionMessage(Component component, Exception e) {
        exceptionMessage(component, e, EXCEPTION_TITLE);
    }

    public static void exceptionMessage(Component component, Exception e, String title) {
        exceptionMessage(component, e, title, e.getMessage());
    }

    //final exception handler
    public static void exceptionMessage(Component component, Exception e, String title, String message) {
        if (e == null) {
            mainHandler(component, 
                message,
                title,
                JOptionPane.OK_OPTION,
                JOptionPane.ERROR_MESSAGE,
                SINGLE_OPTION,
                SINGLE_OPTION[0]);
        } else {
            int value = mainHandler(component, 
                message,
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

                mainHandler(component, 
                    sp,
                    title,
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    SINGLE_OPTION,
                    SINGLE_OPTION[0]);
            }
        }

    }

    public static void informationMessage(Component component, String message) {
        informationMessage(component, message, "Información!");
    }

    public static void informationMessage(Component component, String message, String title) {
        mainHandler(component, 
            message,
            title,
            JOptionPane.OK_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            SINGLE_OPTION,
            SINGLE_OPTION[0]);

    }

    public static void warningMessage(Component component, String message) {
        warningMessage(component, message, WARNING_TITLE);
    }

    public static void warningMessage(Component component, String message, String title) {
        mainHandler(component, message,
            title,
            JOptionPane.OK_OPTION,
            JOptionPane.WARNING_MESSAGE,
            SINGLE_OPTION,
            SINGLE_OPTION[0]);

    }

    public static int questionMessage(Component component, String message) {
        return questionMessage(component, message, QUESTION_TITLE);
    }

    public static int questionMessage(Component component, String message, String title) {
        return mainHandler(component, 
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            QUESTION_OPTIONS,
            QUESTION_OPTIONS[0]);
    }

    private static int mainHandler(Component component, Object message, String title, int optionType,
        int messageType, Object[] options, Object selectedOption) {
        return JOptionPane.showOptionDialog(
            component, //parent container
            message,
            title,
            optionType, //Option type
            messageType, //MessageType
            null, //Icon
            options, //Buttons label
            selectedOption); //Preselected button
    }

}
