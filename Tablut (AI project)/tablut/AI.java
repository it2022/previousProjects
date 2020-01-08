package tablut;


import java.util.ArrayList;

/**
 * A Player that automatically generates moves.
 *
 * @author IanTien
 */
class AI extends Player {

    /**
     * A position-score magnitude indicating a win (for white if positive,
     * black if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;
    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private static int maxDepth(Board board) {
        if (board.moveCount() <= 5) {
            return 4;
        } else {
            return 4;
        }
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move mv = findMove();
        _controller.reportMove(mv);
        return mv.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        int sense = (b.turn() == Piece.WHITE ? 1 : -1);
        findMove(b, maxDepth(b), true, sense,
                -1 * WINNING_VALUE, WINNING_VALUE);
        return _lastFoundMove;
    }

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     *
     * @author wikipedia
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        if (sense == 1) {
            int value = -1 * WINNING_VALUE;
            ArrayList<Move> moves =
                    (ArrayList<Move>) board.legalMoves(Piece.WHITE);
            for (int ind = moves.size() - 1; ind >= 1; ind--) {
                int j = _controller.randInt(ind + 1);
                Move tmp = moves.get(j);
                moves.set(j, moves.get(ind)); moves.set(ind, tmp);
            }
            if (moves.size() > 0) {
                Move mv = moves.get(0);
                for (int i = 0; i < moves.size(); i++) {
                    mv = moves.get(i); board.makeMove(mv);
                    value = Math.max(value, findMove(board, depth - 1,
                            false, sense * -1, alpha, beta));
                    board.undo(); alpha = Math.max(alpha, value);
                    if (alpha >= beta) {
                        break;
                    }
                }
                if (saveMove) {
                    _lastFoundMove = mv;
                }
            }
            return value;
        } else if (sense == -1) {
            int value = WINNING_VALUE;
            ArrayList<Move> moves =
                    (ArrayList<Move>) board.legalMoves(Piece.BLACK);
            for (int ind = moves.size() - 1; ind >= 1; ind--) {
                int j = _controller.randInt(ind + 1);
                Move tmp = moves.get(j);
                moves.set(j, moves.get(ind));
                moves.set(ind, tmp);
            }
            if (moves.size() > 0) {
                Move mv = moves.get(0);
                for (int i = 0; i < moves.size(); i++) {
                    mv = moves.get(i); board.makeMove(mv);
                    value = Math.min(value, findMove(board, depth - 1,
                            false,
                            sense * -1, alpha, beta));
                    board.undo(); beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
                if (saveMove) {
                    _lastFoundMove = mv;
                }
            }
            return value;
        } else {
            return 0;
        }
    }
    /**
     * helps return a heuristic value for BOARD.
     */
    private static final int MAGICNUM = 60;
    /**
     * helps return a heuristic value for BOARD.
     */
    private static final float MAGICNUM1 = 0.75f;
    /**
     * Return a heuristic value for BOARD.
     */
    public static int staticScore(Board board) {
        if (board.winner() == Piece.BLACK) {
            return -1 * WINNING_VALUE;
        } else if (board.winner() == Piece.WHITE) {
            return WINNING_VALUE;
        }
        Square kingSq = board.kingPosition();
        double minInWay = 100f;
        for (int i = 0; i < 4; i++) {
            Square s;
            int steps = 1;
            double inWay = 0;
            while (true) {
                s = kingSq.rookMove(i, steps);
                if (s == null || board.get(s).side() != Piece.EMPTY) {
                    inWay = Math.min(inWay, minInWay);
                    break;
                }
                if (s.row() == 8 || s.row() == 0
                        || s.col() == 8 || s.col() == 0) {
                    return WINNING_VALUE - (MAGICNUM);
                }
                if (board.get(s).side() == Piece.BLACK) {
                    inWay++;
                }
                steps++;
            }
            inWay = Math.min(inWay, minInWay);
        }
        double wallDist = Math.min(Math.min(kingSq.col(),
                8 - kingSq.col()), Math.min(kingSq.row(), 8 - kingSq.row()));
        double wallDistMul = MAGICNUM1;
        double kscore = wallDist * wallDistMul + minInWay;
        double ceil = 1000.0;
        kscore = kscore / ceil;
        int[][] tups = {{0, 2}, {2, 0}, {1, 3}, {3, 1}};
        for (int ti = 0; ti < 4; ti++) {
            int d1 = tups[ti][0];
            int d2 = tups[ti][1];
            if (board.get(kingSq.rookMove(d1, 1)) == Piece.BLACK) {
                for (int d = 0; d < 4; d++) {
                    int steps = 1;
                    while (true) {
                        Square s = kingSq.rookMove(d2, 1).rookMove(d, steps);
                        if (s == null) {
                            break;
                        }
                        if (board.get(s).side() == Piece.BLACK) {
                            return -1 * WINNING_VALUE + MAGICNUM;
                        } else if (board.get(s).side() == Piece.WHITE) {
                            break;
                        }
                        steps++;
                    }
                }
            }
        }
        return (int) (kscore * ((double) WINNING_VALUE));
    }

}
