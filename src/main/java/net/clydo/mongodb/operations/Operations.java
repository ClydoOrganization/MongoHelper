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

package net.clydo.mongodb.operations;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.util.MongoUtil;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class Operations<M> extends AbstractOperations<M> {
    private final MongoModelValue<M> model;

    public Operations(
            MongoModelValue<M> model
    ) {
        this.model = model;
    }

    @Override
    public @Nullable M findFirstRaw(@NotNull Bson filter) {
        return this.collection().find(filter).first();
    }

    @Override
    public @Nullable M findUniqueRaw(@NotNull Bson filter) {
        this.validateFilterUniques(filter, this.uniques());

        return this.collection().find(filter).first();
    }

    @Override
    public @NotNull FindIterable<M> findManyRaw(@NotNull Bson filter) {
        return this.collection().find(filter);
    }

    @Override
    public @NotNull InsertOneResult createRaw(@NotNull M datum) {
        return this.collection().insertOne(datum);
    }

    @SafeVarargs
    @Override
    public final @NotNull InsertManyResult createManyRaw(@NotNull M... data) {
        return this.collection().insertMany(List.of(data));
    }

    @Override
    public @NotNull UpdateResult updateRaw(@NotNull Bson filter, @NotNull Bson update) {
        return this.collection().updateOne(filter, update);
    }

    @Override
    public @NotNull UpdateResult updateManyRaw(@NotNull Bson filter, @NotNull Bson update) {
        return this.collection().updateMany(filter, update);
    }

    @Override
    public @NotNull UpdateResult upsertRaw(@NotNull Bson filter, @NotNull Bson update, @NotNull Bson create) {
        return this.collection().updateOne(
                filter,
                Updates.combine(
                        Updates.setOnInsert(create)
                ),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public @NotNull UpdateResult upsertRaw(@NotNull Bson filter, @NotNull Bson update, @NotNull M create) {
        val createDocument = MongoUtil.toBsonDocument(this.collection().getCodecRegistry(), this.type(), create);

        return this.upsertRaw(filter, update, createDocument);
    }

    @Override
    public @NotNull DeleteResult deleteRaw(@NotNull Bson filter) {
        return this.collection().deleteOne(filter);
    }

    @Override
    public @NotNull DeleteResult deleteManyRaw(@NotNull Bson filter) {
        return this.collection().deleteMany(filter);
    }

    @Override
    public long count(@NotNull Bson filter) {
        return this.collection().countDocuments(filter);
    }

    @Override
    public @NotNull MongoModelValue<M> model() {
        return this.model;
    }
}
