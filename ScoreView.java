package score;

import model.Note;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MusicXMLExporter {

    public static void save(List<Note> notes, int bpm, File file) throws Exception {
        FileWriter w = new FileWriter(file);

        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        w.write("<!DOCTYPE score-partwise PUBLIC\n");
        w.write("  \"-//Recordare//DTD MusicXML 3.1 Partwise//EN\"\n");
        w.write("  \"http://www.musicxml.org/dtds/partwise.dtd\">\n");
        w.write("<score-partwise version=\"3.1\">\n");
        w.write("  <part-list>\n");
        w.write("    <score-part id=\"P1\">\n");
        w.write("      <part-name>Piano</part-name>\n");
        w.write("    </score-part>\n");
        w.write("  </part-list>\n");

        int divisions = 480; // resolución rítmica
        int measure = 1;
        int beatsPerMeasure = 4;
        int measureBeatsUsed = 0;
        double beatSec = 60.0 / bpm;
        double timeInMeasure = 0;
        double beatsUsed = 0.0;
        
        String clef = detectClef(notes);
        w.write(measureHeader(divisions));


        double currentTime = 0.0;

        for (Note n : notes) {

            // 1) Silencio antes de la nota
            double gap = n.start - currentTime;
            if (gap > 0.01) {
                int restDiv = RhythmQuantizer.quantizeDurationDivisions(gap, bpm, divisions);
                w.write(restXML(restDiv, divisions));
                beatsUsed += RhythmQuantizer.beatsFromDivisions(restDiv, divisions);
            }

            // 2) Nota (puede dividirse y ligarse)
            int totalDiv = RhythmQuantizer.quantizeDurationDivisions(n.dur, bpm, divisions);
            int remainingDiv = totalDiv;

            while (remainingDiv > 0) {

                double beatsLeft = beatsPerMeasure - beatsUsed;
                int maxDivHere = (int) (beatsLeft * divisions);

                int writeDiv = Math.min(remainingDiv, maxDivHere);

                boolean tieStart = remainingDiv == totalDiv && remainingDiv > writeDiv;
                boolean tieStop  = remainingDiv < totalDiv;

                w.write(tiedNoteXML(n, writeDiv, bpm, divisions, tieStart, tieStop));

                remainingDiv -= writeDiv;
                beatsUsed += RhythmQuantizer.beatsFromDivisions(writeDiv, divisions);

                if (beatsUsed >= beatsPerMeasure) {
                    w.write("    </measure>\n");
                    w.write("    <measure>\n");
                    beatsUsed = 0;
                }
            }

            currentTime = n.end;
        }
        
        if (beatsUsed > 0) {
        }

        w.write("    </measure>\n");
        w.write("  </part>\n");
        w.write("</score-partwise>\n");
        w.close();
    }

    private static String measureHeader(int divisions) {
        return
            "    <measure number=\"1\">\n" +
            "      <attributes>\n" +
            "        <divisions>" + divisions + "</divisions>\n" +
            "        <key>\n" +
            "          <fifths>0</fifths>\n" +
            "        </key>\n" +
            "        <time>\n" +
            "          <beats>4</beats>\n" +
            "          <beat-type>4</beat-type>\n" +
            "        </time>\n" +
            "        <staves>2</staves>\n" +
            "        <clef number=\"1\">\n" +
            "          <sign>G</sign>\n" +
            "          <line>2</line>\n" +
            "        </clef>\n" +
            "        <clef number=\"2\">\n" +
            "          <sign>F</sign>\n" +
            "          <line>4</line>\n" +
            "        </clef>\n" +
            "      </attributes>\n";
    }

    private static String restXML(int durDiv, int divisions) {
        String type = RhythmQuantizer.typeFromDivisions(durDiv, divisions);

        return
            "      <note>\n" +
            "        <rest/>\n" +
            "        <duration>" + durDiv + "</duration>\n" +
            "        <voice>1</voice>\n" +
            "        <type>" + type + "</type>\n" +
            "      </note>\n";
    }

    
    private static String tiedNoteXML(Note n, int durDiv, int bpm, int divisions, boolean tieStart, boolean tieStop) {

      String staff = (n.midi >= 60) ? "1" : "2";
      String step = n.name.substring(0,1);
      boolean sharp = n.name.contains("#");
      String type = RhythmQuantizer.typeFromDivisions(durDiv, divisions);
      boolean dotted = RhythmQuantizer.dottedFromDivisions(durDiv, divisions);

      String tie1 = tieStart ? "          <tie type=\"start\"/>\n" : "";
      String tie2 = tieStop  ? "          <tie type=\"stop\"/>\n"  : "";

      String notation = "";
      if (tieStart || tieStop) {
          notation =
              "        <notations>\n" +
              (tieStart ? "          <tied type=\"start\"/>\n" : "") +
              (tieStop  ? "          <tied type=\"stop\"/>\n"  : "") +
              "        </notations>\n";
      }

      return
          "      <note>\n" +
          "        <pitch>\n" +
          "          <step>" + step + "</step>\n" +
          (sharp ? "          <alter>1</alter>\n" : "") +
          "          <octave>" + n.oct + "</octave>\n" +
          "        </pitch>\n" +
          tie1 + tie2 +
          "        <duration>" + durDiv + "</duration>\n" +
          "        <voice>1</voice>\n" +
          "        <type>" + type + "</type>\n" +
          (dotted ? "        <dot/>\n" : "") +
          "        <staff>" + staff + "</staff>\n" +
          notation +
          "      </note>\n";
    }

    private static String detectClef(List<Note> notes) {
        if (notes.isEmpty()) return "G";

        double sum = 0;
        for (Note n : notes) {
            sum += n.midi;
        }

        double avgMidi = sum / notes.size();

        // Do central = 60
        return avgMidi < 60 ? "F" : "G";
    }

}
