package cse.ssu.guitar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Network.Get;
import VO.AlbumVO;
import VO.ArtistVO;
import VO.DataVO;
import VO.GenreVO;
import VO.MusicVO;

public class MusicFragment extends Fragment {
    private View view;
    private String name;
    private String artist;
    private String lyrics;
    private String imageUrl;
    private RelativeLayout layout, layout1, layout2;
    private TextView lyrics_text;
    private DataVO dataVO;

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_music, container, false);
        TextView name_text = (TextView)view.findViewById(R.id.name);
        TextView artist_text = (TextView)view.findViewById(R.id.artist);

        TextView name_text1 = (TextView)view.findViewById(R.id.name1);
        TextView artist_text1 = (TextView)view.findViewById(R.id.artist1);


        Button music_button, lyrics_button;
        Boolean key;
        layout = (RelativeLayout)view.findViewById(R.id.musiclayout);
        layout1=(RelativeLayout)view.findViewById(R.id.layout1);
        layout2=(RelativeLayout)view.findViewById(R.id.layout2);
        lyrics_text = (TextView)view.findViewById(R.id.lyrics_text);

        String tmp = getArguments().getString("data");
        Log.v("data", tmp+"");
        try {
            JSONObject object = new JSONObject(tmp);
            dataVO = new DataVO(object.getString("artist"), object.getString("title"),
                    object.getString("searched_date"), object.getString("image"), object.getString("lyric"));

            Log.v("data >> ", dataVO.toString());

            name = dataVO.getTitle();
            artist = dataVO.getArtist();
            lyrics = dataVO.getLyric();
            imageUrl = dataVO.getImage();

            name_text.setText(name);
            artist_text.setText(artist);
            name_text1.setText(name);
            artist_text1.setText("("+artist+")");
            artist_text1.setTextSize(30);

            if (!lyrics.equals("null"))
                lyrics_text.setText(lyrics);
            else {
                lyrics_text.setText("가사가 존재하지 않습니다.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        music_button = (Button)view.findViewById(R.id.music);
        lyrics_button = (Button)view.findViewById(R.id.lyrics);

        MelonCommunication communication = new MelonCommunication();
        communication.execute();

        music_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);

            }
        });

        lyrics_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.INVISIBLE);

            }
        });


        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

    private class MelonCommunication extends AsyncTask<Void, Void, Void> {
        private URL imgUrl;
        private HttpURLConnection conn;
        private Bitmap bitmap;
        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                if(!imageUrl.equals("null")) {
                    imgUrl = new URL(imageUrl);
                    conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }
                else
                    bitmap = null;
            } catch (IOException e)

            {
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            BitmapDrawable drawable;
            try {
                if (bitmap != null) {
                    drawable = new BitmapDrawable(getResources(), bitmap);
                    layout.setBackground(drawable);
                }
                else {
                    layout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.color));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}