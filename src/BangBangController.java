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
	private final int motorLow, motorHigh;
	private final int motorStraight = 300;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	
	// distances are in millimeters
	private int distance, legitCount = 0;
	boolean maxToZero = false;
	
	public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		straight();
		leftMotor.backward();
		rightMotor.backward();
	}
	
	@Override
	// 0mm <= sensorDistance <= 255mm
	public void processUSData(int sensorDistance) {
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
	public int readUSDistance() {
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
		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorHigh);
	}
	
	private void right() {
		leftMotor.setSpeed(motorHigh);
		rightMotor.setSpeed(motorLow);
	}
	
	private void straight() {
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
	}
}
