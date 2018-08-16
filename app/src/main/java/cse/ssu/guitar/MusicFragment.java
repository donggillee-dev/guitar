package cse.ssu.guitar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
    private LinearLayout layout;

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
        layout = (LinearLayout)view.findViewById(R.id.musiclayout);

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
                //musicVO.setLabel(object.getString("label"));
                musicVO.setDuration_ms(Integer.parseInt(object.getString("duration_ms")));
                musicVO.setAlbum(new AlbumVO(object.getString("album")));
                //musicVO.setAcrid(object.getString("acrid"));
                //musicVO.setResult_from(Integer.parseInt(object.getString("result_from")));
                musicVO.setScore(Integer.parseInt(object.getString("score")));

                Log.v("final debug", musicVO.toString());

                name_text.setText(musicVO.getTitle());
                artist_text.setText(musicVO.getArtist().getName());

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
        private String url;
        private String query;
        private String title;
        private Get get;
        private String id;
        private String melonUrl;
        private URL imgUrl;
        private HttpURLConnection conn;
        private Bitmap bitmap;

        @Override
        protected Void doInBackground(Void... voids) {
            url = "https://www.melon.com/search/keyword/index.json?";
            query = "jscallback=jQuery19102187322591402996_1534318713156";
            title = "&query="+name;
            get = new Get(url+query+title);
            String returnString = null;
            try {
                returnString = get.run(get.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int length = returnString.length();
            int start = query.length() - 10;
            int end = 2;
            String result = returnString.substring(start, length - end);
            try {
//                JSONObject tmp = new JSONObject(result);
//                JSONArray array = new JSONArray(tmp.getString("SONGCONTENTS"));
//                for(int j = 0; j < array.length(); j++) {
//                    JSONObject object = array.getJSONObject(j);
//                    if(object.getString("ARTISTNAME").contains(artist)) {
//                        id = object.getString("SONGID");
//                        melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;
//                        Log.v("melonURL", melonUrl+"");
//                        break;
//                    }
//                }

                JSONObject tmp = new JSONObject(result);
                JSONArray array = new JSONArray(tmp.getString("SONGCONTENTS"));
                JSONObject object = array.getJSONObject(0);
                id = object.getString("SONGID");
                melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;

            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                if(melonUrl != null) {
                    Document doc = Jsoup.connect(melonUrl).get();
                    Elements imgElement = doc.select("a.image_typeAll img");
                    String imgPath = imgElement.attr("src");
                    imgUrl = new URL(imgPath);
                    conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            BitmapDrawable drawable;
            if(bitmap != null) {
                drawable = new BitmapDrawable(getResources(), bitmap);
                layout.setBackground(drawable);
            }
        }
    }

}
