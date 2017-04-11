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

    public final static int POPULATION_LIMIT = 10;
    public final static int GENERATIONS = 150;
    public static Random random;

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        random = new Random();

        try {
            rankPopulation();
        } catch (IOException e) {

        }
    }

    private static void rankPopulation() throws IOException {
        List<Double> population = new ArrayList<Double>();
        population.add(0.0);
        population.add(0.1);
        population.add(0.2);
        population.add(0.3);
        population.add(0.4);
        population.add(0.5);
        population.add(0.6);
        population.add(0.7);
        population.add(0.8);
        population.add(0.9);
        population.add(1.0);
        population.add(3.0);
        population.add(5.0);

        SortedMap<Double, Map<Double, Integer>> resultMatrix = new TreeMap<Double, Map<Double, Integer>>();

        //build up the hashmap resultmatrix
        for(Double current : population) {
            TreeMap<Double, Integer> results = new TreeMap<Double, Integer>();
            for(Double opponent : population) {
                results.put(opponent, 0);
            }
            resultMatrix.put(current, results);
        }

        TreeMap<Double, Integer> score = new TreeMap<Double, Integer>();

        BufferedWriter result = new BufferedWriter(new FileWriter("result.csv"));
        BufferedWriter matrix = new BufferedWriter(new FileWriter("matrix.csv"));
        int gamesPlayed = 0;
        for(Double c : population) {
            for(int round = 0; round < 10; round++) {
                for(Double opponent : population) {
                    if(c == opponent) {
                        continue;
                    }
                    double winner = runSingleGame(c, opponent);

                    int currentScore = score.getOrDefault(winner, 0);
                    score.put(winner, currentScore+1);

                    if(winner == c) {
                        int currentWins = resultMatrix.get(c).get(opponent);
                        resultMatrix.get(c).put(opponent, currentWins+1);
                    } else {
                        int currentWins = resultMatrix.get(opponent).get(c);
                        resultMatrix.get(opponent).put(c, currentWins+1);
                    }
                    gamesPlayed++;
                    System.out.println("Played: " + gamesPlayed);
                }
            }

            result.write("=============new================");
            result.newLine();
            for(Map.Entry<Double, Integer> entry : score.entrySet()) {
                result.write(entry.getKey() + ", " + entry.getValue());
                result.newLine();
            }

            matrix.write("=============new================");
            matrix.newLine();
            for(Map.Entry<Double, Map<Double, Integer>> entry : resultMatrix.entrySet()) {
                matrix.write("," + entry.getKey());
            }
            matrix.newLine();
            for(Map.Entry<Double, Map<Double, Integer>> entry : resultMatrix.entrySet()) {
                matrix.write(String.valueOf(entry.getKey()));
                for(Map.Entry<Double, Integer> winsEntry : entry.getValue().entrySet()) {
                    matrix.write(", " + winsEntry.getValue());
                }
                matrix.newLine();
            }
            result.flush();
            matrix.flush();
        }

        result.flush();
        matrix.flush();
        result.close();
        matrix.close();
    }

    private static void findBestC(BufferedReader br) {
        double lowEnd = 0.2;
        double highEnd = 0.6;

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
                Genome child2 = new Genome(child1);
                child2.mutate(random);

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
            g.fitness = 10;
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
        Algorithm ai1 = new MonteCarloTreeSearch(value1, false, false);
        Algorithm ai2 = new MonteCarloTreeSearch(value2, false, false);
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

    int n = 0;
    double variance = 1;

    public Genome(int fitness, double cValue) {
        this.fitness = fitness;
        this.cValue = cValue;
    }

    public Genome(Genome g) {
        this.fitness = g.fitness;
        this.cValue = g.cValue;
        this.n = 0;
        this.variance = g.variance;
    }

    public void mutate(Random random) {
        n++;
        double randomNumber = random.nextGaussian();
        variance = variance * Math.exp(randomNumber / Math.sqrt(n));
        cValue = cValue + variance * random.nextGaussian();
    }
}
