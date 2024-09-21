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
 * Copyright (C) 2024 ClydoNetwork
 */

package net.clydo.mongodb.operations.update;

import com.mongodb.client.result.UpdateResult;
import net.clydo.mongodb.operations.IOperations;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UpdateOneOperations<M> extends IOperations<M> {
    @NotNull UpdateResult one(@NotNull Bson filter, @NotNull Bson update);

    @NotNull UpdateResult one(@NotNull Bson filter, @NotNull M datum);

    @NotNull UpdateResult one(@NotNull Bson filter, @NotNull M datum, @NotNull String @NotNull ... justFields);

    @NotNull UpdateResult one(@NotNull String fieldName, @Nullable Object value, @NotNull M datum);

    @NotNull UpdateResult one(@NotNull String fieldName, @Nullable Object value, @NotNull M datum, @NotNull String @NotNull ... justFields);

    @NotNull UpdateResult one(@Nullable Object uniqueValue, @NotNull M datum);

    @NotNull UpdateResult one(@Nullable Object uniqueValue, @NotNull M datum, @NotNull String @NotNull ... justFields);

    @NotNull UpdateResult one(@NotNull M datum);

    @NotNull UpdateResult one(@NotNull M datum, @NotNull String @NotNull ... justFields);
}
