package cse.ssu.guitar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import VO.DataVO;

/**
 * Created by 성민우 on 2018-08-01.
 */

public class HomeFragment extends Fragment {
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private ListView listview;
    private ListViewAdapter adapter;
    private View view;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home_fragment, container, false);

        Activity activity = (MainActivity)getActivity();
        final BottomNavigationView menu = (BottomNavigationView)activity.findViewById(R.id.navigation);


        ImageButton start = (ImageButton) view.findViewById(R.id.start_record);
        start.setOnClickListener(new ImageButton.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                //Log.v("debug","clickBnt");
                replaceFragment(RecordFragment.newInstance());
                //누르는 경우 하단 메뉴도 함께 변경
                menu.setSelectedItemId(R.id.navigation_dashboard);
            }
        });

        ImageButton sheet = (ImageButton) view.findViewById(R.id.start_sheet);
        sheet.setOnClickListener(new ImageButton.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                //Log.v("debug","clickBnt");
                replaceFragment(MakeSheetFragment.newInstance());
                //누르는 경우 하단 메뉴도 함께 변경
                menu.setSelectedItemId(R.id.navigation_sheet);
            }
        });
        // 리스트뷰 참조 및 Adapter달기

        // Adapter 생성
        adapter = new ListViewAdapter();
        listview = (ListView) view.findViewById(R.id.curSearch);
        listview.setAdapter(adapter);

        addlist();


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name_view, artist_view;
                String name, artist;
                Fragment fragment = MusicFragment.newInstance();
                Bundle bundle = new Bundle();

                name_view = (TextView)view.findViewById(R.id.textView1);
                artist_view = (TextView)view.findViewById(R.id.textView2);
                name = name_view.getText().toString();
                artist = artist_view.getText().toString();

                Log.v("debug", "item selected > " + name + " : " + artist);

                DataVO data;
                data = findData(name, artist);

                Log.v("in Homefragment", data.toString());
                bundle.putString("data", data.toString());
                bundle.putBoolean("key", false);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });


        //더보기 버튼 지정
        Button button = (Button)view.findViewById(R.id.more);
        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("debug", "more");
                replaceFragment(SearchedMusicFragment.newInstance());
            }
        });


        return view; // 여기서 UI를 생성해서 View를 return
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

    private void addlist() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SSUGuitar/log";
        File directory = new File(path);
        if(!directory.exists()){
            directory.mkdirs();
        }

        File[] files = directory.listFiles();



        List<String> filesNameList = new ArrayList<>();

        if(files == null || files.length == 0) {
            listview.setVisibility(View.GONE);
            TextView text = (TextView) view.findViewById(R.id.text);
            Button more = (Button)view.findViewById(R.id.more);
            text.setVisibility(View.VISIBLE);
            more.setVisibility(View.INVISIBLE);
        }
        else {
            for (int i = 0; i < files.length; i++) {
                filesNameList.add(files[i].getName());
            }

            Collections.sort(filesNameList, new AscendingString());
            DataVO dataVO = null;
            if (files.length >= 4) {
                for (int i = 0; i < 4; i++) {
                    String realPath = path + "/"+filesNameList.get(i);

                    try {
                        FileInputStream fis = new FileInputStream(realPath);
                        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));

                        String result="", temp="";
                        while( (temp = bufferReader.readLine()) != null ) {
                            result += temp;
                        }

                        dataVO= parseData(result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                            dataVO.getTitle(), dataVO.getArtist());
                }
            } else {
                for (int i = 0; i < files.length; i++) {
                    String realPath = path + "/"+filesNameList.get(i);

                    try {
                        FileInputStream fis = new FileInputStream(realPath);
                        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));

                        String result="", temp="";
                        while( (temp = bufferReader.readLine()) != null ) {
                            result += temp;
                        }

                        dataVO = parseData(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),dataVO.getTitle(), dataVO.getArtist());
                }
            }

        }
    }

    class AscendingString implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    }

    private DataVO findData(String title, String artist) {
        DataVO resultData = null;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SSUGuitar/log";
        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }

        File[] files = directory.listFiles();
        List<String> filesNameList = new ArrayList<>();
        for (int i = 0; i < files.length; i++)
            filesNameList.add(files[i].getName());
        for(int i = 0; i < files.length; i++)
            if(filesNameList.get(i).contains(title) && filesNameList.get(i).contains(artist)) {
                String realPath = path + "/" + filesNameList.get(i);
                try {
                    FileInputStream fis = new FileInputStream(realPath);
                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));

                    String result="", temp="";
                    while( (temp = bufferReader.readLine()) != null ) {
                        result += temp+"\n";
                    }

                    resultData = parseData(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        return resultData;
    }

    private DataVO parseData(String data) {
        DataVO result = null;
        Log.v("in parseData", data+"");
        try {
            JSONObject object = new JSONObject(data);
            result = new DataVO(object.getString("artist"), object.getString("title"), object.getString("searched_date"), object.getString("image"), object.getString("lyric"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
