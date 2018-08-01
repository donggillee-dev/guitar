package cse.ssu.guitar;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * Created by 성민우 on 2018-08-01.
 */

public class HomeFragment extends Fragment {
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listview;
        ListViewAdapter adapter;

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        //View view2 = inflater.inflate(R.layout.activity_main, container, false);
        //final View view3=(BottomNavigationView) view2.findViewById(R.id.navigation_dashboard);
        // Adapter 생성
        adapter = new ListViewAdapter();
        ImageButton start = (ImageButton) view.findViewById(R.id.start);
        start.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View view) {
            Log.v("debug","clickBnt");
          //      view3.performClick();
            }
        });
        // 리스트뷰 참조 및 Adapter달기

        listview = (ListView) view.findViewById(R.id.curSearch);
        listview.setAdapter(adapter);
        Log.v("debug", "yes!");
        adapter.addItem(null, "recent", null);
        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp),
                "Box", "Account Box Black 36dp");
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_home_black_24dp),
                "Circle", "Account Circle Black 36dp");
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_24dp),
                "Ind", "Assignment Ind Black 36dp");

        return view; // 여기서 UI를 생성해서 View를 return
    }
}
