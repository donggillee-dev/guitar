package cse.ssu.guitar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

        // 임의로 20개  입력
        for(int i = 0; i < 20; i++)
            adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mypage_black_24dp),
                    "Test Sheet "+i, "Account Box Black 36dp");





        return view;
    }

}
