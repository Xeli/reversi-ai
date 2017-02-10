package uucki;

import uucki.game.reversi.Board;
import uucki.algorithm.Algorithm;
import uucki.algorithm.MonteCarloTreeSearch;
import uucki.algorithm.Minimax;
import uucki.type.FieldValue;
import uucki.modes.VersusAI;
import uucki.modes.AIvsAI;
import uucki.modes.CustomBoard;

import java.io.*;
import java.util.*;

public class OptimizeParameter {

    public final static int POPULATION_LIMIT = 4;
    public final static int GENERATIONS = 5;
    public static Random random;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        random = new Random();

        int lowEnd = 0;
        int highEnd = 10;
        try {
            System.out.println("Low end number to search");
            lowEnd = Integer.parseInt(br.readLine());
            System.out.println("high end number to search");
            highEnd = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.out.println("IO error");
            return;
        }

        List<Double> population = new ArrayList<Double>(POPULATION_LIMIT);

        for(int i = 0; i < POPULATION_LIMIT; i++) {
            double value = random.nextDouble() * (highEnd - lowEnd) + lowEnd;
            population.add(value);
        }

        for(int i = 0; i < GENERATIONS; i++) {
            population = grow(population, i, lowEnd, highEnd);
            population = tournament(population);
        }

    }

    public static List<Double> grow(List<Double> population, int generation, double low, double high) {
        List<Double> newPopulation = new ArrayList<Double>(population);
        population.stream().map(d -> mutate(d, generation, low, high)).forEach(d -> newPopulation.add(d));
        System.out.println(population);
        System.out.println(newPopulation);
        return newPopulation;
    }

    private static double mutate(double value, int generation, double low, double high) {
        double range = 0.3 - Math.pow(0.3 * (generation / (double)GENERATIONS), 3.0);
        return random.nextGaussian() * (high - low) * range + value;
    }

    public static List<Double> tournament(List<Double> population) {
        List<Double> newPopulation = new ArrayList<Double>(POPULATION_LIMIT);

        while(population.size() >= 2) {
            System.out.println(population.size());
            Double value1 = anyItem(population);
            Double value2 = anyItem(population);
            newPopulation.add(runGame(value1, value2));
        }

        return newPopulation;
    }

    //get and remove random element from list
    private static double anyItem(List<Double> population) {
        int index = random.nextInt(population.size());
        return population.remove(index);
    }

    public static double runGame(double value1, double value2) {
        System.out.println("Running game, white: " + value1 + ", black: " + value2);
        int value1Wins = 0;
        int value2Wins = 0;
        for(int i = 0; i < 4; i++) {
            double winningValue = 0;
            if (i % 2 == 0) {
                winningValue = runSingleGame(value1, value2);
            } else {
                winningValue = runSingleGame(value2, value1);
            }
            if(winningValue == value1) {
                value1Wins++;
            } else {
                value2Wins++;
            }
        }
        System.out.println("Winner: " + (value1Wins > value2Wins ? value1 : value2));
        if(value1Wins > value2Wins) {
            return value1;
        }
        return value2;
    }

    public static double runSingleGame(double value1, double value2) {
        Algorithm ai1 = new MonteCarloTreeSearch(value1);
        Algorithm ai2 = new MonteCarloTreeSearch(value2);
        Board board = Board.initialBoard(false);
        AIvsAI mode = new AIvsAI(board, ai1, ai2);
        board = mode.game();
        if(board.getWinner() == FieldValue.WHITE) {
            return value1;
        } else {
            return value2;
        }
    }
}
