import java.text.NumberFormat;

public class BusAlarm {
  public int hour;
  public int minute;
  public BusAlarm next;
  private NumberFormat myFormat;

  public BusAlarm(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
    myFormat = NumberFormat.getInstance();
    myFormat.setMinimumIntegerDigits(2);
  }

  public BusAlarm setNext(BusAlarm next) {
    this.next = next;
    return this;
  }

  public String toString() {
    return myFormat.format(hour) + ":" + myFormat.format(minute);
  }

}
