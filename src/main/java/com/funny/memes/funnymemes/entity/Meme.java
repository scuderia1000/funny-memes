package com.funny.memes.funnymemes.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "memes")
public class Meme {

    @Id
    private String id;
    private String authorName;
    // url источника
    private String sourceMediaUrl;
    // url на файловом хранилище
    private String fullMediaUrl;
    private String previewUrl;
    private Date publishDate;
    private String description;
    private Integer score;
    private String title;
    private String lang;
    private String md5Sum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSourceMediaUrl() {
        return sourceMediaUrl;
    }

    public void setSourceMediaUrl(String sourceMediaUrl) {
        this.sourceMediaUrl = sourceMediaUrl;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFullMediaUrl() {
        return fullMediaUrl;
    }

    public void setFullMediaUrl(String fullMediaUrl) {
        this.fullMediaUrl = fullMediaUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }
}
