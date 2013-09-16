import lejos.nxt.LCD;

/*
*  !!! IMPORTANT SETUP !!!
*
*  The PController expects the robot to look 45deg to the
*  LEFT with respect to the orientation of the LCD display. So it 
*  should be placed on the right of whatever it is supposed sense.
*/
	
/*
*  The P controller accelerates the motor speeds to make adjustments
*  to the path the robot takes. Once the robot is back on track, it
*  resets the motor speeds to the default speed.
*/

public class PController implements UltrasonicController {
	private final int bandCenter, bandwidth;
	private int distance;
	
	// speed of motors
	private int rightMotorSpeed;
	private int leftMotorSpeed;
	
	private static final int MIN_SPEED = 50;
	private static final int MAX_SPEED = 450;
	
	private static final int SLOW_ACCELERATION = 8;
	private static final int FAST_ACCELERATION = 20;
	
	// time out gaps before a left turn
	private int gapCount = 0;
	private final int GAP_TIMEOUT = 10;
		
	// if direction is -1, too close. +1, too far. 0 just right.
	private int direction = 0;

	public PController(int bandCenter, int bandwidth) {
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		
		// reset the motor speeds
		reset();
		
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
		
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public String getType() {
		return "P Type";
	}
	
	/*
	*  The P controller works by reseting the motor speeds when the
	*  robot gets back within the bandwidth. If it gets too close, it
	*  quickly accelerates to turn right, if it gets too far, it slowly
	*  acclerates to turn left.
	*/
	
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
			gapCount ++;
			if (direction < 0) reset();
			if (gapCount >= GAP_TIMEOUT) left();
		// too close
		} else if (delta < -bandwidth) { 
			gapCount = 0;
			if (direction > 0) reset();
			right();
		} else {
			gapCount = 0;
			reset();
		}
		
		// assign the new motor speed
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}
	
	// accelerate the right motor positively to turn left
	private void left() {
		direction = 1;
		rightMotorSpeed = Math.min(rightMotorSpeed + SLOW_ACCELERATION, MAX_SPEED);
	}
	
	// accelerate the right motor negatively, and the accelerate
	// the left motor positively to turn right
	private void right() {
		direction = -1;
		rightMotorSpeed = Math.max(rightMotorSpeed - FAST_ACCELERATION, MIN_SPEED);
		leftMotorSpeed = Math.min(leftMotorSpeed + FAST_ACCELERATION, MAX_SPEED);
	}
	
	// reset the motor speeds and the direction
	private void reset() {
		direction = 0;
		rightMotorSpeed = DEFAULT_SPEED;
		leftMotorSpeed = DEFAULT_SPEED;
	}
	
	@Override
	public int readSensorDistance() {
		return distance;
	}
}
