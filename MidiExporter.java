package audio;

import model.WavData;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class WavReader {

    public static WavData readWavPcm16Full(String path) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
            String riff = readStr(in,4);
            if (!"RIFF".equals(riff)) throw new IOException("No RIFF");
            skip(in,4);
            String wave = readStr(in,4);
            if (!"WAVE".equals(wave)) throw new IOException("No WAVE");

            short audioFormat=0, numChannels=0;
            int sr=0;
            byte[] data = null;

            while (in.available()>0){
                String id = readStr(in,4);
                int size = Integer.reverseBytes(in.readInt());
                if ("fmt ".equals(id)){
                    audioFormat = Short.reverseBytes(in.readShort());
                    numChannels = Short.reverseBytes(in.readShort());
                    sr = Integer.reverseBytes(in.readInt());
                    skip(in,6);
                    skip(in,2);
                    int rem = size - 16;
                    if (rem>0) skip(in, rem);
                } else if ("data".equals(id)){
                    data = in.readNBytes(size);
                } else {
                    skip(in, size);
                }
            }

            if (audioFormat!=1) throw new IOException("No PCM");
            if (data==null) throw new IOException("Sin data");

            int total = data.length/2;
            float[] x = new float[total];
            for (int i=0;i<total;i++){
                int lo = data[2*i] & 0xFF;
                int hi = data[2*i+1];
                short s = (short)((hi<<8)|lo);
                x[i] = s/32768f;
            }
            return new WavData(sr, numChannels==2, x);
        }
    }

    static String readStr(DataInputStream in, int n) throws IOException {
        byte[] b = in.readNBytes(n);
        return new String(b, StandardCharsets.US_ASCII);
    }

    static void skip(DataInputStream in, int n) throws IOException {
        long left=n;
        while (left>0){
            long s=in.skip(left);
            if (s<=0) break;
            left-=s;
        }
    }

    public static float[] downmixStereo(float[] xLR){
        int N = xLR.length/2;
        float[] y = new float[N];
        for (int i=0;i<N;i++) y[i] = 0.5f*(xLR[2*i] + xLR[2*i+1]);
        return y;
    }
}
