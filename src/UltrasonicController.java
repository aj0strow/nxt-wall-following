public interface UltrasonicController {
	static final int HIGH_SPEED = 400;
	static final int MEDIUM_SPEED = 300;
	static final int LOW_SPEED = 200;
	public void processUSData(int distance);
	public int readUSDistance();
}
