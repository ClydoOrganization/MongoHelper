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

import lombok.Getter;
import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.clytil.reflect.accessor.Accessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@Getter
public final class FieldMetaImpl implements FieldMeta {

    private final String name;
    private final AccessorResolver resolver;

    private final List<String> aliases;
    private final Accessor<Object, Object> accessor;
    private final Class<?> type;
    private final Type genericType;
    private final boolean isOptional;

    public FieldMetaImpl(
            @NotNull final String name,
            @NotNull final AccessorResolver resolver
    ) {
        this.name = name;
        this.resolver = resolver;
        this.aliases = resolver.resolveAliases(name);
        this.accessor = resolver.resolveAccessor();
        this.type = resolver.resolveType();
        this.genericType = resolver.resolveGenericType();
        this.isOptional = resolver.resolveIsOptional();
    }

    @Override
    public void set(
            @NotNull final Object owner,
            @Nullable final Object value
    ) {
        if (!this.isOptional()) {
            Validates.requireMsg(value, "Null value is not allowed when 'isOptional' is false for '" + this.name + "'");
        }

        this.accessor.set(owner, value);
    }

    @Nullable
    @Override
    public Object get(
            @NotNull final Object owner
    ) {
        val value = this.accessor.get(owner);
        if (!this.isOptional()) {
            Validates.requireMsg(value, "Null value is not allowed when 'isOptional' is false for '" + this.name + "'");
        }

        return value;
    }

}
