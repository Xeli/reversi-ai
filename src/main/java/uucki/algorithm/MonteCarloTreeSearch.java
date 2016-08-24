package uucki.algorithm;

import uucki.game.reversi.Board;
import uucki.type.*;
import uucki.heuristic.reversi.Basic;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloTreeSearch extends Algorithm {

    private Board currentBoard = null;
    private FieldValue currentColor = null;
    private int simulationCount = 0;

    private static final long MAX_TIME = 1000 * 7;
    private HashMap<Board, Node<Board>> nodesBlack = new HashMap<Board, Node<Board>>();
    private HashMap<Board, Node<Board>> nodesWhite = new HashMap<Board, Node<Board>>();

    public Move run(Board board, FieldValue color) {
        long startingTime = System.currentTimeMillis();
        currentBoard = board;
        currentColor = color;
        Node<Board> currentNode = new Node<Board>(board, color);

        while(System.currentTimeMillis() < startingTime + MAX_TIME) {
            List<Node<Board>> nodes = new ArrayList<Node<Board>>();
            nodes.add(currentNode);
            selectAndExpand(nodes);
            Node<Board> lastNode = nodes.get(nodes.size() - 1);
            FieldValue winner = simulate(lastNode);
            getNodes(lastNode.color).put(lastNode.item, lastNode);
            double lambda = 0.5;
            double heuristic = Basic.getValue(lastNode.item, FieldValue.WHITE);
            double whiteScore = (winner == FieldValue.WHITE ? 1 : 0) * (1-lambda) + (heuristic > 0 ? 1 : 0) * lambda;
            double blackScore = (winner == FieldValue.BLACK ? 1 : 0) * (1-lambda) + (heuristic > 0 ? 0 : 1) * lambda;
            update(nodes, whiteScore, blackScore);
        }
        System.out.println(simulationCount);
        currentBoard = null;
        simulationCount = 0;
        return getBestMove(currentNode);
    }

    private HashMap<Board, Node<Board>> getNodes(FieldValue color) {
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
        if(children.size() == 0) {
            child = new Node<Board>(parent.item, opponentColor);
            if(getNodes(child.color).containsKey(child.item)) {
                child = getNodes(child.color).get(child.item);
            } else {
                ancestors.add(child);
                return;
            }
        } else {
            double logTotalPlays = children.stream().mapToDouble(c -> c.plays).reduce(0.0, (i, c) -> i + c);
            child = children.get(0);
            double oldScore = Double.NEGATIVE_INFINITY;
            for(Node<Board> node : children) {
                double T = node.plays;
                double X = node.score / (double)node.plays;
                double S = X * X;
                double V = S - X + Math.sqrt((2 * logTotalPlays) / T);
                double score    = X + Math.sqrt((logTotalPlays / T) * Math.min(0.25, V));
                if(child == null || score > oldScore) {
                    oldScore = score;
                    child = node;
                }
            }
        }
        ancestors.add(child);
        selectAndExpand(ancestors);
    }

    private FieldValue simulate(Node<Board> node) {
        Board board = node.item;
        FieldValue color = node.color;
        while(!board.isFinished()) {
            List<Position> positions = board.getPossiblePositions(color);
            if(positions.size() > 0) {
                Position randomPosition = positions.get(ThreadLocalRandom.current().nextInt(positions.size()));
                board = board.makeMove(new Move(randomPosition, color));
            }
            color = color.getOpponent();
        }
        simulationCount++;
        return board.getWinner();
    }

    private void update(List<Node<Board>> nodes, double whiteScore, double blackScore) {
        for(Node<Board> node : nodes) {
            node.plays++;
            if(node.color == FieldValue.WHITE) {
                node.score += blackScore;
            } else {
                node.score += whiteScore;
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
            double newScore = newNode.score/ (double)newNode.plays;
            System.out.println(newScore + " " + newNode.score + " / " + newNode.plays);
            if(move == null || newScore >= score) {
                score = newScore;
                move = newMove;
            }
        }

        return move;
    }

    class Node<T> {
        public int score = 0;
        public int plays = 0;
        public T item = null;
        public FieldValue color = null;

        public Node(T item, FieldValue color) {
            this.item = item;
            this.color = color;
        }
    }
}
