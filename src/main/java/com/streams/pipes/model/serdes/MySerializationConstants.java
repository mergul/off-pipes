package com.streams.pipes.model.serdes;

enum MySerializationConstants {

    USER_TYPE(1),
    NEWS_TYPE(2),
    REVIEW_TYPE(2),
    EXTENDED_ARRAYLIST_TYPE(3);


    private int id;

    MySerializationConstants(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}