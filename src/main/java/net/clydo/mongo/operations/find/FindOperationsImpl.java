/*
 * This file is part of MongoHelper.
 *
 * MongoHelper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * MongoHelper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MongoHelper. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2024-2025 ClydoNetwork
 */

package net.clydo.mongo.operations.find;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.BaseOperations;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class FindOperationsImpl<M> extends BaseOperations<M> implements FindFirstOperations<M>, FindUniqueOperations<M>, FindManyOperations<M> {

    public FindOperationsImpl(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        super(collection, model);
    }

    @Nullable
    @Override
    public final M first(
            @NotNull final Bson filter
    ) {
        Validates.require(filter, "filter");

        return this.collection.find(filter).first();
    }

    @Nullable
    @Override
    public final M first() {
        return this.first(new BsonDocument());
    }

    @Nullable
    @Override
    public final M first(
            @NotNull final String fieldName,
            @Nullable final Object value
    ) {
        Validates.require(fieldName, "fieldName");

        return this.first(Filters.eq(fieldName, value));
    }

    @Nullable
    @Override
    public final M unique(
            @NotNull final Bson filter
    ) {
        Validates.require(filter, "filter");
        this.validateFilterUniques(filter);

        return this.first(filter);
    }

    @Nullable
    @Override
    public final M unique(
            @NotNull final String indexName,
            @Nullable final Object value
    ) {
        Validates.require(indexName, "indexName");

        return this.unique(indexName, Collections.singletonList(value));
    }

    @Nullable
    @Override
    public final M unique(
            @NotNull final String indexName,
            @Nullable final List<Object> values
    ) {
        Validates.require(indexName, "indexName");
        Validates.require(values, "values");

        return this.first(FilterBuilder.buildFilterByIndex(
                this.model,
                this.indices,
                indexName,
                values
        ));
    }

    @NotNull
    @Override
    public final FindIterable<M> many(
            @NotNull final Bson filter
    ) {
        Validates.require(filter, "indexName");

        return this.collection.find(filter);
    }

    @NotNull
    @Override
    public final FindIterable<M> many() {
        return this.many(new BsonDocument());
    }

    @NotNull
    @Override
    public final FindIterable<M> many(
            @NotNull final String fieldName,
            @Nullable final Object value
    ) {
        Validates.require(fieldName, "fieldName");

        return this.many(Filters.eq(fieldName, value));
    }

}
