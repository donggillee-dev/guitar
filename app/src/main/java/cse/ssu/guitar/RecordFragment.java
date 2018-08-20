package cse.ssu.guitar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import VO.AlbumVO;
import VO.ArtistVO;
import VO.DataVO;
import VO.MusicVO;

/**
 * Created by 성민우 on 2018-08-01.
 */
public class RecordFragment extends Fragment implements IACRCloudListener {
    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXETERNAL_STORAGE = 1;
    ToggleButton listenBtn;

    private ACRCloudClient mClient;
    private ACRCloudConfig mConfig;

    private boolean mProcessing = false;
    private boolean initState = false;

    private String path = "";
    private boolean recordCheck = true;

    private long startTime = 0;
    private long stopTime = 0;
    // Environment.getExternalStorageDirectory()로 각기 다른 핸드폰의 내장메모리의 디렉토리를 알수있다.
    final private static File RECORDED_FILE = Environment.getExternalStorageDirectory();


    private ImageView loader;
    private Animation animation;

    private String title;
    private String artist;
    private String image;
    private String lyric;
    private DataVO dataVO;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.record_fragment, container, false);
        path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud/model";

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }



        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;

        // If you implement IACRCloudResultWithAudioListener and override "onResult(ACRCloudResult result)", you can get the Audio data.
        //this.mConfig.acrcloudResultWithAudioListener = this;

        this.mConfig.context = getActivity();
        this.mConfig.host = "identify-ap-southeast-1.acrcloud.com";
        this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = "6b65a3f0beacac7bb9d9e60a0ce9a939";
        this.mConfig.accessSecret = "VfKG2Y8PGSkOxl3b9So9jhHlGpaepZHxMLESypl9";
        this.mConfig.protocol = ACRCloudConfig.ACRCloudNetworkProtocol.PROTOCOL_HTTP; // PROTOCOL_HTTPS
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        // If reqMode is REC_MODE_LOCAL or REC_MODE_BOTH,
        // the function initWithConfig is used to load offline db, and it may cost long time.
        this.initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }

        loader = (ImageView)view.findViewById(R.id.loader);
        listenBtn = (ToggleButton) view.findViewById(R.id.listenBtn);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // 권한 없음
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }
        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // 권한 없음
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXETERNAL_STORAGE);
        }
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenBtn.isChecked() == true) {


                    // 실험 결과 왠만하면 아래 recorder 객체의 속성을 지정하는 순서는 이대로 하는게 좋다 위치를 바꿨을때 에러가 났었음
                    // 녹음 시작을 위해  MediaRecorder 객체  recorder를 생성한다.
                    animation = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate);
                    loader.startAnimation(animation);
                    start();

                } else {
                    //loader.setVisibility(View.INVISIBLE);

                    loader.clearAnimation();
                    animation.setAnimationListener(null);

                    stop();
                    Toast.makeText(getActivity(),"녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();

                    Log.v("debug", "before stop");

                    // TODO Auto-generated method stub


                }

            }
        });

        return view; // 여기서 UI를 생성해서 View를 return
    }

    public void start() {
        Log.v("debug", "start api");
        if (!this.initState) {
            Toast.makeText(getActivity(), "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
            }
            startTime = System.currentTimeMillis();
        }
    }

    protected void stop() {
        Log.v("debug", "in stop");
        if (mProcessing && this.mClient != null) {
            this.mClient.stopRecordToRecognize();
        }
        mProcessing = false;

        stopTime = System.currentTimeMillis();
    }

