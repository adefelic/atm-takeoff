package app;

public class TimeSource implements ITimeSource {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
