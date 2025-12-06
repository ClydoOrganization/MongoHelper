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

package net.clydo.mongo.operations.update;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.BaseOperations;
import net.clydo.mongo.operations.find.FilterBuilder;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpdateOperationsImpl<M> extends BaseOperations<M> implements UpdateOneOperations<M>, UpdateManyOperations<M> {

    public UpdateOperationsImpl(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        super(collection, model);
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update
    ) {
        Validates.require(filter, "filter");
        Validates.require(update, "update");

        return this.collection.updateOne(filter, update);
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final M datum
    ) {
        Validates.require(filter, "filter");
        Validates.require(datum, "datum");

        val updates = this.model.fieldMetaMap()
                .values()
                .stream()
                .map(field ->
                        Updates.set(field.getName(), field.get(datum))
                )
                .toList();

        return this.one(filter, Updates.combine(updates));
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final M datum,
            @NotNull final List<String> fields
    ) {
        Validates.require(filter, "filter");
        Validates.require(datum, "datum");
        Validates.requireFilled(fields, "fields");

        val modelFields = this.model.fieldMetaMap();
        val updates = fields.stream()
                .map(modelFields::get)
                .map(field ->
                        Updates.set(field.getName(), field.get(datum))
                )
                .toList();

        return this.one(filter, Updates.combine(updates));
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final String fieldName,
            @Nullable final Object value,
            @NotNull final M datum
    ) {
        Validates.require(fieldName, "fieldName");
        Validates.require(datum, "datum");

        return this.one(Filters.eq(fieldName, value), datum);
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final String fieldName,
            @Nullable final Object value,
            @NotNull final M datum,
            @NotNull final List<String> fields
    ) {
        Validates.require(fieldName, "fieldName");
        Validates.require(datum, "datum");
        Validates.requireFilled(fields, "fields");

        return this.one(Filters.eq(fieldName, value), datum, fields);
    }

    @NotNull
    @Override
    public final UpdateResult one(
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
                ),
                datum
        );
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final String indexName,
            @NotNull final M datum,
            @NotNull final List<String> fields
    ) {
        Validates.require(indexName, "indexName");
        Validates.require(datum, "datum");
        Validates.requireFilled(fields, "fields");

        val modelFields = this.model.fieldMetaMap();
        val updates = fields.stream()
                .map(modelFields::get)
                .map(field ->
                        Updates.set(field.getName(), field.get(datum))
                )
                .toList();

        return this.one(
                FilterBuilder.buildFilterByIndex(
                        this.model,
                        this.indices,
                        indexName,
                        datum
                ),
                Updates.combine(updates)
        );
    }

    @NotNull
    @Override
    public final UpdateResult many(
            @NotNull final Bson filter,
            @NotNull final Bson update
    ) {
        Validates.require(filter, "filter");
        Validates.require(update, "update");

        return this.collection.updateMany(filter, update);
    }

    @NotNull
    @Override
    public final BulkWriteResult many(
            @NotNull final String indexName,
            @NotNull final List<M> data
    ) {
        Validates.require(indexName, "indexName");
        Validates.requireFilled(data, "data");

        val modelFields = this.model.fieldMetaMap();
        val updates = data.stream()
                .map(datum ->
                        new UpdateOneModel<M>(
                                FilterBuilder.buildFilterByIndex(
                                        this.model,
                                        this.indices,
                                        indexName,
                                        datum
                                ),
                                Updates.combine(
                                        modelFields
                                                .values()
                                                .stream()
                                                .map(field ->
                                                        Updates.set(field.getName(), field.get(datum))
                                                )
                                                .toList()
                                )
                        )
                )
                .toList();

        return this.collection.bulkWrite(
                updates,
                new BulkWriteOptions()
                        .ordered(false)
        );
    }

}
