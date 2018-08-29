package Network;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAudioFile {
    private static final String TAG = "AudioFile";

    private Response response;
    private RequestBody formBody;
    private Request request;
    private OkHttpClient client;

    public String post(String url, String audioFileName) throws IOException {
//        String filename = RECORDED_FILE.getAbsolutePath() + "/SSUGuitar/" + audioFileName + ".mp3";
        File f = new File(audioFileName);
        Log.v(TAG,String.valueOf(f.length()));

        client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("record","tmp.mp3", RequestBody.create(MultipartBody.FORM, new File(audioFileName)))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Log.v(TAG, request.url().toString());
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.body().string();
    }
}