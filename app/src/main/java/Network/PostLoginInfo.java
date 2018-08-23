package Network;

/**
 * Created by misconstructed on 2018. 8. 23..
 */

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by choisunpil on 2018. 5. 31..
 */

public class PostLoginInfo {

    private Response response;
    private RequestBody formBody;
    private Request request;
    private OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String post(String url, String id, String password) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("ID", id);
// dynamically add more parameter like this:
        formBuilder.add("password", password);
        formBody = formBuilder.build();
        request = new Request.Builder()
                .addHeader("accept","application/x-www-form-urlencoded")
                .url(url)
                .post(formBody)
                .build();
        response = client.newCall(request).execute();

        return response.body().string();
    }
}