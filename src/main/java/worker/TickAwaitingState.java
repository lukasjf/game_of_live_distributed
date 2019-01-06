package worker;

import util.SocketIO;
import util.State;

/**
 * Created by lukas on 04.01.19.
 */
public class TickAwaitingState implements State {

    private Worker w;

    public TickAwaitingState(Worker w){
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("gameTick")){
            for (SocketIO s: w.workers){
                s.writeLine("tick");
            }
            w.setState(STATES.C_TICKED);
        }
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }
}
