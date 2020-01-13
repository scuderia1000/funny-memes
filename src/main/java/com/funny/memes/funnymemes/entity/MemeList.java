package com.funny.memes.funnymemes.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "memes")
public class MemeList {

    @Id
    private String id;
    private String name;
    private String imagePath;
    private Date publishDate;

    public MemeList() {
    }

    public MemeList(String name, String imagePath, Date publishDate) {
        this.name = name;
        this.imagePath = imagePath;
        this.publishDate = publishDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}
