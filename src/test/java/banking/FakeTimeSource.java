package banking;

import app.ITimeSource;

public class FakeTimeSource implements ITimeSource {
    private long millis = 0;

    @Override
    public long currentTimeMillis() {
        return millis;
    }

    public void incrementTimeMillis(long millisToAdd) {
        millis += millisToAdd;
    }

    public void setTimeMillis(long newMillis) {
        millis = newMillis;
    }
}
