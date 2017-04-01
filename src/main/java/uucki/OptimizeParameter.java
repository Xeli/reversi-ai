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

    public final static int POPULATION_LIMIT = 1;
    public final static int GENERATIONS = 1;
    public static Random random;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        random = new Random();

        List<Double> population = new ArrayList<Double>();
        population.add(0.25);
        population.add(0.75);
        population.add(1.0);
        population.add(1.25);
        population.add(1.5);
        population.add(1.75);
        population.add(2.0);
        rankPopulation(population);
    }

    private static void rankPopulation(List<Double> population) {
        HashMap<Double, Integer> score = new HashMap<Double, Integer>(population.size());
        for(Double c : population) {
            for(int round = 0; round < 10; round++) {

                for(Double opponent : population) {
                    if (opponent == c) {
                        continue;
                    }

                    double winner = runGame(c, opponent);
                    int currentScore = score.getOrDefault(winner, 0);
                    score.put(winner, currentScore+1);
                }
            }
        }

        for(Map.Entry<Double, Integer> entry : score.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void findBestC(BufferedReader br) {
        double lowEnd = 0.0;
        double highEnd = 10.0;
        try {
            System.out.println("Low end number to search");
            lowEnd = Double.parseDouble(br.readLine());
            System.out.println("high end number to search");
            highEnd = Double.parseDouble(br.readLine());
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
            double mean = mean(population);
            double variance = variance(population);
            System.out.println(i + "," + mean + "," + variance);
            population = grow(population, variance);
            population = tournament(population);
        }
    }

    private static double mean(List<Double> list) {
        return list.stream().mapToDouble(a -> a).average().getAsDouble();
    }

    private static double variance(List<Double> list) {
        double mean = mean(list);
        return list.stream().mapToDouble(a -> Math.abs(mean - a)).sum() / list.size();
    }

    public static List<Double> grow(List<Double> population, double variance) {
        List<Double> newPopulation = new ArrayList<Double>(population);
        population.stream().map(d -> mutate(d, variance)).forEach(d -> newPopulation.add(d));
        return newPopulation;
    }

    private static double mutate(double value, double variance) {
        double range = variance;
        return random.nextGaussian() * range + value;
    }

    public static List<Double> tournament(List<Double> population) {
        List<Double> newPopulation = new ArrayList<Double>(POPULATION_LIMIT);

        while(population.size() >= 2) {
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
        //System.out.println("Running game, white: " + value1 + ", black: " + value2);
        int value1Wins = 0;
        int value2Wins = 0;
        for(int i = 0; i < 2; i++) {
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
        //System.out.println("Winner: " + (value1Wins > value2Wins ? value1 : value2));
        if(value1Wins > value2Wins) {
            return value1;
        }
        return value2;
    }

    public static double runSingleGame(double value1, double value2) {
        Algorithm ai1 = new MonteCarloTreeSearch(value1, true, false);
        Algorithm ai2 = new MonteCarloTreeSearch(value2, true, false);
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
