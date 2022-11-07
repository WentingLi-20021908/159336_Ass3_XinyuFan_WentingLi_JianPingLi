package com.example.myapplication.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashMap;

import androidx.room.*;

import com.example.myapplication.dao.ImageMapTypeConverter;

@Entity
public class AMemo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private String title;
    private String content;
    private boolean finished = false;
    private long modDate;

    public AMemo() {
        this.modDate = System.currentTimeMillis();
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setModDate(long modDate) {
        this.modDate = modDate;
    }

    public String getTitle() {
        return title;
    }

    public long getModDate() {
        return modDate;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
