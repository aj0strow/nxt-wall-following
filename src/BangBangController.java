import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.C, rightMotor = Motor.A;
	
	// distances are in centimeters
	private int distance, targetDistance = 15, deltaDistance = 3;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		straight();
		leftMotor.backward();
		rightMotor.backward();
	}
	
	@Override
	// 0 < distance < 255 (in centimeters)
	public void processUSData(int distance) {
		this.distance = distance;
		
		int diff = targetDistance - distance;
		if (diff < -deltaDistance) {
			right();
		} else if (diff > deltaDistance) {
			left();
		} else {
			straight();
		}		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
	
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
