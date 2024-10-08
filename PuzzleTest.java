import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

public class PuzzleTest {
    private Puzzle puzzle;

    @Before
    public void setUp() {
        // Asumimos que Puzzle tiene un constructor que inicializa la matriz y otros atributos
        puzzle = new Puzzle(5, 5); // dimensiones h=5, w=5
    }
    
    @After
    public void tearDown() {
       
        puzzle = null; // Eliminamos referencia al objeto actual
    }
    
    @Test
    public void addTile(){
        puzzle.addTile(2,2,"blue");
        assertNotNull(puzzle.getTile(2,2));
        assertTrue(puzzle.ok());
        puzzle.makeHole(1, 1);
        puzzle.addTile(1, 1, "red");
        assertNull(puzzle.getTile(1, 1)); // No se debe añadir la tile
        assertFalse(puzzle.ok());
        puzzle.addTile(-1, 2, "green"); // Fuera de los límites
        assertFalse(puzzle.ok()); // `last` debe ser false
        assertEquals("blue", puzzle.getTile(2, 2).getColor()); // Debe seguir siendo la misma tile
        assertFalse(puzzle.ok());
    }
    
    @Test
    public void makeHole(){
        puzzle.makeHole(1, 1); //hacer agujero
        assertNull(puzzle.getTile(1,1));
        assertTrue(puzzle.ok());
        puzzle.makeHole(-1,7); //fuera de los limites
        assertFalse(puzzle.ok());
    }
    
    @Test
    public void deleteTile(){
        puzzle.addTile(2,2,"blue");
        puzzle.deleteTile(2,2); //eiminar bien
        assertTrue(puzzle.ok());
        puzzle.deleteTile(2,2); //eliminar una tile que no existe
        assertFalse(puzzle.ok());
        puzzle.deleteTile(-1,6); //Fuera de los limites
        assertFalse(puzzle.ok());
    }
    
    @Test
    public void relocateTile(){
        puzzle.addTile(2,2,"blue");
        assertNull(puzzle.getTile(0,0));//verificar que no haya tile 
        puzzle.relocateTile(new int[]{2, 2}, new int[]{0, 0});
        assertNotNull(puzzle.getTile(0,0)); //verificar el movimiento
        assertTrue(puzzle.ok());
        puzzle.relocateTile(new int[]{0, 0}, new int[]{-1, 5}); //fuera de los limites
        assertFalse(puzzle.ok());
        puzzle.relocateTile(new int[]{2, 2}, new int[]{1, 1}); //mover tile que no existe
        assertFalse(puzzle.ok());
    }
    
    @Test
    public void addGlue(){
        puzzle.addTile(0,0,"blue");
        puzzle.addTile(0,1,"red");
        puzzle.addGlue(0,0);
        assertTrue(puzzle.ok());
        puzzle.addGlue(-1,8); //fuera de limites
        assertFalse(puzzle.ok());
        puzzle.relocateTile(new int[]{0, 0}, new int[]{1, 1});
        assertNotNull(puzzle.getTile(1,2));
        
    }
    
    @Test
    public void deleteGlue(){
        puzzle.addTile(0,0,"blue");
        puzzle.addTile(0,1,"red");
        puzzle.deleteGlue(0,0);
        assertFalse(puzzle.ok());
        puzzle.addGlue(0,0);
        puzzle.deleteGlue(0,0);
        puzzle.relocateTile(new int[]{0, 0}, new int[]{1, 1});
        assertNotNull(puzzle.getTile(0,1));
        assertTrue(puzzle.ok());
    }
    
    @Test
    public void exchange(){
        puzzle = new Puzzle(new char [][]{{'r','g'},{'.','b'}},new char [][]{{'y','.'},{'.','.'}});
        char [][] startingDado= new char [][]{{'y','.'},{'.','.'}};
        char [][] startingObtenido = puzzle.actualArrangment();
        boolean fin=true;
        for(int i=0; i<startingDado.length;i++){
            for(int j=0; j<startingDado[0].length;j++){
                if(startingDado[i][j] != startingObtenido[i][j]){
                    fin=false;
                }
            }
        }
        assertTrue(fin);
    }
    
    
}
