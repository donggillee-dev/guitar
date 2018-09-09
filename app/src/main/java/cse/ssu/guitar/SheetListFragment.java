package cse.ssu.guitar;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Network.GetSheet;
import VO.NoteVO;
import VO.SheetVO;

public class SheetListFragment extends Fragment implements MainActivity.onKeyBackPressedListener{
    public static SheetListFragment newInstance() {
        return new SheetListFragment();
    }

    private ListView list;
    private ListViewAdapter adapter;
    private View view;
    private TextView trackNum;
    private JSONArray jArray;
    private ArrayList <SheetVO> sheetList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sheet_list, container, false);
        sheetList = new ArrayList<>();
        adapter = new ListViewAdapter();

        trackNum = (TextView)view.findViewById(R.id.number);
        list = (ListView)view.findViewById(R.id.sheet_list);
        list.setAdapter(adapter);

        //현재 날짜 구하기
        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_string = sdf.format(date);
        Log.v("debug", date_string);

        GetSheetTask task = new GetSheetTask();
        task.start();
        try {
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        trackNum.setText(sheetList.size() + " Track");

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name_view, date_view;
                String name, date;
                Fragment fragment = SheetFragment.newInstance();
                Bundle bundle = new Bundle();

                name_view = (TextView)view.findViewById(R.id.textView1);
                date_view = (TextView)view.findViewById(R.id.textView2);
                name = name_view.getText().toString();
                date = date_view.getText().toString();

                Log.v("debug", "item selected > " + name + " : " + date);
                SheetVO sheetVO = sheetList.get(position);

                bundle.putString("name", sheetVO.getName());
                bundle.putString("date", sheetVO.getDate());
                bundle.putString("data", sheetVO.getNote().toString());
                bundle.putString("chord", sheetVO.getChord().toString());
                bundle.putInt("flag",3);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });
        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

    private class GetSheetTask extends Thread {
        @Override
        public void run() {
            super.run();
            GetSheet sheetSearch = new GetSheet();
            String response = null;
            try {
                Log.v("sheet list ", "sheet list");
                response = sheetSearch.run(MainActivity.serverUrl+"sheet/load", LoginActivity.token, LoginActivity.id);
                Log.v("sheet response", response);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jObject = null;

            try {
                try {
                    jObject = new JSONObject(response);
                    String returnValue = jObject.getString("status");
                    Log.v("return Value", returnValue+"");

                    if(returnValue.compareTo("ERROR") == 0) {
                        Log.v("ERROR", "no sheet list in DB");
                    }
                } catch (Exception e) {
                    jArray = new JSONArray(response);

                    for(int i = 0; i < jArray.length(); i++) {
                        JSONObject object = jArray.getJSONObject(i);
                        SheetVO sheet = new SheetVO(object.getString("name"), object.getString("date"));
                        JSONArray dataArray = new JSONArray(object.getString("data"));
                        for(int j = 0; j < dataArray.length(); j++) {
                            NoteVO tmpNote = new NoteVO(dataArray.getJSONObject(j).getInt("note"), dataArray.getJSONObject(j).getInt("tempo"), dataArray.getJSONObject(j).getInt("octave"), dataArray.getJSONObject(j).getInt("bar"));
                            sheet.addNote(tmpNote);
                        }
                        String tmpString = object.getString("chord");
                        tmpString = tmpString.replace("[", " ");
                        tmpString = tmpString.replace("]", " ");
                        String chordList[] = tmpString.split(",");

                        for(int j = 0; j < chordList.length; j++) {
                            sheet.addChord(chordList[j].trim());
                            Log.v("add chord", chordList[j].trim());
                        }

                        sheetList.add(sheet);
                        Log.v("sheet list", sheet.toString());
                    }

                    SortSheet sortSheet = new SortSheet();
                    Collections.sort(sheetList, sortSheet);

                    for(int i = 0; i < sheetList.size(); i++) {
                        SheetVO tmp = sheetList.get(i);
                        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                tmp.getName(), tmp.getDate());
                    }

                }
            } catch (Exception e) {
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
        MainActivity activity = (MainActivity)getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnKeyBackPressedListener(null);
        Fragment fragment = MyPageFragment.newInstance();
        // MainFragment 로 교체
        replaceFragment(fragment);
    }

    private class SortSheet implements Comparator<SheetVO> {
        @Override
        public int compare(SheetVO o1, SheetVO o2) {
            return o2.getName().compareTo(o1.getName());
        }
    }
}


