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

package net.clydo.mongo.meta.enm;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record EnumMetaImpl<E extends Enum<E>>(
        @NotNull Map<E, String> enumToKey,
        @NotNull Map<String, E> keyToEnum
) implements EnumMeta<E> {

    @Override
    @NotNull
    public String encode(
            @NotNull final E value
    ) {
        val key = this.enumToKey.get(value);
        if (key == null) {
            throw new IllegalStateException("No mapping for " + value);
        }

        return key;
    }

    @Override
    @NotNull
    public E decode(
            @NotNull final String key
    ) {
        val value = this.keyToEnum.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Unknown key '" + key + "'");
        }

        return value;
    }

}
