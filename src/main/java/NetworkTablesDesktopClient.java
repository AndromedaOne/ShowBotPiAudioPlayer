
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NetworkTablesDesktopClient {
  private final int m_teamNumber = 4905;

  public static void main(String[] args) throws Exception {
    System.out.println("Hello, World!");
    new NetworkTablesDesktopClient().run();
  }

  public void run() {
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("datatable");
    NetworkTableEntry xEntry = table.getEntry("x");
    NetworkTableEntry yEntry = table.getEntry("y");
    inst.startClientTeam(m_teamNumber);
    // recommended if running on DS computer; this gets the robot IP from the DS
    inst.startDSClient();
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        System.out.println("interrupted");
        return;
      }
      double x = xEntry.getDouble(0.0);
      double y = yEntry.getDouble(0.0);
      System.out.println("X: " + x + " Y: " + y);
    }
  }
}