package util;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by lukas on 12.11.18.
 */
public class SocketIO implements Closeable{

    private BufferedReader in;
    private PrintStream out;
    private Socket socket;
    private Thread listener;

    public SocketIO(Socket socket, CallbackAble c){
        try {
            socket.setReceiveBufferSize(2097152);
            socket.setSendBufferSize(2097152);
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            listener = new Thread(() -> {
                while (!Thread.interrupted()){
                    try {
                        String message = in.readLine();
                        if (message == null){
                            message = "terminate";
                            c.callback(message);
                            socket.close();
                            break;
                        }
                        c.callback(message);
                    }
                    catch (SocketException e){
                        c.callback("terminate");
                        break;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String text){
        out.println(text);
    }

    public void close(){
        try {
            listener.interrupt();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
