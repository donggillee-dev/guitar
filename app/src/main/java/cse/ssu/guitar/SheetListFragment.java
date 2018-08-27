package cse.ssu.guitar;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Network.PostLogin;

public class SheetListFragment extends Fragment implements MainActivity.onKeyBackPressedListener{
    public static SheetListFragment newInstance() {
        return new SheetListFragment();
    }

    private ListView list;
    private ListViewAdapter adapter;
    private View view;
    private TextView trackNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sheet_list, container, false);


        adapter = new ListViewAdapter();

        list = (ListView)view.findViewById(R.id.sheet_list);
        list.setAdapter(adapter);

        //현재 날짜 구하기
        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date_string = sdf.format(date);
        Log.v("debug", date_string);

        add_sheet_list();



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
                bundle.putInt("flag",2);
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

    private void add_sheet_list() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SSUGuitar";
        File directory = new File(path);
        File[] files = directory.listFiles();

        List<String> filesNameList = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            filesNameList.add(files[i].getName());
        }

        Collections.sort(filesNameList, new AscendingString());

        for(int i = 1; i < filesNameList.size(); i++) {

            String filename = filesNameList.get(i).substring(0, 12);
            String date = parseDate(filename);
            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.music),
                    filename , date);
        }

        trackNum = (TextView)view.findViewById(R.id.number);
        trackNum.setText(files.length - 1 + " Track");
    }

    public String parseDate(String filename) {
        String year = filename.substring(0, 2);
        String month = filename.substring(2, 4);
        String day = filename.substring(4, 6);

        String date = "20"+year+"-"+month+"-"+day;
        return date;

    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onBack() {
        MainActivity activity = (MainActivity)getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnKeyBackPressedListener(null);
        Fragment fragment = MyPageFragment.newInstance();
        // MainFragment 로 교체
        replaceFragment(fragment);
    }
    class AscendingString implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    }

}


