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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import lombok.val;
import net.clydo.mongodb.loader.classes.values.MongoMutableField;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.operations.error.NotFoundResult;
import net.clydo.mongodb.util.MongoUtil;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractOperations<M> {
    //
    //  Find First
    //
    public abstract @Nullable M findFirstRaw(@NotNull Bson filter);

    public @Nullable M findFirst() {
        return this.findFirstRaw(new BsonDocument());
    }

    public @Nullable M findFirst(@NotNull String fieldName, @Nullable Object value) {
        return this.findFirstRaw(Filters.eq(fieldName, value));
    }

    public @Nullable M findFirstByUnique(@NotNull Object uniqueValue) {
        return this.findFirst(this.firstUniqueFieldName(), uniqueValue);
    }

    public @NotNull M findFirstOrThrowRaw(@NotNull Bson filter) throws NotFoundResult {
        val result = this.findFirstRaw(filter);
        if (result != null) {
            return result;
        }
        throw new NotFoundResult("Failed to find data");
    }

    //
    //  Find Unique
    //
    public abstract @Nullable M findUniqueRaw(@NotNull Bson filter);

    public @Nullable M findUnique(@NotNull String fieldName, @Nullable Object value) {
        return this.findUniqueRaw(Filters.eq(fieldName, value));
    }

    public @Nullable M findUniqueByUnique(@NotNull Object uniqueValue) {
        return this.findUnique(this.firstUniqueFieldName(), uniqueValue);
    }

    public @NotNull M findUniqueOrThrowRaw(@NotNull Bson filter) throws NotFoundResult {
        val result = this.findUniqueRaw(filter);
        if (result != null) {
            return result;
        }
        throw new NotFoundResult("Failed to find data");
    }

    //
    //  Find Many
    //
    public abstract @NotNull FindIterable<M> findManyRaw(@NotNull Bson filter);

    public @NotNull FindIterable<M> findMany() {
        return this.findManyRaw(new BsonDocument());
    }

    public @NotNull FindIterable<M> findMany(@NotNull String fieldName, @Nullable Object value) {
        return this.findManyRaw(Filters.eq(fieldName, value));
    }

    //
    //  Create
    //
    public abstract @NotNull InsertOneResult createRaw(@NotNull M datum);

    //
    //  Create Many
    //
    public abstract @NotNull InsertManyResult createManyRaw(@NotNull M... data);

    //
    //  Update
    //
    public abstract @NotNull UpdateResult updateRaw(@NotNull Bson filter, @NotNull Bson update);

    public @NotNull UpdateResult update(@NotNull Bson filter, @NotNull M datum) {
        val datumDocument = MongoUtil.toBsonDocument(this.codecRegistry(), this.type(), datum);

        return this.updateRaw(filter, datumDocument);
    }

    public @NotNull UpdateResult update(@NotNull String fieldName, @Nullable Object value, @NotNull M datum) {
        val datumDocument = MongoUtil.toBsonDocument(this.codecRegistry(), this.type(), datum);

        return this.updateRaw(Filters.eq(fieldName, value), datumDocument);
    }

    public @NotNull UpdateResult update(@Nullable Object uniqueValue, @NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();

        return this.update(fieldName, uniqueValue, datum);
    }

    public @NotNull UpdateResult update(@NotNull M datum) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), datum, fieldName);

        return this.update(fieldName, uniqueValue, datum);
    }

    //
    //  Update Many
    //
    public abstract @NotNull UpdateResult updateManyRaw(@NotNull Bson filter, @NotNull Bson update);

    //
    //  Upsert
    //
    public abstract @NotNull UpdateResult upsertRaw(@NotNull Bson filter, @NotNull Bson update, @NotNull Bson create);

    public abstract @NotNull UpdateResult upsertRaw(@NotNull Bson filter, @NotNull Bson update, @NotNull M create);

    //
    //  Delete
    //
    public abstract @NotNull DeleteResult deleteRaw(@NotNull Bson filter);

    public @NotNull DeleteResult delete(@NotNull String fieldName, @Nullable Object value) {
        return this.deleteRaw(Filters.eq(fieldName, value));
    }

    public @NotNull DeleteResult deleteByUnique(@NotNull Object uniqueValue) {
        return this.delete(this.firstUniqueFieldName(), uniqueValue);
    }

    public @NotNull DeleteResult delete(@NotNull M value) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValue = this.getFieldValue(this.fields(), value, fieldName);

        return this.delete(fieldName, uniqueValue);
    }

    //
    //  Delete Many
    //
    public abstract @NotNull DeleteResult deleteManyRaw(@NotNull Bson filter);

    public @NotNull DeleteResult deleteMany() {
        return this.deleteManyRaw(new BsonDocument());
    }

    public @NotNull DeleteResult deleteMany(@NotNull String fieldName, @NotNull Object... uniqueValues) {
        return this.deleteManyRaw(Filters.in(fieldName, uniqueValues));
    }

    public @NotNull DeleteResult deleteManyByUniques(@NotNull Object... uniqueValues) {
        return this.deleteMany(this.firstUniqueFieldName(), uniqueValues);
    }

    public @NotNull DeleteResult deleteMany(@NotNull M... values) {
        val fieldName = this.firstUniqueFieldName();
        val uniqueValues = this.getFieldValues(this.fields(), values, fieldName);

        return this.deleteMany(this.firstUniqueFieldName(), uniqueValues);
    }

    //
    //  Count
    //
    public abstract long count(@NotNull Bson filter);

    protected void validateFilterUniques(@NotNull Bson filter, List<String> uniques) throws NotFoundResult {
        val filterString = filter.toString();
        val hasUnique = uniques != null && uniques.stream().anyMatch(unique -> filterString.contains("fieldName='" + unique + "'"));
        if (!hasUnique) {
            throw new IllegalStateException("No unique keys found");
        }
    }

    protected Object[] getFieldValues(HashMap<String, MongoMutableField> fields, @NotNull M @NotNull [] data, @NotNull String fieldName) {
        val values = new Object[data.length];

        for (int i = 0; i < data.length; i++) {
            val datum = data[i];
            val fieldValue = this.getFieldValue(fields, datum, fieldName);
            values[i] = fieldValue;
        }

        return values;
    }

    protected Object getFieldValue(@NotNull HashMap<String, MongoMutableField> fields, @NotNull M datum, @NotNull String fieldName) {
        val field = fields.get(fieldName);
        if (field == null) {
            throw new NullPointerException("No such field: " + fieldName);
        }

        val value = field.get(datum);
        if (value == null) {
            throw new NullPointerException(fieldName + " unique value is null");
        }

        return value;
    }

    protected @NotNull CodecRegistry codecRegistry() {
        return this.model().collection().getCodecRegistry();
    }

    protected @NotNull HashMap<String, MongoMutableField> fields() {
        return this.model().fields();
    }

    protected List<String> uniques() {
        return this.model().uniques();
    }

    protected @NotNull Class<M> type() {
        return this.model().type();
    }

    protected @NotNull String firstUniqueFieldName() {
        if (this.uniques().size() > 1) {
            throw new IllegalStateException("More than one unique field found");
        }

        val fieldName = this.uniques().get(0);
        if (fieldName != null) {
            return fieldName;
        }
        throw new NullPointerException("No unique field found automatically");
    }

    protected @NotNull MongoCollection<M> collection() {
        return this.model().collection();
    }

    protected abstract @NotNull MongoModelValue<M> model();
}
