package Network;

/**
 * Created by misconstructed on 2018. 8. 23..
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import VO.UserVO;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;



public class PostRegister {

    public static final MediaType JSON
            = MediaType.parse("application/x-www-form-urlencoded");

    private OkHttpClient client;
    private RequestBody formBody;
    private Request request;
    private Response response;

    public String post(String url, UserVO user) throws IOException {
        client = new OkHttpClient();
        formBody = new FormBody.Builder()
                .add("name",user.getName())
                .add("email", user.getEmail())
                .add("ID", user.getId())
                .add("password",user.getPassword())
                .build();
        request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Log.v("body", request.url().toString());
            Log.v("url", url+"");
            Log.v("in post register", user.toString());
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return response.body().string();
    }

}