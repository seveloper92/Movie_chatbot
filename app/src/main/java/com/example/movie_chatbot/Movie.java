package com.example.movie_chatbot;


//모델 부분
//각각의 영화데이터를 위한 오브젝트이다. 리퀘스트로 받는 json 데이터의 이름대로 만들었다. 더 필요한것이 있다면 추가하거나 삭제하여 수정하면 된다.
//json형태로 받아오니까
public class Movie {
    //리퀘스로 받아 오는 Json 데이터를 참조해서 만들자.
    private String id;
    private String title;
    private String original_title;
    private String poster_path;
    private String overview;
    private String backdrop_path;
    private String release_date;

    public Movie(String id,String title, String original_title, String poster_path, String overview, String backdrop_path, String release_date) {
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.backdrop_path = backdrop_path;
        this.release_date = release_date;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getRelease_date() {
        return release_date;
    }
}


