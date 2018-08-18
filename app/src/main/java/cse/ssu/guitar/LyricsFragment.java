package cse.ssu.guitar;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import Network.Get;


/**
 * A simple {@link Fragment} subclass.
 */
public class LyricsFragment extends Fragment {
    private View view;
    private TextView name_text;
    private TextView artist_text;
    private TextView lyrics_text;
    private String name;
    private String artist;

    public static LyricsFragment newInstance() {
        return new LyricsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        name_text = (TextView) view.findViewById(R.id.name);
        artist_text = (TextView) view.findViewById(R.id.artist);
        lyrics_text = (TextView) view.findViewById(R.id.lyrics_text);

        Button music_button, lyrics_button, similar_button;

        name = getArguments().getString("name");
        artist = getArguments().getString("artist");

        name_text.setText(name);
        artist_text.setText(artist);


        MelonCommunication communication = new MelonCommunication();
        communication.execute();

        music_button = (Button) view.findViewById(R.id.music);
        lyrics_button = (Button) view.findViewById(R.id.lyrics);

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

        return view;
    }

    public Bundle makeBundle() {
        TextView name_text = (TextView) view.findViewById(R.id.name);
        TextView artist_text = (TextView) view.findViewById(R.id.artist);
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

    private class MelonCommunication extends AsyncTask<Void, Void, Void> {
        private String url;
        private String query;
        private String title;
        private Get get;
        private String id;
        private String melonUrl;
        private String frontUrl, endUrl;
        private String lyrics;
        private URL imgUrl;
        private HttpURLConnection conn;
        private Bitmap bitmap;
        private boolean check;

        @Override
        protected Void doInBackground(Void... voids) {
            frontUrl = "https://www.melon.com/search/song/index.htm?startIndex=1&pageSize=50&q=";
            endUrl = "&sort=hit&section=all&sectionId=&genreDir=&subLinkOrText=L";
            name = name.replace(" ", "");
            url = frontUrl + name + endUrl;
            try {
                Document doc = Jsoup.connect(url).get();
                Log.v("url", url + "");
                Elements elements = doc.select("table tbody").select(".fc_mgray");
                check = false;
                for(Element element : elements) {
                    String compareArtist = element.select("a").text();

                    Log.v("@@@@@@@@", compareArtist+"");
                    if(compareArtist.contains(artist) || artist.contains(compareArtist)) {
                        String ref = element.select("a").attr("href");
                        Log.v("^^^^^", ref+"");
                        String[] tmpArray = ref.split(",");
                        String id = tmpArray[tmpArray.length - 1];
                        id = id.substring(1, id.length()-3);
                        Log.v("*******", id+"");
                        melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;
                        check = true;
                        break;
                    }
                }
                if (check == false) {
                    melonUrl = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try
            {
                if (melonUrl != null) {
                    Document doc = Jsoup.connect(melonUrl).get();
                    Elements lyricsElement = doc.select("div.lyric");
                    int pos = -1;
                    String tmp;
                    lyrics = lyricsElement.first().html();
                    Log.v("aaa", lyrics);
                    pos = lyrics.indexOf("<br>");
                    tmp = lyrics.substring(0, pos);
                    lyrics = lyrics.substring(pos);
                    pos = tmp.lastIndexOf('>');
                    if (pos != -1)
                        tmp = tmp.substring(pos + 1);
                    lyrics = tmp + lyrics;

                    lyrics = lyrics.replaceAll("<br>", "\n");

                    Elements imgElement = doc.select("a.image_typeAll img");
                    String imgPath = imgElement.attr("src");
                    imgUrl = new URL(imgPath);
                    conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }


            } catch (
                    IOException e)

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
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.lyricslayout);
            if (bitmap != null) {
                drawable = new BitmapDrawable(getResources(), bitmap);
                layout.setBackground(drawable);
            } else {
                layout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.color));
            }
            if (lyrics != null)
                lyrics_text.setText(lyrics);
            else {
                lyrics_text.setText("가사가 존재하지 않습니다.");
            }

        }
    }


}
