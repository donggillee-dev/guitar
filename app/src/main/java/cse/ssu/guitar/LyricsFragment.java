package cse.ssu.guitar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsFragment extends Fragment {
    View view;

    public static LyricsFragment newInstance() {
        return new LyricsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        TextView name_text = (TextView)view.findViewById(R.id.name);
        TextView artist_text = (TextView)view.findViewById(R.id.artist);
        TextView lyrics_text = (TextView)view.findViewById(R.id.lyrics_text);

        Button music_button, lyrics_button, similar_button;

        String name, artist, lyrics;

        name = getArguments().getString("name");
        artist = getArguments().getString("artist");

        name_text.setText(name);
        artist_text.setText(artist);

        //임의 가사 설정
        lyrics = "1. 동해물과 백두산이 마르고 닳도록 \n" +
                "하느님이 보우하사 우리나라 만세\n" +
                "무궁화 삼천리 화려 강산\n" +
                "대한 사람 대한으로 길이 보전하세\n" +
                "\n" +
                "2. 남산 위에 저 소나무 철갑을 두른 듯 \n" +
                "바람 서리 불변함은 우리 기상일세\n" +
                "무궁화 삼천리 화려 강산 \n" +
                "대한 사람 대한으로 길이 보전하세\n" +
                "\n" +
                "3. 가을 하늘 공활한데 높고 구름 없이 \n" +
                "밝은 달은 우리 가슴 일편단심일세\n" +
                "무궁화 삼천리 화려 강산\n" +
                "대한 사람 대한으로 길이 보전하세\n" +
                "\n" +
                "4. 이 기상과 이 맘으로 충성을 다하여 \n" +
                "괴로우나 즐거우나 나라 사랑하세\n" +
                "무궁화 삼천리 화려 강산\n" +
                "대한 사람 대한으로 길이 보전하세";

        lyrics_text.setText(lyrics);

        music_button = (Button)view.findViewById(R.id.music);
        lyrics_button = (Button)view.findViewById(R.id.lyrics);
        similar_button = (Button)view.findViewById(R.id.similar);

        music_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = makeBundle();

                Fragment fragment = MusicFragment.newInstance();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });

        lyrics_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = makeBundle();

                Fragment fragment = LyricsFragment.newInstance();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });

        similar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = makeBundle();

                Fragment fragment = MusicFragment.newInstance();
                fragment.setArguments(bundle);
                //replaceFragment(fragment);

                Log.v("debug", "미구현 : 제일 마지막에 구현");
            }
        });


        return view;
    }

    public Bundle makeBundle() {
        TextView name_text = (TextView)view.findViewById(R.id.name);
        TextView artist_text = (TextView)view.findViewById(R.id.artist);
        String name, artist;
        Bundle bundle = new Bundle();

        name = name_text.getText().toString();
        artist = artist_text.getText().toString();
        bundle.putString("name", name);
        bundle.putString("artist", artist);

        return bundle;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }
}
