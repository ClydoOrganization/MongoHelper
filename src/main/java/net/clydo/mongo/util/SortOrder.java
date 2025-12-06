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

package net.clydo.mongo.util;

import com.mongodb.client.model.Indexes;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum SortOrder {

    ASCENDING,
    DESCENDING;

    public Bson createIndexKeys(
            @NotNull final Set<String> fieldNames
    ) {
        return switch (this) {
            case ASCENDING -> Indexes.ascending(fieldNames.toArray(new String[0]));
            case DESCENDING -> Indexes.descending(fieldNames.toArray(new String[0]));
        };
    }

}
