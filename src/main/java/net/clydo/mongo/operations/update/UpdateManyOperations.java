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
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface UpdateManyOperations<M> {

    @NotNull
    UpdateResult many(
            @NotNull final Bson filter,
            @NotNull final Bson update
    );

    @NotNull
    BulkWriteResult many(
            @NotNull final String indexName,
            @NotNull final List<M> data
    );

}
