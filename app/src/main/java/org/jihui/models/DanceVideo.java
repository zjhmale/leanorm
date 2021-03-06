package org.jihui.models;

import org.jihui.leanorm.LeanORMModel;

/**
 * Created by zjh on 2015/5/23.
 */
public class DanceVideo extends LeanORMModel {
    public int id;
    public String title;
    public String authorname;
    public String image_url;
    public String video_url;
    public String speedup_url;
    public String priority;
    public String dancegroup;
    public String dancemusic;
    public String starname;
    public int hot;
    public int type;
    public int star;
    public int update_timestamp;

    @Override
    public String toString() {
        return "DanceVideo{" +
                "_id=" + this._id +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", authorname='" + authorname + '\'' +
                ", image_url='" + image_url + '\'' +
                ", video_url='" + video_url + '\'' +
                ", speedup_url='" + speedup_url + '\'' +
                ", priority='" + priority + '\'' +
                ", dancegroup='" + dancegroup + '\'' +
                ", dancemusic='" + dancemusic + '\'' +
                ", starname='" + starname + '\'' +
                ", hot='" + hot + '\'' +
                ", star='" + star + '\'' +
                ", type=" + type +
                ", update_timestamp=" + update_timestamp +
                '}';
    }
}
