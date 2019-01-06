package display;

import util.CallbackAble;
import util.SocketIO;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

/**
 * Created by lukas on 07.11.18.
 */
public class GameDisplay extends JComponent implements CallbackAble{

    SocketIO registry;
    SocketIO game;

    Color[] partitionColors;
    private java.util.List<Partition> partitions = new ArrayList<>();

    int numberOfWorkers = 12;
    int rows = 90;
    int columns = 90;

    boolean ready = false;

    public GameDisplay(){
        partitionColors = new Color[]{Color.YELLOW, Color.GREEN, Color.CYAN,
                Color.RED, Color.PINK, Color.ORANGE, Color.MAGENTA};
        try {
            registry = new SocketIO(new Socket("localhost", 7777), this);
            registry.writeLine("getWorker");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start(){
        game.writeLine(String.format("createGame %d %d %d", numberOfWorkers, rows, columns));
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, columns * 10, rows * 10);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, columns * 10, rows * 10);

        for (int i = 0; i < partitions.size(); i++){
            Partition p = partitions.get(i);
            Color c = partitionColors[i%partitionColors.length];
            colorPartition(g, p, c);
        }
        g.setColor(Color.BLACK);
    }

    private void colorPartition(Graphics g, Partition p, Color c){
        g.setColor(c);
        g.fillRect(p.y1 * 10, p.x1 * 10, (p.y2 - p.y1 + 1) * 10, (p.x2 - p.x1 + 1) * 10);
    }

    public Dimension getPreferredSize() {
        return new Dimension(columns*10 +1, rows*10+1);
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public synchronized void callback(String message) {
        if (message.contains("terminate")){
            game.close();
        }
        if (message.contains("partition")){
            String[] params = message.split(" ");
            Partition p  =new Partition(Integer.parseInt(params[1]), Integer.parseInt(params[2]),
                    Integer.parseInt(params[3]), Integer.parseInt(params[4]));
            Color c = partitionColors[partitions.size() % partitionColors.length];
            colorPartition(this.getGraphics(), p, c);
            partitions.add(p);
        }
        if (message.contains("worker")){
            try {
                int port = Integer.parseInt(message.split(" ")[1]);
                game = new SocketIO(new Socket("localhost", port), this);
                System.out.println("Connect to Coordinator " + port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (message.contains("cell")){
            String[] parameters = message.split(" ");
            int x = Integer.parseInt(parameters[1]);
            int y = Integer.parseInt(parameters[2]);
            boolean v = Boolean.valueOf(parameters[3]);

            if (v){
                this.getGraphics().fillOval(y * 10 + 2, x* 10 + 2, 8 ,8);
            }
        }
        if (message.contains("gameReady")){
            ready = true;
        }
    }

    class Partition{
        int x1, x2, y1, y2;

        public Partition(int x1, int y1, int x2, int y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}
