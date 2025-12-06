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

package net.clydo.mongo;

import com.mongodb.client.MongoClient;
import lombok.val;
import net.clydo.clytil.reflect.Constructors;
import net.clydo.mongo.operations.OperationsGroup;
import net.clydo.mongo.registry.SchematicRegistry;
import org.jetbrains.annotations.NotNull;

public record MongoHelperImpl(
        @NotNull SchematicRegistry schematicRegistry
) implements MongoHelper {

    public MongoHelperImpl(
            @NotNull final MongoClient schematicRegistry
    ) {
        this(new SchematicRegistry(schematicRegistry));
    }

    @Override
    public <S extends OrmSchematic> void register(
            @NotNull final Class<S> clazz
    ) {
        val constructor = Constructors.of(clazz);
        val schematic = constructor.newInstance();
        this.schematicRegistry.register(schematic);
    }

    @NotNull
    @Override
    public <M> OperationsGroup<M> get(
            @NotNull final Class<M> clazz
    ) {
        return this.schematicRegistry.get(clazz);
    }

}
