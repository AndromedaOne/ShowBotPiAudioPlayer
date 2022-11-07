// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package audioPlayer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import logger.Log;

// Audio player interface. implemented as a singlton to ensure only one exists
public class AudioPlayer {
  private static AudioPlayer m_instance = new AudioPlayer();
  private Clip m_audioClip = null;
  private String m_audioFileBeingPlayed;

  private AudioPlayer() {

  }

  public static AudioPlayer getInstance() {
    if (m_instance == null) {
      m_instance = new AudioPlayer();
    }
    assert (m_instance != null);
    return (m_instance);
  }

  // this method will immediately play the audio file passed in. if the player is
  // already
  // playing an audio file, that audio will be stopped and the new one started.
  // returns true on success, false on error
  public boolean playAudioFile(String audioFilePath) {
    if (m_audioClip != null) {
      m_audioClip.stop();
      m_audioClip.close();
      m_audioClip = null;
    }
    AudioInputStream audioInputStream;
    try {
      try {
        audioInputStream = AudioSystem
            .getAudioInputStream(new File(audioFilePath).getAbsoluteFile());
      } catch (UnsupportedAudioFileException e) {
        Log.getInstance().write("ERROR: unsupported audio file: " + audioFilePath);
        Log.getInstance().writeException(e);
        m_audioClip = null;
        m_audioFileBeingPlayed = "";
        return false;
      }
      try {
        m_audioClip = AudioSystem.getClip();
        m_audioClip.open(audioInputStream);
      } catch (LineUnavailableException e) {
        Log.getInstance().write("ERROR: Audio System threw line unavailable, not playing clip");
        Log.getInstance().writeException(e);
        m_audioClip = null;
        m_audioFileBeingPlayed = "";
        return false;
      }
    } catch (IOException e) {
      Log.getInstance().write("ERROR: Audio System threw IOException, not playing clip");
      Log.getInstance().writeException(e);
      m_audioClip = null;
      m_audioFileBeingPlayed = "";
      return false;
    }
    m_audioClip.start();
    m_audioFileBeingPlayed = audioFilePath;
    return true;
  }

  public void stop() {
    if (m_audioClip != null) {
      m_audioClip.stop();
    }
    m_audioFileBeingPlayed = "";
  }

  public boolean isPlaying() {
    if (m_audioClip != null) {
      return (m_audioClip.isRunning());
    }
    return (false);
  }

  public String fileBeingPlayed() {
    return (m_audioFileBeingPlayed);
  }
}
