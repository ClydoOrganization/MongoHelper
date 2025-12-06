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
 * Copyright (C) 2025 ClydoNetwork
 */

package net.clydo.mongo.registry;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.mongo.OrmSchematic;
import net.clydo.mongo.codec.EnumMetaCodecProvider;
import net.clydo.mongo.codec.TypeMetaCodecProvider;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.meta.model.index.IndexMeta;
import net.clydo.mongo.operations.OperationsGroup;
import net.clydo.mongo.visitor.builder.EnumMetaBuilder;
import net.clydo.mongo.visitor.builder.ModelMetaBuilder;
import net.clydo.mongo.visitor.builder.TypeMetaBuilder;
import net.clydo.mongo.walker.EnumWalker;
import net.clydo.mongo.walker.ModelWalker;
import net.clydo.mongo.walker.TypeWalker;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SchematicRegistry {

    private final MongoClient client;
    private final Set<OrmSchematic> schematics;
    private final Map<Class<?>, OperationsGroup<?>> operationsGroups;

    @Getter
    private final ModelMetaRegistry modelRegistry;
    @Getter
    private final TypeMetaRegistry typeRegistry;
    @Getter
    private final EnumMetaRegistry enumRegistry;

    public SchematicRegistry(
            @NotNull final MongoClient client
    ) {
        this.client = client;

        this.schematics = new HashSet<>();
        this.operationsGroups = new HashMap<>();

        this.modelRegistry = new ModelMetaRegistry();
        this.typeRegistry = new TypeMetaRegistry();
        this.enumRegistry = new EnumMetaRegistry();
    }

    public void register(
            @NotNull final OrmSchematic schematic
    ) {
        Validates.require(schematic, "schematic");

        if (this.schematics.contains(schematic)) {
            throw new IllegalStateException("The schematic " + schematic.getClass().getName() + " is already registered!");
        }

        this.buildCache(schematic);
        this.schematics.add(schematic);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public <M> OperationsGroup<M> get(
            @NotNull final Class<M> clazz
    ) {
        return (OperationsGroup<M>) Validates.requireLazy(
                this.operationsGroups.get(clazz),
                () -> "No operations group found for " + clazz.getName()
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildCache(
            @NotNull final OrmSchematic schematic
    ) {
        val modelMetaBuilder = new ModelMetaBuilder();
        val typeMetaBuilder = new TypeMetaBuilder();
        val enumMetaBuilder = new EnumMetaBuilder();
        val modelWalker = new ModelWalker(modelMetaBuilder);
        val typeWalker = new TypeWalker(typeMetaBuilder);
        val enumWalker = new EnumWalker(enumMetaBuilder);

        for (val modelClass : schematic.getModelClasses()) {
            modelWalker.walk(modelClass);
            val modelMeta = modelMetaBuilder.build();

            this.modelRegistry.register(
                    modelClass,
                    modelMeta
            );

            val database = this.client.getDatabase(schematic.getDatabaseName())
                    .withCodecRegistry(
                            CodecRegistries.fromRegistries(
                                    schematic.handleCodecRegistry(
                                            new ArrayList<>(List.of(
                                                    CodecRegistries.fromProviders(
                                                            new EnumMetaCodecProvider(this.getEnumRegistry()),
                                                            new TypeMetaCodecProvider(this.getModelRegistry(), this.getTypeRegistry())
                                                    ),
                                                    MongoClientSettings.getDefaultCodecRegistry()
                                            ))
                                    )
                            )
                    );

            this.createIndexes(database, modelMeta);

            val collectionName = modelMeta.collectionName();
            val collection = database.getCollection(collectionName, modelClass);

            this.operationsGroups.put(
                    modelClass,
                    new OperationsGroup(
                            collection,
                            modelMeta
                    )
            );
        }

        for (val typeClass : schematic.getTypeClasses()) {
            typeWalker.walk(typeClass);
            val typeMeta = typeMetaBuilder.build();

            this.typeRegistry.register(typeClass, typeMeta);
        }

        for (val enumClass : schematic.getEnumClasses()) {
            enumWalker.walk(enumClass);
            val enumMeta = enumMetaBuilder.build();

            this.enumRegistry.register(enumClass, enumMeta);
        }
    }

    private void createIndexes(
            @NotNull final MongoDatabase database,
            @NotNull final ModelMeta<?> modelMeta
    ) {
        val collectionName = modelMeta.collectionName();
        val collection = database.getCollection(collectionName);
        val indexMetaMap = modelMeta.indexMetaMap();

        val indexModels = indexMetaMap.indices()
                .stream()
                .map(IndexMeta::getIndexModel)
                .filter(Objects::nonNull)
                .toList();

        if (!indexModels.isEmpty()) {
            collection.createIndexes(indexModels);
        }
    }

}
