package spieler;

import java.util.ArrayList;

public class GameResult {
    public int result;
    public ArrayList<StateAndAction> statesAndActions;

    public GameResult(int result, ArrayList<StateAndAction> statesAndActions) {
        this.result = result;
        this.statesAndActions = statesAndActions;
    }
}
