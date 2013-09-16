import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Motor;

/*
*  !!! IMPORTANT SETUP !!!
*
*  Ultrasonic Controllers expect the left motor to be plugged into A, and
*  the right motor to be plugged into B. 
*/

public interface UltrasonicController {	
	static final int DEFAULT_SPEED = 200;
	static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	
	public int readSensorDistance();
	
	// 1mm <= sensorDistance <= 255mm
	public void processSensorData(int distance);
	
	public String getType();
}
