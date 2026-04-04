package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;

public class Piece {
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;


    public Piece(int color, int col, int row){
        this.color = color;
        this.col = col;
        this.row = row;

        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }
    public BufferedImage getImage(String imagePath){
        BufferedImage image = null;

        try{
            String[] pathsTry = {
                "chess/chess/res" + imagePath + ".png",
                "chess/res" + imagePath + ".png",
                "res" + imagePath + ".png",
                new java.io.File("").getAbsolutePath() + "\\chess\\chess\\res" + imagePath + ".png",
                new java.io.File("").getAbsolutePath() + "\\chess\\res" + imagePath + ".png"
            };
            
            java.io.File file = null;
            for (String path : pathsTry) {
                file = new java.io.File(path);
                if (file.exists()) {
                    break;
                }
            }
            
            if (file != null && file.exists()) {
                image = ImageIO.read(file);
            } else {
                System.err.println("Could not find image at any of these paths:");
                for (String path : pathsTry) {
                    System.err.println("  " + new java.io.File(path).getAbsolutePath());
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }   
    public int getX(int col){
    return col*Board.SQUARE_SIZE;
    }
    public int getY(int row){
    return row*Board.SQUARE_SIZE;
    }
    public int getCol(int x){
        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }
    public int getRow(int y){
        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }
    public int getIndex(){
        for(int index = 0; index<GamePanel.simPieces.size(); index++){ // scanning simPieces list
            if(GamePanel.simPieces.get(index) == this){  //if its same piece as 'this' then returns index
                return index;
            }
        }
        return 0;
    }
    public void updatePosition(){
        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
    }
    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }
    public boolean canMove(int targetCol, int targetRow){
        return false;
    }
    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7){
            return true;
        }
        return false;
    }
    public Piece getHittingP(int targetCol, int targetRow){  //recieve targetcol&row then scans simpiece list and sees if there's a list with exactly same row and col except active piece (this)
        for(Piece piece : GamePanel.simPieces){
            if(piece.col == targetCol && piece.row == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetCol, int targetRow){
        
        hittingP = getHittingP(targetCol, targetRow);
        if(hittingP == null){ // VACANT square
            return true;
        }
        else{    // OCCUPIED square
            if(hittingP.color != this.color){ //if color is diff then it can be captured
                return true;
            }
            else{   // can't move if same color
                hittingP = null;
            }
        }
        return false;
    }
    public void draw(Graphics2D g2){
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}


    
