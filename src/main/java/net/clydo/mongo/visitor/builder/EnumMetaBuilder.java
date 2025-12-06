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

package net.clydo.mongo.visitor.builder;

import net.clydo.mongo.meta.enm.EnumMeta;
import net.clydo.mongo.meta.enm.EnumMetaImpl;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EnumMetaBuilder extends MetaBuilder<EnumMeta<?>> {

    private Map<Object, String> enumToKey;
    private Map<String, Object> keyToEnum;

    public void put(
            @NotNull final Class<?> clazz,
            @NotNull final String key,
            @NotNull final Object constant
    ) {
        if (this.keyToEnum.put(key, constant) != null || this.enumToKey.put(constant, key) != null) {
            throw new IllegalStateException(String.format(
                    "Duplicate enum constant mapped name '%s' found in enum class %s. " +
                            "Both constants map to the same name, which is not allowed.",
                    key, clazz.getName()
            ));
        }
    }

    @Override
    public void reset() {
        this.enumToKey = new HashMap<>();
        this.keyToEnum = new HashMap<>();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NotNull
    protected EnumMeta<?> buildInternal() {
        return new EnumMetaImpl(
                this.enumToKey,
                this.keyToEnum
        );
    }

}
