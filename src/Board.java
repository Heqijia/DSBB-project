




import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Board extends JPanel{

    public Board(int width, int height, int row,int col,int[] board) {
        HashMap<Integer, Integer> gridType = Solver.gridType;
        setSize(width, height);
        setLayout(new GridLayout(row, col));
        for (int i = 0; i < board.length; i++) {
            if(board[i]==0){
                add(new JButton("" + board[i])).setBackground(Color.white);
            }

            else {
                if (gridType.get(board[i])==11) {
                    add(new JButton("" + board[i])).setBackground(new Color(173, 216, 230));
                }
                else {
                    add(new JButton("" + board[i])).setBackground(new Color(255, 255, 102));
                }
            }
        }

    }


}

//import javax.swing.*;
//import java.awt.*;
//import java.util.HashMap;
//
//public class Board extends JPanel {
//
//    public Board(int width, int height, int row, int col, HashMap<Integer, Integer> gridType) {
//        setSize(width, height);
//        setLayout(new GridLayout(row, col));
//        for (int i = 0; i < row * col; i++) {
//            if (gridType.get(i) == 0) {
//                add(new JButton("" + i)).setBackground(Color.red);
//            } else {
//                add(new JButton("" + i)).setBackground(Color.green);
//            }
//        }
//    }
//}
