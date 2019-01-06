package worker;

import util.CallbackAble;
import util.SocketIO;
import util.State;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lukas on 07.11.18.
 */
public class Worker implements CallbackAble{

    ServerSocket socket;
    SocketIO registry;
    SocketIO coordinator;
    int port;
    Field field;

    int numberOfWorkers;
    Map<SocketIO, Integer[]> boundaries = new HashMap<>();

    int x1, y1, x2, y2, numberOfRows, numberOfColumns;

    boolean initialized = false;

    Map<STATES, State> stateMap;
    State currentState;
    public List<SocketIO> workers = new ArrayList<>();

    public Worker(){
        field = new Field();

        stateMap = new HashMap<>();
        stateMap.put(STATES.AVAILABLE, new AvailableState(this));
        stateMap.put(STATES.W_SYNCNEEDED, new SyncNeededState(this));
        stateMap.put(STATES.W_TICKREADY, new TickReadyState(this));
        stateMap.put(STATES.C_CREATING, new CreatingState(this));
        stateMap.put(STATES.C_SYNCHRONIZE, new SynchronizeState(this));
        stateMap.put(STATES.C_TICKAWAITING, new TickAwaitingState(this));
        stateMap.put(STATES.C_TICKED, new TickedState(this));
        currentState = stateMap.get(STATES.AVAILABLE);

        try{
            registry = new SocketIO(new Socket("localhost", 7777), this);
            registry.writeLine("getPort");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setState(STATES newState){
        System.out.println("Transition to " + newState);
        currentState.exit();
        currentState = stateMap.get(newState);
        currentState.enter();
    }



    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < 15; i++){
            threads.add(new Thread(() -> {
                Worker w = new Worker();
            }));
        }
        for (Thread t: threads){
            t.start();
        }
    }

    @Override
    public synchronized void callback(String message) {
        if (message.contains("terminate")) {
            for (SocketIO s: workers){
                s.close();
            }
            workers.clear();
            if (initialized){
                registry.writeLine("registerWorker");
                setState(STATES.AVAILABLE);
            } else {
                System.exit(0);
            }
            try {
                coordinator = new SocketIO(socket.accept(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (message.contains("port")){
            initialized = true;
            port = Integer.parseInt(message.split(" ")[1]);
            System.out.println("Start on port " + port);
            try {
                socket = new ServerSocket(port);
                registry.writeLine("registerWorker");
                setState(STATES.AVAILABLE);
                coordinator = new SocketIO(socket.accept(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            currentState.handleMessage(message);
        }
    }
}
