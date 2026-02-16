package score;

import model.Note;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class AsciiStaffExporter {

    public double start;
    public double end;
    public double dur;
    public double freq;
    public int midi;
    public String name;
    public int oct;
    public int velocity;
    public String figure; 

    public static void save(List<Note> notes, int bpm, File file) throws Exception {
        FileWriter w = new FileWriter(file);
        drawStaff(notes, w);
        w.close();
    }

    // ======= PENTAGRAMA =======
    private static void drawStaff(List<Note> notes, FileWriter w) throws Exception {
        int rows = 11;
        int cols = 200;

        char[][] staff = new char[rows][cols];

        // Inicializar con espacios
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                staff[r][c] = ' ';
            }
        }

        // Líneas del pentagrama
        int[] lines = {2, 4, 6, 8, 10};
        for (int l : lines) {
            for (int c = 0; c < cols; c++) {
                staff[l][c] = '-';
            }
        }

        int col = 2;

        for (Note n : notes) {
            int row = pitchToRow(n.midi);
            if (row < 0 || row >= rows) continue;

            char sym = symbolFromFigure(n.figure);
            staff[row][col] = sym;

            col += advanceFromFigure(n.figure);
            if (col >= cols - 1) break;
        }

        // Imprimir
        for (int r = 0; r < rows; r++) {
            w.write(new String(staff[r]) + "\n");
        }
    }

    //FIGURAS
    private static char symbolFromFigure(String fig) {
        if (fig == null) return 'o';

        if (fig.equals("Redonda")) return 'O';
        if (fig.equals("Blanca"))  return 'o';
        if (fig.equals("Negra"))   return '*';
        if (fig.equals("Corchea")) return '.';
        if (fig.equals("Semi"))    return ',';

        return 'o';
    }

    private static int advanceFromFigure(String fig) {
        if (fig == null) return 4;

        if (fig.equals("Redonda")) return 8;
        if (fig.equals("Blanca"))  return 6;
        if (fig.equals("Negra"))   return 4;
        if (fig.equals("Corchea")) return 3;
        if (fig.equals("Semi"))    return 2;

        return 4;
    }

    //ALTURA
    private static int pitchToRow(int midi) {
        return 10 - (midi - 60);
    }
}
