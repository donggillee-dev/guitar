package Network;

/**
 * Created by misconstructed on 2018. 8. 25..
 */

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetSheet {
    private OkHttpClient client;
    private RequestBody formBody;
    private Request request;
    private Response response;

    public String run(String url, String token, String id) throws IOException {
        client = new OkHttpClient();
        formBody = new FormBody.Builder()
                .add("token", token)
                .add("ID", id)
                .build();
        request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.body().string();
    }

}