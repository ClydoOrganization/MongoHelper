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

package net.clydo.mongodb.operations.update;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.operations.IOperations;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface UpdateOneOperations<M> extends IOperations<M> {
    @NotNull UpdateResult one(@NotNull Bson filter, @NotNull Bson update);

    default @NotNull UpdateResult one(@NotNull Bson filter, @NotNull M datum) {
        val updates = new ArrayList<Bson>();

        this.fields().forEach((key, field) -> {
            updates.add(Updates.set(key, field.get(datum)));
        });

        return this.one(filter, Updates.combine(updates));
    }

    default @NotNull UpdateResult one(@NotNull Bson filter, @NotNull M datum, @NotNull String @NotNull ... justFields) {
        val updates = new ArrayList<Bson>();

        val fields = this.fields();

        for (@NotNull String justField : justFields) {
            val field = fields.get(justField);
            updates.add(Updates.set(justField, field.get(datum)));
        }

        return this.one(filter, Updates.combine(updates));
    }

    default @NotNull UpdateResult one(@NotNull String fieldName, @Nullable Object value, @NotNull M datum) {
        val updates = new ArrayList<Bson>();

        this.fields().forEach((key, field) -> {
            updates.add(Updates.set(key, field.get(datum)));
        });

        return this.one(Filters.eq(fieldName, value), Updates.combine(updates));
    }

    default @NotNull UpdateResult one(@NotNull String fieldName, @Nullable Object value, @NotNull M datum, @NotNull String @NotNull ... justFields) {
        val updates = new ArrayList<Bson>();

        val fields = this.fields();

        for (@NotNull String justField : justFields) {
            val field = fields.get(justField);
            updates.add(Updates.set(justField, field.get(datum)));
        }

        return this.one(Filters.eq(fieldName, value), Updates.combine(updates));
    }

    default @NotNull UpdateResult one(@Nullable Object uniqueValue, @NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();

        return this.one(fieldName, uniqueValue, datum);
    }

    default @NotNull UpdateResult one(@Nullable Object uniqueValue, @NotNull M datum, @NotNull String @NotNull ... justFields) {
        val fieldName = this.firstUniqueFieldName();

        return this.one(fieldName, uniqueValue, datum, justFields);
    }

    default @NotNull UpdateResult one(@NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this.one(fieldName, uniqueValue, datum);
    }

    default @NotNull UpdateResult one(@NotNull M datum, @NotNull String @NotNull ... justFields) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this.one(fieldName, uniqueValue, datum, justFields);
    }
}
