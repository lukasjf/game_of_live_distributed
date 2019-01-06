package worker;

import util.SocketIO;
import util.State;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by lukas on 29.12.18.
 */
public class AvailableState implements State {

    private Worker w;

    public AvailableState(Worker w){
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        //causes to stay worker
        if (message.contains("initField")){
            String parameters[] = message.split(" ");
            int x1 = Integer.parseInt(parameters[1]);
            int y1 = Integer.parseInt(parameters[2]);
            int x2 = Integer.parseInt(parameters[3]);
            int y2 = Integer.parseInt(parameters[4]);
            System.out.println(String.format("initfield %d %d %d %d", x1, y1, x2, y2));
            w.x1 = x1;
            w.y1 = y1;
            w.x2 = x2;
            w.y2 = y2;
            w.numberOfRows = x2 - x1 + 1;
            w.numberOfColumns = y2 - y1 + 1;
            w.field.init(w.numberOfRows + 2, w.numberOfColumns + 2);
            w.setState(STATES.W_SYNCNEEDED);
            w.coordinator.writeLine("ackInit");
        }
        //causes to become coordinator
        if (message.contains("createGame")){
            String[] parameters = message.split(" ");
            w.numberOfWorkers = Integer.parseInt(parameters[1]);
            w.numberOfRows = Integer.parseInt(parameters[2]);
            w.numberOfColumns = Integer.parseInt(parameters[3]);
            w.setState(STATES.C_CREATING);
        }
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }
}
