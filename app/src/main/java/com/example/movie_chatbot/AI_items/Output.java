package com.example.movie_chatbot.AI_items;

import android.media.Image;

import java.net.URI;
import java.util.List;

/**
 * Created by User on 2017-08-19.
 */

public class Output {
    private List<String> visit_nodes_text;
    private List<String> visit_nodes;
    private List<String> visit_nodes_name;
    private List<String> text;
    private List<URI> uri;
    private List<Image> images;

    public List<String> getVisit_nodes_text() {
        return visit_nodes_text;
    }

    public void setVisit_nodes_text(List<String> visit_nodes_text) {
        this.visit_nodes_text = visit_nodes_text;
    }

    public List<String> getVisit_nodes() {
        return visit_nodes;
    }

    public void setVisit_nodes(List<String> visit_nodes) {
        this.visit_nodes = visit_nodes;
    }

    public List<String> getVisit_nodes_name() {
        return visit_nodes_name;
    }

    public void setVisit_nodes_name(List<String> visit_nodes_name) {
        this.visit_nodes_name = visit_nodes_name;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
}
