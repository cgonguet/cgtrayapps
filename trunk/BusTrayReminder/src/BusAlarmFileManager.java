import java.io.File;

public class BusAlarmFileManager {
  private static BusAlarmFileManager theInstance;

  private File alarmFile;

  private BusAlarmFileManager() {
  }

  public static BusAlarmFileManager getInstance() {
    if (theInstance == null) {
      theInstance = new BusAlarmFileManager();
    }
    return theInstance;
  }

 public File getAlarmFile() {
    return alarmFile;
  }

  public void setAlarmFile(File alarmFile) {
    this.alarmFile = alarmFile;
  }
}
