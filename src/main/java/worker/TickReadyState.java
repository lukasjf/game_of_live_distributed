package worker;

import util.State;

/**
 * Created by lukas on 05.01.19.
 */
public class TickReadyState implements State {

    private Worker w;

    public TickReadyState(Worker w){
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("tick")){
            this.w.field.tick();
            w.setState(STATES.W_SYNCNEEDED);
            w.coordinator.writeLine("ackTick");
        }
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }
}
