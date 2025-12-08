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
import lombok.val;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.meta.model.index.IndexMetaMap;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

public class BaseOperations<M> {

    protected final MongoCollection<M> collection;
    protected final ModelMeta<M> model;
    protected final IndexMetaMap indices;

    public BaseOperations(
            @NotNull final MongoCollection<M> collection,
            @NotNull final ModelMeta<M> model
    ) {
        this.collection = collection;
        this.model = model;
        this.indices = model.indexMetaMap();
    }

    protected void validateFilterUniques(
            @NotNull final Bson filter
    ) {
        val filterString = filter.toString();
        val hasUnique = this.indices.uniques().stream().anyMatch(mongoIndex ->
                mongoIndex.getFieldsName()
                        .stream()
                        .anyMatch(unique ->
                                filterString.contains("fieldName='" + unique + "'")
                        )
        );
        if (!hasUnique) {
            throw new IllegalStateException("No unique keys found");
        }
    }

}
