public interface UltrasonicController {
	static final int HIGH_SPEED = 400;
	static final int MEDIUM_SPEED = 300;
	static final int LOW_SPEED = 200;
	
	public int readSensorDistance();
	
	public void processSensorData(int distance);
	
	public String getType();
}
