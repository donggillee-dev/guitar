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
import android.widget.TextView;

import org.w3c.dom.Text;

public class MusicFragment extends Fragment {
    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music, container, false);
        TextView name_text = (TextView)view.findViewById(R.id.name);
        TextView artist_text = (TextView)view.findViewById(R.id.artist);

        String name, artist;

        name = getArguments().getString("name");
        artist = getArguments().getString("artist");

        name_text.setText(name);
        artist_text.setText(artist);


        return view;
    }

}
