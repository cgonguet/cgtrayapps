import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.*;

public class BusAlarmFileDialog extends JPanel implements ActionListener {

  private static final String NEWLINE = "\n";

  JButton openButton, saveButton, cancelButton;
  JTextArea log;
  JFileChooser fc;
  private JFrame parent;
  private Runnable loader;

  private BusAlarmFileDialog(JFrame parent, Runnable loader, File alarmFile) {
    super(new BorderLayout());
    this.parent = parent;
    this.loader = loader;

    log = new JTextArea(25, 4);
    log.setMargin(new Insets(5, 5, 5, 5));
    log.setEditable(true);
    JScrollPane logScrollPane = new JScrollPane(log);

    logAlarmFile(alarmFile);

    fc = new JFileChooser(alarmFile);

    openButton = new JButton("Open");
    openButton.addActionListener(this);

    saveButton = new JButton("Save & Load");
    saveButton.addActionListener(this);

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);

    JPanel buttonPanel = new JPanel(); //use FlowLayout
    buttonPanel.add(openButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.PAGE_START);
    add(logScrollPane, BorderLayout.CENTER);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == openButton) {
      int returnVal = fc.showOpenDialog(BusAlarmFileDialog.this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        logAlarmFile(file);
        BusAlarmFileManager.getInstance().setAlarmFile(file);
      }
      log.setCaretPosition(log.getDocument().getLength());

    } else if (e.getSource() == saveButton) {
      int returnVal = fc.showSaveDialog(BusAlarmFileDialog.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        saveAlarmFile(file);
        BusAlarmFileManager.getInstance().setAlarmFile(file);
        loadAlarmFile(file);
        parent.setVisible(false);
      }
    } else if (e.getSource() == cancelButton) {
      parent.setVisible(false);
    }
  }

  private void loadAlarmFile(File file) {
    System.out.println("LOAD: " + file);
    try {
      BusAlarmManager.getInstance().readAlarms(new FileReader(file));
      loader.run();
    } catch (Exception ex) {
      System.err.println("Cannot load file: " + file + "\n" + ex);
    }
  }

  private void logAlarmFile(File file) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuffer text = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        text.append(line).append(NEWLINE);
      }
      log.setText(text.toString());
    } catch (Exception ex) {
      System.out.println("Cannot read file: " + file + "\n" + ex);
    }
  }

  private void saveAlarmFile(File file) {
    try {
      FileWriter writer = new FileWriter(file);
      writer.write(log.getDocument().getText(0, log.getDocument().getLength()));
      writer.flush();
      writer.close();
    } catch (Exception ex) {
      System.out.println("Cannot save file: " + file + "\n" + ex);
    }
  }

  public static void showDialog(Runnable loader, File file) {
    JFrame frame = new JFrame("Bus alarms file chooser");
    frame.setLocation(900,450);
    frame.add(new BusAlarmFileDialog(frame, loader, file));

    frame.pack();
    frame.setVisible(true);
  }


}
