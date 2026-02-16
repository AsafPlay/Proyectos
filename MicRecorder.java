package audio;

import model.Note;
import java.util.*;
import static java.lang.Math.*;
import model.InstrumentType;
import audio.InstrumentClassifier;

public class AudioAnalyzer {

    static final int FRAME = 1024;
    static final int HOP   = 512;
    static final double K_ONSET = 2.0;
    static final int FMIN = 70;
    static final int FMAX = 1000;
    static final double MIN_NOTE_S = 0.07;

    

    public static model.AnalysisResult analyze(float[] x, int fs) {
        List<float[]> frames = frameSignal(x, FRAME, HOP);
        List<Double> E = energyPerFrame(frames);
        int bpm = estimateBpm(E, fs, HOP);
        List<Integer> onsets = pickOnsets(E, K_ONSET);
        if (onsets.isEmpty()) onsets.add(0);
        onsets.add(frames.size()-1);

        List<Note> notes = new ArrayList<>();
        for (int i=0;i<onsets.size()-1;i++) {
            int f0 = onsets.get(i);
            int f1 = onsets.get(i+1);
            float[] seg = concatFrames(frames, f0, f1, FRAME, HOP);
            // === Intensidad (energía RMS) del segmento ===
            double energy = 0;
            for (float v : seg) {
                energy += v * v;
            }
            energy = Math.sqrt(energy / seg.length);
            int velocity = (int) Math.min(127, Math.max(35, energy * 5000));

            if (seg.length==0) continue;

            double f0hz = pitchACF(seg, fs, FMIN, FMAX);
            if (Double.isNaN(f0hz) || f0hz<=0) continue;

            double tStart = (double)(f0 * HOP) / fs;
            double tEnd   = (double)(f1 * HOP) / fs;
            double dur    = Math.max(0, tEnd - tStart);
            if (dur < MIN_NOTE_S) continue;

            int midi = freqToMidi(f0hz);
            NameOct no = midiToName(midi);
            String fig = quantizeFigure(dur);

            Note note = new Note(
                tStart, tEnd, dur, f0hz,
                midi, no.name, no.oct, 0, fig
            );
            note.velocity = velocity;
            notes.add(note);
        }
        // ===== estadísticas de pitch =====
            double sumFreq = 0;
            double sumSq = 0;
            int count = 0;

            for (Note n : notes) {
                sumFreq += n.freq;
                sumSq += n.freq * n.freq;
                count++;
            }

            double avgFreq = sumFreq / Math.max(1, count);
            double pitchStd = Math.sqrt(
                    sumSq / Math.max(1, count) - avgFreq * avgFreq
            );

                //tiempo de ataque
                double attackMs = estimateAttackTime(x, fs);

                // clasificación del instrumento
                InstrumentType instrument = InstrumentClassifier.classify(
                        x, fs, avgFreq, pitchStd, attackMs
                );

        return new model.AnalysisResult(notes, bpm, instrument);

    }

    static float[] concatFrames(List<float[]> frames,int i0,int i1,int N,int H){
        if (i1<=i0) return new float[0];
        if (i1-i0==1) return Arrays.copyOf(frames.get(i0), N);
        int total = N + (i1-i0-1)*H;
        float[] y = new float[total]; int pos=0;
        for (int i=i0;i<i1;i++){
            float[] fr = frames.get(i);
            if (i==i0){ System.arraycopy(fr,0,y,0,N); pos=N; }
            else { System.arraycopy(fr,0,y,pos,H); pos+=H; }
        }
        return y;
    }

    static double pitchACF(float[] x, int fs, int fmin, int fmax){
        if (x.length<4) return Double.NaN;
        int minTau = max(2, fs/fmax);
        int maxTau = min(x.length/2-1, max(minTau+1, fs/fmin));
        double E=0; for (float v: x) E += v*v;
        if (E<1e-12) return Double.NaN;
        int bestTau=-1; double bestVal=-1e9;
        for (int tau=minTau; tau<=maxTau; tau++){
            double acc=0;
            int lim = x.length - tau;
            for (int n=0;n<lim;n++) acc += x[n]*x[n+tau];
            double val = acc/E;
            if (val>bestVal){ bestVal=val; bestTau=tau; }
        }
        if (bestTau<=0) return Double.NaN;
        return (double)fs / bestTau;
    }

