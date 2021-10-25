package gui;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeField extends JSpinner {

    private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm");

    private GUI gui;

    public TimeField(GUI gui, String key) {
        super(new SpinnerDateModel());
        this.gui = gui;
        this.setEditor(new JSpinner.DateEditor(this, "HH:mm"));
        try {
            this.setValue(df.parse((String) gui.getSettings().get(key)));
        } catch (ParseException e) {
            gui.uncaughtException(Thread.currentThread(), e);
        }
    }
}
