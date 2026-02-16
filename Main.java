package audio;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class MicRecorder {

    public interface Callback {
        void onAudioCaptured(float[] samples, int sampleRate);
        void onError(String msg);
    }

    private TargetDataLine line;
    private boolean recording = false;

    public void recordSeconds(int seconds, Callback cb) {
        if (recording) return;

        try {
            int sampleRate = 44100;
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                cb.onError("Micrófono no compatible");
                return;
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            recording = true;

            new Thread(() -> capture(seconds, format, cb)).start();

        } catch (Exception ex) {
            cb.onError(ex.getMessage());
        }
    }

    private void capture(int seconds, AudioFormat format, Callback cb) {
        int bufferSize = 4096;
        int totalBytes = (int) (format.getSampleRate()
                * format.getFrameSize()
                * seconds);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int read = 0;

        try {
            while (read < totalBytes) {
                int n = line.read(buffer, 0,
                        Math.min(buffer.length, totalBytes - read));
                out.write(buffer, 0, n);
                read += n;
            }
        } catch (Exception ex) {
            cb.onError(ex.getMessage());
        } finally {
            line.stop();
            line.close();
            recording = false;
        }

        float[] samples = bytesToFloats(out.toByteArray());
        cb.onAudioCaptured(samples, (int) format.getSampleRate());
    }

    private float[] bytesToFloats(byte[] audio) {
        int n = audio.length / 2;
        float[] x = new float[n];
        for (int i = 0; i < n; i++) {
            int lo = audio[2 * i] & 0xFF;
            int hi = audio[2 * i + 1];
            short s = (short) ((hi << 8) | lo);
            x[i] = s / 32768f;
        }
        return x;
    }
}
