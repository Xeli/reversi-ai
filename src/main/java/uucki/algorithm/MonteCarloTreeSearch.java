package uucki.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import uucki.game.reversi.Board;
import uucki.heuristic.reversi.Basic;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;
import uucki.type.Node;

public class MonteCarloTreeSearch extends Algorithm implements Runnable {

    private static final long MAX_TIME = 500 * 1;
    private final static int THREADS = 4;

    private Board currentBoard = null;
    private FieldValue currentColor = null;
    private AtomicInteger simulationCount = new AtomicInteger();

    private ConcurrentHashMap<Board, Node<Board>> nodesBlack = new ConcurrentHashMap<Board, Node<Board>>();
    private ConcurrentHashMap<Board, Node<Board>> nodesWhite = new ConcurrentHashMap<Board, Node<Board>>();
    public Node<Board> rootNode = null;
    private long cutOffTime = 0;

    private double c = 0;
    private boolean weightedSimulation = false;
    private boolean tuned = false;

    private int simulations = 0;
    private ExecutorService executor = null;

    public MonteCarloTreeSearch() {

    }

    public MonteCarloTreeSearch(double c, boolean weightedSimulation, boolean tuned) {
        this(c, weightedSimulation, tuned, Executors.newCachedThreadPool());
    }

    public MonteCarloTreeSearch(double c, boolean weightedSimulation, boolean tuned, ExecutorService executor) {
        this.c = c;
        this.weightedSimulation = weightedSimulation;
        this.tuned = tuned;
        this.executor = executor;
    }

    public Move run(Board board, FieldValue color) {
        return run(board, color, true);
    }

