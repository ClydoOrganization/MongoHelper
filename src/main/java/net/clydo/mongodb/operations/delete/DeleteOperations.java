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

import com.mongodb.client.result.DeleteResult;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.AbstractOperation;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

public class DeleteOperations<M> extends AbstractOperation<M> implements DeleteOneOperations<M>, DeleteManyOperations<M> {
    public DeleteOperations(MongoModelValue<M> model) {
        super(model);
    }

    @Override
    public @NotNull DeleteResult one(@NotNull Bson filter) {
        return this.collection().deleteOne(filter);
    }

    @Override
    public @NotNull DeleteResult many(@NotNull Bson filter) {
        return this.collection().deleteMany(filter);
    }
}
