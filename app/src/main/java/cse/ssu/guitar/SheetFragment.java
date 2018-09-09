package cse.ssu.guitar;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import Network.PostResendSheet;
import VO.NoteVO;
import VO.SheetVO;


public class SheetFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private View view;
    private TextView name_view, date_view;
    private String name, date;
    private int flag=0;
    private RelativeLayout rl;
    private String response;
    private String note;
    private String chord;
    private String data;
    private SheetVO sheet;

    public static SheetFragment newInstance() {
        return new SheetFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sheet, container, false);
        Log.v("in sheet", "insheet");
        name_view = (TextView) view.findViewById(R.id.name);
        date_view = (TextView) view.findViewById(R.id.date);

        name = getArguments().getString("name");
        date = getArguments().getString("date");
        flag = getArguments().getInt("flag");
        Log.v("flag", flag+"");

        name_view.setText(name);
        date_view.setText(date);

        if(flag == 1 || flag == 3) {
            note = getArguments().getString("data");
            chord = getArguments().getString("chord");
            Log.v("note", note);
            Log.v("chord", chord);
            createStoredSheet(name, date, note, chord);
        }
        else if(flag == 2) {
            Log.v("flag 2", "flag 2");
            response = getArguments().getString("response");
            Log.v("response", response);
            createSheet(name, date, response);
        }

        return view;
    }



    private void createSheet(String name, String date, String response) {
        int size = 0;
        int i;
        int chordCount;
        JSONArray chordTmpArray = null;
        JSONArray tempoArray = null;
        JSONArray octaveArray = null;
        JSONArray barArray = null;
        JSONArray noteArray = null;

        JSONObject object = null;
        ArrayList<String> chordArray = new ArrayList<>();
        sheet = new SheetVO(name, date);

        Log.v("response", response);

        try {
            object = new JSONObject(response);
            if(object.getString("status").compareTo("OK") == 0) {
                JSONArray array = object.getJSONArray("data");
                String tmpString = array.getString(0);
                JSONObject tmpObject = new JSONObject(tmpString);
                Log.v("tmpobject", tmpObject.toString());
                data = tmpObject.toString();

                chordTmpArray = tmpObject.getJSONArray("chords");
                tempoArray = tmpObject.getJSONArray("duration");
                octaveArray = tmpObject.getJSONArray("octave");
                barArray = tmpObject.getJSONArray("bar");
                noteArray = tmpObject.getJSONArray("pitch");

                size = noteArray.length();
            } else {
                Toast.makeText(getActivity(), "악보 생성 실패.", Toast.LENGTH_LONG).show();
                return;
            }
            chordCount = chordTmpArray.length();

            int tmpCount = 0;
            int barIndex = 0;
            for(i = 0; i < size; i++) {
                int bar = 0;
                tmpCount++;
                if(tmpCount == barArray.getInt(barIndex)) {
                    bar = 1;
                    tmpCount = 0;
                    barIndex++;
                }
                NoteVO tmpNote = new NoteVO(noteArray.getInt(i), tempoArray.getInt(i), octaveArray.getInt(i), bar);
                sheet.addNote(tmpNote);
                //list.add(tmpNote);
            }
            for(i = 0; i < chordCount; i++) {
                String tmp = chordTmpArray.getString(i);
                if(tmp.compareTo("...") == 0)
                    chordArray.add(chordArray.get(i - 1));
                else
                    chordArray.add(tmp);
            }

            for(i = 0; i < chordCount; i++) {
                Log.v("chord list", chordArray.get(i));
                sheet.addChord(chordArray.get(i));
            }

            insertNode(sheet, size);
            insertChord(sheet, size);

            ResendSheetTask task = new ResendSheetTask();
            task.start();
            task.join();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createStoredSheet(String name, String date, String note, String chord) {
        int size = 0;
        int i;
        sheet = new SheetVO(name, date);

        try {

            JSONArray tmpArray = new JSONArray(note);
            size = tmpArray.length();
            JSONObject tmpObject;
            NoteVO tmpNote;
            for(i = 0; i < size; i++) {
                tmpObject = tmpArray.getJSONObject(i);
                tmpNote = new NoteVO(tmpObject.getInt("note"), tmpObject.getInt("tempo"), tmpObject.getInt("octave"), tmpObject.getInt("bar"));
                sheet.addNote(tmpNote);
            }

            chord = chord.replace("[", " ");
            chord = chord.replace("]", " ");
            String chordList[] = chord.split(",");

            for(int j = 0; j < chordList.length; j++) {
                sheet.addChord(chordList[j].trim());
                Log.v("add chord", chordList[j].trim());
            }

            insertNode(sheet, size);
            insertChord(sheet, size);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertChord(SheetVO sheet, int size) {
        int node_count = 0;
        int leftMargin = 0;
        int lineMargin = 0;
        int lineCount = 0;
        int i = 0;
        int count = 0;
        ArrayList <NoteVO> list = sheet.getNote();
        ArrayList <String> chordArray = sheet.getChord();
        ArrayList <Integer> bar = new ArrayList<>();
        ArrayList <Integer> mid = new ArrayList<>();

        rl = (RelativeLayout) view.findViewById(R.id.noteLayout);

        count = 0;
        for(i = 0; i < list.size(); i++) {
            NoteVO tmp = list.get(i);
            count++;
            if(tmp.getBar() == 1) {
                bar.add(count);
                count = 0;
            }
        }
        for(i = 0; i < bar.size(); i++) {
            mid.add(bar.get(i) / 2 + 1);
        }




        Log.v("bar", bar.toString());
        Log.v("mid", mid.toString());

        for(i = 0; i < chordArray.size(); i++) {
            lineCount = i / 4;
            if(i % 4 == 0)
                leftMargin = 0;
            else if(i % 2 == 0)
                leftMargin = bar.get(i / 4) + 1;
            else if(i % 4 == 1)
                leftMargin = mid.get(i / 4);
            else
                leftMargin = bar.get(i / 4) + 1 + mid.get(i / 4);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(80, 80);

            TextView textView = new TextView(getActivity());
            textView.setText(chordArray.get(i));
            lp.alignWithParent = true;
            lp.leftMargin = leftMargin * 70 + 90;
            lp.topMargin = lineCount * 210;
            Log.v("left margin", lp.leftMargin+"");
            Log.v("top Margin",lp.topMargin+"" );
            textView.setLayoutParams(lp);
            rl.addView(textView);
        }







    }
    private void insertNode(SheetVO sheet, int size) {
        int[] marginList = {90, 80, 70, 60, 50, 40, 30};
        int i;
        NoteVO note;
        int topMargin = 0;
        int lineCount = 0;
        int lineMargin = 0;
        int leftMargin = 0;
        int nodeCount = 0;
        int startMargin = 0;
        int endMargin = 0;

        ArrayList <NoteVO> list = sheet.getNote();

        rl = (RelativeLayout) view.findViewById(R.id.noteLayout);

        for(i = 0; i < size; i++) {
            note = list.get(i);
            if(note.getNote() == 0 || note.getNote() == 1) {
                topMargin = marginList[0];
            }
            else if(note.getNote() == 2 || note.getNote() == 3) {
                topMargin = marginList[1];
            }
            else if(note.getNote() == 4) {
                topMargin = marginList[2];
            }
            else if(note.getNote() == 5 || note.getNote() == 6) {
                topMargin = marginList[3];
            }
            else if(note.getNote() == 7 || note.getNote() == 8) {
                topMargin = marginList[4];
            }
            else if(note.getNote() == 9 || note.getNote() == 10) {
                topMargin = marginList[5];
            }
            else if(note.getNote() == 11) {
                topMargin = marginList[6];
            }

            if(note.getOctave() == 0) {
                topMargin += 70;
            }
            else if(note.getOctave() == 2) {
                topMargin -= 70;
            }

            topMargin += lineMargin;



            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(80, 80);

            ImageView iv = new ImageView(getActivity());

            if(note.getTempo() == 1) {
                iv.setImageResource(R.drawable.sixteenth_note);
                if(note.getOctave() == 2) {
                    iv.setImageResource(R.drawable.sixteenth_note_reverse);
                    topMargin += 58;
                }
            }
            else if(note.getTempo() == 2) {
                iv.setImageResource(R.drawable.eighth_note);
                if(note.getOctave() == 2) {
                    iv.setImageResource(R.drawable.eighth_note_reverse);
                    topMargin += 58;
                }
            }
            else if(note.getTempo() == 3) {
                iv.setImageResource(R.drawable.eighth_note_dot);
                if(note.getOctave() == 2) {
                    iv.setImageResource(R.drawable.eighth_note_dot_reverse);
                    topMargin += 58;
                }
            }
            else if(note.getTempo() == 4) {
                iv.setImageResource(R.drawable.quarter_note);
                if(note.getOctave() == 2) {
                    iv.setImageResource(R.drawable.quarter_note_reverse);
                    topMargin += 58;
                }
            }
            else if(note.getTempo() == 5) {
                iv.setImageResource(R.drawable.quater_note_dot);
                if(note.getOctave() == 2){
                    iv.setImageResource(R.drawable.quarter_note_reverse_dot);
                   topMargin += 58;
                }
            }
            else if(note.getTempo() == 6) {
                iv.setImageResource(R.drawable.half_note);
                if(note.getOctave() == 2) {
                    iv.setImageResource(R.drawable.half_note_reverse);
                    topMargin += 58;
                }
            }
            else if(note.getTempo() == 7) {
                iv.setImageResource(R.drawable.half_note_dot);
                if(note.getOctave() == 2) {
                    iv.setImageResource(R.drawable.half_note_dot_reverse);
                    topMargin += 58;
                }
            }

            lp.alignWithParent = true;
            lp.leftMargin = leftMargin * 70 + 70;
            lp.topMargin = topMargin;
            iv.setLayoutParams(lp);
            rl.addView(iv);
            nodeCount++;

            if(nodeCount == 1)
                startMargin = leftMargin * 70 + 70 + 20;

            if(note.getBar() == 1) {
                leftMargin++;
                lineCount++;
                nodeCount = 0;
                endMargin = leftMargin * 70 + 50;
                if(lineCount == 1) {
                    RelativeLayout.LayoutParams lpLine = new RelativeLayout.LayoutParams(80, 80);

                    ImageView ivLine = new ImageView(getActivity());
                    ivLine.setImageResource(R.drawable.line);
                    lpLine.alignWithParent = true;

                    lpLine.leftMargin = leftMargin * 70 + 70;
                    lpLine.topMargin = lineMargin + 57;
                    ivLine.setLayoutParams(lpLine);
                    rl.addView(ivLine);
                }
            }

            leftMargin++;

            if(lineCount == 2) {
                lineCount = 0;
                lineMargin += 210;
                leftMargin = 0;
            }
        }
    }

    private class ResendSheetTask extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            super.run();
            PostResendSheet resend = new PostResendSheet();
            response = null;
            try {
                response = resend.post(MainActivity.serverUrl+"sheet/store", LoginActivity.token, LoginActivity.id, name, date, sheet.getNote().toString(), sheet.getChord().toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onBack() {
        MainActivity activity = (MainActivity) getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnKeyBackPressedListener(null);
        if(flag == 1)
            replaceFragment(MyPageFragment.newInstance());
        else if(flag == 2)
            replaceFragment(MakeSheetFragment.newInstance());
        else if(flag == 3)
            replaceFragment(SheetListFragment.newInstance());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }
}
