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
 *
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

@Getter
public class MongoHelper implements Closeable {
    protected MongoClient mongoClient;
    private final MongoSchemaHelper schemaHelper;

    public MongoHelper(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.schemaHelper = new MongoSchemaHelper(this);
    }

    public void newSchema(
            final String schemaName,
            final Class<?>... models
    ) {
        this.schemaHelper.newSchema(schemaName, models);
    }

    public void newSchema(
            final String schemaName,
            final CodecRegistry codecRegistry,
            final Class<?> @NotNull ... models
    ) {
        this.schemaHelper.newSchema(schemaName, codecRegistry, models);
    }

    public <M> @NotNull MongoModelValue<M> getModel(Class<M> clazz) {
        val model = this.schemaHelper.getModel(clazz);
        if (model != null) {
            return model;
        }
        throw new NullPointerException("No model found for " + clazz);
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }
}
