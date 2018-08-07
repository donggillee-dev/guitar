package VO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by misconstructed on 2018. 8. 7..
 */

public class AlbumVO {
    private String name;

    public AlbumVO (String input) {
        JSONObject object = null;
        try {
            object = new JSONObject(input);
            name = object.getString("name");
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
