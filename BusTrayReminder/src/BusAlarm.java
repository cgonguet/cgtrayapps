import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusAlarm {
  public int hour;
  public int minute;
    public String additionalText;
  public BusAlarm next;
  private NumberFormat myFormat;

  private BusAlarm(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
    myFormat = NumberFormat.getInstance();
    myFormat.setMinimumIntegerDigits(2);
  }

  public BusAlarm setNext(BusAlarm next) {
    this.next = next;
    return this;
  }

    public void setAdditionalText(String additionalText)
    {
        this.additionalText = additionalText;
    }

    static Pattern pattern = Pattern.compile("^([0-9][0-9]):([0-9][0-9])[ \t]*(.*)");


    public static BusAlarm decode(String line)
    {
        Matcher matcher = pattern.matcher(line);
        if(matcher.find())
        {
            BusAlarm busAlarm = new BusAlarm(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
            if(matcher.groupCount()==3)                   // TODO check index !!!
            {
                busAlarm.setAdditionalText(matcher.group(3));
            }

            return busAlarm;
        }

//        String[] data = line.split(":");
//        return new BusAlarm(Integer.valueOf(data[0]), Integer.valueOf(data[1]));

        return null;

    }

    public String toString() {
    return myFormat.format(hour) + ":" + myFormat.format(minute) + " " + additionalText;
  }

}
