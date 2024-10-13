import java.util.*;
import javax.swing.JOptionPane;

public class PuzzleContest
{
    // instance variables - replace the example below with your own
    private Puzzle puzzle;
    private char[][] starting;
    private char[][] ending;
    private Stack<char[][]> steps; //para que se respete el orden de inserccion
    private boolean solve;
    private int tamaño;
    /**
     * Constructor for objects of class PuzzleContest
     */
    public PuzzleContest(){
        // initialise instance variables
        this.tamaño=0;
        this.steps=new Stack<>();
        this.solve=false;
    }

    public boolean solve(char[][] ending,char[][] starting){
        puzzle= new Puzzle(ending,starting);
        this.ending=ending;
        //falta logica
        this.tamaño=steps.size();
        return solve;
    }
    
    public void simulate(char[][] ending,char[][] starting){
        this.solve=solve(ending,starting);
        if(!solve){
            JOptionPane.showMessageDialog(null, "No se puede resolver con estas configuraciones", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            for (char[][] matrix : steps) {
                puzzle=new Puzzle(ending,matrix);
                puzzle.makeVisible();
            }
        }
    }
    
    public void lastMove(){
        while (!steps.isEmpty()) {
            puzzle=new Puzzle(ending,steps.pop());
            puzzle.makeVisible();
        }
    }
}