    static List<float[]> frameSignal(float[] x, int N, int H){
        List<float[]> fs = new ArrayList<>();
        for (int i=0;i+N<=x.length;i+=H){
            float[] f = new float[N];
            System.arraycopy(x, i, f, 0, N);
            fs.add(f);
        }
        return fs;
    }

    static List<Double> energyPerFrame(List<float[]> frames){
        List<Double> E = new ArrayList<>();
        for (float[] fr: frames){ double e=0; for (float v: fr) e+=v*v; E.add(e); }
        return E;
    }

    static List<Integer> pickOnsets(List<Double> E, double k){
        int N=E.size(); double mean=0, m2=0;
        for (double v:E) mean+=v; mean/=max(1,N);
        for (double v:E) m2+=(v-mean)*(v-mean);
        double sd = sqrt(m2/max(1,N));
        double thr = mean + k*sd;
        List<Integer> peaks = new ArrayList<>();
        for (int i=1;i<N-1;i++)
            if (E.get(i)>thr && E.get(i)>E.get(i-1) && E.get(i)>E.get(i+1)) peaks.add(i);
        return peaks;
    }

    static double estimateAttackTime(float[] x, int fs) {
        double max = 0;
        for (float v : x) {
            max = Math.max(max, Math.abs(v));
        }

        double thr = max * 0.9;
        for (int i = 0; i < x.length; i++) {
            if (Math.abs(x[i]) >= thr) {
                return i * 1000.0 / fs; // milisegundos
            }
        }
        return 100; // ataque lento por defecto
    }

    static int estimateBpm(List<Double> onsetEnv, int fs, int hop) {
        // onsetEnv: energía por frame (o envelope). Vamos a autocorrelacionarla.
        int N = onsetEnv.size();
        if (N < 16) return 120;

        // Normaliza (quita media) y aplica rectificación suave
        double mean = 0;
        for (double v : onsetEnv) mean += v;
        mean /= N;

        double[] x = new double[N];
        for (int i = 0; i < N; i++) {
            double v = onsetEnv.get(i) - mean;
            x[i] = Math.max(0, v); // solo aumentos
        }

        // Rango típico de BPM
        int bpmMin = 60, bpmMax = 200;

        // Convertir BPM -> lag en frames
        // secondsPerBeat = 60/BPM
        // framesPerBeat = secondsPerBeat * fs / hop
        int lagMin = (int) Math.round((60.0 / bpmMax) * fs / hop);
        int lagMax = (int) Math.round((60.0 / bpmMin) * fs / hop);
        lagMin = Math.max(lagMin, 1);
        lagMax = Math.min(lagMax, N - 2);

        // Autocorrelación en lags candidatos
        int bestLag = lagMin;
        double best = -1e18;

        for (int lag = lagMin; lag <= lagMax; lag++) {
            double acc = 0;
            for (int i = 0; i + lag < N; i++) {
                acc += x[i] * x[i + lag];
            }
            if (acc > best) {
                best = acc;
                bestLag = lag;
            }
        }

        double framesPerBeat = bestLag;
        double secondsPerBeat = framesPerBeat * hop / (double) fs;
        int bpm = (int) Math.round(60.0 / secondsPerBeat);

        // Ajuste típico: a veces cae a mitad/doble
        if (bpm < 70) bpm *= 2;
        if (bpm > 180) bpm = bpm / 2;

        return Math.max(60, Math.min(200, bpm));
    }

    static int freqToMidi(double f0){
        double n = 69.0 + 12.0*(log(f0/440.0)/log(2.0));
        return (int)round(n);
    }

    static NameOct midiToName(int midi){
        String[] names={"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
        return new NameOct(names[midi%12], midi/12 - 1);
    }

    static String quantizeFigure(double durSec){
        if (durSec >= 2.0) return "Redonda";
        if (durSec >= 1.0) return "Blanca";
        if (durSec >= 0.5) return "Negra";
        if (durSec >= 0.25) return "Corchea";
        return "Semi";
    }

    static class NameOct {
        String name; int oct;
        NameOct(String n,int o){name=n;oct=o;}
    }
}
