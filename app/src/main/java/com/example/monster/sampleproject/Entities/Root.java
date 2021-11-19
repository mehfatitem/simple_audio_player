package com.example.monster.sampleproject.Entities;

import java.util.List;

public class Root {

    public List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}