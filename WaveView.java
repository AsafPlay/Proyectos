package score;

public class RhythmQuantizer {

    // Devuelve duración en "ticks" de MusicXML (divisions) para una nota,
    // cuantizada a 1/16 (puedes cambiarlo luego).
    public static int quantizeDurationDivisions(double durSeconds, int bpm, int divisions) {
        double beatSec = 60.0 / bpm;              // duración de una negra en segundos (en 4/4)
        double durBeats = durSeconds / beatSec;   // duración en negras

        // grid 1/16 = 0.25 beats
        double grid = 0.25;
        double qBeats = Math.round(durBeats / grid) * grid;

        // evita cero
        qBeats = Math.max(grid, qBeats);

        // divisions: ticks por negra
        return (int) Math.round(qBeats * divisions);
    }

    public static String typeFromDivisions(int durDiv, int divisions) {
        double beats = durDiv / (double) divisions;

        if (beats >= 4.0) return "whole";
        if (beats >= 3.0) return "half";    // blanca con puntillo
        if (beats >= 2.0) return "half";
        if (beats >= 1.5) return "quarter"; // negra con puntillo
        if (beats >= 1.0) return "quarter";
        if (beats >= 0.75) return "eighth"; // corchea con puntillo
        if (beats >= 0.5) return "eighth";
        if (beats >= 0.25) return "16th";
        return "32nd";
    }

    public static boolean dottedFromDivisions(int durDiv, int divisions) {
        double beats = durDiv / (double) divisions;

        // tolerancia para redondeo
        double eps = 0.01;

        return
            Math.abs(beats - 1.5) < eps ||   // negra con puntillo
            Math.abs(beats - 3.0) < eps ||   // blanca con puntillo
            Math.abs(beats - 0.75) < eps;    // corchea con puntillo
    }

    public static int beatsFromDivisions(int durDiv, int divisions) {
        return (int) Math.round(durDiv / (double) divisions);
    }

}