//    protected void cancel() {
//        if (mProcessing && this.mClient != null) {
//            mProcessing = false;
//            this.mClient.cancel();
//        }
//    }


    // Old api
    @Override
    public void onResult(String result) {
        if (this.mClient != null) {
            this.mClient.cancel();
            mProcessing = false;
        }

        try {
            JSONObject tt = null;
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            if(j1.getString("msg").contains("No"))
                Toast.makeText(getActivity(),"음악을 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
            int j2 = j1.getInt("code");
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");
                if (metadata.has("humming")) {
                    JSONArray hummings = metadata.getJSONArray("humming");
                    for(int i=0; i<hummings.length(); i++) {
                        tt = (JSONObject) hummings.get(i);
                        String title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");
                    }
                }

                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");
                    for(int i=0; i<musics.length(); i++) {
                        tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");
                    }
                }
                if (metadata.has("streams")) {
                    JSONArray musics = metadata.getJSONArray("streams");
                    for(int i=0; i<musics.length(); i++) {
                        tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        String channelId = tt.getString("channel_id");
                    }
                }
                if (metadata.has("custom_files")) {
                    JSONArray musics = metadata.getJSONArray("custom_files");
                    for(int i=0; i<musics.length(); i++) {
                        tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                    }
                }
                Log.v("debug", tt.toString());
                if(tt != null) {
                    stop();
                    listenBtn.setChecked(false);
                    Fragment fragment = MusicFragment.newInstance();
                    Bundle bundle = new Bundle();

                    parseData(tt.toString());

                    bundle.putString("data", dataVO.toString());
                    bundle.putBoolean("key", true);
                    fragment.setArguments(bundle);
                    replaceFragment(fragment);
                }
            }
            else if(j2== 1001) {
                stop();
                listenBtn.setChecked(false);
                loader.clearAnimation();
                animation.setAnimationListener(null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void parseData(String data) {
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

            title = musicVO.getTitle();
            artist = musicVO.getArtist().getName();

            sendData(musicVO);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendData(MusicVO musicVO) {
        Thread thread = new Thread(){
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
            public void run() {
                frontUrl = "https://www.melon.com/search/song/index.htm?startIndex=1&pageSize=50&q=";
                endUrl = "&sort=hit&section=all&sectionId=&genreDir=&subLinkOrText=L";
                String tmpTitle = title.replace(" ", "");
                url = frontUrl + tmpTitle + endUrl;
                try {
                    Document doc = Jsoup.connect(url).get();
                    Log.v("url", url + "");
                    Elements elements = doc.select("table tbody").select(".fc_mgray");
                    check = false;
                    for(Element element : elements) {
                        String compareArtist = element.select("a").text();

                        Log.v("Artist List > ", compareArtist+"");
                        if(compareArtist.toLowerCase().contains(artist.toLowerCase()) || artist.toLowerCase().contains(compareArtist.toLowerCase())) {
                            String ref = element.select("a").attr("href");
                            Log.v("LINK : ", ref+"");
                            String[] tmpArray = ref.split(",");
                            String id = tmpArray[tmpArray.length - 1];
                            id = id.substring(1, id.length()-3);
                            Log.v("SONG ID : ", id+"");
                            melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;
                            check = true;
                            break;
                        }
                    }
                    if (check == false) {
                        if(tmpTitle.contains("(")) {
                            int front = tmpTitle.indexOf('(');
                            tmpTitle = tmpTitle.substring(0, front);
                            url = frontUrl + tmpTitle + endUrl;
                            doc = Jsoup.connect(url).get();
                            Log.v("new url", url + "");
                            elements = doc.select("table tbody").select(".fc_mgray");
                            check = false;
                            for(Element element : elements) {
                                String compareArtist = element.select("a").text();

                                Log.v("Artist List > ", compareArtist+"");
                                if(compareArtist.toLowerCase().contains(artist.toLowerCase()) || artist.toLowerCase().contains(compareArtist.toLowerCase())) {
                                    String ref = element.select("a").attr("href");
                                    Log.v("LINK : ", ref+"");
                                    String[] tmpArray = ref.split(",");
                                    String id = tmpArray[tmpArray.length - 1];
                                    id = id.substring(1, id.length()-3);
                                    Log.v("SONG ID : ", id+"");
                                    melonUrl = "https://www.melon.com/song/detail.htm?songId=" + id;
                                    check = true;
                                    break;
                                }
                            }
                            if (check == false) {
                                melonUrl = null;
                            }
                        }
                        else
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
                        if(lyricsElement.first().html() != null) {
                            lyrics = lyricsElement.first().html();
                            pos = lyrics.indexOf("<br>");
                            tmp = lyrics.substring(0, pos);
                            lyrics = lyrics.substring(pos);
                            pos = tmp.lastIndexOf('>');
                            if (pos != -1)
                                tmp = tmp.substring(pos + 1);
                            lyrics = tmp + lyrics;
                            Log.v("before replacing : ", lyrics);
                            lyrics = lyrics.replaceAll("<br>", "\n");
                            Elements imgElement = doc.select("a.image_typeAll img");
                            String imgPath = imgElement.attr("src");
                            image = imgPath;
                            lyric = lyrics;
                        }
                        else {
                            lyric = null;
                        }

                    }
                    else {
                        lyric = null;
                        image = null;
                    }


                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                }
            }
        };



        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = new Date();
        String date = format.format(currentTime);

        dataVO = new DataVO(artist, title, date, image, lyric);
        write(dataVO);

    }

    //여기서 원래 http 통신을 해야함
    private void write(DataVO dataVO) {
        String filepath = RECORDED_FILE.getAbsolutePath() + "/SSUGuitar/log";

        File file = new File(filepath);
        if(!file.exists()){
            file.mkdirs();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        SimpleDateFormat format_2 = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = new Date();
        String title = format.format(currentTime);
        String date = format_2.format(currentTime);


        File savefile = new File(filepath+"/"+title+" "+dataVO.getTitle()+" "+dataVO.getArtist()+".txt");
        try{
            FileOutputStream fos = new FileOutputStream(savefile);
            fos.write(dataVO.toString().getBytes());
            fos.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

}
