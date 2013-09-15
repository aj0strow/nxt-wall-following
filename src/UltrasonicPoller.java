import lejos.nxt.UltrasonicSensor;

public class UltrasonicPoller extends Thread {
	private UltrasonicSensor sensor;
	private UltrasonicController controller;
	
	public UltrasonicPoller(UltrasonicSensor sensor, UltrasonicController controller) {
		this.sensor = sensor;
		this.controller = controller;
	}
	
	public void run() {
		while (true) {
			controller.processSensorData(sensor.getDistance());
			try { Thread.sleep(10); } catch(Exception e) {}
		}
	}

}
