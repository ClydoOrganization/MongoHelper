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

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface UpsertOperations<M> {

    @NotNull
    UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final Bson create,
            @NotNull final UpdateOptions options
    );

    @NotNull
    UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final Bson create
    );

    @NotNull
    UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final M datum,
            @NotNull final ReplaceOptions options
    );

    @NotNull
    UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final M datum
    );

    @NotNull
    UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final M create
    );

    @NotNull
    UpdateResult one(
            @NotNull final Bson filter,
            @NotNull final Bson update,
            @NotNull final M datum,
            @NotNull final List<String> fields
    );

    @NotNull
    UpdateResult one(
            @NotNull final String indexName,
            @NotNull final M datum,
            @NotNull final List<String> fields
    );

    @NotNull
    UpdateResult one(
            @NotNull final String indexName,
            @NotNull final M datum
    );

}
