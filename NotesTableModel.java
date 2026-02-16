package score;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageScoreExporter {

    public static void export(JPanel panel, File file) throws Exception {

        int w = panel.getWidth();
        int h = panel.getHeight();

        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2 = img.createGraphics();

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        panel.paint(g2);
        g2.dispose();

        ImageIO.write(img, "png", file);
    }
}
