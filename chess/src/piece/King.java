package piece;

import main.GamePanel;

public class King extends Piece {
   
    public King(int color, int col, int row){
        super(color,col,row);

        if(color == GamePanel.WHITE){
            image = getImage("/pieces/white-king");
        }
        else{
            image = getImage("/pieces/black-king");
        }
    }

    public boolean canMove(int targetCol, int targetRow){

        if(isWithinBoard(targetCol, targetRow)){ //for up,down,left and right|               // for diagonal squares 
            if(Math.abs(targetCol-preCol) + Math.abs(targetRow-preRow) == 1 || Math.abs(targetCol-preCol) * Math.abs(targetRow-preRow) == 1){    //Math.abs gives a result as absolute number
                //gives diff bw two no. (targetCol-preCol)
                if(isValidSquare(targetCol, targetRow)){ //if king can reach the square and it is valid then it can move
                    return true;
                }
            }
        }

        return false;
    }
    }      
