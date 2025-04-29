package com.xii.pillar.repository;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class BatchUpdateOptions {
    private Query query;
    private Update update;


    public BatchUpdateOptions(Query query, Update update) {
        this.query = query;
        this.update = update;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }
}
