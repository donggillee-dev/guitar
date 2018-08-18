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
import VO.GenreVO;
import VO.MusicVO;

public class MusicFragment extends Fragment {
    private View view;
    private String name;
    private String artist;
    private RelativeLayout layout, layout1,layout2;
    private TextView lyrics_text;

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

        key = getArguments().getBoolean("key");
        if(key == false) {

            name = getArguments().getString("name");
            artist = getArguments().getString("artist");

            name_text.setText(name);
            artist_text.setText(artist);


            name_text1.setText(name);
            artist_text1.setText(artist);
            artist_text1.setText("("+artist+")");
            artist_text1.setTextSize(30);
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
                //musicVO.setLabel(object.getString("label"));
                musicVO.setDuration_ms(Integer.parseInt(object.getString("duration_ms")));
                musicVO.setAlbum(new AlbumVO(object.getString("album")));
                musicVO.setScore(Integer.parseInt(object.getString("score")));

                Log.v("final debug", musicVO.toString());

                name = musicVO.getTitle();
                artist = musicVO.getArtist().getName();

                name_text.setText(name);
                artist_text.setText(artist);
                name_text1.setText(name);
                artist_text1.setText("("+artist+")");
                artist_text1.setTextSize(30);
               saveData(musicVO);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        music_button = (Button)view.findViewById(R.id.music);
        lyrics_button = (Button)view.findViewById(R.id.lyrics);

        MelonCommunication communication = new MelonCommunication();
        communication.execute();

        music_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle bundle = makeBundle();

                //Fragment fragment = MusicFragment.newInstance();
                //fragment.setArguments(bundle);
                //replaceFragment(fragment);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.INVISIBLE);

            }
        });

        lyrics_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //        Bundle bundle = makeBundle();

          //      Fragment fragment = LyricsFragment.newInstance();
              //  fragment.setArguments(bundle);
            //    replaceFragment(fragment);
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.INVISIBLE);

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

    //디비에 찾은 음악 정보를 저장하는 함수
    private void saveData(MusicVO musicVO) {

    }

    private class MelonCommunication extends AsyncTask<Void, Void, Void> {
        private String frontUrl;
        private String endUrl;
        private String url;
        private String melonUrl;
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

                    pos = lyrics.indexOf("<br>");
                    tmp = lyrics.substring(0, pos);
                   lyrics = lyrics.substring(pos);
                    pos = tmp.lastIndexOf('>');
                    if (pos != -1)
                        tmp = tmp.substring(pos + 1);
                    lyrics = tmp + lyrics;
                    Log.v("aaa", lyrics);
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
            try {
                if (bitmap != null) {
                    drawable = new BitmapDrawable(getResources(), bitmap);
                    layout.setBackground(drawable);
                }
                else {
                    layout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.color));
                }
                if (lyrics != null)
                    lyrics_text.setText(lyrics);
                else {
                    lyrics_text.setText("가사가 존재하지 않습니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}