import javax.swing.*;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class BusTrayReminder {

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (InstantiationException ex) {
      ex.printStackTrace();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    UIManager.put("swing.boldMetal", Boolean.FALSE);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static void createAndShowGUI() {
    if (!SystemTray.isSupported()) {
      System.out.println("SystemTray is not supported");
      return;
    }

    final PopupMenu popup = new PopupMenu();
    final TrayIcon trayIcon = new TrayIcon(createImage("icons/bus_icon.gif", "Bus tray icon"));
    trayIcon.setImageAutoSize(true);

    trayIcon.setToolTip("Bus Reminder");

    final SystemTray tray = SystemTray.getSystemTray();

    MenuItem aboutItem = new MenuItem("Bus Reminder");
    MenuItem showAlarmsItem = new MenuItem("Show alarms");
    MenuItem loadAlarmsItem = new MenuItem("Load alarms...");
    MenuItem delayItem = new MenuItem("Set delay...");
    MenuItem exitItem = new MenuItem("Exit");

    popup.add(aboutItem);
    popup.addSeparator();
    popup.add(showAlarmsItem);
    popup.add(loadAlarmsItem);
    popup.addSeparator();
    popup.add(delayItem);
    popup.addSeparator();
    popup.add(exitItem);

    trayIcon.setPopupMenu(popup);

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      System.out.println("TrayIcon could not be added.");
      return;
    }

    aboutItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(new JFrame(),"Bus Reminder\nChristophe Gonguet\n2010");
      }
    });

    ActionListener showAlarmsAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        BusAlarmManager.getInstance().displayAlarms(new TrayMessage(trayIcon));
      }
    };

    trayIcon.addActionListener(showAlarmsAction);
    showAlarmsItem.addActionListener(showAlarmsAction);

    loadAlarmsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          BusAlarmFileDialog.showDialog(new Runnable(){

            public void run() {
              BusAlarmManager.getInstance().createAlarms(new TrayMessage(trayIcon));
            }
          }, BusAlarmFileManager.getInstance().getAlarmFile());
        }
      });

    exitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tray.remove(trayIcon);
        System.exit(0);
      }
    });

    delayItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        String delay = JOptionPane.showInputDialog("Enter delay in minutes:", BusAlarmManager.getInstance().getDelay());
        if (delay == null) {
          return;
        }
        BusAlarmManager.getInstance().setDelay(Integer.valueOf(delay));
        BusAlarmManager.getInstance().createAlarms(new TrayMessage(trayIcon));
      }
    });

    BusAlarmFileManager.getInstance().setAlarmFile(new File("data/bus.dat"));
    try {
      BusAlarmManager.getInstance().readAlarms(new FileReader(BusAlarmFileManager.getInstance().getAlarmFile()));
    } catch (Exception e) {
      System.err.println("Cannot read data: " + e);
      e.printStackTrace();
    }

    BusAlarmManager.getInstance().createAlarms(new TrayMessage(trayIcon));
  }

  private static Image createImage(String path, String description) {
    URL imageURL = BusTrayReminder.class.getResource(path);

    if (imageURL == null) {
      System.err.println("Resource not found: " + path);
      return null;
    } else {
      return (new ImageIcon(imageURL, description)).getImage();
    }
  }

  static class TrayMessage implements BusAlarmManager.Printer {
    private TrayIcon trayIcon;


    public TrayMessage(TrayIcon trayIcon) {
      this.trayIcon = trayIcon;
    }

    public void print(String title, String desc) {
      trayIcon.displayMessage(title, desc, TrayIcon.MessageType.NONE);
    }
  }
}