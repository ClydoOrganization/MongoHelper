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

package net.clydo.mongodb.loader.classes.values;

import net.clydo.mongodb.loader.CacheValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public record MongoTypeValue<T>(
        Class<T> type,
        HashMap<String, MongoMutableField> fields
) implements CacheValue, ClassCacheValue {

    @Contract("_, _ -> new")
    public static <T> @NotNull MongoTypeValue<T> of(
            Class<T> type,
            HashMap<String, MongoMutableField> fields
    ) {
        return new MongoTypeValue<>(type, fields);
    }
}
