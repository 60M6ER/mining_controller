import gui.GUI;
import settings.Settings;

import javax.swing.*;
import java.util.logging.Logger;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Settings settings = new Settings();


        //Start GUI
        SwingUtilities.invokeLater(() -> new GUI(settings));
    }

}
