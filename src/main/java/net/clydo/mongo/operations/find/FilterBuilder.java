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

package net.clydo.mongo.operations.find;

import com.mongodb.client.model.Filters;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.clydo.clytil.Primitives;
import net.clydo.clytil.Validates;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.meta.model.index.IndexMetaMap;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.IntStream;

@UtilityClass
public class FilterBuilder {

    public <M> Bson buildFilterByIndex(
            @NotNull final ModelMeta<M> model,
            @NotNull final IndexMetaMap indices,
            @NotNull final String indexName,
            @Nullable final Object... values
    ) {
        val indexMeta = indices.getUnique(indexName);
        val fieldNames = new ArrayList<>(indexMeta.getFieldsName());
        val modelFields = model.fieldMetaMap();

        if (values == null || values.length != fieldNames.size()) {
            throw new IllegalArgumentException(String.format(
                    "Expected %d values for index '%s', but got %d",
                    fieldNames.size(),
                    indexName,
                    values != null
                            ? values.length
                            : 0
            ));
        }

        val filters = IntStream.range(0, fieldNames.size())
                .mapToObj(index -> {
                    val fieldName = fieldNames.get(index);
                    val mongoField = modelFields.get(fieldName);

                    val value = values[index];
                    Validates.requireMsg(value, "Value for field '" + fieldName + "' is null");

                    val expectedType = Primitives.wrap(mongoField.getType());
                    val actualType = Primitives.wrap(value.getClass());

                    if (!expectedType.isAssignableFrom(actualType)) {
                        throw new IllegalArgumentException(String.format(
                                "Type mismatch for field '%s': expected %s but got %s",
                                fieldName, expectedType.getSimpleName(), actualType.getSimpleName()
                        ));
                    }

                    return Filters.eq(fieldName, value);
                })
                .toList();

        return Filters.and(filters);
    }

    public <M> Bson buildFilterByIndex(
            @NotNull final ModelMeta<M> model,
            @NotNull final IndexMetaMap indices,
            @NotNull final String indexName,
            @Nullable final Object datum
    ) {
        val fieldNames = indices.getUnique(indexName).getFieldsName();
        val fieldMetaMap = model.fieldMetaMap();
        val values = fieldNames.stream()
                .map(name ->
                        fieldMetaMap
                                .get(name)
                                .get(datum)
                )
                .toArray();

        return buildFilterByIndex(model, indices, indexName, values);
    }

}
