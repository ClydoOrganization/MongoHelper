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
 */

package net.clydo.mongodb.operations.delete;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.val;
import net.clydo.mongodb.operations.IOperations;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

public interface DeleteManyOperations<M> extends IOperations<M> {
    @NotNull DeleteResult many(@NotNull Bson filter);

    default @NotNull DeleteResult many() {
        return this.many(new BsonDocument());
    }

    default @NotNull DeleteResult many(@NotNull String fieldName, @NotNull Object... uniqueValues) {
        return this.many(Filters.in(fieldName, uniqueValues));
    }

    default @NotNull DeleteResult manyByUniques(@NotNull Object... uniqueValues) {
        return this.many(this.firstUniqueFieldName(), uniqueValues);
    }

    default @NotNull DeleteResult many(@NotNull M... values) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValues = this.getFieldValues(this.fields(), values, fieldName);

        return this.many(this.firstUniqueFieldName(), uniqueValues);
    }
}
