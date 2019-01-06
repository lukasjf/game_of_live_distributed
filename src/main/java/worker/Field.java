package worker;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 06.11.18.
 */
public class Field {

    public boolean[][] field;
    private Random random = new Random();

    public void init(int numberRows, int numberColumns){
        field = new boolean[numberRows][numberColumns];
        for (int i = 0; i < field.length; i++){
            for (int j = 0; j < field[0].length; j++){
                if (random.nextDouble() < 0.15){
                    field[i][j] = true;
                }
            }
        }
    }

    public void set(int x, int y, boolean v){
        field[x][y] = v;
    }

    public void tick(){
        List<Pair<Integer, Integer>> changes = new ArrayList<>();
        for (int i = 0; i < field.length; i++){
            for (int j = 0; j < field[0].length; j++){
                if (doesChange(i,j)){
                    changes.add(new Pair<>(i, j));
                }
            }
        }
        for (Pair<Integer, Integer> p: changes){
            field[p.getKey()][p.getValue()] = ! field[p.getKey()][p.getValue()];
        }
    }

    private boolean doesChange(int i, int j) {
        int aliveAround = 0;
        aliveAround += check(i-1, j-1) ? 1 : 0;
        aliveAround += check(i-1, j) ? 1 : 0;
        aliveAround += check(i-1, j+1) ? 1 : 0;
        aliveAround += check(i, j-1) ? 1 : 0;
        aliveAround += check(i, j+1) ? 1 : 0;
        aliveAround += check(i+1, j-1) ? 1 : 0;
        aliveAround += check(i+1, j) ? 1 : 0;
        aliveAround += check(i+1, j+1) ? 1 : 0;
        if (!field[i][j] && aliveAround == 3){
            return true;
        }
        if (field[i][j] && aliveAround > 3){
            return true;
        }
        if (field[i][j] && aliveAround <= 1){
            return true;
        }
        return false;
    }

    private boolean check(int i, int j) {
        if (i < 0 || j < 0 || i >= field.length || j >= field[0].length){
            return false;
        }
        return field[i][j];
    }
}
