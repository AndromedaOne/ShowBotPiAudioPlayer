// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package networkTableInterface;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import logger.Log;

// interface to between network tables and audio player.
public class NetworkTableInterface {
  private final String m_smartDashboardName = "SmartDashboard";
  private final int m_teamNumber = 4905;

  // smartdashboard keys
  private final String m_audioFileToPlayKey = "showBotAudioFileToPlay";
  private final String m_currentAudioFilePlayingKey = "showBotAudioFileCurrentlyPlaying";
  private final String m_audioIsPlayingKey = "showBotAudioIsPlaying";
  private final String m_showBotPiAudioPlayerConnectedKey = "showBotPiAudioConnected";
  private final String m_errorStatusKey = "showBotPiAudioErrorStatus";
  private final String m_stopAudioKey = "showBotStopPiAudio";

  private final String m_showBotPiIsConnected = "ShowBotPiIsConnected";
  private final String m_roborioAckPiConnected = "RoborioAckPiConnected";
  private final String m_noError = "";

  private NetworkTableInstance m_ntInst = NetworkTableInstance.getDefault();
  private NetworkTable m_smartDashboardTable = m_ntInst.getTable(m_smartDashboardName);
  private NetworkTableEntry m_showBotPiConnected = m_smartDashboardTable
      .getEntry(m_showBotPiAudioPlayerConnectedKey);
  private NetworkTableEntry m_audioFileToPlay = m_smartDashboardTable
      .getEntry(m_audioFileToPlayKey);
  private NetworkTableEntry m_currentAudioPlaying = m_smartDashboardTable
      .getEntry(m_currentAudioFilePlayingKey);
  private NetworkTableEntry m_audioIsPlaying = m_smartDashboardTable.getEntry(m_audioIsPlayingKey);
  private NetworkTableEntry m_errorStatus = m_smartDashboardTable.getEntry(m_errorStatusKey);
  private NetworkTableEntry m_stopAudio = m_smartDashboardTable.getEntry(m_stopAudioKey);

  public NetworkTableInterface() {
    Log.getInstance().write("INFO: Connecting to SmartDashboard");
    m_ntInst.startClientTeam(m_teamNumber);
    while (!m_ntInst.isConnected()) {
      try {
        Log.getInstance().write("INFO: waiting to for connection to roborio");
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    Log.getInstance().write("INFO: connected to SmartDashboard");
    m_showBotPiConnected.setString(m_showBotPiIsConnected);
    m_audioIsPlaying.setBoolean(false);
    m_currentAudioPlaying.setString("");
    m_errorStatus.setString(m_noError);
    m_audioFileToPlay.setString("");
    m_stopAudio.setBoolean(false);
  }

  public boolean didRoborioAckPiConnected() {
    String ack = m_showBotPiConnected.getString(m_showBotPiIsConnected);
    if (ack.equals(m_roborioAckPiConnected)) {
      return true;
    }
    return false;
  }

  public String getRequestedAudioFileToPlay() {
    return m_audioFileToPlay.getString("");
  }

  public void clearRequestedAudioFileToPlay() {
    m_audioFileToPlay.setString("");
  }

  public void setErrorStatus(String err) {
    m_errorStatus.setString(err);
  }

  public void clearErrorStatus() {
    m_errorStatus.setString(m_noError);
  }

  public void setCurrentAudioPlaying(String audio) {
    m_currentAudioPlaying.setString(audio);
  }

  public void setAudioIsPlaying(boolean isPlaying) {
    m_audioIsPlaying.setBoolean(isPlaying);
  }

  public boolean getStopAudio() {
    return m_stopAudio.getBoolean(false);
  }

  public void clearStopAudio() {
    m_stopAudio.setBoolean(false);
  }
}
