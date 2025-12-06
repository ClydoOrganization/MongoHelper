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
import net.clydo.mongo.meta.type.TypeMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record TypeMetaRegistry(
        @NotNull Map<Class<?>, TypeMeta<?>> types
) {

    public TypeMetaRegistry() {
        this(new HashMap<>());
    }

    public void register(
            @NotNull final Class<?> clazz,
            @NotNull final TypeMeta<?> typeMeta
    ) {
        if (this.types.containsKey(clazz)) {
            throw new IllegalStateException("The mongo-type " + clazz.getName() + " is already registered!");
        }

        this.types.put(
                clazz,
                typeMeta
        );
    }

    @NotNull
    public <T> TypeMeta<T> get(
            @NotNull final Class<T> clazz
    ) {
        return Validates.requireMsg(
                this.getNullable(clazz),
                "No @OrmType mapping found for class '" + clazz
        );
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> TypeMeta<T> getNullable(
            @NotNull final Class<T> clazz
    ) {
        return (TypeMeta<T>) this.types.get(clazz);
    }

}
