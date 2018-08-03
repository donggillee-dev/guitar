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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 성민우 on 2018-08-01.
 */
public class MyPageFragment extends Fragment {
    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypage_fragment, container, false);

        ListView music, sheet;
        ListViewAdapter music_adapter = new ListViewAdapter();
        ListViewAdapter sheet_adapter = new ListViewAdapter();
        Button music_more, sheet_more;

        music = (ListView)view.findViewById(R.id.curSearch);
        sheet = (ListView)view.findViewById(R.id.musicSheet);

        music_more = (Button)view.findViewById(R.id.more_music);
        sheet_more = (Button)view.findViewById(R.id.more_sheet);

        music.setAdapter(music_adapter);
        sheet.setAdapter(sheet_adapter);

        //임의로 데이터 3개씩 삽입
        for(int i = 0; i < 3; i++)
            music_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_home_black_24dp),
                "Test Music", "Account Circle Black 36dp");

        //현재 날짜 구하기
        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_string = sdf.format(date);
        Log.v("debug", date_string);

        for(int i = 0; i < 3; i++)
            sheet_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_24dp),
                    "Test Sheet", date_string);

        music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                bundle.putString("name", name);
                bundle.putString("artist", artist);
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

                name_view = (TextView)view.findViewById(R.id.textView1);
                date_view = (TextView)view.findViewById(R.id.textView2);
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
}
