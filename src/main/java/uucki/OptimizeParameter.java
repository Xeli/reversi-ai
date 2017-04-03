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

    public final static int POPULATION_LIMIT = 5;
    public final static int GENERATIONS = 150;
    public static Random random;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        random = new Random();

        findBestC(br);
    }

    private static void rankPopulation() {
        List<Double> population = new ArrayList<Double>();
        population.add(0.25);
        population.add(0.75);
        population.add(1.0);
        population.add(1.25);
        population.add(1.5);
        population.add(1.75);
        population.add(2.0);

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

            for(Map.Entry<Double, Integer> entry : score.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
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

        //initial population
        List<Genome> population = new ArrayList<Genome>(POPULATION_LIMIT);
        while(population.size() < POPULATION_LIMIT) {
            double value = random.nextDouble() * (highEnd - lowEnd) + lowEnd;
            Genome g = new Genome(0, value);
            evaluate(g);
            population.add(g);
        }

        for(int i = 0; i < GENERATIONS; i++) {
            //print some intermediate stats
            double mean = mean(population);
            double variance = variance(population);
            int fitnessSum = population.stream().mapToInt(x -> x.fitness).sum();
            int wins = fitnessSum - population.size() / 3;
            System.out.println(i + "," + mean + "," + variance + "," + wins);

            //build the new children population
            List<Genome> children = new ArrayList<Genome>(POPULATION_LIMIT);
            while(children.size() < POPULATION_LIMIT) {
                Genome parent1 = selectGenome(population);
                Genome parent2 = selectGenome(population);

                Genome child1 = crossOver(parent1, parent2);
                Genome child2 = mutate(child1);

                evaluate(child1);
                evaluate(child2);
                children.add(child1);
                children.add(child2);
            }

            //new population
            List<Genome> newPopulation = new ArrayList<Genome>(POPULATION_LIMIT);

            //add 25% of the parents
            int parentsToAdd = (int)Math.round(POPULATION_LIMIT * 0.25);
            while(newPopulation.size() < parentsToAdd) {
                newPopulation.add(selectGenome(population));
            }

            //fill the other 75% with children
            while(newPopulation.size() < POPULATION_LIMIT) {
                newPopulation.add(selectGenome(children));
            }

            //swap populations
            population = newPopulation;
        }
    }

    private static Genome selectGenome(List<Genome> population) {
        int fitnessSum = population.stream().mapToInt(i -> i.fitness).sum();
        double edge = random.nextDouble() * fitnessSum;

        int currentEdge = 0;
        for(Genome g : population) {
            currentEdge += g.fitness;
            if(edge <= currentEdge) {
                return g;
            }
        }
        return population.get(1);
    }

    private static Genome mutate(Genome g) {
        double range = 0.125;
        return new Genome(0, random.nextGaussian() * range + g.cValue);
    }

    private static Genome crossOver(Genome g1, Genome g2) {
        double newCValue = (g1.cValue + g2.cValue) * 0.5;
        return new Genome(0, newCValue);
    }


    private static double mean(List<Genome> list) {
        return list.stream().mapToDouble(a -> a.cValue).average().getAsDouble();
    }

    private static double variance(List<Genome> list) {
        double mean = mean(list);
        return list.stream().mapToDouble(a -> Math.abs(mean - a.cValue)).sum() / list.size();
    }

    public static void evaluate(Genome g) {
        double winner = runGame(g.cValue, 0.25);
        if(winner == g.cValue) {
            g.fitness = 3;
        } else {
            g.fitness = 1;
        }
    }

    public static List<Double> tournament(List<Double> population) {
        List<Double> newPopulation = new ArrayList<Double>(POPULATION_LIMIT);

        while(population.size() > 0) {
            Double value1 = anyItem(population);
            double winner = runGame(value1, 0.25);
            newPopulation.add(winner);
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
        AIvsAI mode = new AIvsAI(board, ai1, ai2, false);
        board = mode.game();
        if(board.getWinner() == FieldValue.WHITE) {
            return value1;
        } else {
            return value2;
        }
    }

}
class Genome {
    int fitness = 1;
    double cValue = 1;

    public Genome(int fitness, double cValue) {
        this.fitness = fitness;
        this.cValue = cValue;
    }
}
