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

package net.clydo.mongo.operations.count;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.BaseOperations;
import net.clydo.mongo.operations.find.FilterBuilder;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CountOperationsImpl<M> extends BaseOperations<M> implements CountOperations {

    public CountOperationsImpl(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        super(collection, model);
    }

    @Override
    public final long by(
            @NotNull final Bson filter
    ) {
        Validates.require(filter, "filter");

        return this.collection.countDocuments(filter);
    }

    @Override
    public final long by(
            @NotNull final String fieldName,
            @NotNull final Object value
    ) {
        Validates.require(fieldName, "fieldName");
        Validates.require(value, "value");

        return this.by(Filters.eq(fieldName, value));
    }

    @Override
    public final long byIndex(
            @NotNull final String indexName,
            @NotNull final Object value
    ) {
        Validates.require(indexName, "indexName");

        return this.by(
                FilterBuilder.buildFilterByIndex(
                        this.model,
                        this.indices,
                        indexName,
                        new Object[]{value}
                )
        );
    }

    @Override
    public final long byIndex(
            @NotNull final String indexName,
            @NotNull final List<Object> values
    ) {
        Validates.require(indexName, "indexName");
        Validates.requireFilled(values, "values");

        return this.by(
                FilterBuilder.buildFilterByIndex(
                        this.model,
                        this.indices,
                        indexName,
                        values.toArray()
                )
        );
    }

}
