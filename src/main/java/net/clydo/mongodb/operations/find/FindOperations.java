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

package net.clydo.mongodb.operations.find;

import com.mongodb.client.FindIterable;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FindOperations<M> extends AbstractOperation<M> implements FindFirstOperations<M>, FindUniqueOperations<M>, FindManyOperations<M> {
    public FindOperations(MongoModelValue<M> model) {
        super(model);
    }

    @Override
    public @Nullable M first(@NotNull Bson filter) {
        return this.collection().find(filter).first();
    }

    @Override
    public @Nullable M unique(@NotNull Bson filter) {
        this.validateFilterUniques(filter, this.uniques());
        return this.first(filter);
    }

    @Override
    public @NotNull FindIterable<M> many(@NotNull Bson filter) {
        return this.collection().find(filter);
    }
}
