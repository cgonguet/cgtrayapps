import scheduler.DailyIterator;
import scheduler.Scheduler;
import scheduler.SchedulerTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class BusAlarmManager {
  private static BusAlarmManager theInstance;
  private List<BusAlarm> busAlarms = new ArrayList<BusAlarm>();
  private Properties properties;
    private int delay;
    private File dataFile;

    private BusAlarmManager()
  {
      properties = new Properties();
      try
      {
          properties.load(new FileReader("data/default.properties"));
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }
      delay = Integer.valueOf(properties.getProperty("delay", "10"));
      dataFile = new File(properties.getProperty("data", "data/bus.dat"));
  }

  public static BusAlarmManager getInstance()
  {
    if (theInstance == null) {
      theInstance = new BusAlarmManager();
    }
    return theInstance;
  }

  public void setAlarms(List<BusAlarm> busAlarms) {
    this.busAlarms = new ArrayList<BusAlarm>(busAlarms);
  }

  public void readAlarms(Reader reader) throws Exception {
    busAlarms.clear();
    BufferedReader br = new BufferedReader(reader);
    String line;
    while ((line = br.readLine()) != null) {
      busAlarms.add(BusAlarm.decode(line));
    }
  }

  public void displayAlarms(Printer printer) {
    String text = "";

    Date currentDate = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    long currentTime = calendar.getTimeInMillis();

    BusAlarm nextAlarm = null;
    for (final BusAlarm busAlarm : busAlarms) {
      calendar.setTime(currentDate);
      calendar.set(Calendar.HOUR_OF_DAY, busAlarm.hour);
      calendar.set(Calendar.MINUTE, busAlarm.minute);
      if (nextAlarm == null && calendar.getTimeInMillis() - currentTime > 0) {
        nextAlarm = busAlarm;
      }
      text += busAlarm + "\n";
    }
    printer.print("Next " + nextAlarm, text);
  }

  public File getDataFile() {
      return dataFile;
  }

  public void createAlarms(final Printer printer) {
    Scheduler.getInstance().stop();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());

    int i = 0;
    for (final BusAlarm busAlarm : busAlarms) {
      if (i < busAlarms.size() - 1) {
        busAlarm.setNext(busAlarms.get(i + 1));
      } else {
        busAlarm.setNext(busAlarms.get(0));
      }
      i++;

      calendar.set(Calendar.HOUR_OF_DAY, busAlarm.hour);
      calendar.set(Calendar.MINUTE, busAlarm.minute);
      calendar.setTimeInMillis(calendar.getTimeInMillis() - delay * 60000);

      Scheduler.getInstance().schedule(new SchedulerTask() {
        public void run() {
          printer.print("Bus " + busAlarm, "Next " + busAlarm.next);
        }
      }, new DailyIterator(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0));
    }
  }

  public interface Printer {
    void print(String title, String desc);
  }


}
