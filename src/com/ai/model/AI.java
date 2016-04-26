package com.ai.model;

import java.util.ArrayList;
import java.util.TreeSet;

public class AI {
    static final int[][] central = new int[8][8];

    private int ply;
    TreeSet<Node> tree;

    public AI(int difficulty) {
        setPly(difficulty);
        tree = new TreeSet<Node>();
        for (int i = 0; i < 4; i++) {
            for (int j = i; j < 8 - i; j++) {
                for (int j2 = i; j2 < 8 - i; j2++) {
                    central[j][j2] = i;
                }
            }
        }
    }

    public AI(Board b) {
        tree = new TreeSet<Node>();
        for (int i = 0; i < 4; i++) {
            for (int j = i; j < 8 - i; j++) {
                for (int j2 = i; j2 < 8 - i; j2++) {
                    central[j][j2] = i;
                }
            }
        }
    }

    private ArrayList<Board> evaluateAllPossibleMoves(Board board) {
        int oldTurn = board.getTurn();
        board.setTurn(1);
        ArrayList<Board> childTemp = new ArrayList<Board>();
        for (int i = 0; i < board.getBoard().length; i++) {
            for (int j = 0; j < board.getBoard().length; j++) {
                if (board.getBoard()[i][j] == 'w') {
                    ArrayList<Point> moves = board.getPossibleMoves(new Point(
                            j, i));
                    for (int k = 0; k < moves.size(); k++) {
                        Board temp = new Board(board);
                        temp.move(new Point(j, i), moves.get(k));
                        childTemp.add(temp);
                    }
                }
            }
        }
        board.setTurn(oldTurn);
        return childTemp;
    }

