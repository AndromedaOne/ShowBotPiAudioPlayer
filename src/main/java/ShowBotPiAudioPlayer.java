
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class ShowBotPiAudioPlayer {
  private final int m_teamNumber = 4905;

  public static void main(String[] args) throws Exception {
    System.out.println("Hello, World!");
    new ShowBotPiAudioPlayer().run();
  }

  public void run() {
    double piNumb = 0;
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("SmartDashboard");
    NetworkTableEntry xEntry = table.getEntry("myCounter");
    NetworkTableEntry piNumbEntry = table.getEntry("piNumb");
    inst.startClientTeam(m_teamNumber);
    // recommended if running on DS computer; this gets the robot IP from the DS
    inst.startDSClient();
    while (true) {
      try {
        Thread.sleep(250);
      } catch (InterruptedException ex) {
        System.out.println("interrupted");
        return;
      }
      Number myCounter = xEntry.getNumber(0.0);
      System.out.println("myCounter = " + myCounter);
      piNumbEntry.setDouble(piNumb++);
      System.out.println("piNumb = " + piNumb);
    }
  }
}