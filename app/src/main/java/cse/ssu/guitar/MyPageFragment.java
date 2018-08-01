package cse.ssu.guitar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 성민우 on 2018-08-01.
 */
public class MyPageFragment extends Fragment {
    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypage_fragment, container, false);

        return view; // 여기서 UI를 생성해서 View를 return
    }
}
