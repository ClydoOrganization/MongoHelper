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

package net.clydo.mongo.operations.delete;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.BaseOperations;
import net.clydo.mongo.operations.find.FilterBuilder;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeleteOperationsImpl<M> extends BaseOperations<M> implements DeleteOneOperations<M>, DeleteManyOperations<M> {

    public DeleteOperationsImpl(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        super(collection, model);
    }

    @NotNull
    @Override
    public final DeleteResult one(
            @NotNull final Bson filter
    ) {
        Validates.require(filter, "filter");

        return this.collection.deleteOne(filter);
    }

    @NotNull
    @Override
    public final DeleteResult one(
            @NotNull final String fieldName,
            @Nullable final Object value
    ) {
        Validates.require(fieldName, "fieldName");

        return this.one(Filters.eq(fieldName, value));
    }

    @NotNull
    @Override
    public final DeleteResult one(
            @NotNull final M datum
    ) {
        Validates.require(datum, "datum");

        val filters = this.model.fieldMetaMap()
                .values()
                .stream()
                .map(field -> Filters.or(
                        field.getAliases()
                                .stream()
                                .map(name -> Filters.eq(name, field.get(datum)))
                                .toList()
                ))
                .toList();

        return this.one(Filters.and(filters));
    }

    @NotNull
    @Override
    public final DeleteResult unique(
            @NotNull final String indexName,
            @NotNull final M datum
    ) {
        Validates.require(indexName, "indexName");
        Validates.require(datum, "datum");

        return this.one(
                FilterBuilder.buildFilterByIndex(
                        this.model,
                        this.indices,
                        indexName,
                        datum
                )
        );
    }

    @NotNull
    @Override
    public final DeleteResult many(
            @NotNull final Bson filter
    ) {
        Validates.require(filter, "filter");

        return this.collection.deleteMany(filter);
    }

    @NotNull
    @Override
    public DeleteResult many() {
        return this.many(new BsonDocument());
    }

    @NotNull
    @Override
    public final DeleteResult many(
            @NotNull final String fieldName,
            @NotNull final List<Object> values
    ) {
        Validates.require(fieldName, "fieldName");
        Validates.require(values, "values");

        return this.many(Filters.in(fieldName, values));
    }

    @NotNull
    @Override
    public final DeleteResult many(
            @NotNull final List<M> data
    ) {
        Validates.requireFilled(data, "data");

        val fields = this.model.fieldMetaMap();
        val filters = data.stream()
                .map(datum -> Filters.and(
                        fields.values()
                                .stream()
                                .map(field -> Filters.eq(field.getName(), field.get(datum)))
                                .toList()
                ))
                .toList();

        return this.many(Filters.or(filters));
    }

    @NotNull
    @Override
    public final DeleteResult uniques(
            @NotNull final String indexName,
            @NotNull final List<M> data
    ) {
        Validates.require(indexName, "indexName");
        Validates.requireFilled(data, "data");

        val filters = data.stream()
                .map(datum -> FilterBuilder.buildFilterByIndex(
                        this.model,
                        this.indices,
                        indexName,
                        datum
                ))
                .toList();

        return this.many(Filters.or(filters));
    }

}