    public Move run(Board board, FieldValue color, boolean cleanup) {
        currentBoard = board;
        currentColor = color;
        rootNode = new Node<Board>(board, color);

        List<Position> positions = rootNode.item.getPossiblePositions(color);
        if(positions.size() == 0) {
            return null;
        }
        if(positions.size() == 1) {
            return new Move(positions.get(0), color);
        }

        long startingTime = System.currentTimeMillis();
        cutOffTime = startingTime + MAX_TIME;

        for(int i = 0; i < THREADS; i++) {
            executor.execute(this);
        }

        try {
            Thread.sleep(MAX_TIME);
            //executor.shutdownNow();
            executor.awaitTermination(MAX_TIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {

        }

        Move bestMove = getBestMove(rootNode);

        if(cleanup) {
            cleanup();
        }
        return bestMove;
    }

    public void run() {
        int biggestDepth = 0;
        int simulations = 0;
        while(System.currentTimeMillis() < cutOffTime) {
            List<Node<Board>> ancestors = new ArrayList<Node<Board>>();
            ancestors.add(rootNode);
            selectAndExpand(ancestors);
            biggestDepth = Math.max(biggestDepth, ancestors.size());
            Node<Board> lastNode = ancestors.get(ancestors.size() - 1);
            FieldValue winner = simulate(lastNode, weightedSimulation);
            getNodes(lastNode.color).put(lastNode.item, lastNode);
            double heuristic = Basic.getValue(lastNode.item, FieldValue.WHITE);
            double lambda = 0.25;
            double whiteScore = (winner == FieldValue.WHITE ? 1 : 0) * (1-lambda) + (heuristic > 0 ? 1 : 0) * lambda;
            double blackScore = (winner == FieldValue.BLACK ? 1 : 0) * (1-lambda) + (heuristic > 0 ? 0 : 1) * lambda;
            update(ancestors, blackScore, whiteScore);
            simulations++;
        }
        synchronized(this) {
            this.simulations += simulations;
        }
    }

    public ConcurrentHashMap<Board, Node<Board>> getNodes(FieldValue color) {
        if(color == FieldValue.BLACK) {
            return nodesBlack;
        } else {
            return nodesWhite;
        }
    }

    private void selectAndExpand(List<Node<Board>> ancestors) {
        Node<Board> parent = ancestors.get(ancestors.size() - 1);

        if(parent.item.isFinished()) {
            return;
        }

        List<Position> positions = parent.item.getPossiblePositions(parent.color);
        FieldValue opponentColor = parent.color == FieldValue.WHITE ? FieldValue.BLACK : FieldValue.WHITE;

        List<Node<Board>> children = new ArrayList<Node<Board>>();
        for(Position p : positions) {
            Move move = new Move(p, parent.color);
            Board b = parent.item.makeMove(move);
            Node<Board> node = new Node<Board>(b, opponentColor);
            if(getNodes(node.color).containsKey(node.item)) {
                node = getNodes(node.color).get(node.item);
                children.add(node);
            } else {
                //if it isn't in nodes, we have not visited this node yet.
                //So we can use it.
                ancestors.add(node);
                return;
            }
        }

        //at this point we have all the children in our list, so we pick one
        Node<Board> child = null;

        //if there are no children, this means this player cannot make a move
        //so per othello rules the other player can go
        //this is represented by the same board as the parent, but for the opponent
        if(children.size() == 0) {
            child = new Node<Board>(parent.item, opponentColor);
            if(getNodes(child.color).containsKey(child.item)) {
                child = getNodes(child.color).get(child.item);
            } else {
                ancestors.add(child);
                return;
            }
        } else {
            //there are one or more children, using uct to pick one

            //get plays from all children and sum them
            double totalPlays = children.stream().mapToDouble(c -> c.plays).reduce(0.0, (i, c) -> i + c);
            double logTotalPlays = Math.log(totalPlays);
            double oldScore = Double.NEGATIVE_INFINITY;
            for(Node<Board> node : children) {
                double T = node.plays;
                double X = node.score / (double)node.plays;
                double S = X * X;
                double V = S - X + Math.sqrt((2 * logTotalPlays) / T);
                double score    = X + 2 * this.c * Math.sqrt(logTotalPlays / T);
                if(child == null || score > oldScore) {
                    oldScore = score;
                    child = node;
                }
            }
        }
        ancestors.add(child);
        selectAndExpand(ancestors);
    }

    private FieldValue simulate(Node<Board> node, boolean weighted) {
        Board board = node.item;
        FieldValue color = node.color;
        while(!board.isFinished()) {
            board = makeRandomMove(board, color, weighted);
            color = color.getOpponent();
        }
        simulationCount.incrementAndGet();
        return board.getWinner();
    }

    private Board makeRandomMove(Board board, FieldValue color, boolean weighted) {
        List<Position> positions = board.getPossiblePositions(color);
        if (positions.size() == 0) {
            return board;
        }


        Position randomPosition = null;
        if(weighted) {
            double sumWeights = positions.stream().mapToDouble(p -> board.getWeight(p)).sum();
            double random = ThreadLocalRandom.current().nextDouble() * sumWeights;

            double edge = 0;
            for(Position p : positions) {
                edge += board.getWeight(p);
                if(random <= edge) {
                    randomPosition = p;
                    break;
                }
            }
            if(randomPosition == null) {
                randomPosition = positions.get(positions.size()-1);
            }
        } else {
            randomPosition = positions.get(ThreadLocalRandom.current().nextInt(positions.size()));
        }
        return board.makeMove(new Move(randomPosition, color));
    }

    private void update(List<Node<Board>> nodes, double whiteScore, double blackScore) {
        for(Node<Board> node : nodes) {
            synchronized(node) {
                node.plays++;
                if(node.color == FieldValue.WHITE) {
                    node.score += whiteScore;
                } else {
                    node.score += blackScore;
                }
            }
        }
    }

    public HashMap<Position, Double> getMoveProbability() {
        HashMap<Position, Double> probabilities = new HashMap<Position, Double>();
        if(currentBoard == null) {
            return probabilities;
        }

        double sumScores = 0;
        List<Position> positions = currentBoard.getPossiblePositions(currentColor);
        for(Position position : positions) {
            Move newMove = new Move(position, currentColor);
            Board board = currentBoard.makeMove(newMove);
            Node<Board> newNode = getNodes(currentColor.getOpponent()).get(board);
            double score = 0;
            if(newNode != null) {
                score = newNode.score/ (double)newNode.plays;
            }
            sumScores += score;

            probabilities.put(position, score);
        }

        //normalize to 100%
        final double sumScoresFinal = sumScores;
        probabilities.replaceAll((k,v) -> v / sumScoresFinal);

        return probabilities;
    }

    private Move getBestMove(Node<Board> node) {
        List<Position> positions = node.item.getPossiblePositions(node.color);
        Move move = null;
        double score = Integer.MIN_VALUE;
        for(Position position : positions) {
            Move newMove = new Move(position, node.color);

            Board board = node.item.makeMove(newMove);
            Node<Board> newNode = getNodes(node.color.getOpponent()).get(board);
            double newScore = (double)newNode.score / (double)newNode.plays;
            if(move == null || newScore >= score) {
                score = newScore;
                move = newMove;
            }
        }

        return move;
    }

    public boolean hasCornerMove(Node<Board> root) {
        List<Position> positions = root.item.getPossiblePositions(root.color);

        boolean hasCornerMove = false;
        for(Position p : positions) {
            hasCornerMove |= p.isCorner();
        }
        return hasCornerMove;
    }

    public void cleanup() {
        currentBoard = null;
        nodesBlack.clear();
        nodesWhite.clear();

    }
}
