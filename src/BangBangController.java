import lejos.nxt.LCD;

/*
*  !!! IMPORTANT SETUP !!!
*
*  The BangBangController expects the robot to look 45deg to the
*  LEFT with respect to the orientation of the LCD display. So it 
*  should be placed on the right of whatever it is supposed sense.
*/
	
/*
*  The BangBang controller sets the right motor to slow / fast speed
*  to make path adjustments when the robot strays too far from the
*  band center.
*/

public class BangBangController implements UltrasonicController {
	
	// the slow and fast speeds for right motor adjustments
	private static final int LOW_SPEED = 70;
	private static final int MEDIUM_SPEED = 150;
	private static final int HIGH_SPEED = 300;
	
	
	// desired distance from the wall
	private final int bandCenter;
	
	// allowed deviation from the band center before an adjustment is made
	private final int bandwidth;
	private int distance;
	
	private int gapCount = 0;
	private final int GAP_TIMEOUT = 8;
	
	public BangBangController(int bandCenter, int bandwidth) {
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		
		straight();
		
		// wheels are on backwards, so it should drive in reverse
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public String getType() {
		return "BangBang";
	}
	
	@Override
	public void processSensorData(int pollerDistance) {
		this.distance = pollerDistance;
		int delta = distance - bandCenter;
		
		LCD.clear();
		LCD.drawString("distance: " + distance, 0, 0);
		LCD.drawString("band center: " + bandCenter, 0, 1);
		LCD.drawString("delta: " + delta, 0, 2);
		
		// too far away
		if (delta > bandwidth) {
			if (gapCount >= GAP_TIMEOUT) {
				left();
			} else {
				gapCount ++;
				straight();
			}
		// too close
		} else if (delta < -bandwidth) { 
			gapCount = 0;
			right();
		} else { 
			gapCount = 0;
			straight(); 
		}
	}

	@Override
	public int readSensorDistance() {
		return distance;
	}
	
	/*
	* left(), right(), straight() set the wheel motors to go respective
	* directions. The motors are set to low / high for turning, and the
	* straight speed for going straight. 
	*/
	private void left() {
		LCD.drawString("LEFT", 0, 3);
		leftMotor.setSpeed(MEDIUM_SPEED);
		rightMotor.setSpeed(HIGH_SPEED);
	}
	
	private void right() {
		LCD.drawString("RIGHT", 0, 3);
		leftMotor.setSpeed(HIGH_SPEED);
		rightMotor.setSpeed(LOW_SPEED);
	}
	
	private void straight() {
		LCD.drawString("STRAIGHT", 0, 3);
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.setSpeed(DEFAULT_SPEED);
	}
}
