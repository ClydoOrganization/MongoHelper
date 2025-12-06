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

import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record ModelMetaRegistry(
        @NotNull Map<Class<?>, ModelMeta<?>> models
) {

    public ModelMetaRegistry() {
        this(new HashMap<>());
    }

    public void register(
            @NotNull final Class<?> clazz,
            @NotNull final ModelMeta<?> modelMeta
    ) {
        if (this.models.containsKey(clazz)) {
            throw new IllegalStateException("The mongo-model " + clazz.getName() + " is already registered!");
        }

        this.models.put(
                clazz,
                modelMeta
        );
    }

    @NotNull
    public <M> ModelMeta<M> get(
            @NotNull final Class<M> clazz
    ) {
        return Validates.requireMsg(
                this.getNullable(clazz),
                "No @OrmModel mapping found for class '" + clazz
        );
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <M> ModelMeta<M> getNullable(
            @NotNull final Class<M> clazz
    ) {
        return (ModelMeta<M>) this.models.get(clazz);
    }

}
