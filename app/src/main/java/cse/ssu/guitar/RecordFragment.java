package cse.ssu.guitar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by 성민우 on 2018-08-01.
 */
public class RecordFragment extends Fragment {
    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment, container, false);

        return view; // 여기서 UI를 생성해서 View를 return
    }
}
