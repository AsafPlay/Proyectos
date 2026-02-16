package model;

public class Note {
    public double start, end, dur, freq;
    public int midi, oct, cents;
    public String name, figure;
    public int velocity;

    public Note(double s,double e,double d,double f,int m,String nm,int o,int c,String fig){
        start=s; end=e; dur=d; freq=f;
        midi=m; name=nm; oct=o; cents=c; figure=fig;
        this.velocity = 80; // valor por defecto
    }
}
