import java.util.HashMap;
import javax.swing.JOptionPane;
import java.util.*;

public class Puzzle extends Rectangle {
    private int TILE_SIZE=50; // Tamaño de las tiles
    private Tile [][] matrixStarting; // matriz inicial
    private Tile [][] matrixEnding; // matriz final
    private HashMap<Integer, int[]> setTileStarting; //conjuntos matriz inicial  
    private HashMap<Integer, int[]> setTileEnding; //conjuntos matriz final  
    private int h; //Alto
    private int w; //Ancho
    private int currentId=0;  // Elegir los id de los tiles, para evitar problemas
    private boolean last;
    private boolean isVisible; 
    private boolean hasTwoPuzzle;
    public static final Map<String, String> COLORS;
    private Rectangle puzzleStarting;
    private Rectangle puzzleEnding;
    private Circle [][] holes;
    
    static {
        COLORS = new HashMap<>();
        COLORS.put("r", "red");
        COLORS.put("b", "blue");
        COLORS.put("g", "green");
        COLORS.put("y", "yellow");
    }
    
    public Puzzle(int h, int w) {
        setTileStarting = new HashMap<>();
        setTileEnding= new HashMap<>();
        matrixStarting = new Tile[h][w];
        matrixEnding = new Tile[h][w];
        holes= new Circle [h][w];
        this.h = h;
        this.w = w;
        this.isVisible = false;
        this.puzzleStarting=new Rectangle(h*TILE_SIZE,w*TILE_SIZE,0,0,"black");
        this.puzzleEnding=new Rectangle(h*TILE_SIZE,w*TILE_SIZE,w*TILE_SIZE+50,0,"white");
    }
    
    public Puzzle(char[][] ending){
        this(ending.length,ending[0].length);
        matrixEnding=convertir(ending);
        this.puzzleEnding=new Rectangle(ending.length*TILE_SIZE,ending[0].length*TILE_SIZE,(w*TILE_SIZE)+50,0,"black");
        addTile(matrixEnding,true);
        addTile(matrixStarting,false);
    }
    
    public Puzzle(char[][] ending,char[][] starting){
        this(ending);
        matrixStarting =convertir(starting);
        addTile(matrixStarting,false);
    }
    
