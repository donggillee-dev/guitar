package VO;

/**
 * Created by misconstructed on 2018. 8. 28..
 */

public class NoteVO {
    private int note;
    private int tempo;
    private int octave;
    private int bar;

    public NoteVO(int note, int tempo, int octave, int bar) {
        //0 ~ 11
        this.note = note;
        //1 ~ 7
        this.tempo = tempo;
        //0 ~ 2
        this.octave = octave;
        //0, 1
        this.bar = bar;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    @Override
    public String toString() {
        return "{" +
                "\"note\":\"" + note + "\"" +
                ", \"tempo\":\"" + tempo + "\"" +
                ", \"octave\":\"" + octave + "\"" +
                ", \"bar\":\"" + bar + "\"" +
                "}";
    }
}
