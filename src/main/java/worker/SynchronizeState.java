package worker;

import util.SocketIO;
import util.State;

/**
 * Created by lukas on 04.01.19.
 */
public class SynchronizeState implements State {

    private Worker w;

    int workersReady;


    public SynchronizeState(Worker w){
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("cell")){
            w.coordinator.writeLine(message);
            handleCellMessage(message);
        }
        if (message.contains("workerReady")){
            workersReady++;
            if (workersReady == w.workers.size()){
                w.coordinator.writeLine("gameReady");
                w.setState(STATES.C_TICKAWAITING);
            }
        }
    }

    private void handleCellMessage(String message){
        String[] parameters = message.split(" ");
        int x = Integer.parseInt(parameters[1]);
        int y = Integer.parseInt(parameters[2]);
        for (SocketIO s: w.boundaries.keySet()){
            Integer[] corners = w.boundaries.get(s);
            int x1 = corners[0];
            int y1 = corners[1];
            int x2 = corners[2];
            int y2 = corners[3];

            if (x >= x1 -1 && x <= x2 + 1){
                if (y >= y1 -1 && y <= y2 + 1){
                    if (y == y1 -1 || y == y2+1 || x == x1 -1 || x == x2 + 1){
                        s.writeLine(message);
                    }
                }
            }
        }
    }

    @Override
    public void enter() {
        workersReady = 0;
        for (int j = 0; j < w.numberOfColumns; j++){
            handleCellMessage(String.format("cell %d %d %b", -1, j, false));
            handleCellMessage(String.format("cell %d %d %b", w.numberOfRows, j, false));
        }
        for (int i = 0; i < w.numberOfRows; i++){
            handleCellMessage(String.format("cell %d %d %b", i, -1, false));
            handleCellMessage(String.format("cell %d %d %b", i, w.numberOfColumns, false));
        }
        handleCellMessage(String.format("cell %d %d %b", -1, -1, false));
        handleCellMessage(String.format("cell %d %d %b", -1, w.numberOfColumns, false));
        handleCellMessage(String.format("cell %d %d %b", w.numberOfRows, -1, false));
        handleCellMessage(String.format("cell %d %d %b", w.numberOfRows, w.numberOfColumns, false));
        for (SocketIO s: w.workers){
            s.writeLine("sendCells ");
        }
    }

    @Override
    public void exit() {

    }
}
