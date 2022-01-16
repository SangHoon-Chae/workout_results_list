package com.example.workout_results_list;

import android.graphics.Bitmap;

public class SampleData {
    private int poster;
    private String movieName;
    private String grade;
    private Bitmap bm;

    public SampleData(int poster, String movieName, String grade, Bitmap bm){
        this.poster = poster;
        this.movieName = movieName;
        this.grade = grade;
        this.bm = bm;
    }

    public int getPoster()
    {
        return this.poster;
    }

    public Bitmap getBitmap()
    {
        return this.bm;
    }

    public String getMovieName()
    {
        return this.movieName;
    }

    public String getGrade()
    {
        return this.grade;
    }
}