package scheduler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
  private static Scheduler theInstance;

  class SchedulerTimerTask extends TimerTask {
    private SchedulerTask schedulerTask;
    private ScheduleIterator iterator;

    public SchedulerTimerTask(SchedulerTask schedulerTask,
                              ScheduleIterator iterator) {
      this.schedulerTask = schedulerTask;
      this.iterator = iterator;
    }

    public void run() {
      schedulerTask.run();
      reschedule(schedulerTask, iterator);
    }
  }

  private final Timer timer;

  private Scheduler() {
    timer = new Timer();
  }

  public static Scheduler getInstance() {
    if (theInstance == null) {
      theInstance = new Scheduler();
    }
    return theInstance;
  }

  public void stop() {
    timer.cancel();
    theInstance = null;
  }

  public void schedule(SchedulerTask schedulerTask,
                       ScheduleIterator iterator) {

    Date time = iterator.next();
    if (time == null) {
      schedulerTask.cancel();
    } else {
      synchronized (schedulerTask.lock) {
        if (schedulerTask.state != SchedulerTask.VIRGIN) {
          throw new IllegalStateException("Task already scheduled or cancelled");
        }
        schedulerTask.state = SchedulerTask.SCHEDULED;
        schedulerTask.timerTask =
                new SchedulerTimerTask(schedulerTask, iterator);
        timer.schedule(schedulerTask.timerTask, time);
      }
    }
  }

  private void reschedule(SchedulerTask schedulerTask,
                          ScheduleIterator iterator) {

    Date time = iterator.next();
    if (time == null) {
      schedulerTask.cancel();
    } else {
      synchronized (schedulerTask.lock) {
        if (schedulerTask.state != SchedulerTask.CANCELLED) {
          schedulerTask.timerTask =
                  new SchedulerTimerTask(schedulerTask, iterator);
          timer.schedule(schedulerTask.timerTask, time);
        }
      }
    }
  }

}
