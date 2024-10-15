import java.util.*;
import javax.swing.JOptionPane;

public class PuzzleContest {
    // instance variables
    private Puzzle puzzle;
    private Puzzle puzzleCopy;
    private char[][] ending;
    private Queue<char[][]> steps; // Para respetar el orden de inserción
    private boolean solve;
    private int tamaño;
    private static final char[] DIRECTIONS = {'W','N','S','E'};

    /**
     * Constructor para objetos de la clase PuzzleContest
     */
    public PuzzleContest() {
        this.tamaño = 0;
        this.steps = new LinkedList<>(); // Usar LinkedList para respetar FIFO
        this.solve = false;
    }
    public String boardToString() {
        // Obtener el estado actual del tablero del puzzle
        char[][] board = puzzle.actualArrangment();
        
        // Utilizamos StringBuilder para construir la cadena
        StringBuilder sb = new StringBuilder();
        
        // Recorremos la matriz para construir la representación en cadena
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                sb.append(board[i][j]);
            }
            sb.append("\n"); // Para separar cada fila con un salto de línea (opcional, depende de cómo quieres almacenar el estado)
        }
    
        return sb.toString(); // Devolvemos la cadena que representa el tablero
    }

    // Método que resuelve el puzzle usando BFS
    public boolean solve(char[][] starting, char[][] ending) {
        puzzle = new Puzzle(starting, ending);
        this.ending = ending;

        Queue<char[][]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(puzzle.actualArrangment());
        visited.add(boardToString());

        while (!queue.isEmpty()) {
            char[][] currentBoard = queue.poll();

            // Si hemos llegado al estado final
            if (puzzle.isEqual()) {
                return true;  // Se encontró una solución
            }

            // Probar todos los posibles movimientos
            for (char direction : DIRECTIONS) {
                puzzleCopy=new Puzzle (ending,starting);
                puzzleCopy.tilt(direction);

                String boardString = boardToString();
                if (!visited.contains(boardString)) {
                    visited.add(boardString);
                    queue.add(puzzleCopy.actualArrangment());
                    
                    // Agregar el tablero actual a la lista de pasos
                    steps.add(puzzleCopy.actualArrangment());
                }
            }
        }

        return false;  // No se encontró ninguna secuencia de tilts válida
    }

    // Simula los movimientos de los pasos guardados
    public void simulate(char[][] starting, char[][] ending) {
        this.solve = solve(starting, ending);
        if (!solve) {
            JOptionPane.showMessageDialog(null, "No se puede resolver con estas configuraciones", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            for (char[][] matrix : steps) {
                puzzle = new Puzzle(this.ending, matrix);
                puzzle.makeVisible(); // Mostrar cada paso del movimiento
            }
        }
    }

    // Deshacer el último movimiento
    public void lastMove() {
        // Mostrar los pasos en orden inverso (LIFO)
        LinkedList<char[][]> reverseSteps = new LinkedList<>(steps);
        while (!reverseSteps.isEmpty()) {
            puzzle = new Puzzle(ending, reverseSteps.removeLast());
            puzzle.makeVisible();
        }
    }
}
