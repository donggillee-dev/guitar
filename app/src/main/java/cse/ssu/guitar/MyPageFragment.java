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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import VO.DataVO;

public class MyPageFragment extends Fragment {
    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    private ListView music, sheet;
    private ListViewAdapter music_adapter, sheet_adapter;
    private TextView music_more, sheet_more;
    private View view;
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

        add_music_list();
        add_sheet_list();


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

                DataVO data;
                data = findData(name, artist);

                Log.v("in Music List", data.toString());
                bundle.putString("data", data.toString());
                bundle.putBoolean("key", true);
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
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });


        //음악 더보기 버튼
        music_more.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                replaceFragment(SearchedMusicFragment.newInstance());
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

    private void add_sheet_list() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SSUGuitar";
        File directory = new File(path);
        File[] files = directory.listFiles();

        List<String> filesNameList = new ArrayList<>();

        if(files == null || files.length<= 1) {
            sheet.setVisibility(View.GONE);
            TextView text = (TextView) view.findViewById(R.id.sheet_text);
            text.setVisibility(View.VISIBLE);
            sheet_more.setVisibility(View.INVISIBLE);
        }
        else {
            Log.v("length", ""+files.length);
            for (int i = 0; i < files.length; i++) {
                filesNameList.add(files[i].getName());
            }

            Collections.sort(filesNameList, new AscendingString());
            if(files.length > 3) {
                for (int i = 1; i < 4; i++) {

                    String filename = filesNameList.get(i).substring(0, 12);
                    String date = parseDate(filename);
                    sheet_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                            filename, date);
                }
            }
            else {
                for (int i = 1; i < files.length; i++) {
                    String filename = filesNameList.get(i).substring(0, 12);
                    String date = parseDate(filename);
                    sheet_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                            filename, date);
                }
            }
        }
    }

    private void add_music_list() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SSUGuitar/log";
        File directory = new File(path);
        File[] files = directory.listFiles();
        DataVO dataVO = null;
        List<String> filesNameList = new ArrayList<>();

        if(files == null || files.length == 0) {
            music.setVisibility(View.GONE);
            TextView text = (TextView) view.findViewById(R.id.music_text);
            text.setVisibility(View.VISIBLE);
            music_more.setVisibility(View.INVISIBLE);
        }
        else  {
            Log.v("length in music", ""+files.length);
            for (int i = 0; i < files.length; i++) {
                filesNameList.add(files[i].getName());
            }
            Log.v("file name", filesNameList.get(0));

            Collections.sort(filesNameList, new AscendingString());
            if(files.length >= 3) {
                for (int i = 0; i < 3; i++) {
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
                    music_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),dataVO.getTitle(), dataVO.getArtist());
                }
            }
            else {
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
                    music_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),dataVO.getTitle(), dataVO.getArtist());
                }
            }
        }
    }

    public String parseDate(String filename) {
            String year = filename.substring(0, 2);
            String month = filename.substring(2, 4);
            String day = filename.substring(4, 6);

            String date = "20"+year+"-"+month+"-"+day;
            return date;

    }

    class AscendingString implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
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
                        result += temp;
                    }

                    resultData = parseData(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        return resultData;
    }
}
