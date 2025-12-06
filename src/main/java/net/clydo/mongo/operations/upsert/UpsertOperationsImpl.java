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

package net.clydo.mongo.operations.upsert;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.BaseOperations;
import net.clydo.mongo.operations.find.FilterBuilder;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UpsertOperationsImpl<M> extends BaseOperations<M> implements UpsertOperations<M> {

    public UpsertOperationsImpl(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        super(collection, model);
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final Bson create,
            @NotNull final UpdateOptions options
    ) {
        Validates.require(filter, "filter");
        Validates.require(update, "update");
        Validates.require(create, "create");
        Validates.require(options, "options");

        return this.collection.updateOne(
                filter,
                Updates.combine(
                        update,
                        create
                ),
                options
        );
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final Bson create
    ) {
        Validates.require(filter, "filter");
        Validates.require(update, "update");
        Validates.require(create, "create");

        return this.one(
                filter,
                update,
                create,
                new UpdateOptions()
                        .upsert(true)
        );
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final M datum,
            @NotNull final ReplaceOptions options
    ) {
        Validates.require(filter, "filter");
        Validates.require(datum, "datum");
        Validates.require(options, "options");

        return this.collection.replaceOne(filter, datum, options);
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final M datum
    ) {
        Validates.require(filter, "filter");
        Validates.require(datum, "datum");

        return this.one(
                filter,
                datum,
                new ReplaceOptions()
                        .upsert(true)
        );
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final M create
    ) {
        Validates.require(filter, "filter");
        Validates.require(update, "update");
        Validates.require(create, "create");

        val updates = this.model.fieldMetaMap()
                .values()
                .stream()
                .map(field ->
                        Updates.setOnInsert(field.getName(), field.get(create))
                )
                .toList();

        return this.one(filter, update, Updates.combine(updates));
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final M datum,
            @NotNull final List<String> fields
    ) {
        Validates.require(filter, "filter");
        Validates.require(update, "update");
        Validates.require(datum, "datum");
        Validates.requireFilled(fields, "fields");

        val modelFields = this.model.fieldMetaMap();
        val creates = fields.stream()
                .map(modelFields::get)
                .map(field ->
                        Updates.setOnInsert(field.getName(), field.get(datum))
                )
                .toList();

        return this.one(filter, update, Updates.combine(creates));
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
                Updates.combine(updates),
                datum,
                fields
        );
    }

    @NotNull
    @Override
    public final UpdateResult one(
            @NotNull final String indexName,
            @NotNull final M datum
    ) {
        return this.one(
                FilterBuilder.buildFilterByIndex(
                        this.model,
                        this.indices,
                        indexName,
                        datum
                ),
                datum,
                new ReplaceOptions()
                        .upsert(true)
        );
    }

}
