

//import controller.GameController;

import javax.swing.*;

import java.util.LinkedList;

public class MyGUI extends JFrame {
//    private int[] data;
    static LinkedList<String> boards = new LinkedList<>();
    private final   int WIDTH;
    private final int HEIGTH;
    JLabel Label;
    static int row;
    static int column;
    Board myboard;
    static int currentR=0;

    private static int[] fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        int[] res = new int[strings.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Integer.parseInt(strings[i]);
        }
        return res;
    }

    public MyGUI(int width, int height, LinkedList<String> data, int m, int n) {
        setTitle("华容道"); //设置标题
        row=m;
        column=n;
        boards=data;
        this.WIDTH = width;
        this.HEIGTH = height;
        myboard=new Board(400,400,row,column,fromString(boards.get(currentR)));
        myboard.setBounds(HEIGTH/7,WIDTH/7,400,400);
//        myboard.setLocation(HEIGTH / 7, HEIGTH / 7);

        add(myboard);
        myboard.repaint();

        JButton next=new JButton("下一步");
        add(next).setBounds(320,20,100,50);

        next.addActionListener((e) -> {
            if(currentR==boards.size()-1){
                return;
            }
            myboard.removeAll();
            currentR++;
            myboard=new Board(400,400,row,column,fromString(boards.get(currentR)));
//            myboard.setLocation(HEIGTH / 7, HEIGTH / 7);
            myboard.setBounds(HEIGTH/7,WIDTH/7,400,400);

            add(myboard);

            myboard.validate();
        });

        JButton last=new JButton("上一步");
        add(last).setBounds(150,20,100,50);
        last.addActionListener((e) -> {
            if(currentR==0){
                return;
            }
            myboard.removeAll();
            currentR--;
            myboard=new Board(400,400,row,column,fromString(boards.get(currentR)));
//            myboard.setLocation(HEIGTH / 7, HEIGTH / 7);
            myboard.setBounds(HEIGTH/7,WIDTH/7,400,400);

            add(myboard);

            myboard.validate();
        });


        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        String currentDirectory = System.getProperty("user.dir");

        ImageIcon picture = new ImageIcon(currentDirectory + "/src/img.jpeg");
        Label=new JLabel(picture);
        add(Label);
        setVisible(true);
    }





    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MyGUI mainFrame = new MyGUI(800, 608, boards, row, column);
            mainFrame.setVisible(true);
        });
    }

}