import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

public class Sampler {
  private static final String SOUNDS_DIR = "sounds";

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

    final TrayIcon trayIcon = new TrayIcon(createImage("icons/sound.png", "Sampler tray icon"));
    trayIcon.setImageAutoSize(true);

    trayIcon.setToolTip("Sampler");

    final SystemTray tray = SystemTray.getSystemTray();

    final MenuItem openItem = new MenuItem("Open...");
    final MenuItem loadItem = new MenuItem("Load");
    final MenuItem stopItem = new MenuItem("Stop");
    final MenuItem exitItem = new MenuItem("Exit");

    final PopupMenu popup = createPopup(openItem, loadItem, stopItem, exitItem);

    trayIcon.setPopupMenu(popup);

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      System.out.println("TrayIcon could not be added.");
      return;
    }

    openItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Runtime.getRuntime().exec(new String[]{"explorer.exe", SOUNDS_DIR});
        } catch (IOException ex) {
          System.out.println("Cannot open explorer: " + ex);
        }
      }
    });

    loadItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        trayIcon.setPopupMenu(createPopup(openItem, loadItem, stopItem, exitItem));
      }
    });

    stopItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SoundPlayer.getInstance().stop();
      }
    });

    exitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tray.remove(trayIcon);
        System.exit(0);
      }
    });

    trayIcon.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        final JFrame frame = new JFrame("Sampler");
        frame.setLocation(1000,700);

        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        loadSounds(new SoundVisitor() {

          public void visit(File sound, String name, ActionListener player) {
            JButton button = new JButton(name);
            button.addActionListener(player);
            panel.add(button, BorderLayout.CENTER);
          }
        });

        panel.add(Box.createVerticalStrut(10));
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SoundPlayer.getInstance().stop();
          }
        });
        panel.add(stopButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
      }
    });
  }

  private static PopupMenu createPopup(MenuItem openItem, MenuItem loadItem, MenuItem stopItem, MenuItem exitItem) {
    final PopupMenu popup = new PopupMenu();
    popup.add("SAMPLER");
    popup.addSeparator();
    popup.add(openItem);
    popup.add(loadItem);
    popup.addSeparator();

    loadSounds(new SoundVisitor() {

      public void visit(File sound, String name, ActionListener player) {
        MenuItem soundItem = new MenuItem(name);
        popup.add(soundItem);
        soundItem.addActionListener(player);
      }
    });

    popup.addSeparator();
    popup.add(stopItem);
    popup.addSeparator();
    popup.add(exitItem);
    return popup;
  }

  private static void loadSounds(SoundVisitor visitor) {
    File[] sounds = new File(SOUNDS_DIR).listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name) {
        return name.endsWith(".wav");
      }
    });
    for (final File sound : sounds) {
      String name = sound.getName().substring(0, sound.getName().indexOf("."));
      visitor.visit(sound, name, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SoundPlayer.getInstance().play(sound);
        }
      });
    }

  }


  interface SoundVisitor {
    void visit(File sound, String name, ActionListener player);
  }

  private static Image createImage(String path, String description) {
    URL imageURL = Sampler.class.getResource(path);

    if (imageURL == null) {
      System.err.println("Resource not found: " + path);
      return null;
    } else {
      return (new ImageIcon(imageURL, description)).getImage();
    }
  }
}
