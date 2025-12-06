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

package net.clydo.mongo.operations.create;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.BaseOperations;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreateOperationsImpl<M> extends BaseOperations<M> implements CreateOneOperations<M>, CreateManyOperations<M> {

    public CreateOperationsImpl(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        super(collection, model);
    }

    @NotNull
    @Override
    public final InsertOneResult one(
            @NotNull final M datum
    ) {
        Validates.require(datum, "datum");

        return this.collection.insertOne(datum);
    }

    @NotNull
    @Override
    public final InsertManyResult many(
            @NotNull final List<M> data
    ) {
        Validates.requireFilled(data, "data");

        return this.collection.insertMany(data);
    }

}
