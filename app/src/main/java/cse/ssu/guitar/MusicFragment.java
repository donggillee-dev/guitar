package cse.ssu.guitar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import VO.AlbumVO;
import VO.ArtistVO;
import VO.GenreVO;
import VO.MusicVO;

public class MusicFragment extends Fragment {
    View view;

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_music, container, false);
        TextView name_text = (TextView)view.findViewById(R.id.name);
        TextView artist_text = (TextView)view.findViewById(R.id.artist);
        Button music_button, lyrics_button, similar_button;
        Boolean key;
        String name, artist;

        key = getArguments().getBoolean("key");
        if(key == false) {

            name = getArguments().getString("name");
            artist = getArguments().getString("artist");

            name_text.setText(name);
            artist_text.setText(artist);
        }
        else {
            String data = getArguments().getString("data");
            Log.v("before parsing ** ", data);
            MusicVO musicVO = new MusicVO();
            try {
                //필요없다고 생각한 정보들은 일단 주석처리

                JSONObject object = null;
                object = new JSONObject(data);
                musicVO.setExternal_ids(object.getString("play_offset_ms"));
                //musicVO.setExternal_metadata(object.getString("external_metadata"));
                musicVO.setArtist(new ArtistVO(object.getString("artists")));
                //장르 정보가 넘어올때가 있고 안넘어올때가 있어서 일단은 주석
                //musicVO.setGenres(new GenreVO(object.getString("genres")));
                musicVO.setTitle(object.getString("title"));
                musicVO.setRelease_date(object.getString("release_date"));
                musicVO.setLabel(object.getString("label"));
                musicVO.setDuration_ms(Integer.parseInt(object.getString("duration_ms")));
                musicVO.setAlbum(new AlbumVO(object.getString("album")));
                //musicVO.setAcrid(object.getString("acrid"));
                //musicVO.setResult_from(Integer.parseInt(object.getString("result_from")));
                musicVO.setScore(Integer.parseInt(object.getString("score")));

                Log.v("final debug", musicVO.toString());

                name_text.setText(musicVO.getTitle());
                artist_text.setText(musicVO.getArtist().getName());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

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
