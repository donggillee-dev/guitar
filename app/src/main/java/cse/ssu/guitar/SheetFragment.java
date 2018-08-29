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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import Network.PostNote;
import Network.PostRecord;
import VO.NoteVO;
import VO.SheetVO;


public class SheetFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private View view;
    private TextView name_view, date_view;
    private String name, date;
    private int flag=0;
    private RelativeLayout rl;
    private JSONArray array;
    private String response;

    public static SheetFragment newInstance() {
        return new SheetFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sheet, container, false);
        name_view = (TextView) view.findViewById(R.id.name);
        date_view = (TextView) view.findViewById(R.id.date);

        name = getArguments().getString("name");
        date = getArguments().getString("date");
        flag = getArguments().getInt("flag");
        response = getArguments().getString("response");

        name_view.setText(name);
        date_view.setText(date);

        createSheet(response);

        return view;
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
        if(flag==1)
            replaceFragment(MyPageFragment.newInstance());
        else if(flag==2)
            replaceFragment(SheetListFragment.newInstance());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }


    private void createSheet(String response) {
        NoteVO note;
        int count = 0;
        int line = 0;
        int size = 0;
        int i;
        int chordCount;
        int tempoCount;
        int octaveCount;
        int barCount;
        int noteCount;
        JSONArray chordArray = null;
        JSONArray tempoArray = null;
        JSONArray octaveArray = null;
        JSONArray barArray = null;
        JSONArray noteArray = null;

        JSONObject object = null;
        ArrayList<NoteVO> list = new ArrayList<>();
        rl = (RelativeLayout) view.findViewById(R.id.noteLayout);

        Log.v("response", response);

        try {
            object = new JSONObject(response);
            if(object.getString("status").compareTo("OK") == 0) {
                JSONArray array = object.getJSONArray("data");
                String tmpString = array.getString(0);
                JSONObject tmpObject = new JSONObject(tmpString);
                Log.v("tmpobject", tmpObject.toString());
                chordArray = tmpObject.getJSONArray("chords");
                tempoArray = tmpObject.getJSONArray("duration");
                octaveArray = tmpObject.getJSONArray("octave");
                barArray = tmpObject.getJSONArray("bar");
                noteArray = tmpObject.getJSONArray("pitch");

                size = noteArray.length();
            } else {
                Toast.makeText(getActivity(), "악보 생성 실패.", Toast.LENGTH_LONG).show();
                return;
            }
            chordCount = chordArray.length();
            tempoCount = tempoArray.length();
            octaveCount = octaveArray.length();
            barCount = barArray.length();
            noteCount = noteArray.length();
            barCount = (int) barArray.get(1);

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
                list.add(tmpNote);
            }

            for(i = 0; i < size; i++)
                Log.v("final result", list.get(i).toString());



        } catch (JSONException e) {
            e.printStackTrace();
        }



//
//        for (i = 0; i < 13; i++) {
//
//            i = 0;
//            for (; i < 13; i++) {
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(80, 80);
//
//                ImageView iv = new ImageView(getActivity());
//                iv.setImageResource(R.drawable.eighth_note);
//
//
//                lp.alignWithParent = true;
//                lp.leftMargin = i * 30 + 70;
//                lp.topMargin = i * 10;
//                iv.setLayoutParams(lp);
//                rl.addView(iv);
//            }
//        }
    }
}
