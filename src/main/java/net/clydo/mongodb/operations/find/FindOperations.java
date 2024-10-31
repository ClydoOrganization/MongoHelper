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
 * Copyright (C) 2024 ClydoNetwork
 */

package net.clydo.mongodb.operations.find;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.val;
import net.clydo.mongodb.error.NotFoundResult;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides operations for finding documents in a MongoDB collection.
 * This class extends {@link AbstractOperation} and implements {@link FindFirstOperations}, {@link FindUniqueOperations}, and {@link FindManyOperations}.
 *
 * @param <M> The type of the model for which find operations are performed.
 */
public class FindOperations<M> extends AbstractOperation<M> implements FindFirstOperations<M>, FindUniqueOperations<M>, FindManyOperations<M> {

    /**
     * Constructs a new {@link FindOperations} instance.
     *
     * @param model The {@link MongoModelValue} instance associated with the find operations.
     */
    public FindOperations(MongoModelValue<M> model) {
        super(model);
    }

    /**
     * Finds the first document in the collection that matches the specified filter.
     *
     * @param filter The filter to apply when finding the document.
     * @return The first document that matches the filter, or {@code null} if no document matches.
     */
    @Override
    public @Nullable M one(@NotNull Bson filter) {
        return this.collection().find(filter).first();
    }

    /**
     * Finds the first document in the collection with no filter applied.
     *
     * @return The first document in the collection, or {@code null} if no document is found.
     */
    @Override
    public @Nullable M one() {
        return this.one(new BsonDocument());
    }

    /**
     * Finds the first document in the collection where the specified field matches the given value.
     *
     * @param fieldName The name of the field to filter on.
     * @param value     The value to match in the field.
     * @return The first document that matches the field and value, or {@code null} if no document matches.
     */
    @Override
    public @Nullable M one(@NotNull String fieldName, @Nullable Object value) {
        return this.one(Filters.eq(fieldName, value));
    }

    /**
     * Finds the first document in the collection where the value of the unique field matches the given value.
     *
     * @param uniqueValue The value to match in the unique field.
     * @return The first document that matches the unique value, or {@code null} if no document matches.
     */
    @Override
    public @Nullable M firstByUnique(@NotNull Object uniqueValue) {
        return this.one(this.firstUniqueFieldName(), uniqueValue);
    }

    /**
     * Finds the first document in the collection that matches the specified filter and throws an exception if no document is found.
     *
     * @param filter The filter to apply when finding the document.
     * @return The first document that matches the filter.
     * @throws NotFoundResult if no document matches the filter.
     */
    @Override
    public @NotNull M firstOrThrowRaw(@NotNull Bson filter) throws NotFoundResult {
        val result = this.one(filter);
        if (result != null) {
            return result;
        }
        throw new NotFoundResult("Failed to find data");
    }

    /**
     * Finds a unique document in the collection that matches the specified filter.
     * The filter is validated to ensure it only targets unique fields.
     *
     * @param filter The filter to apply when finding the document.
     * @return The unique document that matches the filter, or {@code null} if no document matches.
     */
    @Override
    public @Nullable M unique(@NotNull Bson filter) {
        this.validateFilterUniques(filter, this.uniques());
        return this.one(filter);
    }

    /**
     * Finds a unique document in the collection where the specified field matches the given value.
     *
     * @param fieldName The name of the field to filter on.
     * @param value     The value to match in the field.
     * @return The unique document that matches the field and value, or {@code null} if no document matches.
     */
    @Override
    public @Nullable M unique(@NotNull String fieldName, @Nullable Object value) {
        return this.unique(Filters.eq(fieldName, value));
    }

    /**
     * Finds a unique document in the collection where the value of the unique field matches the given value.
     *
     * @param uniqueValue The value to match in the unique field.
     * @return The unique document that matches the unique value, or {@code null} if no document matches.
     */
    @Override
    public @Nullable M uniqueByUnique(@NotNull Object uniqueValue) {
        return this.unique(this.firstUniqueFieldName(), uniqueValue);
    }

    /**
     * Finds a unique document in the collection that matches the specified filter and throws an exception if no document is found.
     *
     * @param filter The filter to apply when finding the document.
     * @return The unique document that matches the filter.
     * @throws NotFoundResult if no document matches the filter.
     */
    @Override
    public @NotNull M uniqueOrThrowRaw(@NotNull Bson filter) throws NotFoundResult {
        val result = this.unique(filter);
        if (result != null) {
            return result;
        }
        throw new NotFoundResult("Failed to find data");
    }

    /**
     * Finds multiple documents in the collection that match the specified filter.
     *
     * @param filter The filter to apply when finding the documents.
     * @return An iterable of documents that match the filter.
     */
    @Override
    public @NotNull FindIterable<M> many(@NotNull Bson filter) {
        return this.collection().find(filter);
    }

    /**
     * Finds all documents in the collection.
     *
     * @return An iterable of all documents in the collection.
     */
    @Override
    public @NotNull FindIterable<M> many() {
        return this.many(new BsonDocument());
    }

    /**
     * Finds multiple documents in the collection where the specified field matches the given value.
     *
     * @param fieldName The name of the field to filter on.
     * @param value     The value to match in the field.
     * @return An iterable of documents where the field matches the value.
     */
    @Override
    public @NotNull FindIterable<M> many(@NotNull String fieldName, @Nullable Object value) {
        return this.many(Filters.eq(fieldName, value));
    }
}