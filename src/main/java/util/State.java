package util;

/**
 * Created by lukas on 29.12.18.
 */
public interface State {

    void handleMessage(String message);

    void enter();

    void exit();
}
