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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MongoHelper.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2024 ClydoNetwork
 */

package net.clydo.mongodb.operations.count;

import com.mongodb.client.model.Filters;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

/**
 * Provides operations for counting documents in a MongoDB collection.
 * This class extends {@link AbstractOperation} and implements {@link ICountOperations}.
 *
 * @param <M> The type of the model for which count operations are performed.
 */
public class CountOperations<M> extends AbstractOperation<M> implements ICountOperations<M> {

    /**
     * Constructs a new {@link CountOperations} instance.
     *
     * @param model The {@link MongoModelValue} instance associated with the count operations.
     */
    public CountOperations(MongoModelValue<M> model) {
        super(model);
    }

    /**
     * Counts the number of documents in the collection that match the specified filter.
     *
     * @param filter The filter to apply when counting documents.
     * @return The count of documents that match the filter.
     */
    @Override
    public long raw(@NotNull Bson filter) {
        return this.collection().countDocuments(filter);
    }

    /**
     * Counts the number of documents in the collection where the specified field matches the given value.
     *
     * @param fieldName The name of the field to filter on.
     * @param value     The value to match in the field.
     * @return The count of documents where the field matches the given value.
     */
    @Override
    public long raw(@NotNull String fieldName, @NotNull Object value) {
        return this.raw(Filters.eq(fieldName, value));
    }
}
