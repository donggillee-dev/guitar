package cse.ssu.guitar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by 성민우 on 2018-08-01.
 */

public class HomeFragment extends Fragment {
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ListView listview;
        ListViewAdapter adapter;

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        Activity activity = (MainActivity)getActivity();
        final BottomNavigationView menu = (BottomNavigationView)activity.findViewById(R.id.navigation);


        ImageButton start = (ImageButton) view.findViewById(R.id.start);
        start.setOnClickListener(new ImageButton.OnClickListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Log.v("debug","clickBnt");
                replaceFragment(RecordFragment.newInstance());
                //누르는 경우 하단 메뉴도 함께 변경
                menu.setSelectedItemId(R.id.navigation_dashboard);
            }
        });
        // 리스트뷰 참조 및 Adapter달기

        // Adapter 생성
        adapter = new ListViewAdapter();
        listview = (ListView) view.findViewById(R.id.curSearch);
        listview.setAdapter(adapter);

        Log.v("debug", "yes!");
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp),
                "Box", "Account Box Black 36dp");
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_home_black_24dp),
                "Circle", "Account Circle Black 36dp");
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_24dp),
                "Ind", "Assignment Ind Black 36dp");

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

                bundle.putString("name", name);
                bundle.putString("artist", artist);
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
}
