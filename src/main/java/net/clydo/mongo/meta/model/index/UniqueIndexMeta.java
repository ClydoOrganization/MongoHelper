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

package net.clydo.mongo.meta.model.index;

import lombok.Getter;
import net.clydo.mongo.util.SortOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Getter
public class UniqueIndexMeta extends BaseIndexMeta {

    private final String name;

    public UniqueIndexMeta(
            @NotNull final String collectionName,
            @NotNull final SortOrder order,
            @NotNull final Set<String> fieldNames,
            @Nullable final String indexName,
            @Nullable final String name
    ) {
        super(collectionName, order, fieldNames, indexName, "key", options -> options.unique(true));
        this.name = UniqueIndexMeta.getName(fieldNames, name);
    }

    public UniqueIndexMeta(
            @NotNull final String collectionName,
            @NotNull final SortOrder order,
            @NotNull final String fieldName,
            @Nullable final String name
    ) {
        this(collectionName, order, Set.of(fieldName), name, null);
    }

    @NotNull
    private static String getName(
            @NotNull final Set<String> fieldNames,
            @Nullable final String name
    ) {
        if (name != null && !name.isBlank()) {
            return name;
        }

        return String.join("_", fieldNames);
    }

}
