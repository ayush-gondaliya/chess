package pieces;

import main.GamePanel;

public class Knight extends Piece {
    
    public Knight(int color, int col, int row){
        super(color,col,row);

        if(color == GamePanel.WHITE){
            image = getImage("/pieces/white-knight");
        }
        else{
            image = getImage("/pieces/black-knight");
        }
    } 
}
