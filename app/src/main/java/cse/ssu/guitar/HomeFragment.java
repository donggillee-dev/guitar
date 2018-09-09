package cse.ssu.guitar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Network.GetMusicSearch;
import VO.DataVO;

/**
 * Created by 성민우 on 2018-08-01.
 */

public class HomeFragment extends Fragment {


    private ListView listview;
    private ListViewAdapter adapter;
    private View view;
    private JSONArray jArray;
    private ArrayList<DataVO> musiclist;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        musiclist = new ArrayList<>();

        Activity activity = (MainActivity)getActivity();
        final BottomNavigationView menu = (BottomNavigationView)activity.findViewById(R.id.navigation);

        ImageButton start = (ImageButton) view.findViewById(R.id.start_record);
        start.setOnClickListener(new ImageButton.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Fragment fragment = RecordFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("flag",1);
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
                replaceFragment(MakeSheetFragment.newInstance());
                //누르는 경우 하단 메뉴도 함께 변경
                menu.setSelectedItemId(R.id.navigation_sheet);
            }
        });

        // Adapter 생성
        adapter = new ListViewAdapter();
        listview = (ListView) view.findViewById(R.id.curSearch);
        listview.setAdapter(adapter);

        //addlist();
        GetDataTask getData = new GetDataTask();
        try {
            getData.start();
            getData.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = MusicFragment.newInstance();
                Bundle bundle = new Bundle();
                DataVO data = musiclist.get(position);
                bundle.putString("data", data.toString());
                bundle.putInt("flag",1);
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });

        //더보기 버튼 지정
        Button button = (Button)view.findViewById(R.id.more);
        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("flag",1);
                Fragment fragment = SearchedMusicFragment.newInstance();
                // MainFragment 로 교체
                replaceFragment(fragment);
                fragment.setArguments(bundle);
            }
        });

        return view; // 여기서 UI를 생성해서 View를 return
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

    private class GetDataTask extends Thread {
        @Override
        public void run() {
            super.run();
            GetMusicSearch musicSearch = new GetMusicSearch();
            String response = null;
            try {
                response = musicSearch.run(MainActivity.serverUrl + "musiclist", LoginActivity.token, LoginActivity.id);
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
                        listview.setVisibility(View.GONE);
                        TextView text = (TextView) view.findViewById(R.id.text);
                        Button more = (Button) view.findViewById(R.id.more);
                        text.setVisibility(View.VISIBLE);
                        more.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    jArray = new JSONArray(response);
                    for (int i = 0; i < jArray.length(); i++) {
                        jObject = jArray.getJSONObject(i);
                        DataVO dataVO = new DataVO(jObject.getString("artist"), jObject.getString("title"), jObject.getString("date"), jObject.getString("image"), jObject.getString("lyric"));
                        Log.v("data", dataVO.toString());
                        musiclist.add(dataVO);
                    }

                    SortMusic sortMusic = new SortMusic();
                    Collections.sort(musiclist, sortMusic);

                    if(musiclist.size() > 4) {
                        for(int i = 0; i < 4; i++) {
                            DataVO tmp = musiclist.get(i);
                            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                    tmp.getTitle(), tmp.getArtist());
                        }
                    }
                    else {
                        for(int i = 0; i < musiclist.size(); i++) {
                            DataVO tmp = musiclist.get(i);
                            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                                    tmp.getTitle(), tmp.getArtist());
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
}
