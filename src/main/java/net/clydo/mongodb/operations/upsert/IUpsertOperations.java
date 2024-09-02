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

package net.clydo.mongodb.operations.upsert;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.operations.IOperations;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface IUpsertOperations<M> extends IOperations<M> {
    @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull Bson create);

    @NotNull UpdateResult _replaceRaw(@NotNull Bson filter, @NotNull M datum, ReplaceOptions options);

    default @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull M datum) {
        return this._replaceRaw(filter, datum, new ReplaceOptions().upsert(true));
    }

    default @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull M create) {
        val creates = new ArrayList<Bson>();
        this.fields().forEach((key, field) -> {
            creates.add(Updates.setOnInsert(key, field.get(create)));
        });

        return this.raw(filter, update, Updates.combine(creates));
    }

    default @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull M create, @NotNull String @NotNull ... justFields) {
        val creates = new ArrayList<Bson>();

        val fields = this.fields();

        for (@NotNull String justField : justFields) {
            val field = fields.get(justField);
            if (field == null) {
                throw new IllegalArgumentException("Field '" + justField + "' not found");
            }
            creates.add(Updates.setOnInsert(justField, field.get(create)));
        }

        return this.raw(filter, update, Updates.combine(creates));
    }

    default @NotNull UpdateResult raw(@NotNull M datum, @NotNull String @NotNull ... justFields) {
        val justFieldsList = List.of(justFields);

        val fields = this.fields();

        val creates = new ArrayList<Bson>();
        {
            fields.forEach((key, field) -> {
                if (!justFieldsList.contains(key)) {
                    creates.add(Updates.setOnInsert(key, field.get(datum)));
                }
            });
        }

        val updates = new ArrayList<Bson>();
        {
            for (@NotNull String justField : justFieldsList) {
                val field = fields.get(justField);
                if (field == null) {
                    throw new IllegalArgumentException("Field '" + justField + "' not found");
                }
                updates.add(Updates.set(justField, field.get(datum)));
            }
        }

        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this.raw(Filters.eq(fieldName, uniqueValue), Updates.combine(updates), Updates.combine(creates));
    }

    default @NotNull UpdateResult raw(@NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this._replaceRaw(Filters.eq(fieldName, uniqueValue), datum, new ReplaceOptions().upsert(true));
    }
}
