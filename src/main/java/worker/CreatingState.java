package worker;

import util.SocketIO;
import util.State;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by lukas on 05.01.19.
 */
public class CreatingState implements State {

    private Worker w;
    private int requestedWorkers;
    private int readyWorkers;



    public CreatingState(Worker w) {
        this.w = w;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("worker")){
            int workerPort = Integer.parseInt(message.split(" ")[1]);
            if (workerPort > 0){
                try{
                    SocketIO s = new SocketIO(new Socket("localhost", workerPort), w);
                    w.workers.add(s);
                    int i = requestedWorkers++;
                    int workerRows = w.numberOfWorkers, workerColumns = 1;
                    for (int c = (int) Math.sqrt(w.numberOfWorkers); c > 1; c--){
                        if (w.numberOfWorkers % c == 0){
                            workerRows = c;
                            workerColumns = w.numberOfWorkers / workerRows;
                            break;
                        }
                    }
                    int rowIndex = i / workerColumns;
                    int columnIndex = i % workerColumns;
                    int rowStep = (int) Math.ceil(w.numberOfRows * 1.0 / workerRows);
                    int columnStep = (int) Math.ceil(w.numberOfColumns * 1.0 / workerColumns);
                    int startX = rowIndex * rowStep;
                    int startY = columnIndex * columnStep;
                    int endX = Math.min(startX + rowStep, w.numberOfRows) -1;
                    int endY = Math.min(startY + columnStep, w.numberOfColumns) -1;
                    s.writeLine(String.format("initField %d %d %d %d", startX, startY, endX, endY));
                    w.boundaries.put(s, new Integer[]{startX, startY, endX, endY});
                    w.coordinator.writeLine(String.format("partition %d %d %d %d", startX, startY, endX, endY));
                } catch (IOException e){
                    e.printStackTrace();
                }
            } else{
                System.err.println("too few workers");
                System.exit(1);
            }
        }
        if (message.contains("ackInit")){
            readyWorkers++;
            if (readyWorkers == w.numberOfWorkers){
                w.setState(STATES.C_SYNCHRONIZE);
            }
        }
    }

    @Override
    public void enter() {
        requestedWorkers = 0;
        readyWorkers = 0;
        for (int i = 0; i < w.numberOfWorkers; i++){
            w.registry.writeLine("getWorker");
        }
    }

    @Override
    public void exit() {

    }
}
