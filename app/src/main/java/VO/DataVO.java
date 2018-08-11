package VO;

/**
 * Created by misconstructed on 2018. 8. 11..
 */

public class DataVO {
    private String artist;
    private String title;
    private String album;
    private String searched_date;

    public DataVO(String artist, String title, String album, String searched_date) {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.searched_date = searched_date;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSearched_date() {
        return searched_date;
    }

    public void setSearched_date(String searched_date) {
        this.searched_date = searched_date;
    }

    @Override
    public String toString() {
        return artist + "^" + title + "^" + album + "^" + searched_date;
    }
}
