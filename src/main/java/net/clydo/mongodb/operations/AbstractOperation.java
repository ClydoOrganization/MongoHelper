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
 * Copyright (C) 2024 ClydoNetwork
 */

package net.clydo.mongodb.operations;

import com.mongodb.client.MongoCollection;
import lombok.val;
import net.clydo.mongodb.error.NotFoundResult;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.loader.classes.values.MongoMutableField;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class AbstractOperation<M> implements IOperations<M> {
    protected final MongoModelValue<M> model;

    public AbstractOperation(MongoModelValue<M> model) {
        this.model = model;
    }

    @Override
    public Object[] getFieldValues(HashMap<String, MongoMutableField> fields, @NotNull M @NotNull [] data, @NotNull String fieldName) {
        val values = new Object[data.length];

        for (int i = 0; i < data.length; i++) {
            val datum = data[i];
            val fieldValue = this.getFieldValue(fields, datum, fieldName);
            values[i] = fieldValue;
        }

        return values;
    }

    @Override
    public Object getFieldValue(@NotNull HashMap<String, MongoMutableField> fields, @NotNull M datum, @NotNull String fieldName) {
        val field = fields.get(fieldName);
        if (field == null) {
            throw new NullPointerException("No such field: " + fieldName);
        }

        val value = field.get(datum);
        if (value == null) {
            throw new NullPointerException(fieldName + (field.unique() ? " unique" : "") + " value is null");
        }

        return value;
    }

    @Override
    public @NotNull HashMap<String, MongoMutableField> fields() {
        return this.model.fields();
    }

    protected List<String> uniques() {
        return this.model.uniques();
    }

    protected @NotNull Class<M> type() {
        return this.model.type();
    }

    @Override
    public @NotNull String firstUniqueFieldName() {
        val uniques = this.uniques();
        if (uniques.size() > 1) {
            throw new IllegalStateException("More than one unique field found");
        }

        val fieldName = uniques.get(0);
        if (fieldName != null) {
            return fieldName;
        }
        throw new NullPointerException("No unique field found automatically");
    }

    protected @NotNull MongoCollection<M> collection() {
        return this.model.collection();
    }

    protected void validateFilterUniques(@NotNull Bson filter, List<String> uniques) throws NotFoundResult {
        val filterString = filter.toString();
        val hasUnique = uniques != null && uniques.stream().anyMatch(unique -> filterString.contains("fieldName='" + unique + "'"));
        if (!hasUnique) {
            throw new IllegalStateException("No unique keys found");
        }
    }
}
