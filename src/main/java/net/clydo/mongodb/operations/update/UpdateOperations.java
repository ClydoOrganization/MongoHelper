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

package net.clydo.mongodb.operations.update;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Provides operations for updating documents in a MongoDB collection.
 * This class extends {@link AbstractOperation} and implements {@link UpdateOneOperations} and {@link UpdateManyOperations}.
 *
 * @param <M> The type of the model for which update operations are performed.
 */
public class UpdateOperations<M> extends AbstractOperation<M> implements UpdateOneOperations<M>, UpdateManyOperations<M> {

    /**
     * Constructs a new {@link UpdateOperations} instance.
     *
     * @param model The {@link MongoModelValue} instance associated with the update operations.
     */
    public UpdateOperations(MongoModelValue<M> model) {
        super(model);
    }

    /**
     * Updates a single document in the collection that matches the specified filter with the given update.
     *
     * @param filter The filter to apply when selecting the document to update.
     * @param update The update to apply to the selected document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull Bson filter, @NotNull Bson update) {
        return this.collection().updateOne(filter, update);
    }

    /**
     * Updates multiple documents in the collection that match the specified filter with the given update.
     *
     * @param filter The filter to apply when selecting the documents to update.
     * @param update The update to apply to the selected documents.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult many(@NotNull Bson filter, @NotNull Bson update) {
        return this.collection().updateMany(filter, update);
    }

    /**
     * Updates a single document in the collection that matches the specified filter with the values from the given datum.
     *
     * @param filter The filter to apply when selecting the document to update.
     * @param datum  The object containing the new values to set in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull Bson filter, @NotNull M datum) {
        val updates = new ArrayList<Bson>();

        this.fields().forEach((key, field) -> updates.add(Updates.set(key, field.get(datum))));

        return this.one(filter, Updates.combine(updates));
    }

    /**
     * Updates a single document in the collection that matches the specified filter with the values from the given datum,
     * only updating the specified fields.
     *
     * @param filter     The filter to apply when selecting the document to update.
     * @param datum      The object containing the new values to set in the document.
     * @param justFields The fields to update in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull Bson filter, @NotNull M datum, @NotNull String @NotNull ... justFields) {
        val updates = new ArrayList<Bson>();

        val fields = this.fields();

        for (@NotNull String justField : justFields) {
            val field = fields.get(justField);
            updates.add(Updates.set(justField, field.get(datum)));
        }

        return this.one(filter, Updates.combine(updates));
    }

    /**
     * Updates a single document in the collection where the specified field matches the given value with the values from the given datum.
     *
     * @param fieldName The name of the field to filter on.
     * @param value     The value to match in the field.
     * @param datum     The object containing the new values to set in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull String fieldName, @Nullable Object value, @NotNull M datum) {
        val updates = new ArrayList<Bson>();

        this.fields().forEach((key, field) -> updates.add(Updates.set(key, field.get(datum))));

        return this.one(Filters.eq(fieldName, value), Updates.combine(updates));
    }

    /**
     * Updates a single document in the collection where the specified field matches the given value with the values from the given datum,
     * only updating the specified fields.
     *
     * @param fieldName  The name of the field to filter on.
     * @param value      The value to match in the field.
     * @param datum      The object containing the new values to set in the document.
     * @param justFields The fields to update in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull String fieldName, @Nullable Object value, @NotNull M datum, @NotNull String @NotNull ... justFields) {
        return this.one(Filters.eq(fieldName, value), datum, justFields);
    }

    /**
     * Updates a single document in the collection where the value of the unique field matches the given unique value with the values from the given datum.
     *
     * @param uniqueValue The value to match in the unique field.
     * @param datum       The object containing the new values to set in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@Nullable Object uniqueValue, @NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();

        return this.one(fieldName, uniqueValue, datum);
    }

    /**
     * Updates a single document in the collection where the value of the unique field matches the given unique value with the values from the given datum,
     * only updating the specified fields.
     *
     * @param uniqueValue The value to match in the unique field.
     * @param datum       The object containing the new values to set in the document.
     * @param justFields  The fields to update in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@Nullable Object uniqueValue, @NotNull M datum, @NotNull String @NotNull ... justFields) {
        val fieldName = this.firstUniqueFieldName();

        return this.one(fieldName, uniqueValue, datum, justFields);
    }

    /**
     * Updates a single document in the collection where the value of the unique field matches the value obtained from the given datum with the values from the given datum.
     *
     * @param datum The object containing the new values to set in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(datum, fieldName);

        return this.one(fieldName, uniqueValue, datum);
    }

    /**
     * Updates a single document in the collection where the value of the unique field matches the value obtained from the given datum with the values from the given datum,
     * only updating the specified fields.
     *
     * @param datum      The object containing the new values to set in the document.
     * @param justFields The fields to update in the document.
     * @return The result of the update operation.
     */
    @Override
    public @NotNull UpdateResult one(@NotNull M datum, @NotNull String @NotNull ... justFields) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(datum, fieldName);

        return this.one(fieldName, uniqueValue, datum, justFields);
    }
}
