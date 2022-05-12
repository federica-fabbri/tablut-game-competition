package HIMYT; 

public class Timer {
    private long time;
    private long start;

    Timer(int maxSeconds) {
        this.time = 1000 * maxSeconds;
    }

    void start() {
        start = System.currentTimeMillis();
    }

    boolean timeOut() {
        return System.currentTimeMillis() > start + time;
    }

    @SuppressWarnings("unused")
	long timePassed() {
    	return System.currentTimeMillis() - start;
    }
}