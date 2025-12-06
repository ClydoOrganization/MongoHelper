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

package net.clydo.mongo.meta.type.field;

import lombok.val;
import net.clydo.clytil.Validates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FieldMetaMap {

    private final Map<String, FieldMeta> fields;
    private final Map<String, FieldMeta> aliasToField;

    public FieldMetaMap() {
        this.fields = new LinkedHashMap<>();
        this.aliasToField = new LinkedHashMap<>();
    }

    public void put(
            @NotNull final Class<?> clazz,
            @NotNull final FieldMeta field
    ) {
        val name = field.getName();
        if (this.fields.put(name, field) != null) {
            throw new IllegalStateException("Duplicate @OrmField/[OrmSetter/OrmGetter] name: '" + name + "' in class " + clazz.getName());
        }

        for (val alias : field.getAliases()) {
            if (this.aliasToField.put(alias, field) != null) {
                throw new IllegalStateException("Duplicate @OrmAlias name: '" + alias + "' in class " + clazz.getName());
            }
        }
    }

    public void remove(
            @NotNull final String fieldName
    ) {
        val removed = this.fields.remove(fieldName);
        for (val alias : removed.getAliases()) {
            this.aliasToField.remove(alias);
        }
    }

    @NotNull
    public FieldMeta get(
            @NotNull final String fieldName
    ) {
        return Validates.requireMsg(
                this.fields.get(fieldName),
                "No @OrmField mapping found for field name '" + fieldName + "'"
        );
    }

    @Nullable
    public FieldMeta getNullable(
            @NotNull final String fieldName
    ) {
        return this.fields.get(fieldName);
    }

    @NotNull
    public FieldMeta getByAlias(
            @NotNull final String alias
    ) {
        return Validates.requireMsg(
                this.aliasToField.get(alias),
                "No @OrmField/OrmAlias mapping found for alias '" + alias
        );
    }

    @Nullable
    public FieldMeta getByAliasNullable(
            @NotNull final String alias
    ) {
        return this.aliasToField.get(alias);
    }

    public boolean has(
            @NotNull final String fieldName
    ) {
        return this.fields.containsKey(fieldName);
    }

    @NotNull
    public Set<String> keys() {
        return this.fields.keySet();
    }

    @NotNull
    public Collection<FieldMeta> values() {
        return this.fields.values();
    }

    public int size() {
        return this.fields.size();
    }

}
