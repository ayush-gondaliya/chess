package main;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 880;
    public static final int HEIGHT = 640;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    // BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        //test();
        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {

        // WHITE Team
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new King(WHITE, 4, 7));
        pieces.add(new Queen(WHITE, 3, 7));

        // BLACK Team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new King(BLACK, 4, 0));
        pieces.add(new Queen(BLACK, 3, 0));

    }

    public void test() {
        pieces.add(new Pawn(WHITE, 7, 5));
        pieces.add(new King(WHITE, 3, 6));
        pieces.add(new King(BLACK, 0, 3));
        pieces.add(new Bishop(BLACK, 1, 4));
        pieces.add(new Queen(BLACK, 3, 4));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {

        if (promotion) {
            promoting();
        } else if (gameover == false) {
            if (mouse.pressed) {
                if (activeP == null) {
                    for (Piece piece : simPieces) {
                        if (piece.color == currentColor &&
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                piece.row == mouse.y / Board.SQUARE_SIZE) {
                            activeP = piece;
                        }
                    }
                } else {
                    simulate();
                }
            }
            if (mouse.pressed == false) {

                if (activeP != null) {

                    if (validSquare) { // if validSquare=true then position will be updated
                        // Move confirmed

                        // Update the piece list if a piece has been captured or removed during
                        // stimulation
                        copyPieces(simPieces, pieces); // simpiece as source and piece as target
                        activeP.updatePosition(); // so copying simpiece to piece
                        if (castlingP != null) {
                            castlingP.updatePosition();
                        }

                        if (isKingInCheck() && isCheckmate()) {
                            gameover = true;
                        } else {
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }
                    } else {
                        // The move is not valid therefor resetting everything
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }
                }
            }
        }
    }

    private void simulate() {

        canMove = false;
        validSquare = false;

        // Reset the piece list in every lop
        // for restoring the removed pieces during simulation
        copyPieces(pieces, simPieces);

        // reset castling move
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // Check if the piece is hovering over a rechable squaree
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            // if you capture a piece it will be removed
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex()); // removes hittingP from list of simPiece
            }

            checkCastling();

            if (isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
                validSquare = true;
            }
        }
    }

    private boolean isIllegal(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing() {
        Piece king = getKing(false); // for getting current color king not opponent
        for (Piece piece : simPieces) { // checking list if there is a piece that can move to
            if (piece.color != king.color && piece.canMove(king.col, king.row)) { // king's square
                return true;
            }
        }
        return false;
    }

    private boolean isKingInCheck() {

        Piece king = getKing(true);

        if (activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            return true;
        } else {
            checkingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isCheckmate() {

        Piece king = getKing(true);
        if (kingCanMove(king)) {
            return false;
        } else {
            // check to block the attack
            int colDiff = Math.abs(checkingP.col = king.col);
            int rowDiff = Math.abs(checkingP.row = king.row);

            if (colDiff == 0) {
                // vertical
                if (checkingP.row < king.row) {
                    // above king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.col != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // below king
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.col != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                // horizontal
                if (checkingP.col < king.col) {
                    // left
                    for (int col = checkingP.col; col < king.col; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.col != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingP.col > king.col) {
                    // right
                    for (int col = checkingP.col; col > king.col; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.col != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                // diagonally
                if (checkingP.row < king.row) {
                    // above king
                    if (checkingP.col < king.col) {
                        // upper left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king & piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // upper right
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king & piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (checkingP.row > king.row) {
                    // below king
                    if (checkingP.col < king.col) {
                        // lower left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king & piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingP.col > king.col) {
                        // lower right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king & piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            } else {
                // knight

            }
        }
        return true;
    }

    private boolean kingCanMove(Piece king) {

        // any square where the king can move to
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }

        return false;
    }

    private boolean isValidMove(Piece king, int colPlus, int rowPlus) {

        boolean isValidMove = false;

        king.col += colPlus;
        king.row += rowPlus;
        if (king.canMove(king.col, king.row)) {
            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (isIllegal(king) == false) {
                isValidMove = true;
            }
        }
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;

            // Reset Black's twoStepped status
            for (Piece piece : pieces) { // if we're switching to black then we scan the list
                if (piece.color == BLACK) { // and find black pieces
                    piece.twoStepped = false; // and disable all black pieces' twostepped movement
                }
            }
        } else {
            currentColor = WHITE;
            // Reset White's twoStepped status
            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }

    private boolean canPromote() {
        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 ||
                    currentColor == BLACK && activeP.row == 7) {
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void promoting() {
        if (mouse.pressed) {
            for (Piece piece : promoPieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case ROOK:
                            simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                            break;
                        default:
                            break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // BOARD
        board.draw(g2);

        // PIECES
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        if (activeP != null) {
            if (canMove) { // only highlight white background if we select rechable position for particular

                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.gray);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.7));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                } else {
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.7));
                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                }
            }

            activeP.draw(g2);
        }
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 30));
        g2.setColor(Color.WHITE);

        if (promotion) {
            g2.drawString("Promote To", 680, 150);
            for (Piece piece : promoPieces) {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);

            }
        }

        if (currentColor == WHITE) {
            g2.drawString("White's turn", 680, 550);
            if (checkingP != null && checkingP.color == BLACK) {
                g2.setColor(Color.red);
                g2.drawString("The King", 660, 300);
                g2.drawString("is in Check", 660, 350);
            }
        } else {
            g2.drawString("Black's turn", 680, 550);
            if (checkingP != null && checkingP.color == WHITE) {
                g2.setColor(Color.red);
                g2.drawString("The King", 660, 300);
                g2.drawString("is in Check", 660, 350);
            }
        }

        if (gameover) {
            String a = "";
            if (currentColor == WHITE) {
                a = "White Wins";
            } else {
                a = "Black Wins";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 80));
            g2.setColor(Color.GREEN);
            g2.drawString(a, 250, 350);
        }
    }
}
