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

/**
 * Created by choisunpil on 2018. 6. 1..
 */

public class PostRegister {

    public static final MediaType JSON
            = MediaType.parse("application/x-www-form-urlencoded");

    private OkHttpClient client;
    private RequestBody formBody;
    private Request request;
    private Response response;

    public String post(String url, UserVO user) throws IOException {
        client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("name",user.getName());
        formBuilder.add("email", user.getEmail());
        formBuilder.add("ID", user.getId());
        formBuilder.add("password",user.getPassword());

        Log.v("in post register", user.toString());

        formBody = formBuilder.build();
        try {
            request = new Request.Builder()
                    .addHeader("accept", "application/x-www-form-urlencoded")
                    .url(url)
                    .post(formBody)
                    .build();
           // request = new Request.Builder().url(url).post(formBody).build();

            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.body().string();
    }

}