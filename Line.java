
import java.awt.geom.Line2D;

public class Line {
    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;
    private String color;
    private boolean isVisible;

    public Line(int xStart, int yStart, int xEnd, int yEnd) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.color = "white";
        this.isVisible = false;
    }

    public Line() {
        this(0, 0, 100, 100);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void makeVisible() {
        isVisible = true;
        draw();
    }

    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    public void moveHorizontal(int distance) {
        erase();
        xStart += distance;
        xEnd += distance;
        draw();
    }

    public void moveVertical(int distance) {
        erase();
        yStart += distance;
        yEnd += distance;
        draw();
    }

    public void setPosition(int xStart, int yStart, int xEnd, int yEnd) {
        erase();
        this.xStart=xStart;
        this.xEnd = xEnd ;
        this.yStart = yStart;
        this.yEnd = yEnd;
        draw();
    }

    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }

    private void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color, new Line2D.Double(xStart, yStart, xEnd, yEnd));
            canvas.wait(10);
        }
    }

    private void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}
