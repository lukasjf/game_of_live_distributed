package display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by lukas on 08.11.18.
 */
public class UI extends JFrame {
    GameDisplay gameDisplay;
    JButton button;
    Timer timer;


    public UI(){
        super();
    }


    public static void main(String args[]) {
        UI ui = new UI();
        ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ui.setLayout(new FlowLayout());
        ui.gameDisplay = new GameDisplay();
        ui.button = new JButton("Game Tick");
        ActionListener gameTick = e -> {
            ui.doGameTick();
        };
        ActionListener toggleTimer = e -> {
            boolean isSelected = ((AbstractButton) (e.getSource())).isSelected();
            if (isSelected){
                ui.timer.start();
                ui.button.setEnabled(false);
            } else {
                ui.timer.stop();
                ui.button.setEnabled(true);
            }
        };
        ui.timer = new Timer(1000, gameTick);
        ui.button.addActionListener(gameTick);
        JCheckBox cb = new JCheckBox("Periodic Ticks");
        cb.addActionListener(toggleTimer);
        ui.getContentPane().add(cb);
        ui.getContentPane().add(ui.button);
        ui.getContentPane().add(ui.gameDisplay);
        ui.pack();
        ui.setVisible(true);
        ui.gameDisplay.start();
    }

    private void doGameTick() {
        if (gameDisplay.ready){
            gameDisplay.ready = false;
            gameDisplay.game.writeLine("gameTick");
            gameDisplay.paintComponent(gameDisplay.getGraphics());
        }

    }
}
