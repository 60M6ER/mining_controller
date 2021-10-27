package gui;

import controller.MiningController;
import process.MinerListener;
import process.ProcessHandler;
import settings.Settings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.util.logging.Logger;

public class GUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, WindowListener, MouseListener, AddDelProgramListener, ChangeListener, MinerListener {
    private final static Logger LOGGER = Logger.getLogger(GUI.class.getName());
    private static final String APPLICATION_NAME = "Mining controller";

    private static final int POS_X = 800;
    private static final int POS_Y = 200;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 600;

    private Settings settings;
    private ProcessHandler processScanner;
    private MiningController miningController;

    private TrayIcon trayIcon;

    private JPanel jp_head;
    private JCheckBox cb_active;
    private JCheckBox cb_allTime;
    private JPanel jp_times;
    private JTextPane TextPane1;
    private JTextPane TextPane2;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JPanel jp_off;
    private JPanel jp_single;
    private JButton b_stopAll;
    private JButton b_startSingle;
    private JList<String> list1;
    private JList<String> list2;
    private JPanel jp_rootPanel;
    private JPanel jp_minigFields;
    private JTextField tf_wallet;
    private JTextPane textPane3;
    private JTextPane textPane4;
    private JTextField tf_indexGPU;

    public GUI(Settings settings) throws HeadlessException {
        this.settings = settings;
        miningController = new MiningController(this);
        processScanner = new ProcessHandler(settings, miningController);
        $$$setupUI$$$();
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        //setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setTitle(APPLICATION_NAME);
        this.addWindowListener(this);
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/static/img/pngegg.png"));
        this.setIconImage(icon);
        //Трэй
        if (SystemTray.isSupported()) {
            PopupMenu trayMenu = new PopupMenu();
            MenuItem item = new MenuItem("Exit");
            item.addActionListener(e -> {
                miningController.stopMining();
                System.exit(0);
            });
            trayMenu.add(item);
            try {
                trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
                trayIcon.addMouseListener(this);
                trayIcon.setImageAutoSize(true);
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }


        setContentPane(jp_rootPanel);
        this.pack();

        this.setVisible(true);

        if (trayIcon != null)
            trayIcon.displayMessage(APPLICATION_NAME, "Запущено!", TrayIcon.MessageType.INFO);
        trayIcon.setToolTip(APPLICATION_NAME + ": " + "is started");
    }

    public Settings getSettings() {
        return settings;
    }

    public ProcessHandler getProcessScanner() {
        return processScanner;
    }

    @Override
    public void miningStarted(String mes, boolean single) {
        if (trayIcon != null)
            trayIcon.displayMessage(APPLICATION_NAME, mes, TrayIcon.MessageType.INFO);
        trayIcon.setToolTip(APPLICATION_NAME + ": " + "Майнер запущен в " + (single ? "сингл" : "полном") + " режиме");
    }

    @Override
    public void miningStopped(String mes) {
        if (trayIcon != null)
            trayIcon.displayMessage(APPLICATION_NAME, mes, TrayIcon.MessageType.INFO);
        trayIcon.setToolTip(APPLICATION_NAME + ": " + "Майнер остановлен");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (trayIcon != null)
            trayIcon.displayMessage(APPLICATION_NAME, "Приложение продолжает работать.", TrayIcon.MessageType.INFO);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        msg = "Exception in " + t.getName() + " " +
                e.getClass().getCanonicalName() + ": " +
                e.getMessage() + "\n\t at " + ste[0];
        JOptionPane.showMessageDialog(this, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    @Override
    public void addProgram(String path, JList<String> list) {
        if (list == list1)
            list.setListData(processScanner.addStopProgram(path));
        else if (list == list2)
            list.setListData(processScanner.addSingleProgram(path));
    }

    @Override
    public void delProgram(JList<String> list) {
        String delProgram = list.getSelectedValue();
        if (list == list1)
            list.setListData(processScanner.delStopProgram(delProgram));
        else if (list == list2)
            list.setListData(processScanner.delSingleProgram(delProgram));
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object src = e.getSource();
        if (src == cb_active) {
            if (cb_active.isSelected())
                cb_active.setText("Майнинг включен");
            else
                cb_active.setText("Майнинг выключен");
            miningController.setActive(cb_active.isSelected());
        }
        if (src == cb_allTime) {
            miningController.setAllTime(cb_allTime.isSelected());
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        jp_rootPanel = new JPanel();
        jp_rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        jp_head = new JPanel();
        jp_head.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        jp_rootPanel.add(jp_head, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 25), null, null, 0, false));
        cb_active.setText("Майниг включен");
        jp_head.add(cb_active, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cb_allTime.setText("Круглосуточно");
        jp_head.add(cb_allTime, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jp_single = new JPanel();
        jp_single.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        jp_rootPanel.add(jp_single, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), null, null, 0, false));
        b_startSingle = new JButton();
        b_startSingle.setText("Запуск сингл майнинга");
        jp_single.add(b_startSingle, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        jp_single.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 100), new Dimension(-1, 100), null, 0, false));
        scrollPane1.setViewportView(list2);
        jp_off = new JPanel();
        jp_off.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        jp_rootPanel.add(jp_off, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), null, null, 0, false));
        b_stopAll = new JButton();
        b_stopAll.setText("Остановка");
        jp_off.add(b_stopAll, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        jp_off.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 100), new Dimension(-1, 100), null, 0, false));
        scrollPane2.setViewportView(list1);
        jp_times = new JPanel();
        jp_times.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        jp_rootPanel.add(jp_times, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(1, 20), null, null, 0, false));
        TextPane1 = new JTextPane();
        TextPane1.setText("Время включения:");
        jp_times.add(TextPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 20), new Dimension(-1, 20), 0, false));
        TextPane2 = new JTextPane();
        TextPane2.setText("Время остановки");
        jp_times.add(TextPane2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 20), new Dimension(-1, 20), 0, false));
        jp_times.add(spinner1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jp_times.add(spinner2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jp_minigFields = new JPanel();
        jp_minigFields.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        jp_rootPanel.add(jp_minigFields, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        jp_minigFields.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textPane3 = new JTextPane();
        textPane3.setBackground(new Color(-1118482));
        textPane3.setForeground(new Color(-16777216));
        textPane3.setText("Кошелек.RigName:");
        jp_minigFields.add(textPane3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 20), new Dimension(150, 20), null, 0, false));
        textPane4 = new JTextPane();
        textPane4.setBackground(new Color(-1118482));
        Font textPane4Font = this.$$$getFont$$$(null, -1, -1, textPane4.getFont());
        if (textPane4Font != null) textPane4.setFont(textPane4Font);
        textPane4.setForeground(new Color(-16777216));
        textPane4.setText("Индекс карты в сингл режиме:");
        jp_minigFields.add(textPane4, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 20), new Dimension(200, 20), null, 0, false));
        jp_minigFields.add(tf_wallet, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        jp_minigFields.add(tf_indexGPU, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jp_rootPanel;
    }

    private void createUIComponents() {
        cb_active = new JCheckBox();
        cb_active.setSelected(Boolean.parseBoolean((String) settings.get(Settings.ACTIVE)));
        if (cb_active.isSelected())
            cb_active.setText("Майнинг включен");
        else
            cb_active.setText("Майнинг выключен");
        cb_active.addChangeListener(this);

        cb_allTime = new JCheckBox();
        cb_allTime.setSelected(Boolean.parseBoolean((String) settings.get(Settings.ALL_TIME)));
        cb_allTime.addChangeListener(this);

        spinner1 = new TimeField(this, Settings.START_TIME);
        spinner2 = new TimeField(this, Settings.END_TIME);
        list1 = new JList<String>();
        list2 = new JList<String>();
        list1.setComponentPopupMenu(new ContextMenuProcesses(this, list1));
        list2.setComponentPopupMenu(new ContextMenuProcesses(this, list2));
        list1.setListData(processScanner.getStopMiningProcesses());
        list2.setListData(processScanner.getSingleGPUProcesses());

        tf_indexGPU = new JTextField(settings.getProperty(Settings.INDEX_SINGLE_GPU));
        tf_wallet = new JTextField(settings.getProperty(Settings.WALLET));

        tf_wallet.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                miningController.setWallet(tf_wallet.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                miningController.setWallet(tf_wallet.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                miningController.setWallet(tf_wallet.getText());
            }
        });
        tf_indexGPU.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!tf_indexGPU.getText().equals(""))
                    miningController.setIndexSingleGPU(Integer.parseInt(tf_indexGPU.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!tf_indexGPU.getText().equals(""))
                    miningController.setIndexSingleGPU(Integer.parseInt(tf_indexGPU.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!tf_indexGPU.getText().equals(""))
                    miningController.setIndexSingleGPU(Integer.parseInt(tf_indexGPU.getText()));
            }
        });
    }
}
