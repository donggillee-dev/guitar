package cse.ssu.guitar;

import android.app.Activity;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Network.GetMusicSearch;
import VO.DataVO;

public class SearchedMusicFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    public static SearchedMusicFragment newInstance() {
        if(instance == null){
            instance = new SearchedMusicFragment();
        }
        return instance;
    }

    private ListView list;
    private ListViewAdapter adapter;
    private TextView trackNum;
    private View view;
    private int flag = 0;
    private JSONArray jArray;

    private static SearchedMusicFragment instance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_searched_music, container, false);
        trackNum = view.findViewById(R.id.number);
        adapter = new ListViewAdapter();
        list = (ListView) view.findViewById(R.id.searched_list);
        list.setAdapter(adapter);

        flag = getArguments().getInt("flag");
        //createList();


        try {
            GetDataTask task = new GetDataTask();
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                bundle.putBoolean("key", true);
                bundle.putInt("flag", 4);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });

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
        if (flag == 1)
            replaceFragment(HomeFragment.newInstance());
        else if (flag == 2) {
            replaceFragment(MyPageFragment.newInstance());
        }
    }

    private class GetDataTask extends Thread {
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
            DataVO dataVO = null;

            try {
                try {
                    jObject = new JSONObject(response);
                    String returnValue = jObject.getString("status");
                    Log.v("return Value", returnValue+"");

                    if(returnValue.compareTo("ERROR") == 0) {
                        Log.v("no data", "no data");
                    }
                } catch (Exception e) {
                    jArray = new JSONArray(response);
                    trackNum.setText(jArray.length() + " Track");
                    for (int i = jArray.length() - 1; i >= 0; i--) {
                        jObject = jArray.getJSONObject(i);
                        dataVO = new DataVO(jObject.getString("artist"), jObject.getString("title"), jObject.getString("date"), jObject.getString("image"), jObject.getString("lyric"));
                        Log.v("data", dataVO.toString());
                        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                dataVO.getTitle(), dataVO.getArtist());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

}
