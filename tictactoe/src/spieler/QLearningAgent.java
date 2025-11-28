package spieler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

public class QLearningAgent implements Serializable {

    private final HashMap<String, double[]> qTable = new HashMap<>();

    private double explorationsrate = 0.2;

    private final transient Random random = new Random();

    // state: Der String, der das Brett repräsentiert.
    // validMoves: Ein Array von booleans, welche Felder frei sind.
    // isTraining: Wenn true, wird manchmal zufällig gezogen (Exploration).
    // return: Der Index (0-8) des gewählten Feldes.

    public int getAction(String state, boolean[] validMoves, boolean isTraining) {
        qTable.putIfAbsent(state, new double[9]);

        if (isTraining && random.nextDouble() < explorationsrate) {
            return getRandomMove(validMoves);
        }

        double[] actions = qTable.get(state);
        int bestAction = -1;
        double maxVal = -Double.MAX_VALUE;

        for (int i = 0; i < 9; i++) {
            if (validMoves[i]) {
                if (actions[i] > maxVal) {
                    maxVal = actions[i];
                    bestAction = i;
                }
            }
        }

        if (bestAction == -1)
            return getRandomMove(validMoves);

        return bestAction;
    }

    private int getRandomMove(boolean[] validMoves) {
        int move;
        do {
            move = random.nextInt(9);
        } while (!validMoves[move]);
        return move;
    }

    public void train(String stateOld, int action, double reward) {
        qTable.putIfAbsent(stateOld, new double[9]);

        double[] oldQValues = qTable.get(stateOld);
        double currentQ = oldQValues[action];

        double maxNextQ = -Double.MAX_VALUE;

        // Hyperparameter
        double lernrage = 0.1;
        double zukunftsgewichtung = 0.9;
        double newQ = currentQ + lernrage * (reward + (zukunftsgewichtung * maxNextQ) - currentQ);

        oldQValues[action] = newQ;
    }

    // public void train(String stateOld, int action, double reward, String
    // stateNew, boolean[] validMovesNew) {
    // qTable.putIfAbsent(stateOld, new double[9]);
    // qTable.putIfAbsent(stateNew, new double[9]);

    // double[] oldQValues = qTable.get(stateOld);
    // double currentQ = oldQValues[action];

    // double maxNextQ = -Double.MAX_VALUE;
    // double[] nextQValues = qTable.get(stateNew);
    // boolean movesAvailable = false;

    // for (int i = 0; i < 9; i++) {
    // if (validMovesNew[i]) {
    // if (nextQValues[i] > maxNextQ) {
    // maxNextQ = nextQValues[i];
    // }
    // movesAvailable = true;
    // }
    // }

    // if (!movesAvailable) {
    // maxNextQ = 0.0;
    // }

    // // Hyperparameter
    // double lernrage = 0.1;
    // double zukunftsgewichtung = 0.9;
    // double newQ = currentQ + lernrage * (reward + (zukunftsgewichtung * maxNextQ)
    // - currentQ);

    // oldQValues[action] = newQ;
    // }
}