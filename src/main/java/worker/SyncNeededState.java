package worker;

import util.State;

/**
 * Created by lukas on 04.01.19.
 */
public class SyncNeededState implements State {

    private Worker w;
    private int surroundingsReceived;
    private int maxSurroundings;

    public SyncNeededState(Worker w){
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("sendCells")){
            for (int i = 0; i < w.numberOfRows; i++){
                for (int j = 0; j < w.numberOfColumns; j++){
                    w.coordinator.writeLine(String.format("cell %d %d %b", w.x1 + i, w.y1 + j, w.field.field[i+1][j+1]));
                }
            }
        }
        if (message.contains("cell")){
            String[] parameters = message.split(" ");
            int x = Integer.parseInt(parameters[1]);
            int y = Integer.parseInt(parameters[2]);
            boolean v = Boolean.valueOf(parameters[3]);
            w.field.set(x - w.x1 + 1, y - w.y1 + 1, v);
            surroundingsReceived++;
            if (surroundingsReceived == maxSurroundings){
                System.out.println(String.format("corners %d %d %d %d: %d", w.x1, w.y1, w.x2, w.y2, maxSurroundings));
                w.setState(STATES.W_TICKREADY);
                w.coordinator.writeLine("workerReady");
            }
        }
    }

    @Override
    public void enter() {
        surroundingsReceived = 0;
        maxSurroundings = 2 * w.numberOfColumns + 2 * w.numberOfRows + 4;
    }

    @Override
    public void exit() {

    }
}
