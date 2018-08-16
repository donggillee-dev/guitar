package Network;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by misconstructed on 2018. 8. 15..
 */

public class Get {
    private String url;
    private String result;
    private OkHttpClient client;

    public Get (String url) {
        this.url = url;
        client = new OkHttpClient();
        try {
            result = run(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Get{" +
                "url='" + url + '\'' +
                ", result='" + result + '\'' +
                ", client=" + client +
                '}';
    }
}
