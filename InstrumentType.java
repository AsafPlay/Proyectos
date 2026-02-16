package midi;

import model.InstrumentType;

public class MidiInstrumentMapper {

    public static int programFor(InstrumentType type) {
        switch (type) {
            case PIANO:     return 0;   // Acoustic Grand Piano
            case GUITARRA:  return 24;  // Acoustic Guitar (nylon)
            case UKULELE:   return 24;  // Guitar (nylon)
            case BAJO:      return 32;  // Acoustic Bass
            case VIENTO:    return 73;  // Flute
            case VOZ:       return 52;  // Choir Aahs
            default:        return 0;
        }
    }

    public static String nameFor(InstrumentType type) {
        switch (type) {
            case PIANO:    return "Piano";
            case GUITARRA: return "Guitarra";
            case UKULELE:  return "Ukulele";
            case BAJO:     return "Bajo";
            case VIENTO:   return "Viento";
            case VOZ:      return "Voz";
            default:       return "Instrumento";
        }
    }
}
