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

                name = musicVO.getTitle();
                artist = musicVO.getArtist().getName();
                name_text.setText(name);
                artist_text.setText(artist);

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
        private boolean check;

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
                JSONObject tmp = new JSONObject(result);
                JSONArray array = null;
                if(!tmp.has("ERROR"))
                    array = new JSONArray(tmp.getString("SONGCONTENTS"));

                if(array != null) {
                    check = false;
                    for(int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Log.v("****"+i, object.toString());
                        String compareName = object.getString("ARTISTNAME");
                        if(compareName.contains(artist)) {
                            check = true;
                            id = object.getString("SONGID");
                            melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;
                            break;
                        }
                    }
                }
                if(check == false) {
                    String frontUrl = "https://www.melon.com/search/song/index.htm?q=";
                    String middleUrl = "&section=&searchGnbYn=Y&kkoSpl=Y&kkoDpType=&linkOrText=T&ipath=srch_form#params%5Bq%5D=";
                    String middleUrl2 = "&params%5Bsort%5D=weight&params%5Bsection%5D=all&params%5BsectionId%5D=&params%5BgenreDir%5D=&params%5Bsq%5D=";
                    String endUrl = "&params%5BsubLinkOrText%5D=T&po=pageObj&startIndex=1";

                    name = name.replace(" ", "");
                    String tmpArtist = artist.replace(" ", "");
                    url = frontUrl + name + middleUrl + name + middleUrl2 + tmpArtist + endUrl;
                    try {
                        Document doc = Jsoup.connect(url).get();
                        Log.v("url", url+"");
                        Elements elements = doc.select("table tbody").select(".fc_mgray");
                        check = false;
                        for(Element element : elements) {
                            String compareArtist = element.select("a").text();

                            Log.v("@@@@@@@@", compareArtist+"");
                            if(compareArtist.contains(artist) || artist.contains(compareArtist)) {
                                String ref = element.select("a").attr("href");
                                Log.v("^^^^^", ref+"");
                                String[] tmpArray = ref.split(",");
                                id = tmpArray[tmpArray.length - 1];
                                id = id.substring(1, id.length()-3);
                                Log.v("*******", id+"");
                                melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;
                                check = true;
                                break;
                            }
                        }
                        if(check == false) {
                            melonUrl = null;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
            LinearLayout layout = (LinearLayout)view.findViewById(R.id.musiclayout);
            if(bitmap != null) {
                drawable = new BitmapDrawable(getResources(), bitmap);
                layout.setBackground(drawable);
            }
            else {
                layout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.color));
            }
        }
    }
}