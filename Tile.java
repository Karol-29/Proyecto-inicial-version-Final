public class Tile extends Rectangle {

    private String color;  // Campo para almacenar el color de la tile
    private int yPosition; // Campo para almacenar la posición Y específica de la tile
    private int xPosition;
    private int id;
    public Tile(int xPosition, int yPosition, String color) {
        super(50, 50, xPosition*50, yPosition*50, color);  // Llama al constructor de Rectangle
        this.color = color;  // Establece el color
        this.yPosition = yPosition; 
        this.xPosition = xPosition; // Guarda la posición X
        this.id=-1;
    }
    //retorna color
    public String getColor() {
        return color;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id){
        this.id=id;
    }
    
    public int getRow(){
        return this.yPosition/50;
    }
    
    public int getColumn(){
        return this.xPosition/50;
    }
    
    public void makeVisible() {
        super.makeVisible();  // Llama al método de Rectangle para hacerlo visible
    }

    public void makeInvisible() {
        super.makeInvisible();  
    }

    public void deleteTile() {
        this.makeInvisible();  // Hace invisible la tile
    }

    public int getYPosition() {
        return yPosition;  // Devuelve la fila actual de la tile
    }

    public int getXPosition() {
        return xPosition;  // Devuelve la columna actual de la tile
    }

    public void relocate(int newRow, int newColumn) {
    // Calcula las nuevas posiciones x e y en función de la fila y columna dadas
        makeInvisible();  // Hace la tile invisible antes de moverla
        
        // Actualiza directamente las posiciones
        int newXPosition = newColumn * 50;
        int newYPosition = newRow * 50;
        
        // Mueve la tile horizontal y verticalmente a su nueva posición
        this.moveHorizontal(newXPosition - this.xPosition);
        this.moveVertical(newYPosition - this.yPosition);
        setPosition(this.xPosition-(newXPosition - this.xPosition),this.yPosition-(newYPosition - this.yPosition));
        makeVisible();  // Hace la tile visible después de moverla
    }
    public void setPosition(int xPosition,int yPosition){
        this.xPosition=xPosition;
        this.yPosition=yPosition;
    }
    public void propiedades() {
    System.out.println("xPosition: " + xPosition + ", yPosition: " + yPosition +"id:" +id);
    }

}