package registry;

import util.CallbackAble;
import util.SocketIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by lukas on 12.11.18.
 */
public class Registry {

    public static void main(String[] args){
        Registry r = new Registry();
        r.start();
    }

    static List<Integer> workers;

    static List<Integer> blockedPorts;
    private Random random;

    public Registry(){
        workers = new ArrayList<>();
        blockedPorts = new ArrayList<>();
        random = new Random();
    }

    public void start(){
        try (ServerSocket s = new ServerSocket(7777)) {
            while (true){
                Socket client = s.accept();
                new Client(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Client implements CallbackAble{

        private SocketIO s;
        private int port;

        Client(Socket client){
            this.s = new SocketIO(client, this);
        }

        @Override
        public synchronized void callback(String message){
            if (message.contains("terminate")){
                if (blockedPorts.contains(port)) {
                    System.out.println("unregister " + port);
                    if (workers.contains(port)) {
                        workers.remove(workers.indexOf(port));
                    }
                    blockedPorts.remove(blockedPorts.indexOf(port));
                }
            }
            if (message.contains("getPort")){
                synchronized (this) {
                    int port = random.nextInt(57000) + 8000;
                    while (blockedPorts.contains(port)) {
                        port = random.nextInt(57000) + 8000;
                    }
                    blockedPorts.add(port);
                    this.port = port;
                    s.writeLine("port " + port);
                }
            }
            if (message.contains("registerWorker")){
                System.out.println("register " + port);
                workers.add(port);
            }
            if (message.contains("unregisterWorker")){
                System.out.println("unregister " + port);
                if (workers.contains(port)) {
                    workers.remove(workers.indexOf(port));
                }
                blockedPorts.remove(blockedPorts.indexOf(port));
            }
            if (message.contains("getWorker")){
                if (workers.size() > 0){
                    Integer worker = workers.get(0);
                    System.out.println("notify worker " + worker);
                    s.writeLine("worker "+ worker);
                    workers.remove(worker);
                } else {
                    s.writeLine("-1");
                }
            }
        }
    }
}
