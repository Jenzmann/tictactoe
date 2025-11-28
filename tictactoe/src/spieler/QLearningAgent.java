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

    double learningRate = 0.1;
    private double explorationRate = 0.9;

    private final transient Random random = new Random();

    public int getAction(String state, boolean[] validMoves, boolean isTraining) {
        qTable.putIfAbsent(state, new double[9]);

        if (isTraining && random.nextDouble() < explorationRate) {
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

        if (bestAction == -1) return getRandomMove(validMoves);

        return bestAction;
    }

    private int getRandomMove(boolean[] validMoves) {
        int move;
        do {
            move = random.nextInt(9);
        } while (!validMoves[move]);
        return move;
    }

    public void train(String stateOld, int action, double reward, String stateNew, boolean[] validMovesNew) {
        if (action == -1) {
            return;
        }

        qTable.putIfAbsent(stateOld, new double[9]);
        qTable.putIfAbsent(stateNew, new double[9]);

        double[] oldQValues = qTable.get(stateOld);
        double currentQ = oldQValues[action];

        double maxNextQ = -Double.MAX_VALUE;
        double[] nextQValues = qTable.get(stateNew);
        boolean movesAvailable = false;

        for (int i = 0; i < 9; i++) {
            if (validMovesNew[i]) {
                if (nextQValues[i] > maxNextQ) {
                    maxNextQ = nextQValues[i];
                }
                movesAvailable = true;
            }
        }

        if (!movesAvailable) {
            maxNextQ = 0.0;
        }

        // Hyperparameter
        double newQ = currentQ + learningRate * (reward + (explorationRate * maxNextQ) - currentQ);

        oldQValues[action] = newQ;
    }

    public void decayEpsilon() {
        if (explorationRate > 0.01) {
            explorationRate *= 0.99995;
        }
    }

    public void setExplorationRate(double eps) {
        this.explorationRate = eps;
    }

    public void save(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
        }
    }

    public static QLearningAgent load(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (QLearningAgent) ois.readObject();
        }
    }
}