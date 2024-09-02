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

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

public class UpsertOperations<M> extends AbstractOperation<M> implements IUpsertOperations<M> {
    public UpsertOperations(MongoModelValue<M> model) {
        super(model);
    }

    @Override
    public @NotNull UpdateResult raw(@NotNull Bson filter, @NotNull Bson update, @NotNull Bson create) {
        val combined = Updates.combine(
                update,
                create
        );

        return this.collection().updateOne(
                filter,
                combined,
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public @NotNull UpdateResult _replaceRaw(@NotNull Bson filter, @NotNull M datum, ReplaceOptions replaceOptions) {
        return this.collection().replaceOne(filter, datum, replaceOptions);
    }
}
