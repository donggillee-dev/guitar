package cse.ssu.guitar;

import android.os.Bundle;
import android.os.Environment;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Network.GetMusicSearch;
import VO.DataVO;

public class MyPageFragment extends Fragment {
    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    private ListView music, sheet;
    private ListViewAdapter music_adapter, sheet_adapter;
    private TextView music_more, sheet_more;
    private View view;
    private JSONArray jArray;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mypage_fragment, container, false);


        music_adapter = new ListViewAdapter();
        sheet_adapter = new ListViewAdapter();

        music = (ListView) view.findViewById(R.id.curSearch);
        sheet = (ListView) view.findViewById(R.id.musicSheet);

        music_more = (TextView) view.findViewById(R.id.more_music);
        sheet_more = (TextView) view.findViewById(R.id.more_sheet);

        music.setAdapter(music_adapter);
        sheet.setAdapter(sheet_adapter);


        //현재 날짜 구하기
        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_string = sdf.format(date);
        Log.v("debug", date_string);

        //add_music_list();
        GetMusicTask getMusic = new GetMusicTask();
        try {
            getMusic.start();
            getMusic.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // add_sheet_list();


        music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name_view, artist_view;
                String name, artist;
                Fragment fragment = MusicFragment.newInstance();
                Bundle bundle = new Bundle();

                name_view = (TextView) view.findViewById(R.id.textView1);
                artist_view = (TextView) view.findViewById(R.id.textView2);
                name = name_view.getText().toString();
                artist = artist_view.getText().toString();

                Log.v("debug", "item selected > " + name + " : " + artist);

                DataVO data = null;
                try {
                    JSONObject jObject = jArray.getJSONObject(jArray.length() - 1 - position);
                    data = new DataVO(jObject.getString("artist"), jObject.getString("title"), jObject.getString("date"), jObject.getString("image"), jObject.getString("lyric"));
                    Log.v("data selected", data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                TextView name_view, date_view;
                String name, date;
                Fragment fragment = SheetFragment.newInstance();
                Bundle bundle = new Bundle();

                name_view = (TextView) view.findViewById(R.id.textView1);
                date_view = (TextView) view.findViewById(R.id.textView2);
                name = name_view.getText().toString();
                date = date_view.getText().toString();

                Log.v("debug", "item selected > " + name + " : " + date);

                bundle.putString("name", name);
                bundle.putString("date", date);
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
                response = musicSearch.run("http://54.180.30.183:3000/musiclist", LoginActivity.token, LoginActivity.id);
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
                        music.setVisibility(View.GONE);
                        TextView text = (TextView) view.findViewById(R.id.music_text);
                        Button more = (Button) view.findViewById(R.id.more_music);
                        text.setVisibility(View.VISIBLE);
                        more.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    jArray = new JSONArray(response);
                    for (int i = jArray.length() - 1; i > jArray.length() - 4; i--) {
                        jObject = jArray.getJSONObject(i);
                        dataVO = new DataVO(jObject.getString("artist"), jObject.getString("title"), jObject.getString("date"), jObject.getString("image"), jObject.getString("lyric"));
                        Log.v("data", dataVO.toString());
                        music_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                dataVO.getTitle(), dataVO.getArtist());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
