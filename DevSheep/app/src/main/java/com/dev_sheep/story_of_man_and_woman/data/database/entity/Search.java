package com.dev_sheep.story_of_man_and_woman.data.database.entity;



import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "Search")
public class Search {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;


    @Ignore
    public Search(String title){
        this.title = title;
    }

    public Search(int id, String title){
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "search{"+
                "title="+title+ '}';
    }
}
