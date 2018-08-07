package VO;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by misconstructed on 2018. 8. 7..
 */

public class GenreVO {
    private ArrayList<String> arraylist;

    public GenreVO (String input) {
        JSONArray array = null;
        JSONObject object = null;
        arraylist = new ArrayList<>();

        Log.v("in VO", input);
        try {
            array = new JSONArray(input);
            for(int i = 0; i < array.length(); i++)
                arraylist.add(array.getJSONObject(i).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getArraylist() {
        return arraylist;
    }

    public void setArraylist(ArrayList<String> arraylist) {
        this.arraylist = arraylist;
    }

    @Override
    public String toString() {
        return "GenreVO{" +
                "arraylist=" + arraylist +
                '}';
    }
}
