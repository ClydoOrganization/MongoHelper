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

package net.clydo.mongodb.operations.upsert;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides operations for upserting (inserting or updating) documents in a MongoDB collection.
 * This class extends {@link AbstractOperation} and implements {@link IUpsertOperations}.
 *
 * @param <M> The type of the model for which upsert operations are performed.
 */
public class UpsertOperations<M> extends AbstractOperation<M> implements IUpsertOperations<M> {

    /**
     * Constructs a new {@link UpsertOperations} instance.
     *
     * @param model The {@link MongoModelValue} instance associated with the upsert operations.
     */
    public UpsertOperations(MongoModelValue<M> model) {
        super(model);
    }

    /**
     * Upserts a single document in the collection that matches the specified filter.
     * If the document exists, it is updated with the specified update. If not, it is created with the specified create values.
     *
     * @param filter The filter to apply when selecting the document to upsert.
     * @param update The update to apply to the selected document.
     * @param create The fields and values to set if a new document is created.
     * @return The result of the upsert operation.
     */
    @Override
    public @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull Bson create) {
        val combined = Updates.combine(
                update,
                create
        );

        return this.collection().updateOne(
                filter,
                combined,
                new UpdateOptions().upsert(true)
        );
    }

    /**
     * Replaces a single document that matches the specified filter with the given datum.
     * If the document does not exist, it is created with the provided ReplaceOptions.
     *
     * @param filter         The filter to apply when selecting the document to replace.
     * @param datum          The document to replace with.
     * @param replaceOptions Options for the replace operation.
     * @return The result of the replace operation.
     */
    @Override
    public @NotNull UpdateResult _replaceRaw(@NotNull Bson filter, @NotNull M datum, ReplaceOptions replaceOptions) {
        return this.collection().replaceOne(filter, datum, replaceOptions);
    }

    /**
     * Upserts a single document in the collection that matches the specified filter with the values from the given datum.
     * If the document does not exist, it is created with the provided datum.
     *
     * @param filter The filter to apply when selecting the document to upsert.
     * @param datum  The document to use for the upsert.
     * @return The result of the upsert operation.
     */
    @Override
    public @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull M datum) {
        return this._replaceRaw(filter, datum, new ReplaceOptions().upsert(true));
    }

    /**
     * Upserts a single document in the collection that matches the specified filter.
     * If the document does not exist, it is created with the values from the given datum and fields.
     *
     * @param filter The filter to apply when selecting the document to upsert.
     * @param update The update to apply to the selected document.
     * @param create The fields and values to set if a new document is created.
     * @return The result of the upsert operation.
     */
    @Override
    public @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull M create) {
        val creates = new ArrayList<Bson>();
        this.fields().forEach((key, field) -> {
            creates.add(Updates.setOnInsert(key, field.get(create)));
        });

        return this.raw(filter, update, Updates.combine(creates));
    }

    /**
     * Upserts a single document in the collection that matches the specified filter.
     * If the document does not exist, it is created with the values from the given datum and specified fields.
     *
     * @param filter     The filter to apply when selecting the document to upsert.
     * @param update     The update to apply to the selected document.
     * @param create     The fields and values to set if a new document is created.
     * @param justFields The fields to include in the create operation.
     * @return The result of the upsert operation.
     */
    @Override
    public @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull M create, @NotNull String @NotNull ... justFields) {
        val creates = new ArrayList<Bson>();

        val fields = this.fields();

        for (@NotNull String justField : justFields) {
            val field = fields.get(justField);
            if (field == null) {
                throw new IllegalArgumentException("Field '" + justField + "' not found");
            }
            creates.add(Updates.setOnInsert(justField, field.get(create)));
        }

        return this.raw(filter, update, Updates.combine(creates));
    }

    /**
     * Upserts a single document in the collection using the values from the given datum.
     * If the document does not exist, it is created with the values from the datum and specified fields.
     *
     * @param datum      The document to use for the upsert.
     * @param justFields The fields to include in the create operation.
     * @return The result of the upsert operation.
     */
    @Override
    public @NotNull UpdateResult raw(@NotNull M datum, @NotNull String @NotNull ... justFields) {
        val justFieldsList = List.of(justFields);

        val fields = this.fields();

        val creates = new ArrayList<Bson>();
        {
            fields.forEach((key, field) -> {
                if (!justFieldsList.contains(key)) {
                    creates.add(Updates.setOnInsert(key, field.get(datum)));
                }
            });
        }

        val updates = new ArrayList<Bson>();
        {
            for (@NotNull String justField : justFieldsList) {
                val field = fields.get(justField);
                if (field == null) {
                    throw new IllegalArgumentException("Field '" + justField + "' not found");
                }
                updates.add(Updates.set(justField, field.get(datum)));
            }
        }

        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this.raw(Filters.eq(fieldName, uniqueValue), Updates.combine(updates), Updates.combine(creates));
    }

    /**
     * Upserts a single document in the collection using the values from the given datum.
     * The document is replaced if it exists, or inserted if it does not.
     *
     * @param datum The document to use for the upsert.
     * @return The result of the upsert operation.
     */
    @Override
    public @NotNull UpdateResult raw(@NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this._replaceRaw(Filters.eq(fieldName, uniqueValue), datum, new ReplaceOptions().upsert(true));
    }
}