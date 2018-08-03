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
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SheetListFragment extends Fragment {
    public static SheetListFragment newInstance() {
        return new SheetListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sheet_list, container, false);

        ListView list;
        ListViewAdapter adapter = new ListViewAdapter();

        list = (ListView)view.findViewById(R.id.sheet_list);
        list.setAdapter(adapter);

        //현재 날짜 구하기
        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_string = sdf.format(date);
        Log.v("debug", date_string);

        // 임의로 20개  입력
        for(int i = 0; i < 20; i++)
            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mypage_black_24dp),
                    "Test Sheet " + i, date_string);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

}
