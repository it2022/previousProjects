package tablut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.List;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;

import static tablut.Piece.*;
import static tablut.Square.*;


/**
 * The state of a Tablut Game.
 *
 * @author IanTien
 */
class Board {
    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 9;
    /**
     * The throne (or castle) square and its four surrounding squares..
     */
    static final Square THRONE = sq(4, 4),
            NTHRONE = sq(4, 5),
            STHRONE = sq(4, 3),
            WTHRONE = sq(3, 4),
            ETHRONE = sq(5, 4);
    /**
     * Initial positions of attackers.
     */
    static final Square[] INITIAL_ATTACKERS = {
            sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
            sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
            sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
            sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };
    /**
     * Initial positions of defenders of the king.
     */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2),
        sq(2, 4), sq(6, 4)
    };
    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     */
    private final ArrayList<Square> throneAndorthogonalSquares =
            new ArrayList<Square>(Arrays.asList(sq(4, 4), sq(4, 5),
                    sq(4, 3), sq(3, 4), sq(5, 4)));
    /**
     * _lim.
     **/
    protected int _lim;
    /**
     * _lim.
     **/
    protected final int _inft = 999999;
    /**
     * gameBoard.
     **/
    private Piece[][] gameBoard = new Piece[9][9];
    /**
     * Stack for save states.
     **/
    private Stack<String> gameBoardStates = new Stack<String>();
    /**
     * ArrayList for save states.
     **/
    private ArrayList<String> gameBoardStates1 = new ArrayList<>();
    /**
     * True when current board is a repeated position (ending the game).
     */
    private Square _kingPosition;
    /**
     * Piece whose turn it is (WHITE or BLACK).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or null if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * Number of (still undone) moves since initial position.
     */
    private int _moveCount;
    /**
     * True when current board is a repeated position (ending the game).
     */
    private boolean _repeated;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }

        _winner = model._winner;
        _moveCount = model._moveCount;
        _turn = model.turn();
        _kingPosition = model.kingPosition();
        _lim = _inft;
        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < 9; x++) {
                gameBoard[i][x] = EMPTY;
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < 9; x++) {
                this.gameBoard[i][x] = model.gameBoard[i][x];
            }
        }
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _winner = null;
        _moveCount = 0;
        _turn = BLACK;
        _kingPosition = THRONE;
        _lim = _inft;
        clearUndo();
        for (int i = 0; i < 9; i++) {
            for (int x = 0; x < 9; x++) {
                gameBoard[i][x] = EMPTY;
            }
        }
        gameBoard[THRONE.col()][THRONE.row()] = KING;
        for (Square s : INITIAL_DEFENDERS) {
            gameBoard[s.col()][s.row()] = WHITE;

        }
        for (Square s : INITIAL_ATTACKERS) {
            gameBoard[s.col()][s.row()] = BLACK;

        }
        gameBoardStates1.add(encodedBoard().substring(1));
        gameBoardStates.push(encodedBoard());
    }

    /**
     * Set the move _limit to _lim.  It is an error if 2*_lim <= moveCount().
     * @param n n
     */
    void setMoveLimit(int n) {
        if (2 * _lim <= moveCount()) {
            throw new IllegalArgumentException("MOVES Exceeded");
        }
        _lim = n * 2;
    }

    /**
     * Return a Piece representing whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the winner in the current position, or null if there is no winner
     * yet.
     */
    Piece winner() {
        return _winner;
    }

    /**
     * Returns true iff this is a win due to a repeated position.
     */
    boolean repeatedPosition() {
        return _repeated;
    }

    /**
     * Record current position and set winner() next mover if the current
     * position is a repeat.
     */
    private void checkRepeated() {
        String n = encodedBoard().substring(1);
        if (gameBoardStates1.contains(n)) {
            _repeated = true;
            _winner = _turn.opponent();
        } else {
            gameBoardStates1.add(n);
        }
    }

    /**
     * Return the number of moves since the initial position that have not been
     * undone.
     */
    int moveCount() {
        return _moveCount;
    }
    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     */
    void addMoveCount() {
        _moveCount += 1;
    }

    /**
     * Return location of the king.
     */
    Square kingPosition() {
        return _kingPosition;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return gameBoard[col][row];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        gameBoard[s.col()][s.row()] = p;
        if (p == KING) {
            _kingPosition = s;
        }
    }

    /**
     * Set square S to P and record for undoing.
     */
    final void revPut(Piece p, Square s) {
        gameBoardStates.push(encodedBoard());
        put(p, s);
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /**
     * Return true iff FROM - TO is an unblocked rook move on the current
     * board.  For this to be true, FROM-TO must be a rook move and the
     * squares along it, other than FROM, must be empty.
     */
    boolean isUnblockedMove(Square from, Square to) {
        int dir = from.direction(to);

        SqList n = ROOK_SQUARES[from.index()][dir];

        for (Square i : n) {
            if (i == to && get(to) == EMPTY) {
                break;
            } else if (gameBoard[i.col()][i.row()] != EMPTY) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        if (get(from) == KING && _turn == WHITE) {
            return true;
        }
        return get(from).side() == _turn;
    }

    /**
     * Return true iff FROM-TO is a valid move.
     */
    boolean isLegal(Square from, Square to) {
        if (isLegal(from) && isUnblockedMove(from, to)) {
            return get(from) == KING || to != THRONE;
        }
        return false;
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }
    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     * @param sq0 q
     * @param sq2 a
     * @return g
     */
    private boolean captureHelper(Square sq0, Square sq2) {
        Square r = sq0.between(sq2);
        if (get(r) != KING) {
            return hostile(sq0, get(r)) && hostile(sq2, get(r));
        } else {
            if (throneAndorthogonalSquares.contains(kingPosition())
                    && hostile(kingPosition().rookMove(0, 1), WHITE)
                    && hostile(kingPosition().rookMove(1, 1), WHITE)
                    && hostile(kingPosition().rookMove(2, 1), WHITE)
                    && hostile(kingPosition().rookMove(3, 1), WHITE)) {
                return true;
            } else if (!throneAndorthogonalSquares.contains(kingPosition())) {
                return hostile(sq0, get(r)) && hostile(sq2, get(r));
            }
        }
        return false;
    }

    /**
     * Move FROM-TO, assuming this is a legal move.
     */
    void makeMove(Square from, Square to) {
        Piece temp = gameBoard[from.col()][from.row()];
        revPut(temp, to);
        gameBoard[from.col()][from.row()] = EMPTY;
        _moveCount += 1;
        HashSet<Square> t = pieceLocations(get(to));
        for (int i = 0; i < 4; i++) {
            Square test = to.rookMove(i, 2);
            if ((t.contains(test) || test == THRONE)
                    && get(to.between(test)) != EMPTY
                    && captureHelper(to, test)) {
                capture(to, test);
            }
        }
        checkRepeated();
        checkForWinner();
        _turn = temp.opponent();
    }
    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     */
    void checkForWinner() {
        if (kingPosition().row() == 0 || kingPosition().row() == 8
                || kingPosition().col() == 0 || kingPosition().col() == 8) {
            _winner = WHITE;
        }
        if (!hasMove(_turn.opponent())) {
            _winner = _turn;
        }
        if (moveCount() >= _lim) {
            _winner = _turn;
        }
        if (get(kingPosition()) == EMPTY) {
            _winner = BLACK;
        }

    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }
    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     * @param s s
     * @param p p
     * @return t
     */
    private boolean hostile(Square s, Piece p) {
        int occupiedHostile = 0;
        for (int i = 0; i < 4; i++) {
            Square test = s.rookMove(i, 1);
            if (get(s) == KING && THRONE == _kingPosition
                    && get(test).side() == BLACK) {
                occupiedHostile += 1;
            }
        }
        if (occupiedHostile == 3) {
            return true;
        } else {
            return (s == THRONE && THRONE != kingPosition())
                    || get(s).side() == p.opponent();
        }
    }

    /**
     * Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     * SQ0 and the necessary conditions are satisfied.
     */
    private void capture(Square sq0, Square sq2) {
        Square sq1 = sq0.between(sq2);
        put(EMPTY, sq1);

    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        gameBoard = new Piece[SIZE][SIZE];
        String prev = gameBoardStates.pop();
        gameBoardStates1.remove(gameBoardStates1.size() - 1);
        _moveCount -= 1;
        _turn = symbolAcquirer(Character.toString(prev.charAt(0)));
        for (int i = 0; i < prev.length() - 1; i++) {
            String symbol = Character.toString(prev.charAt(i + 1));
            if (symbol.equals("K")) {
                _kingPosition = sq(i % SIZE, i / SIZE);
            }
            put(symbolAcquirer(symbol), sq(i % SIZE, i / SIZE));
        }
        _winner = null;
    }
    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     * @param symbol symbol
     * @return n
     */
    Piece symbolAcquirer(String symbol) {
        if (symbol.equals("W")) {
            return WHITE;
        }
        if (symbol.equals("B")) {
            return BLACK;
        }
        if (symbol.equals("K")) {
            return KING;
        }
        return EMPTY;
    }

    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     */
    private void undoPosition() {
        if (!_repeated || _moveCount > 0) {
            gameBoardStates.pop();
        }
        _repeated = false;
    }

    /**
     * Clear the undo stack and board-position counts. Does not modify the
     * current position or win status.
     */
    void clearUndo() {
        gameBoardStates.clear();
        gameBoardStates1.clear();
    }

    /**
     * Return a new mutable list of all legal moves on the current board for
     * SIDE (ignoring whose turn it is at the moment).
     */
    List<Move> legalMoves(Piece side) {
        List<Move> moves = new ArrayList<Move>();
        Iterator iter = pieceLocations(side).iterator();
        while (iter.hasNext()) {
            Square sq = (Square) iter.next();
            Piece piece = gameBoard[sq.col()][sq.row()];
            if (piece.side() == side.side()) {
                for (int i = 0; i < 4; i++) {
                    int dist = 1;
                    while (true) {
                        Square rookSquare = sq.rookMove(i, dist);
                        if (rookSquare == null
                                || get(rookSquare).side() != EMPTY) {
                            break;
                        }
                        if (get(sq) != KING && rookSquare == THRONE
                                || !isUnblockedMove(sq, rookSquare)) {
                            break;
                        }
                        moves.add(Move.mv(sq, rookSquare));
                        dist++;
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Return true iff SIDE has a legal move.
     */
    boolean hasMove(Piece side) {
        return legalMoves(side).size() > 0;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Return a text representation of this Board.  If COORDINATES, then row
     * and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /**
     * Return the locations of all pieces on SIDE.
     */
    private HashSet<Square> pieceLocations(Piece side) {
        HashSet<Square> locationSides = new HashSet<>();
        assert side != EMPTY;
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                if (gameBoard[i][k].side() == side.side()) {
                    locationSides.add(sq(i, k));
                }
            }
        }
        return locationSides;
    }

    /**
     * Return the contents of _board in the order of SQUARE_LIST as a sequence
     * of characters: the toString values of the current turn and Pieces.
     */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

}
