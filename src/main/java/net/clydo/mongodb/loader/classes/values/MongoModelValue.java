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

package net.clydo.mongodb.loader.classes.values;

import com.mongodb.client.MongoCollection;
import net.clydo.mongodb.loader.CacheValue;
import net.clydo.mongodb.operations.count.CountOperations;
import net.clydo.mongodb.operations.create.CreateOperations;
import net.clydo.mongodb.operations.delete.DeleteOperations;
import net.clydo.mongodb.operations.find.FindOperations;
import net.clydo.mongodb.operations.update.UpdateOperations;
import net.clydo.mongodb.operations.upsert.UpsertOperations;
import net.clydo.mongodb.schematic.MongoSchemaHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a model in MongoDB with associated operations and metadata.
 * This class provides operations for interacting with MongoDB collections and managing schemas.
 *
 * @param <M> The type of the model.
 */
public final class MongoModelValue<M> implements CacheValue, ClassCacheValue<M> {
    private final Class<M> type;
    private final String modelName;
    private final MongoCollection<M> collection;
    private final List<String> uniques;
    private final HashMap<String, MongoMutableField> fields;
    private final MongoSchemaHolder parent;

    private final CountOperations<M> countOperations;
    private final CreateOperations<M> createOperations;
    private final DeleteOperations<M> deleteOperations;
    private final FindOperations<M> findOperations;
    private final UpdateOperations<M> updateOperations;
    private final UpsertOperations<M> upsertOperations;

    /**
     * Constructs a new {@link MongoModelValue} instance.
     *
     * @param type       The class type of the model.
     * @param modelName  The name of the model.
     * @param collection The MongoDB collection associated with this model.
     * @param uniques    The list of unique field names for this model.
     * @param fields     A map of field names to {@link MongoMutableField} instances.
     * @param parent     The parent {@link MongoSchemaHolder} for this model.
     */
    public MongoModelValue(
            Class<M> type,
            String modelName,
            MongoCollection<M> collection,
            List<String> uniques,
            HashMap<String, MongoMutableField> fields,
            MongoSchemaHolder parent
    ) {
        this.type = type;
        this.modelName = modelName;
        this.collection = collection;
        this.uniques = uniques;
        this.fields = fields;
        this.parent = parent;

        this.countOperations = new CountOperations<>(this);
        this.createOperations = new CreateOperations<>(this);
        this.deleteOperations = new DeleteOperations<>(this);
        this.findOperations = new FindOperations<>(this);
        this.updateOperations = new UpdateOperations<>(this);
        this.upsertOperations = new UpsertOperations<>(this);
    }

    /**
     * Creates a new {@link MongoModelValue} instance with the specified parameters.
     *
     * @param type      The class type of the model.
     * @param fields    A map of field names to {@link MongoMutableField} instances.
     * @param modelName The name of the model.
     * @param parent    The parent {@link MongoSchemaHolder} for this model.
     * @param <M>       The type of the model.
     * @return A new {@link MongoModelValue} instance.
     */
    @Contract("_, _, _, _ -> new")
    public static <M> @NotNull MongoModelValue<M> of(
            final Class<M> type,
            final HashMap<String, MongoMutableField> fields,
            final String modelName,
            final @NotNull MongoSchemaHolder parent
    ) {
        return new MongoModelValue<>(
                type,
                modelName,
                parent.database().getCollection(modelName, type),
                fields.entrySet().stream()
                        .filter(entry -> entry.getValue().unique())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()),
                fields,
                parent
        );
    }

    /**
     * Provides access to count operations for this model.
     *
     * @return The {@link CountOperations} instance for this model.
     */
    public CountOperations<M> count() {
        return this.countOperations;
    }

    /**
     * Provides access to create operations for this model.
     *
     * @return The {@link CreateOperations} instance for this model.
     */
    public CreateOperations<M> create() {
        return this.createOperations;
    }

    /**
     * Provides access to delete operations for this model.
     *
     * @return The {@link DeleteOperations} instance for this model.
     */
    public DeleteOperations<M> delete() {
        return this.deleteOperations;
    }

    /**
     * Provides access to find operations for this model.
     *
     * @return The {@link FindOperations} instance for this model.
     */
    public FindOperations<M> find() {
        return this.findOperations;
    }

    /**
     * Provides access to update operations for this model.
     *
     * @return The {@link UpdateOperations} instance for this model.
     */
    public UpdateOperations<M> update() {
        return this.updateOperations;
    }

    /**
     * Provides access to upsert operations for this model.
     *
     * @return The {@link UpsertOperations} instance for this model.
     */
    public UpsertOperations<M> upsert() {
        return this.upsertOperations;
    }

    /**
     * Returns the class type of the model.
     *
     * @return The class type of the model.
     */
    public Class<M> type() {
        return this.type;
    }

    /**
     * Returns the name of the model.
     *
     * @return The name of the model.
     */
    public String modelName() {
        return this.modelName;
    }

    /**
     * Returns the MongoDB collection associated with this model.
     *
     * @return The MongoDB collection for this model.
     */
    public MongoCollection<M> collection() {
        return this.collection;
    }

    /**
     * Returns the list of unique field names for this model.
     *
     * @return The list of unique field names.
     */
    public List<String> uniques() {
        return this.uniques;
    }

    /**
     * Returns a map of field names to {@link MongoMutableField} instances for this model.
     *
     * @return The map of field names to {@link MongoMutableField} instances.
     */
    @Override
    public HashMap<String, MongoMutableField> fields() {
        return this.fields;
    }

    /**
     * Returns the parent {@link MongoSchemaHolder} for this model.
     *
     * @return The parent {@link MongoSchemaHolder}.
     */
    public MongoSchemaHolder parent() {
        return this.parent;
    }
}