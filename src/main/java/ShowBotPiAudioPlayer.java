import java.io.File;

import audioPlayer.AudioPlayer;
import logger.Log;
import networkTableInterface.NetworkTableInterface;

public class ShowBotPiAudioPlayer {
  private final int m_sleepTimeMS = 20;
  private NetworkTableInterface m_networkTableInterface = new NetworkTableInterface();
  private final String m_showBotPiAudioBaseDir = "/home/pi/showBotAudio";
  private final String m_piAudioFilesDirPath = m_showBotPiAudioBaseDir + "/audioFiles";

  public static void main(String[] args) throws Exception {
    System.out.println("Hello, World!");
    new ShowBotPiAudioPlayer().run();
  }

  public void run() {
    boolean connected = false;
    int count = 0;
    while (!connected) {
      if ((count % 50) == 0) {
        // only log this once a second so we don't flood the log file
        Log.getInstance().write("INFO: waiting for roborio to ack connection");
      }
      ++count;
      if (m_networkTableInterface.didRoborioAckPiConnected()) {
        Log.getInstance().write("INFO: roborio has ack'ed connection");
        break;
      }
      try {
        Thread.sleep(m_sleepTimeMS);
      } catch (InterruptedException e) {
        Log.getInstance().writeException(e);
        Log.getInstance().write("EXITING....");
        return;
      }
    }
    String lastAudioFileRequested = "";
    while (true) {
      try {
        String audioFileToPlay = m_networkTableInterface.getRequestedAudioFileToPlay();
        if (!lastAudioFileRequested.equals(audioFileToPlay)) {
          Log.getInstance().write("INFO: roborio has requested the audio file: " + audioFileToPlay);
          String audioFileFullPath = m_piAudioFilesDirPath + "/" + audioFileToPlay;
          File audioFile = new File(audioFileFullPath);
          if (!audioFile.exists()) {
            String err = "ERROR: audio file " + audioFileToPlay + " does not exist";
            Log.getInstance().write(err);
            m_networkTableInterface.setErrorStatus(err);
          } else {
            if (!AudioPlayer.getInstance().playAudioFile(audioFile)) {
              m_networkTableInterface
                  .setErrorStatus("ERROR: unable to play audio file: " + audioFileFullPath);
            } else {
              m_networkTableInterface.clearErrorStatus();
              m_networkTableInterface.setCurrentAudioPlaying(audioFileToPlay);
            }
          }
          lastAudioFileRequested = audioFileToPlay;
          Log.getInstance().write("Last: " + lastAudioFileRequested + "@");
          Log.getInstance().write("Requ: " + audioFileToPlay + "@");
        }
        m_networkTableInterface.setAudioIsPlaying(AudioPlayer.getInstance().isPlaying());
        try {
          Thread.sleep(m_sleepTimeMS);
        } catch (InterruptedException ex) {
          Log.getInstance().writeException(ex);
          Log.getInstance().write("EXITING....");
          return;
        }
      } catch (Throwable throwable) {
        Log.getInstance().writeException(throwable);
      }
    }
  }
}