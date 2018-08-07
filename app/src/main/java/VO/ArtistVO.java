package VO;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by misconstructed on 2018. 8. 7..
 */

public class ArtistVO {
    String name;

    public ArtistVO (String input) {
        JSONArray array = null;
        JSONObject object = null;
        Log.v("in VO", input);
        try {
            array = new JSONArray(input);
            object = array.getJSONObject(0);
            name = object.getString("name");
            Log.v("after parsing", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
