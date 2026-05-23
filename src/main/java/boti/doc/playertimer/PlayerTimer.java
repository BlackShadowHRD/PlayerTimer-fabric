package boti.doc.playertimer;

public class PlayerTimer {

    private TimerMode mode;
    private TimerState state;
    private boolean visible;
    private int time;

    public PlayerTimer(
            TimerMode mode,
            TimerState state,
            boolean visible,
            int time
    ) {
        this.mode = mode;
        this.state = state;
        this.visible = visible;
        this.time = time;
    }

    public TimerMode getMode() {
        return mode;
    }

    public void setMode(TimerMode mode) {
        this.mode = mode;
    }

    public TimerState getState() {
        return state;
    }

    public void setState(TimerState state) {
        this.state = state;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}