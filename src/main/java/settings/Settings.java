package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings extends Properties {

    public static final String ACTIVE = "active";
    public static final String ALL_TIME = "all_time";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String STOP_PROGRAMS = "stop_programs";
    public static final String SINGLE_PROGRAMS = "single_programs";
    public static final String WALLET = "wallet";
    public static final String INDEX_SINGLE_GPU = "index_single_gpu";

    private final static Logger LOGGER = Logger.getLogger(Settings.class.getName());
    private final static String PATH_PROPERTIES = "./mining_controller.properties";
    private static File propFile = new File(PATH_PROPERTIES);

    public Settings() {
        super();
        try {
            if (!propFile.exists()) {
                createProperties();
            } else
                this.load(new FileInputStream(propFile));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void createProperties() throws IOException {
        if (propFile.createNewFile()) {
            LOGGER.log(Level.WARNING, "Создан новый файл настроек");

            this.setProperty(ACTIVE, String.valueOf(true));
            this.setProperty(ALL_TIME, String.valueOf(false));
            this.setProperty(START_TIME, "23:00");
            this.setProperty(END_TIME, "7:00");
            this.setProperty(STOP_PROGRAMS, "");
            this.setProperty(SINGLE_PROGRAMS, "");
            this.setProperty(WALLET, "");
            this.setProperty(INDEX_SINGLE_GPU, "1");


            saveSettings();
        }
    }

    public void saveSettings() throws IOException {
        this.store(new FileOutputStream(propFile), "init");
    }
}
