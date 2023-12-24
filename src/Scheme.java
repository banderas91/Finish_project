import javax.swing.*;
import java.awt.*;


public class Scheme extends JPanel {
    int width, height;

    public Scheme(int w, int h) {
        width = w;
        height = h;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //drawScheme(g);
    }

    private void drawScheme(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("(0,0)", 10, 20);
        g.drawLine(0, 0, width, 0);
        g.drawString("x", width - 35, 20);
        g.drawLine(0, 0, 0, height);
        g.drawString("y", 15, height - 55);

        for(int i = 1; i < width / 100; i++) {
            g.drawLine(i * 100, 0, i * 100, height - 55);
            g.drawString("" + (i * 100), 15 + i * 100, 20);
        }

        for(int i = 1; i < height / 100; i++) {
            g.drawLine(0, i * 100, width - 30, i * 100);
            g.drawString("" + (i * 100), 10, 15 + i * 100);
        }
    }
}
