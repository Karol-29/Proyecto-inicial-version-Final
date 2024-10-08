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
    private ArrayList<Line> glues;
    private int[][] tilesCorrect;
    private ArrayList<Tile> gluedTiles;
    private HashSet<Integer> processedSetIds; // almacenamos IDs únicos de conjuntos de fichas que ya han sido procesados.
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
        this.tilesCorrect = new int[h][w];
        this.gluedTiles = new ArrayList<>();
        this.processedSetIds = new HashSet<>();
        this.h = h;
        this.w = w;
        this.isVisible = false;
        this.puzzleStarting=new Rectangle(h*TILE_SIZE,w*TILE_SIZE,0,0,"black");
        this.puzzleEnding=new Rectangle(h*TILE_SIZE,w*TILE_SIZE,w*TILE_SIZE+50,0,"white");
        this.glues = new ArrayList<Line>();
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
                        matrix[i][j].setPosition(j*TILE_SIZE,i*TILE_SIZE);
                        if(this.isVisible){
                            matrix[i][j].makeVisible();
                        }  
                }
                }
            } 
        }
    }
    
    public void addTile(int row, int column, String color) {
        
        // Verificar si la posición está dentro de los límites
        if (row >= 0 && row <= h-1 && column >= 0 && column <= w-1) {
            if (holes[row][column] != null) {
            JOptionPane.showMessageDialog(null, "No se puede añadir una ficha donde ya existe un agujero.","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
            return;
            }

            //verificar que la posicion este vacia
            if(matrixStarting[row][column]==null){
                Tile newTile = new Tile(column,row, color); 
                matrixStarting[row][column]=newTile;
                setTileStarting.put(currentId, new int[]{currentId});  // Asocia el tile al conjunto
                matrixStarting[row][column].setId(currentId);
                currentId++;
                last = true;
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
        for (Map.Entry<Integer, int[]> entry : setTileStarting.entrySet()) {
            int key = entry.getKey();
            int[] values = entry.getValue();
    
            // Busca el id en el arreglo de valores
            for (int value : values) {
                if (value == id) {
                    return key;  // Retorna la clave si encuentra el ID en el arreglo
                }
            }
        }
        return -1;  // Si no encuentra el ID en ninguno de los conjuntos, retorna -1
    }
    
    public void addGlue(int row, int column){
        // Verificar si la posición está dentro de los límites
        if (row >= 0 && row <= h-1 && column >= 0 && column <= w-1) {
            //verificar que la posicion este vacia
            if(matrixStarting[row][column] !=null){
                int idTile=matrixStarting[row][column].getId();
                Tile tilePrincipal= getTile(row,column);
                ArrayList<Tile> adjacentTiles = new ArrayList<>();
                adjacentTiles.add(getTile(row+1,column));
                adjacentTiles.add(getTile(row-1,column));
                adjacentTiles.add(getTile(row,column+1));
                adjacentTiles.add(getTile(row,column-1));
            for (Tile tile: adjacentTiles) {
                int setTilePrincipal = findKeyByValue(idTile);
                int[] values = setTileStarting.get(setTilePrincipal);
                
                
                if (tile != null) {
                    int id = tile.getId();
                    int setTileA = findKeyByValue(id);
                    int[] value = setTileStarting.get(setTileA);
                    int nombreConjunto = Math.min(setTilePrincipal, setTileA);
                    int[] nuevoConjunto = new int[values.length + value.length];
                    System.arraycopy(values, 0, nuevoConjunto, 0, values.length);
                    System.arraycopy(value, 0, nuevoConjunto, values.length, value.length);
                    setTileStarting.put(setTilePrincipal, new int[]{});
                    setTileStarting.put(setTileA, new int[]{});
                    setTileStarting.put(nombreConjunto, nuevoConjunto);
                    if(tile.getRow()==tilePrincipal.getRow()){
                        int maximo=Math.max(tile.getRow(),tilePrincipal.getRow());
                        Line line=new Line(maximo,tile.getColumn(),maximo-50,tile.getColumn());
                        glues.add(line);
                        if(isVisible){
                            line.makeVisible();
                        }
                    }
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
    
    private void conjunto() {
        for (int idTile : setTileStarting.keySet()) { 
            int[] values = setTileStarting.get(idTile);
            // Imprimir el idTile primero
            System.out.print("idTile: " + idTile + " -> Values: ");
            for (int value : values) {
                // Imprimir cada valor del array
                System.out.print(value + " ");
            }
            // Salto de línea después de imprimir todos los valores de un idTile
            System.out.println();
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
    
    Tile getTile(int row, int column) {
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
        if (fromRow >= 0 && fromRow <= h-1 && fromColumn >= 0 && fromColumn <= w-1 && toRow >= 0 && toRow <= h-1 && toColumn >= 0 && toColumn <= w-1){
            // Verificar si hay una ficha en la posición de origen
            if (tilePrincipal != null) {
                int idTile = tilePrincipal.getId();
                
                if (idTile == -1) {
                    JOptionPane.showMessageDialog(null, "No hay tile para mover", "Error", JOptionPane.ERROR_MESSAGE);
                    last = false;
                    return;
                }
                if (holes[toRow][toColumn] != null) {
                    JOptionPane.showMessageDialog(null, "No se puede mover, hay un hueco es esta posicion.","Error",JOptionPane.ERROR_MESSAGE);
                    last = false;
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
                    
                // Verificar si hay un hueco en la nueva posición
                    if (holes[toRow - (fromRow - tileRow)][toColumn - (fromColumn - tileColumn)] != null) {
                    JOptionPane.showMessageDialog(null, "No se puede mover, hay un hueco en esta posición.", "Error", JOptionPane.ERROR_MESSAGE);
                    last = false;
                    return;
                    }
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
                    if(isVisible){
                        tile.makeVisible();
                    }// Hacer visible la ficha después de moverla
                }
      
                last = true;  // Si todas las fichas se movieron con éxito
            } else {
                JOptionPane.showMessageDialog(null, "No hay tile en la posición de origen", "Error", JOptionPane.ERROR_MESSAGE);
                last = false;
            }
        }else{
            JOptionPane.showMessageDialog(null, "Fuera de los limites", "Error", JOptionPane.ERROR_MESSAGE);
            last=false;
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
            last = false;
            return;
        }
    
        // Verificar si ya existe una ficha en esta posición
        if (matrixStarting[row][column] != null) {
            JOptionPane.showMessageDialog(null, "No se puede crear un agujero donde ya existe una ficha.","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
            return;
        }
    
        // Verificar si ya existe un agujero en esta posición
        if (holes[row][column] != null) {
            JOptionPane.showMessageDialog(null, "Ya existe un agujero en esta posición.","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
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
        last = true;
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
    
    public void exchange() {
        // Guardamos temporalmente la matriz starting
        Tile[][] tempMatrix = new Tile[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                tempMatrix[i][j] = matrixStarting[i][j];
            }
        }
        
        // Limpiamos las posiciones actuales
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (matrixStarting[i][j] != null) {
                    matrixStarting[i][j].makeInvisible();
                }
                //hacemos invisibles las tiles de ending
                if (matrixEnding[i][j] != null) {
                    matrixEnding[i][j].makeInvisible();
                }
            }
        }
        
        // Intercambiamos las matrices y ajustamos las posiciones
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                // Mover ending a starting
                if (matrixEnding[i][j] != null) {
                    matrixEnding[i][j].moveHorizontal(-(w*TILE_SIZE+50));
                    matrixStarting[i][j] = matrixEnding[i][j];
                    matrixStarting[i][j].setPosition(j*TILE_SIZE,i*TILE_SIZE);
                } else {
                    matrixStarting[i][j] = null;
                }
                
                // Mover starting (temp) a ending
                if (tempMatrix[i][j] != null) {
                    tempMatrix[i][j].moveHorizontal(w*TILE_SIZE+50);
                    matrixEnding[i][j] = tempMatrix[i][j];
                    matrixEnding[i][j].setPosition((w+j)*TILE_SIZE+50,i*TILE_SIZE);
                } else {
                    matrixEnding[i][j] = null;
                }
            }
        }
        
        // Hacemos visibles las nuevas posiciones si el puzzle está visible
        if (this.isVisible) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (matrixStarting[i][j] != null) {
                        matrixStarting[i][j].makeVisible();
                    }
                    if (matrixEnding[i][j] != null) {
                        matrixEnding[i][j].makeVisible();
                    }
                }
            }
        }
        
        JOptionPane.showMessageDialog(null, "Puzzles intercambiados exitosamente.");
    }
    
    public int misPlacedTiles() {
        int correctTiles = 0;
        
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (matrixStarting[i][j] == null && matrixEnding[i][j] == null) {
                    // Ambas posiciones vacías se consideran coincidentes
                    correctTiles++;
                } else if (matrixStarting[i][j] != null && matrixEnding[i][j] != null) {
                    // Comparamos los colores de las baldosas
                    String startingColor = matrixStarting[i][j].getColor();
                    String endingColor = matrixEnding[i][j].getColor();
                    if (startingColor.equals(endingColor)) {
                        correctTiles++;
                    }
                }
            }
        }
        
        // Preparar mensaje para mostrar
        String mensaje = String.format("Hay %d baldosas correctamente posicionadas de un total de %d posiciones.", 
                                      correctTiles, h * w);
        
        // Mostrar el mensaje en una ventana
        JOptionPane.showMessageDialog(null, mensaje, "Baldosas Correctamente Posicionadas", JOptionPane.INFORMATION_MESSAGE);
        
        return correctTiles;
    }
    
    public boolean ok(){
        JOptionPane.showMessageDialog(null, last, "Baldosas Correctamente Posicionadas", JOptionPane.INFORMATION_MESSAGE);
        return last;
    }
    
    
    public void deleteTile(int row,int column){
        if (row < 0 || row >= h || column < 0 || column >= w) {
            JOptionPane.showMessageDialog(null, "La posición está fuera de los límites.","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
            return;
        }
        Tile tile=getTile(row,column);
        if (holes[row][column] != null) {
            JOptionPane.showMessageDialog(null, "Es un hueco","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
            return;
        }
        if (tile == null) {
            JOptionPane.showMessageDialog(null, "No hay tile para eliminar.","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
            return;
        }
        int idTile=tile.getId();
        int idConjunto=findKeyByValue(idTile);
        int [] conjunto=setTileStarting.get(idConjunto);
        if (conjunto.length < 1) {
            JOptionPane.showMessageDialog(null, "No se eliminar una tile pegada.","Error",JOptionPane.ERROR_MESSAGE);
            last = false;
            return;
        }
        matrixStarting[row][column].makeInvisible();
        matrixStarting[row][column] = null;
        setTileStarting.remove(idConjunto);
    }
    
    public void deleteGlue(int row, int column) {
        // Verificar si la posición está dentro de los límites del puzzle
        if (row < 0 || row >= h || column < 0 || column >= w) {
            JOptionPane.showMessageDialog(null, "Fuera de límites", "Error", JOptionPane.ERROR_MESSAGE);
            last=false;
            return;
        }
    
        // Verificar que la posición contenga una tile
        if (matrixStarting[row][column] == null) {
            JOptionPane.showMessageDialog(null, "No hay tile en la posición dada", "Error", JOptionPane.ERROR_MESSAGE);
            last=false;
            return;
        }
    
        // Obtener la tile en la posición dada
        Tile tilePrincipal = getTile(row, column);
        int idTile = tilePrincipal.getId(); // Obtener el ID de la tile
        int idConjunto = findKeyByValue(idTile); // Obtener el ID del conjunto donde está la tile
        int[] conjunto = setTileStarting.get(idConjunto); // Obtener las tiles del conjunto
        
        // Si solo hay una tile en el conjunto, no hay nada que separar
        if (conjunto.length <= 1) {
            JOptionPane.showMessageDialog(null, "No hay baldosas pegadas", "Error", JOptionPane.ERROR_MESSAGE);
            last=false;
            return;
        }
    
        // Separar las tiles actuales creando conjuntos individuales para cada tile
        for (int id : conjunto) {
            
            setTileStarting.put(id, new int[]{id}); // Cada tile ahora está en su propio conjunto
        }
    
        // Buscar pares adyacentes que no incluyen el tile seleccionado y volverlos a pegar
        List<int[]> adjacentPairs = findAdjacentPairs(idTile, conjunto);
        

        for (int[] array : adjacentPairs) {
            // Si ambos tiles no incluyen el idTile, los volvemos a pegar
            if (array[0] != idTile && array[1] != idTile) {
                addGlueSecond(array[0], array[1]);
            }
        }
        last=true;
        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(null, "Las tiles adyacentes han sido separadas", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public int[][] fixedTiles() {
        StringBuilder mensaje = new StringBuilder();
        tilesCorrect = new int[h][w];
        
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Tile currentTile = matrixStarting[i][j];
                if (currentTile != null) {
                    int tileId = currentTile.getId();
                    int setId = findKeyByValue(tileId);
                    
                    if (!processedSetIds.contains(setId)) {
                        int[] tilesInSet = setTileStarting.get(setId);
                        
                        if (tilesInSet != null && tilesInSet.length > 1) {
                            processedSetIds.add(setId);
                            
                            for (int tileIdInSet : tilesInSet) {
                                Tile tile = getTileForId(tileIdInSet);
                                if (tile != null) {
                                    gluedTiles.add(tile);
                                    int row = tile.getRow();
                                    int col = tile.getColumn();
                                    tilesCorrect[row][col] = 1;
                                    mensaje.append(String.format("(%d,%d) ", row, col));                                
                                }
                            }
                            mensaje.append("\n");
                        }
                    }
                }
            }
        }
        
        if (gluedTiles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todas las baldosas se pueden mover.", 
                                         "fixedTiles", JOptionPane.INFORMATION_MESSAGE);
            return tilesCorrect;
        }

        // Mostrar el mensaje con las coordenadas
        JOptionPane.showMessageDialog(null, mensaje.toString(), 
                                     "fixedTiles", JOptionPane.INFORMATION_MESSAGE);

        // Llamar al método de parpadeo
        blinkTiles(gluedTiles);

        return tilesCorrect;
    }
    private List<int[]> findAdjacentPairs(int idTile, int[] conjunto) {
        List<int[]> adjacentPairs = new ArrayList<>(); // Lista para almacenar las parejas adyacentes
        Set<String> seenPairs = new HashSet<>(); // Conjunto para rastrear pares vistos
    
        // Buscar el conjunto donde está idTile
        int idConjunto = findKeyByValue(idTile);
        
        // Recorrer el conjunto
        for (int i = 0; i < conjunto.length; i++) {
            Tile currentTile = getTileForId(conjunto[i]); // Obtener la tile actual
    
            int row = currentTile.getRow(); 
            int column = currentTile.getColumn();
    
            // Verificar las tiles adyacentes (arriba, abajo, izquierda, derecha)
            Tile topTile = getTile(row - 1, column);
            Tile bottomTile = getTile(row + 1, column);
            Tile leftTile = getTile(row, column - 1);
            Tile rightTile = getTile(row, column + 1);
    
            // Revisar si las tiles adyacentes están en el mismo conjunto
            if (topTile != null && isInSameConjunto(topTile, idConjunto)) {
                String pairKey = conjunto[i] < topTile.getId() ? conjunto[i] + "," + topTile.getId() : topTile.getId() + "," + conjunto[i];
                if (!seenPairs.contains(pairKey)) {
                    adjacentPairs.add(new int[]{conjunto[i], topTile.getId()});
                    seenPairs.add(pairKey); // Agregar par al conjunto
                }
            }
            if (bottomTile != null && isInSameConjunto(bottomTile, idConjunto)) {
                String pairKey = conjunto[i] < bottomTile.getId() ? conjunto[i] + "," + bottomTile.getId() : bottomTile.getId() + "," + conjunto[i];
                if (!seenPairs.contains(pairKey)) {
                    adjacentPairs.add(new int[]{conjunto[i], bottomTile.getId()});
                    seenPairs.add(pairKey); // Agregar par al conjunto
                }
            }
            if (leftTile != null && isInSameConjunto(leftTile, idConjunto)) {
                String pairKey = conjunto[i] < leftTile.getId() ? conjunto[i] + "," + leftTile.getId() : leftTile.getId() + "," + conjunto[i];
                if (!seenPairs.contains(pairKey)) {
                    adjacentPairs.add(new int[]{conjunto[i], leftTile.getId()});
                    seenPairs.add(pairKey); // Agregar par al conjunto
                }
            }
            if (rightTile != null && isInSameConjunto(rightTile, idConjunto)) {
                String pairKey = conjunto[i] < rightTile.getId() ? conjunto[i] + "," + rightTile.getId() : rightTile.getId() + "," + conjunto[i];
                if (!seenPairs.contains(pairKey)) {
                    adjacentPairs.add(new int[]{conjunto[i], rightTile.getId()});
                    seenPairs.add(pairKey); // Agregar par al conjunto
                }
            }
        }    
        return adjacentPairs;
    }
    private void addGlueSecond(int idTile1, int idTile2){
        Tile tile1=getTileForId(idTile1);
        Tile tile2=getTileForId(idTile2);
        boolean areAdyacent=areTilesAdjacent(idTile1,idTile2);
        if(areAdyacent){
            int setTilePrincipal = findKeyByValue(idTile1);
            int[] values = setTileStarting.get(setTilePrincipal);
            int id = idTile2;
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
    
    private boolean isInSameConjunto(Tile tile, int idConjunto) {
        int idTile = tile.getId();
        int tileConjunto = findKeyByValue(idTile);
        return tileConjunto == idConjunto;
    }
    private void blinkTiles(ArrayList<Tile> tilesToBlink) {
        boolean isVisible = true;
        for (int i = 0; i < 6; i++) {
            if (isVisible) {
                for (Tile tile : tilesToBlink) {
                    tile.makeInvisible();
                }
            } else {
                for (Tile tile : tilesToBlink) {
                    tile.makeVisible();
                }
            }
            isVisible = !isVisible;
            
            puzzleStarting.makeVisible();
            puzzleEnding.makeVisible();
        }
        
        // Nos asegurar que todas las fichas quedan visibles al final
        for (Tile tile : tilesToBlink) {
            tile.makeVisible();
        }
    }
    
    // Método que verifica si dos tiles son adyacentes
    private boolean areTilesAdjacent(int id1, int id2) {
        Tile tile1 = getTileForId(id1); // Obtener la tile por ID
        Tile tile2 = getTileForId(id2);

    // Comprobar si las tiles son adyacentes (vertical y horizontal)
        return (Math.abs(tile1.getRow() - tile2.getRow()) == 1 && tile1.getColumn() == tile2.getColumn()) || // Adyacencia vertical
               (Math.abs(tile1.getColumn() - tile2.getColumn()) == 1 && tile1.getRow() == tile2.getRow()); // Adyacencia horizontal
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
    public void tilt(char direction) {
        // Validar la dirección
        if (direction != 'N' && direction != 'S' && direction != 'E' && direction != 'W') {
            JOptionPane.showMessageDialog(null, "Dirección inválida. Use N, S, E, o W", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        boolean[][] processed = new boolean[h][w];
        
        // Configurar el orden de procesamiento según la dirección
        if (direction == 'N' || direction == 'S') {
            processTiltVertical(direction, processed);
        } else {
            processTiltHorizontal(direction, processed);
        }
    }
    
    private void processTiltVertical(char direction, boolean[][] processed) {
        int startI = (direction == 'N') ? 0 : h - 1;
        int endI = (direction == 'N') ? h : -1;
        int stepI = (direction == 'N') ? 1 : -1;
    
        // Primero, identificar todos los conjuntos de tiles pegadas
        HashMap<Integer, ArrayList<Tile>> setGroups = new HashMap<>();
        ArrayList<ArrayList<Tile>> orderedGroups = new ArrayList<>();
    
        // Agrupar las tiles por conjuntos y mantener el orden
        for (int i = startI; i != endI; i += stepI) {
            for (int j = 0; j < w; j++) {
                if (matrixStarting[i][j] != null && !processed[i][j]) {
                    Tile tile = matrixStarting[i][j];
                    int setId = findKeyByValue(tile.getId());
                    
                    if (!setGroups.containsKey(setId)) {
                        ArrayList<Tile> newGroup = new ArrayList<>();
                        setGroups.put(setId, newGroup);
                        orderedGroups.add(newGroup);
                    }
                    
                    ArrayList<Tile> group = setGroups.get(setId);
                    if (!group.contains(tile)) {
                        group.add(tile);
                    }
                }
            }
        }
    
        // Procesar los grupos en el orden correcto
        for (ArrayList<Tile> tileGroup : orderedGroups) {
            if (tileGroup.size() == 1) {
                // Mover tile individual
                Tile tile = tileGroup.get(0);
                moveSingleTile(tile.getRow(), tile.getColumn(), direction, processed);
            } else {
                // Ordenar tiles dentro del grupo según la dirección
                if (direction == 'S') {
                    tileGroup.sort((t1, t2) -> t2.getRow() - t1.getRow());
                } else { // 'N'
                    tileGroup.sort((t1, t2) -> t1.getRow() - t2.getRow());
                }
    
                // Mover el grupo completo
                moveGluedTilesVertical(tileGroup, direction, processed);
            }
        }
    }
    
    private void processTiltHorizontal(char direction, boolean[][] processed) {
        int startJ = (direction == 'W') ? 0 : w-1;
        int endJ = (direction == 'W') ? w : -1;
        int stepJ = (direction == 'W') ? 1 : -1;
        
        // Primero, identificar todos los conjuntos de tiles pegadas
        HashMap<Integer, ArrayList<Tile>> setGroups = new HashMap<>();
        ArrayList<ArrayList<Tile>> orderedGroups = new ArrayList<>();
        
        // Agrupar las tiles por conjuntos y mantener el orden
        for (int j = startJ; j != endJ; j += stepJ) {
            for (int i = 0; i < h; i++) {
                if (matrixStarting[i][j] != null && !processed[i][j]) {
                    Tile tile = matrixStarting[i][j];
                    int setId = findKeyByValue(tile.getId());
                    
                    if (!setGroups.containsKey(setId)) {
                        ArrayList<Tile> newGroup = new ArrayList<>();
                        setGroups.put(setId, newGroup);
                        orderedGroups.add(newGroup);
                    }
                    
                    ArrayList<Tile> group = setGroups.get(setId);
                    if (!group.contains(tile)) {
                        group.add(tile);
                    }
                }
            }
        }
        
        // Procesar los grupos en el orden correcto
        for (ArrayList<Tile> tileGroup : orderedGroups) {
            if (tileGroup.size() == 1) {
                // Mover tile individual
                Tile tile = tileGroup.get(0);
                moveSingleTile(tile.getRow(), tile.getColumn(), direction, processed);
            } else {
                // Ordenar tiles dentro del grupo según la dirección
                if (direction == 'E') {
                    tileGroup.sort((t1, t2) -> t2.getColumn() - t1.getColumn());
                } else { // 'W'
                    tileGroup.sort((t1, t2) -> t1.getColumn() - t2.getColumn());
                }
                
                // Mover el grupo completo
                moveGluedTilesHorizontal(tileGroup, direction, processed);
            }
        }
    }
    
    private void moveGluedTilesHorizontal(ArrayList<Tile> tileGroup, char direction, boolean[][] processed) {
        boolean canMove = true;
        while (canMove) {
            // Verificar si todo el grupo puede moverse
            boolean moveAllowed = true;
            ArrayList<int[]> newPositions = new ArrayList<>();
            
            for (Tile tile : tileGroup) {
                int currentRow = tile.getRow();
                int currentCol = tile.getColumn();
                int nextCol = (direction == 'E') ? currentCol + 1 : currentCol - 1;
                
                // Verificar límites
                if (nextCol < 0 || nextCol >= w) {
                    moveAllowed = false;
                    break;
                }
                
                // Verificar si hay otra tile que no sea parte del grupo
                if (matrixStarting[currentRow][nextCol] != null) {
                    boolean isInGroup = false;
                    for (Tile groupTile : tileGroup) {
                        if (groupTile.getRow() == currentRow && groupTile.getColumn() == nextCol) {
                            isInGroup = true;
                            break;
                        }
                    }
                    if (!isInGroup) {
                        moveAllowed = false;
                        break;
                    }
                }
                
                newPositions.add(new int[]{currentRow, nextCol});
            }
            
            if (!moveAllowed) {
                break;
            }
            
            // Mover todas las tiles del grupo
            for (int i = 0; i < tileGroup.size(); i++) {
                Tile tile = tileGroup.get(i);
                int oldRow = tile.getRow();
                int oldCol = tile.getColumn();
                int[] newPos = newPositions.get(i);
                
                matrixStarting[oldRow][oldCol] = null;
                matrixStarting[newPos[0]][newPos[1]] = tile;
                tile.relocate(newPos[0], newPos[1]);
                processed[newPos[0]][newPos[1]] = true;
                processed[oldRow][oldCol] = true;
            }
        }
    }
    
    private void moveGluedTilesVertical(ArrayList<Tile> tileGroup, char direction, boolean[][] processed) {
        boolean canMove = true;
        while (canMove) {
            // Verificar si todo el grupo puede moverse
            boolean moveAllowed = true;
            ArrayList<int[]> newPositions = new ArrayList<>();
    
            for (Tile tile : tileGroup) {
                int currentRow = tile.getRow();
                int currentCol = tile.getColumn();
                int nextRow = (direction == 'S') ? currentRow + 1 : currentRow - 1;
    
                // Verificar límites
                if (nextRow < 0 || nextRow >= h) {
                    moveAllowed = false;
                    break;
                }
    
                // Verificar si hay otra tile que no sea parte del grupo
                if (matrixStarting[nextRow][currentCol] != null) {
                    boolean isInGroup = false;
                    for (Tile groupTile : tileGroup) {
                        if (groupTile.getRow() == nextRow && groupTile.getColumn() == currentCol) {
                            isInGroup = true;
                            break;
                        }
                    }
                    if (!isInGroup) {
                        moveAllowed = false;
                        break;
                    }
                }
    
                newPositions.add(new int[]{nextRow, currentCol});
            }
    
            if (!moveAllowed) {
                break;
            }
    
            // Mover todas las tiles del grupo
            for (int i = 0; i < tileGroup.size(); i++) {
                Tile tile = tileGroup.get(i);
                int oldRow = tile.getRow();
                int oldCol = tile.getColumn();
                int[] newPos = newPositions.get(i);
    
                matrixStarting[oldRow][oldCol] = null;
                matrixStarting[newPos[0]][newPos[1]] = tile;
                tile.relocate(newPos[0], newPos[1]);
                processed[newPos[0]][newPos[1]] = true;
                processed[oldRow][oldCol] = true;
            }
        }
    }
        
    private void moveSingleTile(int row, int col, char direction, boolean[][] processed) {
        Tile tile = matrixStarting[row][col];
        int newRow = row;
        int newCol = col;
        boolean canMove = true;
    
        while (canMove) {
            int nextRow = newRow;
            int nextCol = newCol;
    
            // Calcular siguiente posición según la dirección
            switch (direction) {
                case 'N': nextRow--; break;
                case 'S': nextRow++; break;
                case 'W': nextCol--; break;
                case 'E': nextCol++; break;
            }
    
            // Verificar si la siguiente posición es válida
            if (nextRow < 0 || nextRow >= h || nextCol < 0 || nextCol >= w) {
                break; // Llegó al borde
            }
    
            // Verificar si hay un hueco
            if (holes[nextRow][nextCol] != null) {
                // La tile se cae en el hueco
                matrixStarting[row][col].makeInvisible();
                matrixStarting[row][col] = null;
                processed[row][col] = true;
                return;
            }
    
            // Verificar si hay otra tile
            if (matrixStarting[nextRow][nextCol] != null) {
                break; // No puede moverse más
            }
    
            // Mover la tile
            matrixStarting[nextRow][nextCol] = tile;
            matrixStarting[newRow][newCol] = null;
            tile.relocate(nextRow, nextCol);
            processed[nextRow][nextCol] = true;
            processed[newRow][newCol] = true;
    
            newRow = nextRow;
            newCol = nextCol;
        }
    }
    
    private boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
    
    public void tilt() {
        // Guardar el estado actual para poder revertirlo
        Tile[][] originalState = new Tile[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                originalState[i][j] = matrixStarting[i][j];
            }
        }
        
        char[] directions = {'N', 'S', 'E', 'W'};
        int bestScore = misPlacedTiles();
        char bestDirection = ' ';
        
        // Probar cada dirección y encontrar la mejor
        for (char direction : directions) {
            // Restaurar el estado original antes de probar cada dirección
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    matrixStarting[i][j] = originalState[i][j];
                }
            }
            
            // Probar la dirección actual
            tilt(direction);
            int currentScore = misPlacedTiles();
            
            // Si esta dirección da un mejor resultado, guardarla
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestDirection = direction;
            }
        }
        
        // Restaurar el estado original
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                matrixStarting[i][j] = originalState[i][j];
            }
        }
        
        // Si encontramos una dirección mejor, aplicarla
        if (bestDirection != ' ') {
            tilt(bestDirection);
            String directionName = "";
            switch (bestDirection) {
                case 'N': directionName = "Norte"; break;
                case 'S': directionName = "Sur"; break;
                case 'E': directionName = "Este"; break;
                case 'W': directionName = "Oeste"; break;
            }
            JOptionPane.showMessageDialog(null, "La mejor dirección es: " + directionName);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró una dirección que mejore la posición actual");
        }
    }
}
 


