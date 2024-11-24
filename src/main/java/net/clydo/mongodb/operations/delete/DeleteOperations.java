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

package net.clydo.mongodb.operations.delete;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides operations for deleting documents from a MongoDB collection.
 * This class extends {@link AbstractOperation} and implements {@link DeleteOneOperations} and {@link DeleteManyOperations}.
 *
 * @param <M> The type of the model for which delete operations are performed.
 */
public class DeleteOperations<M> extends AbstractOperation<M> implements DeleteOneOperations<M>, DeleteManyOperations<M> {

    /**
     * Constructs a new {@link DeleteOperations} instance.
     *
     * @param model The {@link MongoModelValue} instance associated with the delete operations.
     */
    public DeleteOperations(MongoModelValue<M> model) {
        super(model);
    }

    /**
     * Deletes a single document from the collection that matches the specified filter.
     *
     * @param filter The filter to apply when deleting the document.
     * @return The result of the delete operation, including information about the deletion.
     */
    @Override
    public @NotNull DeleteResult one(@NotNull Bson filter) {
        return this.collection().deleteOne(filter);
    }

    /**
     * Deletes a single document from the collection where the specified field matches the given value.
     *
     * @param fieldName The name of the field to filter on.
     * @param value     The value to match in the field.
     * @return The result of the delete operation, including information about the deletion.
     */
    @Override
    public @NotNull DeleteResult one(@NotNull String fieldName, @Nullable Object value) {
        return this.one(Filters.eq(fieldName, value));
    }

    /**
     * Deletes a single document from the collection where the value of the unique field matches the given value.
     *
     * @param uniqueValue The value to match in the unique field.
     * @return The result of the delete operation, including information about the deletion.
     */
    @Override
    public @NotNull DeleteResult unique(@NotNull Object uniqueValue) {
        return this.one(this.firstUniqueFieldName(), uniqueValue);
    }

    /**
     * Deletes a single document from the collection where the value of the unique field matches the value in the provided model.
     *
     * @param value The model instance containing the unique field value to match.
     * @return The result of the delete operation, including information about the deletion.
     */
    @Override
    public @NotNull DeleteResult one(@NotNull M value) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(value, fieldName);

        return this.one(fieldName, uniqueValue);
    }

    /**
     * Deletes multiple documents from the collection that match the specified filter.
     *
     * @param filter The filter to apply when deleting documents.
     * @return The result of the delete operation, including information about the deletions.
     */
    @Override
    public @NotNull DeleteResult many(@NotNull Bson filter) {
        return this.collection().deleteMany(filter);
    }

    /**
     * Deletes all documents from the collection.
     *
     * @return The result of the delete operation, including information about the deletions.
     */
    @Override
    public @NotNull DeleteResult all() {
        return this.many(new BsonDocument());
    }

    /**
     * Deletes multiple documents from the collection where the specified field matches any of the given values.
     *
     * @param fieldName    The name of the field to filter on.
     * @param uniqueValues The values to match in the field.
     * @return The result of the delete operation, including information about the deletions.
     */
    @Override
    public @NotNull DeleteResult many(@NotNull String fieldName, @NotNull Object... uniqueValues) {
        return this.many(Filters.in(fieldName, uniqueValues));
    }

    /**
     * Deletes multiple documents from the collection where the value of the unique field matches any of the given values.
     *
     * @param uniqueValues The values to match in the unique field.
     * @return The result of the delete operation, including information about the deletions.
     */
    @Override
    public @NotNull DeleteResult uniques(@NotNull Object... uniqueValues) {
        return this.many(this.firstUniqueFieldName(), uniqueValues);
    }

    /**
     * Deletes multiple documents from the collection where the value of the unique field matches the values in the provided models.
     *
     * @param values The model instances containing the unique field values to match.
     * @return The result of the delete operation, including information about the deletions.
     */
    @SafeVarargs
    @Override
    public final @NotNull DeleteResult many(@NotNull M... values) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValues = this.getFieldValues(values, fieldName);

        return this.many(this.firstUniqueFieldName(), uniqueValues);
    }
}