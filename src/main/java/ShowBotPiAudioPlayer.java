import audioPlayer.AudioPlayer;
import logger.Log;
import networkTableInterface.NetworkTableInterface;

public class ShowBotPiAudioPlayer {
  private final int m_sleepTimeMS = 20;
  private NetworkTableInterface m_networkTableInterface = 
    new NetworkTableInterface();


  public static void main(String[] args) throws Exception {
    System.out.println("Hello, World!");
    new ShowBotPiAudioPlayer().run();
  }

  public void run() {
    boolean connected = false;
    int count = 0;
    while(!connected) {
      if((count % 50) == 0) {
        Log.getInstance().write("INFO: testing if roborio has ack'ed");
      }
      if(m_networkTableInterface.didRoborioAckPiConnected()) {
        Log.getInstance().write("INFO: roborio has ack'ed connection");
      }
      count++;
    }
    while (true) {
      String lastAudioFileRequested = "";
      String audioFileToPlay = m_networkTableInterface.getRequestedAudioFileToPlay();
      if(!lastAudioFileRequested.equals(audioFileToPlay)) {
        Log.getInstance().write("INFO: roborio has requested the audio file: " + audioFileToPlay);
        if(!AudioPlayer.getInstance().playAudioFile(audioFileToPlay)) {
          m_networkTableInterface.setErrorStatus("ERROR: unable to play audio file: " + audioFileToPlay);
        } else {
          m_networkTableInterface.clearErrorStatus();
        }
        lastAudioFileRequested = audioFileToPlay;
        m_networkTableInterface.setCurrentAudioPlaying(audioFileToPlay);
      }
      m_networkTableInterface.setAudioIsPlaying(AudioPlayer.getInstance().isPlaying());
      try {
        Thread.sleep(m_sleepTimeMS);
      } catch (InterruptedException ex) {
        System.out.println("interrupted");
        return;
      }
      
    }
  }
}