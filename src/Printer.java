import lejos.nxt.Button;
import lejos.nxt.LCD;

public class Printer extends Thread {
	private UltrasonicController controller;
	
	public Printer(UltrasonicController controller) {
		this.controller = controller;
	}
	
	public void run() {
		while (true) {
			printInfo();
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public static void printMainMenu() {
		LCD.clear();
		LCD.drawString("left = bangbang",  0, 0);
		LCD.drawString("right = p type", 0, 1);
	}
	
	private void printInfo() {
		LCD.clear();
		LCD.drawString("Controller Type is... ", 0, 0);
		LCD.drawString(controller.getType(), 0, 1);
		LCD.drawString("US Distance: " + controller.readSensorDistance(), 0, 2);
	}
}
