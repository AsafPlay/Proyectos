package model;

public class WavData {
    public int sampleRate;
    public boolean stereo;
    public float[] samples;

    public WavData(int sr, boolean st, float[] s){
        sampleRate = sr;
        stereo = st;
        samples = s;
    }
}
