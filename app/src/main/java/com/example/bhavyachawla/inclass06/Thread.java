package com.example.likhi.inclass06;

public class Thread {
    String title,user_id,id;

    public Thread(){

    }

    public Thread(String id, String title, String user_id) {
        this.id = id;
        this.title = title;
        this.user_id = user_id;
    }
    @Override
    public String toString() {
        return "Thread{" +
                "id='" + id +'\''+
                "title='" + title + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
