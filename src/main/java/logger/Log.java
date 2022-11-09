// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

// simple logger implemented as a singleton. this will open log file in <TEMP>/ShowBotAudioLogs
// the file have a count as its extension which is stored in the directory as .logCount
public class Log {

  private static String s_logDirName = "ShowBotAudioLogs";
  private static String s_logFileName = "log";
  private static String s_logCountFileName = "logCount";
  private static Log s_instance = null;
  BufferedWriter m_logWriter = null;
  FlushThread m_flushThread = new FlushThread();

  private Log() {
    createNewLogFile();
    m_flushThread.start();
  }

  public static Log getInstance() {
    if (s_instance == null) {
      s_instance = new Log();
    }
    return (s_instance);
  }

  void createNewLogFile() {
    String logDirPath = System.getProperty("java.io.tmpdir") + "/" + s_logDirName;
    try {
      File directory = new File(logDirPath);
      if (!directory.exists()) {
        if (!directory.mkdir()) {
          System.err
              .println("ERROR: failed to create directory " + logDirPath + " for logging data.");
          return;
        }
      }
      // retrieve the log count if a logCount file exists
      int logCount = 0;
      String logCountFileName = logDirPath + "/" + s_logCountFileName;
      File logCountFile = new File(logCountFileName);
      if (!logCountFile.exists()) {
        System.out.println("INFO: no logCount file, starting at log.0");
        logCount = 0;
      } else {
        BufferedReader reader = new BufferedReader(new FileReader(logCountFileName));
        String line = reader.readLine();
        reader.close();
        logCountFile.delete();
        if (line == null) {
          System.err.println("ERROR: failed to read log file number file: " + logCountFileName
              + " restarting at 0");
          logCount = 0;
        }
        logCount = Integer.parseInt(line);
      }
      // save the log count for the next log in the logCount file
      FileWriter logCountStream = new FileWriter(logCountFileName, false);
      BufferedWriter logCountBuf = new BufferedWriter(logCountStream);
      logCountBuf.write(Integer.toString(logCount + 1));
      logCountBuf.close();
      // open the log file with the count as an extension
      String logFileFullName = logDirPath + "/" + s_logFileName + "." + Integer.toString(logCount);
      FileWriter writer = new FileWriter(logFileFullName, false);
      m_logWriter = new BufferedWriter(writer);
      System.out.println("INFO: openned new log file: " + logFileFullName);
    } catch (IOException e) {
      System.err.println("ERROR: unable to open log file");
      e.printStackTrace();
      m_logWriter = null;
    }
  }

  // this will write to both the stdout and the log file
  public void write(String message) {
    System.out.println("message");
    if (m_logWriter != null) {
      try {
        m_logWriter.write(message + "\n");
      } catch (IOException e) {
        System.err.println("ERROR: unable to write to log file");
        e.printStackTrace();
        m_logWriter = null;
      }
    }
  }

  // this will write to both stderr and the log file
  public void writeException(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    write(sw.toString());
    e.printStackTrace();
  }

  public void flush() {
    if (m_logWriter != null) {
      try {
        m_logWriter.flush();
      } catch (IOException e) {
        System.err.println("ERROR: unable to flush log file");
        e.printStackTrace();
        m_logWriter = null;
      }
    }
  }

  public void close() {
    if (m_logWriter != null) {
      try {
        m_logWriter.close();
      } catch (IOException e) {
        System.err.println("ERROR: unable to close log file");
        e.printStackTrace();
      }
      m_logWriter = null;
    }
  }

  private class FlushThread extends Thread {
    public void run() {
      while (true) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        Log.getInstance().flush();
      }
    }
  }

}