    private void addTile(Tile [][] matrix, boolean type ) {//true=ending, starting=false 

        if(type){
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (matrix[i][j] != null) {
                        matrix[i][j].setId(currentId);
                        matrix[i][j].moveHorizontal((((w)*TILE_SIZE+50+(j)*TILE_SIZE)-(j*TILE_SIZE)));
                        matrix[i][j].setPosition((w+j)*TILE_SIZE+50,i*TILE_SIZE);
                        setTileEnding.put(currentId,new int[]{currentId});
                        currentId++;
                        if(this.isVisible){
                            matrix[i][j].makeVisible();
                        }
                    }
                }
            }
        }else{
           for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (matrix[i][j] != null) {
                        matrix[i][j].setId(currentId);
                        setTileStarting.put(currentId,new int[]{currentId});
                        currentId++;
                        matrix[i][j].setPosition(j*TILE_SIZE,i*50);
                        if(this.isVisible){
                            matrix[i][j].makeVisible();
                        }  
                }
                }
            } 
        }
    }
    
    private Tile[][] convertir(char[][] matrix) {
        Tile[][] convertida = new Tile[h][w];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == '.') {
                    convertida[i][j] = null; // Si es '*', se deja como null
                } else {
                    String color = COLORS.get(String.valueOf(matrix[i][j])); // Obtener color del mapa
                    if (color != null) {
                        convertida[i][j] = new Tile(j, i, color); // Crear nueva Tile con el color adecuado
                    } else {
                        convertida[i][j] = new Tile(i, j, "white"); // Si no hay color en el mapa, asignar un color por defecto
                    }
                }
            }
        }
        return convertida; // Retornar la matriz convertida
    }
    
    public void addTile(int row, int column, String color) {
        if (holes[row][column] != null) {
            JOptionPane.showMessageDialog(null, "No se puede añadir una ficha donde ya existe un agujero.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si la posición está dentro de los límites
        if (row >= 0 && row <= h-1 && column >= 0 && column <= w-1) {
            //verificar que la posicion este vacia
            if(matrixStarting[row][column]==null){
                Tile newTile = new Tile(column,row, color); 
                matrixStarting[row][column]=newTile;
                setTileStarting.put(currentId, new int[]{currentId});  // Asocia el tile al conjunto
                last = true;
                matrixStarting[row][column].setId(currentId);
                
                currentId++;

                if (this.isVisible){
                    newTile.makeVisible();
                }
            }else{
               JOptionPane.showMessageDialog(null, "Hay una tile es esta posicion", "Error", JOptionPane.ERROR_MESSAGE);
               last = false; 
            }
        } else {
            // Mostrar mensaje de error si está fuera de los límites
            JOptionPane.showMessageDialog(null, "Está fuera de los límites", "Error", JOptionPane.ERROR_MESSAGE);
            last = false;
        }
    }
    
    private int findKeyByValue(int id) {
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if(matrixStarting[i][j] !=null){
                int posibleTile = matrixStarting[i][j].getId();
                // Verificar si coinciden tanto la fila como la columna
                if (posibleTile== id) {
                    return posibleTile;
                }
            }}
        }
        return -1;
    }
    
    public void addGlue(int row, int column){
        // Verificar si la posición está dentro de los límites
        if (row >= 0 && row <= h-1 && column >= 0 && column <= w-1) {
            //verificar que la posicion este vacia
            if(matrixStarting[row][column]==null){
                int idTile=matrixStarting[row][column].getId();
                ArrayList<Integer> adjacentTiles = new ArrayList<>();
                adjacentTiles.add(matrixStarting[row+1][column].getId());
                adjacentTiles.add(matrixStarting[row-1][column].getId());
                adjacentTiles.add(matrixStarting[row][column+1].getId());
                adjacentTiles.add(matrixStarting[row][column-1].getId());
            for (int id : adjacentTiles) {
                int setTilePrincipal = findKeyByValue(idTile);
                int[] values = setTileStarting.get(setTilePrincipal);
                if (id != -1) {
                    int setTileA = findKeyByValue(id);
                    int[] value = setTileStarting.get(setTileA);
                    int nombreConjunto = Math.min(setTilePrincipal, setTileA);
                    int[] nuevoConjunto = new int[values.length + value.length];
                    System.arraycopy(values, 0, nuevoConjunto, 0, values.length);
                    System.arraycopy(value, 0, nuevoConjunto, values.length, value.length);
                    setTileStarting.put(setTilePrincipal, new int[]{});
                    setTileStarting.put(setTileA, new int[]{});
                    setTileStarting.put(nombreConjunto, nuevoConjunto);
                }
            }
            last=true;
            }else{
                JOptionPane.showMessageDialog(null, "No hay tile en la posición dada", "Error", JOptionPane.ERROR_MESSAGE);
                last=false;
                return;
            }
        }else{
            JOptionPane.showMessageDialog(null, "Fuera de limites", "Error", JOptionPane.ERROR_MESSAGE);
            last=false;
            return;
        }
        }
    
    public void makeVisible(){
        puzzleStarting.makeVisible();
        puzzleEnding.makeVisible();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (matrixEnding[i][j] != null) {
                    matrixEnding[i][j].makeVisible();
                }
                if (this.matrixStarting[i][j] != null) {
                    matrixStarting[i][j].makeVisible();
                }
            }
        }
        //Para el metodo makeHole
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (holes[i][j] != null) {
                    holes[i][j].makeVisible();
                }
            }
        }
        this.isVisible=true;
    }
    
    public void makeInvisible(){
        puzzleStarting.makeInvisible();
        puzzleEnding.makeInvisible();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (matrixEnding[i][j] != null) {
                    matrixEnding[i][j].makeInvisible();
                }
                if (this.matrixStarting[i][j] != null) {
                    matrixStarting[i][j].makeInvisible();
                }
            }
        }
        this.isVisible=false;
    }
    
    private Tile getTile(int row, int column) {
        // Verificar que las coordenadas estén dentro de los límites de la matriz
        if (row >= 0 && row < h && column >= 0 && column < w) {
            Tile possibleTile = matrixStarting[row][column];
            return possibleTile;  // Puede ser un objeto Tile o null
        }
        return null;  // Si está fuera de los límites, devolver null
    }
    
    public void relocateTile(int[] from, int[] to) {
        int fromRow = from[0];
        int fromColumn = from[1];
        int toRow = to[0];
        int toColumn = to[1];
        Tile tilePrincipal=getTile(fromRow,fromColumn);
        
        // Verificar si hay una ficha en la posición de origen
        if (tilePrincipal != null) {
            int idTile = tilePrincipal.getId();
            
            if (idTile == -1) {
                JOptionPane.showMessageDialog(null, "No hay tile para mover", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (holes[toRow][toColumn] != null) {
            JOptionPane.showMessageDialog(null, "No se puede mover, hay un hueco es esta posicion.","Error",JOptionPane.ERROR_MESSAGE);
            return;
            }

            // Obtener el conjunto de fichas pegadas
            Integer keySetTile = findKeyByValue(idTile);
            int[] tilesInSet = setTileStarting.get(keySetTile);
    
            // Verificar si todas las fichas del conjunto pueden moverse
            for (int tileId : tilesInSet) {
                Tile tile = getTileForId(tileId);
                
                int tileRow = tile.getRow();
                int tileColumn = tile.getColumn();
                
                int[] newCoordinates = canMoveTile(toRow - (fromRow - tileRow), toColumn - (fromColumn - tileColumn));
            
                if (newCoordinates[0] == -1) {
                    JOptionPane.showMessageDialog(null, "No se puede mover, fuera de los límites", "Error", JOptionPane.ERROR_MESSAGE);
                    return;  // Detener el proceso si alguna ficha no puede moverse
                }
            }
            
            // Si todas pueden moverse, realizar el movimiento
            for (int tileId : tilesInSet) {
                Tile tile = getTileForId(tileId);
                tile.makeInvisible();
                
                int tileRow = tile.getRow();
                int tileColumn = tile.getColumn();
                
                // Mover cada ficha pegada a la nueva posición relativa
                tile.relocate(toRow - (fromRow - tileRow), toColumn - (fromColumn - tileColumn)); 
                //cambio las tiles en la matriz
                matrixStarting[toRow - (fromRow - tileRow)][toColumn - (fromColumn - tileColumn)]=tile;
                matrixStarting[tileRow][tileColumn]=null;
                // Actualizar las propiedades visuales de la ficha
                
                tile.makeVisible();  // Hacer visible la ficha después de moverla
            }
  
            last = true;  // Si todas las fichas se movieron con éxito
        } else {
            JOptionPane.showMessageDialog(null, "No hay tile en la posición de origen", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Tile getTileForId(int id){
        for(int i=0; i<h;i++){
            for(int j=0;j<w;j++){
                if(matrixStarting[i][j] !=null){
                int idTile=matrixStarting[i][j].getId();
                if(idTile==id){
                    return matrixStarting[i][j];
                }
            }}
        }
        return null;
    }
    
    private int[] canMoveTile(int newRow, int newColumn) {
        // Verificar que las coordenadas estén dentro de los límites
        if (newRow >= 0 && newRow <= h-1 && newColumn >= 0 && newColumn <= w-1) {
            // Verificar si la nueva posición está ocupada
            if (matrixStarting[newRow][newColumn] == null) {
                return new int[]{newRow, newColumn};  // Es posible mover la ficha
            }
        }
        return new int[]{-1, -1};  // No es posible mover la ficha
    }
    
    
    public char[][] actualArrangment(){
        char [][] matrix= new char[h][w];
        for(int i=0; i<h;i++){
                for(int j=0;j<w;j++){
                    if(matrixStarting[i][j]== null){
                        matrix[i][j]='.';
                    }else{
                        String color=matrixStarting[i][j].getColor();
                        matrix[i][j]=color.charAt(0);
                }
            }
        }
        StringBuilder visualMatrix = new StringBuilder("Configuración actual:\n"); // Concatena linea por linea y despues los une facherito como string
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                visualMatrix.append(matrix[i][j]).append(" ");
            }
            visualMatrix.append("\n");
        }
    
        // Muestra la matriz en una ventana de diálogo
        JOptionPane.showMessageDialog(null, visualMatrix.toString(),"Visualización de la Matriz", JOptionPane.INFORMATION_MESSAGE);
    
        return matrix;
    }
    public void makeHole(int row, int column) {
        // Verificar si la posición está dentro de los límites del puzzle
        if (row < 0 || row >= h || column < 0 || column >= w) {
            JOptionPane.showMessageDialog(null, "La posición está fuera de los límites.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Verificar si ya existe una ficha en esta posición
        if (matrixStarting[row][column] != null) {
            JOptionPane.showMessageDialog(null, "No se puede crear un agujero donde ya existe una ficha.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Verificar si ya existe un agujero en esta posición
        if (holes[row][column] != null) {
            JOptionPane.showMessageDialog(null, "Ya existe un agujero en esta posición.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Crear un nuevo círculo para el agujero
        Circle hole = new Circle();
        hole.changeSize(TILE_SIZE);  // TILE_SIZE es el diámetro deseado
        hole.changeColor("white");  // color según sea necesario
    
        // Posicionar el agujero
        int xPosition = column *TILE_SIZE;
        int yPosition = row *TILE_SIZE;
        hole.moveHorizontal(xPosition);
        hole.moveVertical(yPosition);
    
        // Añadir el agujero a nuestro array de agujeros
        holes[row][column] = hole;
    
        // Hacer visible el agujero si el puzzle está visible
        if (this.isVisible) {
            hole.makeVisible();
        }
    
        JOptionPane.showMessageDialog(null, "Agujero creado en la fila " + row + ", columna " + column);
    }
    public boolean isGoal() {
    boolean isEqual = true;  // Suponemos que inicialmente es verdadero

    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (matrixStarting[i][j] == null && matrixEnding[i][j] == null) {
                continue; // Ambos son null, seguimos
            }
            if ((matrixStarting[i][j] == null && matrixEnding[i][j] != null) || 
                (matrixStarting[i][j] != null && matrixEnding[i][j] == null)) {
                isEqual = false; // Encontramos una discrepancia
                break; // Salimos del bucle
            }
            if (!matrixStarting[i][j].getColor().equals(matrixEnding[i][j].getColor())) {
                isEqual = false; // Encontramos una discrepancia
                break; // Salimos del bucle
            }
        }
        if (!isEqual) {
            break; // Salimos del bucle externo si ya hay una discrepancia
        }
    }

    // Mostramos el mensaje según el resultado
    if (isEqual) {
        JOptionPane.showMessageDialog(null, "¡Felicitaciones! Has alcanzado la configuración objetivo.");
    } else {
        JOptionPane.showMessageDialog(null, "La configuración actual no coincide con el objetivo. ¡Sigue intentando!");
    }
    return isEqual;
    }
}
 // Si no encontramos diferencias, son iguales