    public Point[] alphaBeta(Board board) {
        Point[] theMove = new Point[2];
        Board n = new Board(board);
        alphaValue(n, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ArrayList<Point> change = new ArrayList<Point>();
        while (true) {
            for (int i = 0; i < tree.last().board.getBoard().length; i++) {
                for (int j = 0; j < tree.last().board.getBoard().length; j++) {
                    if (tree.last().board.getBoard()[i][j] != board.getBoard()[i][j]) {
                        change.add(new Point(j, i));
                    }
                }
            }
            for (int i = 0; i < change.size(); i++) {
                ArrayList<Point> compare = board
                        .getPossibleMoves(change.get(i));
                for (int j = 0; j < compare.size(); j++) {
                    if (change.contains(compare.get(j))) {
                        theMove[0] = change.get(i);
                        theMove[1] = compare.get(j);
                        return theMove;
                    }
                }
            }
            // System.out.println("badAlgo");
            tree.pollLast();
        }
    }

    // for (int i = 0; i < change.size(); i++) {
    // System.out.print(change.get(i));
    // ArrayList<Point> compare = board.getPossibleMoves(change.get(i));
    // for (int j = 0; j < compare.size(); j++) {
    // System.out.print("->" + compare.get(j));
    // }
    // System.out.println();
    // }
    // System.out.println(tree.last().board);
    // System.out.println(n);

    int alphaValue(Board n, int depth, int alpha, int beta) {
        ArrayList<Board> child = evaluateAllPossibleMoves(n);
        if (n.isGameOver() | depth >= getPly()) {
            int value = value(n);
            tree.add(new Node(n, value));
            return value;
        }
        int v = Integer.MIN_VALUE;
        depth++;
        for (Board x : child) {
            v = maximum(v, betaValue(x, depth, alpha, beta));
            if (v >= beta) {
                tree.add(new Node(n, v));
                return v;
            }
            alpha = maximum(v, alpha);
        }
        return v;
    }

    int betaValue(Board n, int depth, int alpha, int beta) {

        ArrayList<Board> child = evaluateAllPossibleMoves(n);
        if (n.isGameOver() | depth >= getPly()) {
            int value = value(n);
            tree.add(new Node(n, value));
            return value;
        }
        int v = Integer.MAX_VALUE;
        depth++;
        for (Board cs : child) {
            v = minimum(v, alphaValue(cs, depth, alpha, beta));
            if (v <= alpha) {
                tree.add(new Node(n, v));
                return v;
            }
            beta = minimum(v, beta);
        }
        return v;
    }

    private int value(Board n) {
        int comp = 0;
        int player = 0;
        for (int i = 0; i < n.getBoard().length; i++) {
            for (int j = 0; j < n.getBoard().length; j++) {
                if (n.getBoard()[i][j] == 'w')
                    comp += central[i][j];
                else if (n.getBoard()[i][j] == 'b')
                    player += central[i][j];
            }
        }
        int value = comp - player;
        int compGr = connectedGroups(n.getBoard(), 'w');
        int playerGr = connectedGroups(n.getBoard(), 'b');
        if (compGr == 1)
            return Integer.MAX_VALUE;
        if (playerGr == 1)
            return Integer.MIN_VALUE;
        int quadVal = connectedGroups(n.getBoard(), 'b')
                - connectedGroups(n.getBoard(), 'w');
        value = value + quadVal;
        return value;
    }

    private int minimum(int min, int maxValue) {
        // TODO Auto-generated method stub
        if (min < maxValue)
            return min;
        else
            return maxValue;
    }

    private int maximum(int max, int minValue) {
        // TODO Auto-generated method stub
        if (max > minValue)
            return max;
        else
            return minValue;
    }

    int quad(char[][] b, char search) {
        char[][] board = b;
        double sum = 0;
        ArrayList<quads> quads = quadForm();
        for (int i = 0; i < board.length - 1; i++) {
            for (int j = 0; j < board.length - 1; j++) {
                char[][] compare = new char[2][2];
                compare[0][0] = board[i][j];
                compare[0][1] = board[i][j + 1];
                compare[1][0] = board[i + 1][j];
                compare[1][1] = board[i + 1][j + 1];
                // for (int k = 0; k < compare.length; k++) {
                // for (int k2 = 0; k2 < compare.length; k2++) {
                // System.out.print(compare[k][k2] + " ");
                // }
                // System.out.println();
                // }
                // System.out.println();
                for (quads q : quads) {
                    if (quadEquals(compare, q.quad, search)) {
                        sum = sum + q.value;
                    }
                }
            }
        }
        return (int) (sum / 4);
    }

    private ArrayList<quads> quadForm() {
        // TODO Auto-generated method stub
        ArrayList<quads> quads = new ArrayList<quads>();
        char[][] quad = new char[2][2];
        quad[0][0] = 'x';
        quads q = new quads(.25d, quad);
        quads.add(q);
        quad = new char[2][2];
        quad[0][0] = 'x';
        quad[0][1] = 'x';
        quad[1][1] = 'x';
        quads.add(new quads(-.25d, quad));
        quad = new char[2][2];
        quad[0][0] = 'x';
        quad[1][1] = 'x';
        quads.add(new quads(-.5d, quad));
        quad = new char[2][2];
        quad[0][1] = 'x';
        quad[1][0] = 'x';
        quads.add(new quads(-.5d, quad));
        return quads;
    }

    boolean quadEquals(char[][] board, char[][] quad, char search) {
        for (int i = 0; i < quad.length; i++) {
            for (int j = 0; j < quad.length; j++) {
                if (quad[i][j] == 0 && board[i][j] == search)
                    return false;
            }
        }
        return true;
    }

    class quads {
        double value;
        char[][] quad;

        quads(double value, char[][] quad) {
            this.value = value;
            this.quad = quad;
        }
    }

    int connectedGroups(char[][] board, char search) {
        ArrayList<Point> visited = new ArrayList<Point>();
        int groups = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (!visited.contains(new Point(i, j)) && board[i][j] == search) {
                    dfs(i, j, visited, search, board, 8);
                    groups++;
                }
            }
        }
        return groups;
    }

    private void dfs(int row, int col, ArrayList<Point> list, char search,
            char[][] board, int DIM) {
        Point temp = new Point(row, col);
        if (board[row][col] != search)
            return;
        list.add(temp);

        if ((col + 1) < DIM && board[row][col + 1] == search
                && !list.contains(new Point(row, col + 1))) {
            dfs(row, col + 1, list, search, board, DIM);
        }
        if (col - 1 >= 0 && board[row][col - 1] == search
                && !list.contains(new Point(row, col - 1))) {
            dfs(row, col - 1, list, search, board, DIM);
        }
        if (row + 1 < DIM && board[row + 1][col] == search
                && !list.contains(new Point(row + 1, col))) {
            dfs(row + 1, col, list, search, board, DIM);
        }
        if (row - 1 >= 0 && board[row - 1][col] == search
                && !list.contains(new Point(row - 1, col))) {
            dfs(row - 1, col, list, search, board, DIM);
        }
        if (row + 1 < DIM && col + 1 < DIM && board[row + 1][col + 1] == search
                && !list.contains(new Point(row + 1, col + 1))) {
            dfs(row + 1, col + 1, list, search, board, DIM);
        }
        if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1] == search
                && !list.contains(new Point(row - 1, col - 1))) {
            dfs(row - 1, col - 1, list, search, board, DIM);
        }
        if (row + 1 < DIM && col - 1 >= 0 && board[row + 1][col - 1] == search
                && !list.contains(new Point(row + 1, col - 1))) {
            dfs(row + 1, col - 1, list, search, board, DIM);
        }
        if (row - 1 >= 0 && col + 1 < DIM && board[row - 1][col + 1] == search
                && !list.contains(new Point(row - 1, col + 1))) {
            dfs(row - 1, col + 1, list, search, board, DIM);
        }
    }

    public static void main(String[] args) {
        Board b = new Board();
        b.clear();
        AI a = new AI(2);
        char[][] board = b.getBoard();
        board[0][3] = 'b';
        board[0][6] = 'b';
        board[1][3] = 'b';
        board[2][0] = 'w';
        board[2][2] = 'w';
        board[2][3] = 'w';
        board[2][5] = 'w';
        board[3][0] = 'w';
        board[4][2] = 'w';
        board[4][3] = 'w';
        board[4][4] = 'w';
        board[4][5] = 'b';
        board[5][0] = 'w';
        board[5][3] = 'b';
        board[5][4] = 'w';
        board[5][5] = 'w';
        board[6][5] = 'w';
        board[7][5] = 'b';
        board[7][4] = 'b';
        // board[0][2] = 'w';
        // board[1][2] = 'b';
        // board[2][0] = 'w';
        // board[2][2] = 'w';
        // board[2][3] = 'b';
        // board[2][4] = 'b';
        // board[2][5] = 'w';
        // board[3][0] = 'b';
        // board[3][3] = 'b';
        // board[3][4] = 'w';
        // board[3][5] = 'b';
        // board[4][2] = 'w';
        // board[4][3] = 'b';
        // board[4][4] = 'b';
        // board[5][0] = 'b';
        // board[5][3] = 'w';
        // board[5][4] = 'w';
        b.setBoard(board);
        System.out.println(b);
        System.out.println(a.value(b));
        // a.connectedGroups(b.getBoard(), 'b');
    }

    public int getPly() {
        return ply;
    }

    public void setPly(int ply) {
        this.ply = ply;
    }
}