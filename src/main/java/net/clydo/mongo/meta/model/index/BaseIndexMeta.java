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

package net.clydo.mongo.meta.model.index;

import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.Getter;
import net.clydo.mongo.util.SortOrder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

@Getter
public class BaseIndexMeta implements IndexMeta {

    private final String indexName;
    private final Set<String> fieldsName;
    private final IndexModel indexModel;

    protected BaseIndexMeta(
            @NotNull final String collectionName,
            @NotNull final SortOrder order,
            @NotNull final Set<String> fieldsName,
            @Nullable final String indexName,
            @NotNull final String suffix,
            @NotNull final Function<IndexOptions, IndexOptions> indexOptions
    ) {
        this.indexName = BaseIndexMeta.getIndexName(collectionName, fieldsName, indexName, suffix);
        this.indexModel = new IndexModel(
                order.createIndexKeys(fieldsName),
                indexOptions.apply(
                        new IndexOptions()
                                .name(this.indexName)
                )
        );
        this.fieldsName = fieldsName;
    }

    @NotNull
    private static String getIndexName(
            @NotNull final String collectionName,
            @NotNull final Set<String> fieldNames,
            @Nullable final String indexName,
            @NotNull final String suffix
    ) {
        if (indexName != null && !indexName.isBlank()) {
            return indexName;
        }

        return collectionName + "_" + String.join("_", fieldNames) + "_" + suffix;
    }

}
