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

import net.clydo.mongo.meta.enm.EnumMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record EnumMetaRegistry(
        @NotNull Map<Class<?>, EnumMeta<?>> enums
) {

    public EnumMetaRegistry() {
        this(new HashMap<>());
    }

    public void register(
            @NotNull final Class<?> clazz,
            @NotNull final EnumMeta<?> enumMeta
    ) {
        if (this.enums.containsKey(clazz)) {
            throw new IllegalStateException("The mongo-enum " + clazz.getName() + " is already registered!");
        }

        this.enums.put(
                clazz,
                enumMeta
        );
    }

    @Nullable
    public EnumMeta<?> getNullable(
            @NotNull final Class<?> clazz
    ) {
        return this.enums.get(clazz);
    }

}
