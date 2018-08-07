package VO;

import java.util.Date;

/**
 * Created by misconstructed on 2018. 8. 7..
 */

public class MusicVO {
    private String external_ids;
    private int play_offset_ms;
    private String external_metadata;
    private ArtistVO artist;
    private GenreVO genres;
    private String title;
    private String release_date;
    private String label;
    private int duration_ms;
    private AlbumVO album;
    private String acrid;
    private int result_from;
    private int score;

    public String getExternal_ids() {
        return external_ids;
    }

    public void setExternal_ids(String external_ids) {
        this.external_ids = external_ids;
    }

    public int getPlay_offset_ms() {
        return play_offset_ms;
    }

    public void setPlay_offset_ms(int play_offset_ms) {
        this.play_offset_ms = play_offset_ms;
    }

    public String getExternal_metadata() {
        return external_metadata;
    }

    public void setExternal_metadata(String external_metadata) {
        this.external_metadata = external_metadata;
    }

    public ArtistVO getArtist() {
        return artist;
    }

    public void setArtist(ArtistVO artist) {
        this.artist = artist;
    }

    public GenreVO getGenres() {
        return genres;
    }

    public void setGenres(GenreVO genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }

    public AlbumVO getAlbum() {
        return album;
    }

    public void setAlbum(AlbumVO album) {
        this.album = album;
    }

    public String getAcrid() {
        return acrid;
    }

    public void setAcrid(String acrid) {
        this.acrid = acrid;
    }

    public int getResult_from() {
        return result_from;
    }

    public void setResult_from(int result_from) {
        this.result_from = result_from;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "MusicVO{" +
                "external_ids='" + external_ids + '\'' +
                ", play_offset_ms=" + play_offset_ms +
                ", external_metadata='" + external_metadata + '\'' +
                ", artist=" + artist +
                ", genres=" + genres +
                ", title='" + title + '\'' +
                ", release_date=" + release_date +
                ", label='" + label + '\'' +
                ", duration_ms=" + duration_ms +
                ", album=" + album +
                ", acrid='" + acrid + '\'' +
                ", result_from=" + result_from +
                ", score=" + score +
                '}';
    }
}

