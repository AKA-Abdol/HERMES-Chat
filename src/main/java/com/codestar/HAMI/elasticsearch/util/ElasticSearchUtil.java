package com.codestar.HAMI.elasticsearch.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.val;

import java.util.function.Supplier;

public class ElasticSearchUtil {

    public static Supplier<Query> supplier(){
        return ()->Query.of(q->q.matchAll(matchAllQuery()));
    }

    public static MatchAllQuery matchAllQuery(){
        val  matchAllQuery = new MatchAllQuery.Builder();
        return matchAllQuery.build();
    }

    public static Supplier<Query> supplierWithNameField(String fieldValue){
        return ()->Query.of(q->q.match(matchQueryWithNameField(fieldValue)));
    }

    public static MatchQuery matchQueryWithNameField(String fieldValue){
        val  matchQuery = new MatchQuery.Builder();
        return matchQuery.field("username").query(fieldValue).fuzziness("2").build();
    }
}
