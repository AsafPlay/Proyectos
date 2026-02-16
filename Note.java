package midi;

import model.Note;
import model.InstrumentType;
import midi.MidiInstrumentMapper;

import javax.sound.midi.*;
import java.util.List;


public class MidiPlayer {

    private Sequencer sequencer;

    public MidiPlayer() throws MidiUnavailableException {
        sequencer = MidiSystem.getSequencer(false);
        sequencer.open();

        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();

        sequencer.getTransmitter().setReceiver(synth.getReceiver());
    }

    public void play(List<Note> notes, int bpm, InstrumentType instrument) throws Exception {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }

        Sequence seq = buildSequence(notes, bpm, instrument);
        sequencer.setSequence(seq);
        sequencer.start();
    }

    public void stop() {
        if (sequencer.isRunning()) {
            sequencer.stop();
        }
    }

    public void close() {
        sequencer.close();
    }

    private Sequence buildSequence(List<Note> notes, int bpm, InstrumentType instrument) throws Exception {
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
        int programNum = 0;

        ShortMessage program = new ShortMessage();
        program.setMessage(ShortMessage.PROGRAM_CHANGE, 0, programNum, 0);
        track.add(new MidiEvent(program, 0));

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

        return seq;
    }
}
