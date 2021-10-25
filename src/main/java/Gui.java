import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class Gui extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, WindowListener {

    private static final String APPLICATION_NAME = "Mining controller";

    private static final int POS_X = 800;
    private static final int POS_Y = 200;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;

    private static TrayIcon trayIcon;

    public Gui() throws HeadlessException {
        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        setResizable(false);
        setTitle(APPLICATION_NAME);

        if(SystemTray.isSupported()){
            PopupMenu trayMenu = new PopupMenu();
            MenuItem item = new MenuItem("Exit");
            item.addActionListener(e -> System.exit(0));
            trayMenu.add(item);
            //Image icon = Toolkit.getDefaultToolkit().getImage("static/img/pngegg.png");
            try {
                Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("static/img/pngegg.png"));
//                icon.getScaledInstance(16, 16, 16);
                trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
                trayIcon.addMouseListener(new MouseListener() {
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
                });
                trayIcon.setImageAutoSize(true);
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        //setAlwaysOnTop(true);
//        log.setEditable(false);
//        log.setLineWrap(true);
//        JScrollPane scrollLog = new JScrollPane(log);
//
//        btnStart.addActionListener(this);
//        btnStop.addActionListener(this);
//
//        panelTop.add(btnStart);
//        panelTop.add(btnStop);
//        add(panelTop, BorderLayout.NORTH);
//        add(scrollLog, BorderLayout.CENTER);

        //setVisible(true);

        if (trayIcon != null)
            trayIcon.displayMessage(APPLICATION_NAME, "Started!", TrayIcon.MessageType.INFO);
        trayIcon.setToolTip(APPLICATION_NAME + ": " + "is started");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == trayIcon) {

        }
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
}
