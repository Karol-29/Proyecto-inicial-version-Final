import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PuzzleTest {
    private Puzzle puzzle;

    @Before
    public void setUp() {
        // Asumimos que Puzzle tiene un constructor que inicializa la matriz y otros atributos
        puzzle = new Puzzle(5, 5); // dimensiones h=5, w=5
    }

    @Test
    public void testAddTileSuccess() {
        puzzle.addTile(2, 2, "blue");
        assertNotNull(puzzle.getTile(2, 2)); // Asegura que la tile se haya agregado
        assertTrue(puzzle.ok()); // `last` debe ser true en caso de éxito
    }

    @Test
    public void testAddTileWhereHoleExists() {
        puzzle.makeHole(1, 1);
        puzzle.addTile(1, 1, "red");
        assertNull(puzzle.getTile(1, 1)); // No se debe añadir la tile
        assertFalse(puzzle.ok()); // `last` debe ser false en caso de error
    }

    @Test
    public void testAddTileOutOfBounds() {
        puzzle.addTile(-1, 2, "green"); // Fuera de los límites
        assertFalse(puzzle.ok()); // `last` debe ser false

        puzzle.addTile(6, 5, "green"); // Fuera de los límites
        assertFalse(puzzle.ok());
    }

    @Test
    public void testAddTilePositionOccupied() {
        puzzle.addTile(3, 3, "yellow");
        assertNotNull(puzzle.getTile(3, 3)); // Tile agregada con éxito

        puzzle.addTile(3, 3, "blue"); // Intentar agregar en la misma posición
        assertEquals("yellow", puzzle.getTile(3, 3).getColor()); // Debe seguir siendo la misma tile
        assertFalse(puzzle.ok()); // `last` debe ser false
    }
}
