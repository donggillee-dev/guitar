package VO;

/**
 * Created by misconstructed on 2018. 8. 11..
 */

public class DataVO {
    private String artist;
    private String title;
    private String searched_date;
    private String image;
    private String lyric;

    public DataVO(String artist, String title, String searched_date, String image, String lyric) {
        this.artist = artist;
        this.title = title;
        this.searched_date = searched_date;
        this.image = image;
        this.lyric = lyric;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSearched_date() {
        return searched_date;
    }

    public void setSearched_date(String searched_date) {
        this.searched_date = searched_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    @Override
    public String toString() {
        return "{" +
                "\"artist\":\"" + artist + "\"" +
                ", \"title\":\"" + title + "\"" +
                ", \"searched_date\":\"" + searched_date + "\"" +
                ", \"image\":\"" + image + "\"" +
                ", \"lyric\":\"" + lyric + "\"" +
                "}";
    }
}
