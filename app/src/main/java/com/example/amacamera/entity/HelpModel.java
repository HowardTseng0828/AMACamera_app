package com.example.amacamera.entity;

import java.io.Serializable;

public class HelpModel implements Serializable {

    public String title, content;

    HelpModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
