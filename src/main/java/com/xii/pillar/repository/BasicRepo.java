package com.xii.pillar.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasicRepo {

    @Autowired
    public MongoTemplate mongoTemplate;

    public <T> T getById(Object id, Class<T> entityClass) {
        return mongoTemplate.findById(id, entityClass);
    }

    public <T> List<T> getObjectsByPage(Criteria criteria, int page, int pageSize, Sort sort, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(sort);

        int skipSize = (page - 1) * pageSize;
        query.skip(skipSize).limit(pageSize);
        return mongoTemplate.find(query, clazz);
    }

    public <T> List<T> getObjects(Criteria criteria, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, clazz);
    }

    public <T> List<T> getObjects(Criteria criteria, Sort sort, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(sort);
        return mongoTemplate.find(query, clazz);
    }

    public <T> long countObjects(Criteria criteria, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.count(query, clazz);
    }

    public <T> UpdateResult updateFirst(Criteria criteria, Update update, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.updateFirst(query, update, clazz);
    }

    public <T> T findAndModify(Criteria criteria, Update update, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), clazz);
    }

    public <T> void updateMulti(Criteria criteria, Update update, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        mongoTemplate.updateMulti(query, update, clazz);
    }

    public <T> T findOne(Criteria criteria, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, clazz);
    }

    public <T> T findOne(Criteria criteria, Sort sort, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(sort);
        return mongoTemplate.findOne(query, clazz);
    }

    public <T> void save(T t) {
        mongoTemplate.save(t);
    }

    public <T> void insertAll(List<T> list) {
        mongoTemplate.insertAll(list);
    }

    public <T> UpdateResult upsert(Criteria criteria, Update update, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.upsert(query, update, clazz);
    }

    public <T> List<T> findAll(Class<T> clazz) {
        return mongoTemplate.findAll(clazz);
    }

    public <T> void findAndRemove(Criteria criteria, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(criteria);
        mongoTemplate.findAllAndRemove(query, clazz);
    }

    public BulkWriteResult batchUpdate(List<BatchUpdateOptions> options, Class<?> entityClass) {
        String collectionName = determineCollectionName(entityClass);
        BulkOperations operations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collectionName);

        options.forEach(data -> {
            Query query = data.getQuery();
            Update update= data.getUpdate();
            operations.updateOne(query, update);
        });

        BulkWriteResult result = operations.execute();
        return result;
    }

    private static String determineCollectionName(Class<?> entityClass) {
        if (entityClass == null) {
            throw new InvalidDataAccessApiUsageException(
                    "No class parameter provided, entity collection can't be determined!");
        }
        String collName = entityClass.getSimpleName();
        if(entityClass.isAnnotationPresent(Document.class)) {
            Document document = entityClass.getAnnotation(Document.class);
            collName = document.collection();
        } else {
            collName = collName.replaceFirst(collName.substring(0, 1)
                    ,collName.substring(0, 1).toLowerCase()) ;
        }
        return collName;
    }
}
