import lejos.nxt.*;

public class Lab1 {
	private static final SensorPort SENSOR_PORT = SensorPort.S1;	
	private static final int BAND_CENTER = 20, BAND_WIDTH = 3;
	
	public static void main(String [] args) {
		Printer.printMainMenu();
		
		/*
		 * Button press determines controller:
		 *   Left Button -> BangBang controller
		 *   Right Button -> P Controller
		*/
		int option = 0;
		while (option == 0) {
			option = Button.waitForAnyPress();
		}
		
		UltrasonicController controller = null;
		if (option == Button.ID_LEFT) {
			controller = new BangBangController(BAND_CENTER, BAND_WIDTH);
		} else if (option == Button.ID_RIGHT) {
			controller = new PController(BAND_CENTER, BAND_WIDTH);
		} else {
			
			// a button wasn't chosen
			System.out.println("Error - invalid button");
			System.exit(-1);
		}
		
		// with the controller chosen, set up printer, sensor, poller
		UltrasonicSensor sensor = new UltrasonicSensor(SENSOR_PORT);
		UltrasonicPoller poller = new UltrasonicPoller(sensor, controller);
		Printer printer = new Printer(controller);
		
		// the poller and printer are threads, must be started
		poller.start();
		printer.start();
		
		// wait for another button press to exit
		Button.waitForAnyPress();
		System.exit(0);
	}
}
