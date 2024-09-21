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

package net.clydo.mongodb;

import com.mongodb.client.MongoClient;
import lombok.Getter;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.schematic.MongoSchemaHelper;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

/**
 * A helper class for managing MongoDB connections and schemas.
 * This class provides methods to create and manage schemas and models within a MongoDB client context.
 */
public class MongoHelper implements Closeable {
    @Getter
    protected MongoClient mongoClient;
    private final MongoSchemaHelper schemaHelper;

    /**
     * Constructs a new {@link MongoHelper} instance.
     *
     * @param mongoClient The MongoDB client to be used by this helper.
     */
    public MongoHelper(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.schemaHelper = new MongoSchemaHelper(this);
    }

    /**
     * Creates a new schema with the specified name and models.
     * This method initializes a schema within the MongoDB context, allowing for model management.
     *
     * @param schemaName The name of the schema to create.
     * @param models     The model classes to include in the schema.
     */
    public void newSchema(
            final String schemaName,
            final Class<?>... models
    ) {
        this.schemaHelper.newSchema(schemaName, models);
    }

    /**
     * Creates a new schema with the specified name, codec registry, and models.
     * This method initializes a schema within the MongoDB context with a custom codec registry,
     * allowing for model management with specific codecs.
     *
     * @param schemaName    The name of the schema to create.
     * @param codecRegistry The codec registry to use for the schema.
     * @param models        The model classes to include in the schema.
     */
    public void newSchema(
            final String schemaName,
            final CodecRegistry codecRegistry,
            final Class<?> @NotNull ... models
    ) {
        this.schemaHelper.newSchema(schemaName, codecRegistry, models);
    }

    /**
     * Retrieves the model associated with the specified class.
     * This method allows access to the model definition for a given class within the schema.
     *
     * @param clazz The class of the model to retrieve.
     * @param <M>   The type of the model.
     * @return The {@link MongoModelValue} representing the model for the specified class.
     * @throws NullPointerException If no model is found for the specified class.
     */
    public <M> @NotNull MongoModelValue<M> getModel(Class<M> clazz) {
        val model = this.schemaHelper.getModel(clazz);
        if (model != null) {
            return model;
        }
        throw new NullPointerException("No model found for " + clazz);
    }

    /**
     * Closes the MongoDB client and releases any associated resources.
     * This method is part of the {@link Closeable} interface and should be called
     * when the MongoDB client is no longer needed.
     */
    @Override
    public void close() {
        this.mongoClient.close();
    }
}
