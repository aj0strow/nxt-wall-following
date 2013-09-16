import lejos.nxt.LCD;

public class PController implements UltrasonicController {
	private final int bandCenter, bandwidth;
	private int distance;
	private int rightMotorSpeed;
	private int leftMotorSpeed;
	
	private static final int MIN_SPEED = 50;
	private static final int MAX_SPEED = 450;
	
	private static final int ACCELERATION = 10;
	
	private int gapCount = 0;
	private final int GAP_TIMEOUT = 8;
	
	public PController(int bandCenter, int bandwidth) {
		/*
		* The P Controller uses varies the right wheel speed linearly to the distance from
		* the wall. When it is too far, the right wheel is sped up, too near, right wheel is slowed.
		*/
		
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		
		straight();
		
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
		
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public String getType() {
		return "P Type";
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
			if (gapCount >= GAP_TIMEOUT) left();
			else gapCount ++;
		// too close
		} else if (delta < -bandwidth) { 
			gapCount = 0;
			right();
		} else {
			gapCount = 0;
			straight();
		}
		
		// assign the new motor speed
		leftMotor.setSpeed(leftMotorSpeed);
		rightMotor.setSpeed(rightMotorSpeed);
	}
	
	private void left() {
		rightMotorSpeed = Math.min(rightMotorSpeed + 5, MAX_SPEED);
	}
	
	private void right() {
		rightMotorSpeed = Math.max(rightMotorSpeed - 5, MIN_SPEED);
		leftMotorSpeed = Math.min(leftMotorSpeed + 5, MAX_SPEED);
	}
	
	private void straight() {
		rightMotorSpeed = DEFAULT_SPEED;
		leftMotorSpeed = DEFAULT_SPEED;
	}

	
	@Override
	public int readSensorDistance() {
		return distance;
	}
}
