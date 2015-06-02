package com.darfootech.dbdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.darfootech.dbdemo.darfooorm.Configuration;
import com.darfootech.dbdemo.darfooorm.DarfooORMDao;
import com.darfootech.dbdemo.darfooorm.Tuple;
import com.darfootech.dbdemo.models.DanceVideo;

import java.util.List;

/**
 * Created by zjh on 2015/6/1.
 */
public class MockUp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*showVideos();
        deleteVideos();
        showVideos();*/
        //insertVideos();
        //showVideosByField();
        //addColumnToTable();
        insertVideos();
        showVideos();
    }

    public void addColumnToTable() {
        Log.d("DARFOO_ORM", DarfooORMDao.executeMigrations("add_star_column_to_dancevideo") + "");
    }

    public void insertVideos() {
        for (int i = 0; i < 10; i++) {
            DanceVideo video = new DanceVideo();
            video.title = "hehe" + i;
            video.id = 3;
            video.star = i;
            video.starname = "star" + i;
            video.save();
        }
    }

    public void showVideos() {
        List<DanceVideo> videos = DarfooORMDao.findAll(DanceVideo.class);
        Log.d("DARFOO_ORM", videos.size() + "");
        for (DanceVideo v : videos) {
            Log.d("DARFOO_ORM", v.toString());
        }
    }

    public void showVideosByField() {
        //List<DanceVideo> videos = DarfooORMDao.findByField(DanceVideo.class, new Tuple("id", 3));
        List<DanceVideo> videos = DarfooORMDao.findByField(DanceVideo.class, new Tuple("title", "hehe3"));
        Log.d("DARFOO_ORM", videos.size() + "");
        for (DanceVideo v : videos) {
            Log.d("DARFOO_ORM", v.toString());
        }
    }

    public void updateVideos() {
        List<DanceVideo> videos = DarfooORMDao.findAll(DanceVideo.class);
        Log.d("DARFOO_ORM", videos.size() + "");
        for (DanceVideo v : videos) {
            Log.d("DARFOO_ORM", v.title);
            v.title = "meme";
            v.priority = "memeda";
            v.dancemusic = "dancemusic";
            v.hot += 1;
            v.save();
        }
    }

    public void deleteVideos() {
        List<DanceVideo> videos = DarfooORMDao.findAll(DanceVideo.class);
        Log.d("DARFOO_ORM", videos.size() + "");
        for (DanceVideo v : videos) {
            Log.d("DARFOO_ORM", v.title);
            v.title = "meme";
            v.delete();
        }
    }

    public void singleVideo() {
        DanceVideo video = DarfooORMDao.findById(DanceVideo.class, 3);
        Log.d("DARFOO_ORM", video.toString());
        video.title = "cleantha";
        video.save();
        video = DarfooORMDao.findById(DanceVideo.class, 3);
        Log.d("DARFOO_ORM", video.toString());
    }
}
