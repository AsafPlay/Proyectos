package midi;

import model.Note;

import javax.sound.midi.*;
import java.io.File;
import java.util.List;

public class MidiExporter {

    public static void save(List<Note> notes, int bpm, File outputFile) throws Exception {
        Sequence seq = buildSequence(notes, bpm);
        MidiSystem.write(seq, 1, outputFile);
    }

    private static Sequence buildSequence(List<Note> notes, int bpm) throws Exception {
        Sequence seq = new Sequence(Sequence.PPQ, 480);
        Track track = seq.createTrack();

        // Tempo
        MetaMessage tempo = new MetaMessage();
        int mpq = 60000000 / bpm;
        byte[] data = new byte[]{
                (byte) (mpq >> 16),
                (byte) (mpq >> 8),
                (byte) mpq
        };
        tempo.setMessage(0x51, data, 3);
        track.add(new MidiEvent(tempo, 0));

        // Instrumento: Piano acústico
        ShortMessage program = new ShortMessage();
        program.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
        track.add(new MidiEvent(program, 0));

        // Pedal de sustain ON (piano real)
        ShortMessage pedalOn = new ShortMessage();
        pedalOn.setMessage(ShortMessage.CONTROL_CHANGE, 0, 64, 127);
        track.add(new MidiEvent(pedalOn, 0));

        double ticksPerSecond = (bpm / 60.0) * seq.getResolution();

        for (Note n : notes) {
            double jitter = (Math.random() - 0.5) * 0.015; // ±15 ms
            long start = (long) ((n.start + jitter) * ticksPerSecond);
            long end = (long) ((n.end + 0.03) * ticksPerSecond);

            ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, 0, n.midi, n.velocity);
            track.add(new MidiEvent(on, start));

            ShortMessage off = new ShortMessage();
            off.setMessage(ShortMessage.NOTE_OFF, 0, n.midi, 0);
            track.add(new MidiEvent(off, end));
        }
        ShortMessage pedalOff = new ShortMessage();
        pedalOff.setMessage(ShortMessage.CONTROL_CHANGE, 0, 64, 0);
        track.add(new MidiEvent(pedalOff, track.ticks() + 10));

        return seq;
    }
}
