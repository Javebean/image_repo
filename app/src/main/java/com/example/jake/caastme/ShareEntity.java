package com.example.jake.caastme;

/**
 * Created by jake on 2016/12/3.
 */

public class ShareEntity {

    private int _id;

    private String favicon;
    private String title;
    private String url;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFavicon() {
        return favicon;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
