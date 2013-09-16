import lejos.nxt.UltrasonicSensor;

/*
*  The Ultrasonic Poller is in charge of reading the sensor, filtering out
*  bad readings, and sending the latest distance to the controller to be
*  processed in a timed loop. 
*/

public class UltrasonicPoller extends Thread {
	 // polling timeout (milliseconds)
	private static final int TIMEOUT = 10;
	
	// maximum and minimum sensor distance readings
	private static final int MIN_DISTANCE = 1;
	private static final int MAX_DISTANCE = 255;
	
	// the amount of times the filter needs to see a max reading
	// before the reading can be trusted
	private static final int FILTER_AMOUNT = 8;
	private int filterCount = 0;
	
	// whether a max reading is actually a max, or really a min
	private boolean filterToMax = true;
	
	// saved distance in case the distance from the sensor is filtered
	private int distance;
	
	private UltrasonicSensor sensor;
	private UltrasonicController controller;
	
	public UltrasonicPoller(UltrasonicSensor sensor, UltrasonicController controller) {
		this.sensor = sensor;
		this.controller = controller;
	}
	
	/*
	*  This thread reads the ultrasonic sensor for the distance, filters
	*  the distance, saves the filtered distance, and passes the filtered
	*  distance to the ultrasonic controller for processing every 10ms. 
	*/
	
	public void run() {
		while (true) {
			int realDistance = filter(sensor.getDistance());
			saveDistance(realDistance);
			controller.processSensorData(realDistance);
			try { Thread.sleep(TIMEOUT); } catch(Exception e) {}
		}
	}

	/*
	* The sensor reads erroneous max distance measurements when it is
	* very close to a wall, or also occasionally for no reason.
	* 
	* To make sure the measured distance is legit, we need to make sure 
	* it shows up multiple times in a row, and also check whether it was 
	* heading towards the min or to the max distance. 
	*/
	private int filter(int sensorDistance) {
		if (sensorDistance >= MAX_DISTANCE) {
			
			// sensor reading is at an extreme, and may need to be filtered
			if (filterCount > FILTER_AMOUNT) {
				
				// sensor really is at the min / max distance
				filterCount = 0;
				return getExtremeDistance();
			} else {
				
				// extreme sensor reading cannot be trusted
				filterCount ++;
				return distance;
			}
		}
		
		// sensor reading is ok
		filterCount = 0;
		return sensorDistance;
	}
	
	/*
	*  A max distance reading from the sensor means an extreme distance,
	*  not necessarily a maximum. (Could be a minimum).
	*/
	private int getExtremeDistance() {
		return filterToMax ? MAX_DISTANCE : MIN_DISTANCE;
	}
	
	/*
	*  The distance must be saved in case the next reading is filtered.
	*  We keep track if the robot is headed towards the min or max to
	*  get the correct extreme distance. 
	*/
	private void saveDistance(int distance) {
		this.filterToMax = distance > 12;
		this.distance = distance;
	}
}
