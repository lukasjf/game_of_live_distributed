package worker;

import util.State;

/**
 * Created by lukas on 05.01.19.
 */
public class TickedState implements State {

    private Worker w;
    int readyWorkers;


    public TickedState(Worker w){
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("ackTick")){
            readyWorkers++;
            if (readyWorkers == w.workers.size()){
                w.setState(STATES.C_SYNCHRONIZE);
            }
        }
    }

    @Override
    public void enter() {
        readyWorkers = 0;
    }

    @Override
    public void exit() {

    }
}
