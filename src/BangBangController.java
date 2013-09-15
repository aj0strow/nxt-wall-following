import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

/*
* !!! IMPORTANT !!!
*
* The BangBangController expects the robot to look 45deg to the
* LEFT with respect to the orientation of the LCD display. So it 
* should be placed on the right of whatever it is supposed to sense.
*/

public class BangBangController implements UltrasonicController {
	
	// maximum sensor distance, and the amount of times
	// it needs to show up to be considered a legit reading
	private static final int MAX_DISTANCE = 255;
	private static final int LEGIT_AMOUNT = 5;
	
	private final int bandCenter, bandwidth;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	
	// distances are in millimeters
	private int distance, legitCount = 0;
	private boolean maxToZero = false;
	
	public BangBangController(int bandCenter, int bandwidth) {
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		straight();
		
		// wheels are on backwards, so it should drive in reverse
		leftMotor.backward();
		rightMotor.backward();
	}
	
	@Override
	public String getType() {
		return "BangBang";
	}
	
	@Override
	// 0mm <= sensorDistance <= 255mm
	public void processSensorData(int sensorDistance) {
		setDistance(legitDistance(sensorDistance));
		
		LCD.clear();
		LCD.drawString("sensor: " + sensorDistance, 0, 0);
		LCD.drawString("real: " + distance, 0, 1);
		
		int diff = distance - bandCenter;
		// too far away
		if (diff > bandwidth) {
			LCD.drawString("LEFT", 0, 2);
			left();
		// too close
		} else if (diff < -bandwidth) { 
			LCD.drawString("RIGHT", 0, 2);
			right();
		} else { 
			LCD.drawString("STRAIGHT", 0, 2);
			straight(); 
		}
	}

	@Override
	public int readSensorDistance() {
		return this.distance;
	}
	
	/*
	* The sensor reads erroneous max distance measurements when it is
	* very close to a wall, or also occasionally for no reason.
	* 
	* To make sure the measured distance is legit, we need to make sure 
	* it shows up multiple times in a row, and also check whether it was 
	* heading towards 0 or to the max distance. 
	*/
	private int legitDistance(int sensorDistance) {
		if (sensorDistance >= MAX_DISTANCE) {
			if (legitCount > LEGIT_AMOUNT) {
				legitCount = 0;
				return extremeDistance();
			} else {
				legitCount++;
				return distance;
			}
		} 
		return sensorDistance;
	}
	
	private int extremeDistance() {
		return maxToZero ? 0 : MAX_DISTANCE;
	}

	private void setDistance(int distance) {
		this.maxToZero = distance < MAX_DISTANCE / 2;
		this.distance = distance;
	}
	
	/*
	* left(), right(), straight() set the wheel motors to go respective
	* directions. The motors are set to low / high for turning, and the
	* straight speed for going straight. 
	*/
	private void left() {
		leftMotor.setSpeed(LOW_SPEED);
		rightMotor.setSpeed(HIGH_SPEED);
	}
	
	private void right() {
		leftMotor.setSpeed(HIGH_SPEED);
		rightMotor.setSpeed(LOW_SPEED);
	}
	
	private void straight() {
		leftMotor.setSpeed(MEDIUM_SPEED);
		rightMotor.setSpeed(MEDIUM_SPEED);
	}
}
