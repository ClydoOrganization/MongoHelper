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
import net.clydo.clytil.reflect.Annotations;
import net.clydo.clytil.reflect.FieldValues;
import net.clydo.clytil.reflect.MethodInvokers;
import net.clydo.clytil.reflect.accessor.Accessor;
import net.clydo.mongo.annotations.OrmAlias;
import net.clydo.mongo.annotations.OrmOptional;
import net.clydo.mongo.annotations.OrmUnique;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record AccessorResolver(
        Class<?> clazz,
        Field field,
        Method setter,
        Method getter
) {

    public AccessorResolver {
        if (field == null && setter == null && getter == null) {
            throw new IllegalArgumentException("At least one of field, setter, or getter must be non-null");
        }
    }

    public AccessorResolver(
            @NotNull final Class<?> clazz,
            @Nullable final FieldMeta fieldMeta,
            final Method setter,
            final Method getter
    ) {
        this(
                clazz,
                fieldMeta != null
                        ? fieldMeta.getResolver().field
                        : null,
                setter,
                getter
        );
    }

    public AccessorResolver(
            @NotNull final Class<?> clazz,
            @NotNull final Field field
    ) {
        this(clazz, field, null, null);
    }

    @NotNull
    public Accessor<Object, Object> resolveAccessor() {
        val fieldValue = (this.field != null)
                ? FieldValues.of(this.clazz, this.field)
                : null;
        val setterInvoker = (this.setter != null)
                ? MethodInvokers.of(this.clazz, this.setter)
                : null;
        val getterInvoker = (this.getter != null)
                ? MethodInvokers.of(this.clazz, this.getter)
                : null;

        return Accessor.fromLambda(
                setterInvoker != null
                        ? setterInvoker::invoke
                        : fieldValue != null
                        ? fieldValue::set
                        : (o, o2) -> {
                },
                getterInvoker != null
                        ? getterInvoker::invoke
                        : fieldValue != null
                        ? fieldValue::get
                        : o -> null
        );
    }

    public Class<?> resolveType() {
        if (this.getter != null) {
            return this.getter.getReturnType();
        }
        if (this.setter != null) {
            return this.setter.getParameterTypes()[0];
        }
        return this.field.getType();
    }

    public Type resolveGenericType() {
        if (this.getter != null) {
            return this.getter.getGenericReturnType();
        }
        if (this.setter != null) {
            return this.setter.getGenericParameterTypes()[0];
        }
        return this.field.getGenericType();
    }

    public boolean resolveIsOptional() {
        val optionals = this.getAnnotatedElements()
                .filter(element -> Annotations.get(element, OrmOptional.class) != null)
                .toList();

        if (optionals.size() > 1) {
            throw new IllegalStateException("You must use only one @OrmOptional annotation per field.");
        }

        return !optionals.isEmpty();
    }

    @Nullable
    public OrmUnique resolveOrmUnique() {
        val uniques = this.getAnnotatedElements()
                .map(element -> Annotations.get(element, OrmUnique.class))
                .filter(Objects::nonNull)
                .toList();

        if (uniques.size() > 1) {
            throw new IllegalStateException("You must use only one @OrmUnique annotation per field.");
        }

        return !uniques.isEmpty()
                ? uniques.get(0)
                : null;
    }

    @NotNull
    @Unmodifiable
    public List<String> resolveAliases(
            @NotNull final String fieldName
    ) {
        return Stream.concat(
                Stream.of(fieldName),
                this.getAnnotatedElements()
                        .map(element -> Annotations.get(element, OrmAlias.class))
                        .filter(Objects::nonNull)
                        .map(OrmAlias::value)
                        .flatMap(Arrays::stream)
        ).toList();
    }

    @NotNull
    public Stream<AnnotatedElement> getAnnotatedElements() {
        return Stream.of(this.getter, this.setter, (AnnotatedElement) this.field).filter(Objects::nonNull);
    }

    public boolean isFieldOnly() {
        return this.field != null && this.getter == null && this.setter == null;
    }

}
