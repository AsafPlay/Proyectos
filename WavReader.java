package audio;

import model.InstrumentType;

public class InstrumentClassifier {

    public static InstrumentType classify(
            float[] samples,
            int sampleRate,
            double avgFreq,
            double pitchStd,
            double attackTimeMs
    ) {

        // BAJO: frecuencia muy baja
        if (avgFreq < 120) {
            return InstrumentType.BAJO;
        }

        // VOZ: vibrato + frecuencia media
        if (avgFreq > 120 && avgFreq < 350 && pitchStd > 5.0) {
            return InstrumentType.VOZ;
        }

        // PIANO: ataque muy rápido, amplio rango
        if (attackTimeMs < 30 && avgFreq > 200 && avgFreq < 2000) {
            return InstrumentType.PIANO;
        }

        // GUITARRA / UKULELE
        if (avgFreq > 150 && avgFreq < 1000 && pitchStd < 3.0) {
            if (avgFreq < 300) {
                return InstrumentType.UKULELE;
            }
            return InstrumentType.GUITARRA;
        }

        // VIENTO: pitch muy estable
        if (pitchStd < 1.0 && avgFreq > 200 && avgFreq < 2000) {
            return InstrumentType.VIENTO;
        }

        return InstrumentType.DESCONOCIDO;
    }
}
