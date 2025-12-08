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

package net.clydo.mongo.operations;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.operations.count.CountOperationsImpl;
import net.clydo.mongo.operations.create.CreateOperationsImpl;
import net.clydo.mongo.operations.delete.DeleteOperationsImpl;
import net.clydo.mongo.operations.find.FindOperationsImpl;
import net.clydo.mongo.operations.update.UpdateOperationsImpl;
import net.clydo.mongo.operations.upsert.UpsertOperationsImpl;
import org.jetbrains.annotations.NotNull;

public class OperationsGroup<M> {

    @Getter
    private final MongoCollection<M> collection;

    private final CountOperationsImpl<M> countOperations;
    private final CreateOperationsImpl<M> createOperations;
    private final DeleteOperationsImpl<M> deleteOperations;
    private final FindOperationsImpl<M> findOperations;
    private final UpdateOperationsImpl<M> updateOperations;
    private final UpsertOperationsImpl<M> upsertOperations;

    public OperationsGroup(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        this.collection = collection;
        this.countOperations = new CountOperationsImpl<>(collection, model);
        this.createOperations = new CreateOperationsImpl<>(collection, model);
        this.deleteOperations = new DeleteOperationsImpl<>(collection, model);
        this.findOperations = new FindOperationsImpl<>(collection, model);
        this.updateOperations = new UpdateOperationsImpl<>(collection, model);
        this.upsertOperations = new UpsertOperationsImpl<>(collection, model);
    }

    public CountOperationsImpl<M> count() {
        return this.countOperations;
    }

    public CreateOperationsImpl<M> create() {
        return this.createOperations;
    }

    public DeleteOperationsImpl<M> delete() {
        return this.deleteOperations;
    }

    public FindOperationsImpl<M> find() {
        return this.findOperations;
    }

    public UpdateOperationsImpl<M> update() {
        return this.updateOperations;
    }

    public UpsertOperationsImpl<M> upsert() {
        return this.upsertOperations;
    }

}
