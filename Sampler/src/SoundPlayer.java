import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer implements Runnable {
  private static SoundPlayer theInstance;

  private final int EXTERNAL_BUFFER_SIZE = 500;

  private File soundFile;
  private Boolean shouldStop;
  private Thread soundThread;

  private SoundPlayer() {
  }

  public static SoundPlayer getInstance() {
    if (theInstance == null) {
      theInstance = new SoundPlayer();
    }
    return theInstance;
  }

  public void play(File soundFile) {
    this.soundFile = soundFile;
    stop();
    if (soundThread != null) {
      try {
        soundThread.join();
      } catch (InterruptedException e) {
        System.out.println("Caught exception " + e);
      }
    }
    shouldStop = false;
    soundThread = new Thread(this);
    soundThread.start();
  }

  public void stop() {
    shouldStop = true;
  }

  public void run() {
    AudioInputStream audioInputStream = null;
    try {
      audioInputStream = AudioSystem.getAudioInputStream(soundFile);
    } catch (UnsupportedAudioFileException e1) {
      e1.printStackTrace();
      return;
    } catch (IOException e1) {
      e1.printStackTrace();
      return;
    }

    AudioFormat format = audioInputStream.getFormat();
    SourceDataLine auline = null;
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

    try {
      auline = (SourceDataLine) AudioSystem.getLine(info);
      auline.open(format);
    } catch (LineUnavailableException e) {
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    auline.start();
    int nBytesRead = 0;
    byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

    try {
      while (nBytesRead != -1) {
        if (shouldStop) {
          return;
        }

        nBytesRead = audioInputStream.read(abData, 0, abData.length);
        if (nBytesRead >= 0) {
          auline.write(abData, 0, nBytesRead);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    } finally {
      auline.drain();
      auline.close();
      try {
        audioInputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
