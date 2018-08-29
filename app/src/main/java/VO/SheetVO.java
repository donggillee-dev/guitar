package VO;

import java.util.ArrayList;

/**
 * Created by misconstructed on 2018. 8. 28..
 */

public class SheetVO {
    private String name;
    private String date;
    private ArrayList <NoteVO> note;

    public SheetVO(String name, String date) {
        this.name = name;
        this.date = date;
        this.note = new ArrayList<>();
    }

    public int addNote(NoteVO input) {
        note.add(input);
        return note.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<NoteVO> getNote() {
        return note;
    }

    public void setNote(ArrayList<NoteVO> note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "SheetVO{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", note=" + note +
                '}';
    }
}
