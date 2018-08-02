package cse.ssu.guitar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SearchedMusicFragment extends Fragment {
    public static SearchedMusicFragment newInstance() {
        return new SearchedMusicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_searched_music, container, false);

        ListView list;
        ListViewAdapter adapter;

        adapter = new ListViewAdapter();
        list = (ListView)view.findViewById(R.id.searched_list);
        list.setAdapter(adapter);


        //임의로 데이터 삽입함
        //디비에서 데이터 가져와서 화면에 뿌려줘야함
        for(int i = 0; i < 15; i++) {
            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp),
                    "Music Test " + i, "Account Box Black 36dp");
        }


        return view;
    }

}
