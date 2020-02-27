package com.salaboy.conferences.agenda.model;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class AgendaItem {

    private String id;
    private String title;
    private String author;
    private Date talkTime;


    public AgendaItem() {
        this.id = UUID.randomUUID().toString();
    }

    public AgendaItem(String title, String author, Date talkTime) {
        this.title = title;
        this.author = author;
        this.talkTime = talkTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getTalkTime() {
        return talkTime;
    }

    public void setTalkTime(Date talkTime) {
        this.talkTime = talkTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaItem agendaItem = (AgendaItem) o;
        return Objects.equals(id, agendaItem.id);
    }

    @Override
    public String toString() {
        return "AgendaItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", talkTime=" + talkTime +
                '}';
    }
}
