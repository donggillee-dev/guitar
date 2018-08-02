package cse.ssu.guitar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

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

        for(int i = 0; i < 3; i++)
            sheet_adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_24dp),
                    "Test Sheet", "Assignment Ind Black 36dp");

        music_more.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
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
}
