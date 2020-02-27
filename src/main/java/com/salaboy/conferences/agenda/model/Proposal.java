package com.salaboy.conferences.agenda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Proposal {
    private String author;
    private String title;
    private Date talkTime;

    public Proposal() {
    }

    public Proposal(String author, String title, Date talkTime) {
        this.author = author;
        this.title = title;
        this.talkTime = talkTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTalkTime() {
        return talkTime;
    }

    public void setTalkTime(Date talkTime) {
        this.talkTime = talkTime;
    }
}
