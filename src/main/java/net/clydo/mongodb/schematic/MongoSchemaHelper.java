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

package net.clydo.mongodb.schematic;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.val;
import net.clydo.mongodb.MongoHelper;
import net.clydo.mongodb.codec.CodecsHelper;
import net.clydo.mongodb.codec.type.ClassCodecProvider;
import net.clydo.mongodb.loader.LoaderRegistry;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MongoSchemaHelper {
    private final Set<String> schemas;
    private final MongoHelper mongoHelper;
    private final LoaderRegistry registry;

    public MongoSchemaHelper(MongoHelper mongoHelper) {
        this.schemas = new HashSet<>();

        this.mongoHelper = mongoHelper;
        this.registry = new LoaderRegistry();
    }

    private @NotNull MongoSchemaHolder addSchema(@NotNull MongoSchemaHolder schemaHolder) {
        val name = schemaHolder.name();

        if (this.schemas.contains(name)) {
            throw new IllegalStateException(name + " is already registered");
        }

        this.schemas.add(name);
        return schemaHolder;
    }

    public void newSchema(
            final String schemaName,
            final Class<?>... models
    ) {
        this.newSchema(schemaName, null, models);
    }

    public void newSchema(
            final String schemaName,
            final CodecRegistry codecRegistry,
            final Class<?> @NotNull ... models
    ) {
        val schemaHolder = this.addSchema(new MongoSchemaHolder(
                this.mongoHelper.getMongoClient().getDatabase(schemaName)
                        .withCodecRegistry(
                                CodecRegistries.fromRegistries(
                                        this.createCodecRegistry(codecRegistry)
                                )
                        ),
                this.registry
        ));

        for (Class<?> clazz : models) {
            val holder = this.registry.buildModel(clazz, schemaHolder);
            this.createIndexes(holder);
        }
    }

    private void createIndexes(@NotNull MongoModelValue<?> modelHolder) {
        val collection = modelHolder.collection();
        for (String unique : modelHolder.uniques()) {
            collection.createIndex(Indexes.ascending(unique), new IndexOptions().name(modelHolder.modelName() + "_" + unique + "_key").unique(true));
        }
    }

    private @NotNull ArrayList<CodecRegistry> createCodecRegistry(CodecRegistry codecRegistry) {
        val codecRegistries = new ArrayList<>(Arrays.asList(
                CodecRegistries.fromProviders(
                        new ClassCodecProvider(this, this.registry)
                ),
                CodecsHelper.getDefaultCodecRegistry(),
                MongoClientSettings.getDefaultCodecRegistry()
        ));
        if (codecRegistry != null) {
            codecRegistries.add(codecRegistry);
        }
        return codecRegistries;
    }

    public @NotNull <M> MongoModelValue<M> getModel(Class<M> clazz) {
        return this.registry.getModel(clazz);
    }

    public @Nullable <M> MongoModelValue<M> getModelNullable(Class<M> clazz) {
        try {
            return this.registry.getModel(clazz);
        } catch (Exception e) {
            return null;
        }
    }

}
