package model;

import java.util.List;

public class AnalysisResult {

    public final List<Note> notes;
    public final int bpm;
    public final InstrumentType instrument;

    public AnalysisResult(List<Note> notes, int bpm, InstrumentType instrument) {
        this.notes = notes;
        this.bpm = bpm;
        this.instrument = instrument;
    }
}
