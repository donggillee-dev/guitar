package Network;

/**
 * Created by misconstructed on 2018. 8. 23..
 */

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

import VO.DataVO;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by choisunpil on 2018. 5. 31..
 */

public class PostMusicSearch {

    private Response response;
    private RequestBody formBody;
    private Request request;
    private OkHttpClient client = new OkHttpClient();
    private String image;
    private String lyric;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String post(String url, String id, DataVO dataVO) throws IOException {
        client = new OkHttpClient();
        if(dataVO.getImage() == null)
            image = "null";
        else
            image = dataVO.getImage();

        if(dataVO.getLyric() == null)
            lyric = "null";
        else
            lyric = dataVO.getLyric();

        formBody = new FormBody.Builder()
                .add("ID", id)
                .add("title", dataVO.getTitle())
                .add("artist", dataVO.getArtist())
                .add("date", dataVO.getSearched_date())
                .add("image", image)
                .add("lyric", lyric)
                .build();
        request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Log.v("body", request.url().toString());
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.body().string();
    }
}