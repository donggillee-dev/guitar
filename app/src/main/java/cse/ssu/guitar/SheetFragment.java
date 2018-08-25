package cse.ssu.guitar;


import android.app.Activity;
import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SheetFragment extends Fragment implements MainActivity.onKeyBackPressedListener{
    View view;
    TextView name_view, date_view;
    String name, date;
    int flag=0;
    RelativeLayout rl;
    public static SheetFragment newInstance() {
        return new SheetFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sheet, container, false);
        name_view = (TextView)view.findViewById(R.id.name);
        date_view = (TextView)view.findViewById(R.id.date);

        name = getArguments().getString("name");
        date = getArguments().getString("date");
        rl = (RelativeLayout) view.findViewById(R.id.noteLayout);
        flag = getArguments().getInt("flag");
        int i=0;
        for(;i<13;i++) {

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(80, 80);

            ImageView iv = new ImageView(getActivity());
            iv.setImageResource(R.drawable.eighth_note);


            lp.alignWithParent = true;
            lp.leftMargin = i*30+70;
            lp.topMargin = i*10;
            iv.setLayoutParams(lp);
            rl.addView(iv);
        }
        name_view.setText(name);
        date_view.setText(date);


        return view;
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
        if(flag==1)
            replaceFragment(MyPageFragment.newInstance());
        else if(flag==2)
            replaceFragment(SheetListFragment.newInstance());
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }
}
