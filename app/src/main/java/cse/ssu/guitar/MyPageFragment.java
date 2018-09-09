package cse.ssu.guitar;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Network.GetMusicSearch;
import Network.GetSheet;
import VO.DataVO;
import VO.NoteVO;
import VO.SheetVO;

public class MyPageFragment extends Fragment {
    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    private ListView music, sheet;
    private ListViewAdapter music_adapter, sheet_adapter;
    private TextView music_more, sheet_more;
    private View view;
    private JSONArray jArray;
    private ArrayList<SheetVO> sheetlist;
    private ArrayList <DataVO> musiclist;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mypage_fragment, container, false);

        sheetlist = new ArrayList<>();
        musiclist = new ArrayList<>();
        music_adapter = new ListViewAdapter();
        sheet_adapter = new ListViewAdapter();

        music = (ListView) view.findViewById(R.id.curSearch);
        sheet = (ListView) view.findViewById(R.id.musicSheet);

        music_more = (TextView) view.findViewById(R.id.more_music);
        sheet_more = (TextView) view.findViewById(R.id.more_sheet);

        music.setAdapter(music_adapter);
        sheet.setAdapter(sheet_adapter);

        //add_music_list();
        GetMusicTask getMusic = new GetMusicTask();
        GetSheetTask getSheet = new GetSheetTask();

        try {
            getMusic.start();
            getMusic.join();
            getSheet.start();
            getSheet.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = MusicFragment.newInstance();
                Bundle bundle = new Bundle();

                DataVO data = musiclist.get(position);

                Log.v("in Music List", data.toString());
                bundle.putString("data", data.toString());
                bundle.putInt("flag",3);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });

        sheet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = SheetFragment.newInstance();
                Bundle bundle = new Bundle();

                SheetVO sheetVO = sheetlist.get(position);

                bundle.putString("name", sheetVO.getName());
                bundle.putString("date", sheetVO.getDate());
                bundle.putString("data", sheetVO.getNote().toString());
                bundle.putString("chord", sheetVO.getChord().toString());
                bundle.putInt("flag",1);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });


        //음악 더보기 버튼
        music_more.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("flag",2);
                Fragment fragment = SearchedMusicFragment.newInstance();
                // MainFragment 로 교체
                replaceFragment(fragment);
                fragment.setArguments(bundle);
            }
        });

        //악보 더보기 버튼
        sheet_more.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                replaceFragment(SheetListFragment.newInstance());
            }
        });

        return view; // 여기서 UI를 생성해서 View를 return
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }


    private class GetMusicTask extends Thread {
        @Override
        public void run() {
            super.run();
            GetMusicSearch musicSearch = new GetMusicSearch();
            String response = null;
            try {
                Log.v("musiclist", "music list");
                response = musicSearch.run(MainActivity.serverUrl+"musiclist", LoginActivity.token, LoginActivity.id);
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
                        music.setVisibility(View.GONE);
                        TextView text = (TextView) view.findViewById(R.id.music_text);
                        Button more = (Button) view.findViewById(R.id.more_music);
                        text.setVisibility(View.VISIBLE);
                        more.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    jArray = new JSONArray(response);
                    for (int i = 0; i < jArray.length(); i++) {
                        jObject = jArray.getJSONObject(i);
                        DataVO dataVO = new DataVO(jObject.getString("artist"), jObject.getString("title"), jObject.getString("date"), jObject.getString("image"), jObject.getString("lyric"));
                        musiclist.add(dataVO);
                        Log.v("data", dataVO.toString());
                    }

                    SortMusic sortMusic = new SortMusic();
                    Collections.sort(musiclist, sortMusic);

                    if(musiclist.size() > 3) {
                        for(int i = 0; i < 3; i++) {
                            DataVO tmp = musiclist.get(i);
                            music_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                    tmp.getTitle(), tmp.getArtist());
                        }
                    }
                    else {
                        for(int i = 0; i < musiclist.size(); i++) {
                            DataVO tmp = musiclist.get(i);
                            music_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                    tmp.getTitle(), tmp.getArtist());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

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
            DataVO dataVO = null;

            try {
                try {
                    jObject = new JSONObject(response);
                    String returnValue = jObject.getString("status");
                    Log.v("return Value", returnValue+"");

                    if(returnValue.compareTo("ERROR") == 0) {
                        sheet.setVisibility(View.GONE);
                        TextView text = (TextView) view.findViewById(R.id.sheet_text);
                        Button more = (Button) view.findViewById(R.id.more_sheet);
                        text.setVisibility(View.VISIBLE);
                        more.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    jArray = new JSONArray(response);

                    for(int i = 0; i < jArray.length(); i++) {
                        Log.v("length"+jArray.length(), i+"");
                        JSONObject object = jArray.getJSONObject(i);
                        SheetVO sheet = new SheetVO(object.getString("name"), object.getString("date"));
                        Log.v("data", object.getString("data"));
                        Log.v("chord", object.getString("chord"));
                        JSONArray dataArray = new JSONArray(object.getString("data"));
                        //JSONObject chordArray = new JSONObject(object.getString("chord"));
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

                        sheetlist.add(sheet);
                        Log.v("sheet list", sheet.toString());
                    }

                    SortSheet sortSheet = new SortSheet();
                    Collections.sort(sheetlist, sortSheet);

                    Log.v("sheet list size", sheetlist.size()+"");

                    if(sheetlist.size() > 3) {
                        for(int i = 0; i < 3; i++) {
                            SheetVO tmp = sheetlist.get(i);
                            sheet_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                    tmp.getName(), tmp.getDate());
                        }
                    }
                    else {
                        for(int i = 0; i < sheetlist.size(); i++) {
                            SheetVO tmp = sheetlist.get(i);
                            sheet_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                    tmp.getName(), tmp.getDate());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class SortMusic implements Comparator<DataVO> {
        @Override
        public int compare(DataVO o1, DataVO o2) {
            return o2.getSearched_date().compareTo(o1.getSearched_date());
        }
    }

    private class SortSheet implements Comparator<SheetVO> {
        @Override
        public int compare(SheetVO o1, SheetVO o2) {
            return o2.getName().compareTo(o1.getName());
        }
    }
}
