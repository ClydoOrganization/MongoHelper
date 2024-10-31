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

package net.clydo.mongodb.loader.classes;

import lombok.val;
import net.clydo.mongodb.annotations.*;
import net.clydo.mongodb.loader.LoaderRegistry;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.loader.classes.values.MongoMutableField;
import net.clydo.mongodb.loader.classes.values.MongoTypeValue;
import net.clydo.mongodb.schematic.MongoSchemaHolder;
import net.clydo.mongodb.util.ReflectionUtil;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ClassCacheLoader {
    private final LoaderRegistry registry;

    public ClassCacheLoader(LoaderRegistry registry) {
        this.registry = registry;
    }

    public <C> MongoModelValue<C> buildModel(Class<C> clazz, MongoSchemaHolder schemaHolder) {
        Objects.requireNonNull(schemaHolder, "The schema holder cannot be null.");
        Objects.requireNonNull(clazz, "The model class cannot be null.");

        val mongoModel = ReflectionUtil.getAnnotation(clazz, MongoModel.class);
        Objects.requireNonNull(mongoModel, "Class " + clazz.getSimpleName() + " must be annotated with @MongoModel.");

        val fields = this.collectFields(clazz, schemaHolder);

        return MongoModelValue.of(clazz, fields, mongoModel.value(), schemaHolder);
    }

    public <C> MongoTypeValue<C> buildType(Class<C> clazz) {
        val isMongoType = ReflectionUtil.hasAnnotation(clazz, MongoType.class, true);
        if (!isMongoType) {
            return null;
        }

        val fields = this.collectFields(clazz, null);

        return MongoTypeValue.of(clazz, fields);
    }

    protected <M> @NotNull HashMap<String, MongoMutableField> collectFields(@NotNull Class<M> clazz, MongoSchemaHolder schemaHolder) {
        val fields = new HashMap<String, MongoMutableField>();

        Arrays.stream(clazz.getDeclaredFields())
                .map(field -> Pair.of(field, ReflectionUtil.getAnnotation(field, MongoField.class)))
                .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                .forEach(entry -> {
                    val field = entry.getLeft();
                    val mongoFieldInfo = entry.getRight();

                    val fieldName = mongoFieldInfo.value();
                    val fieldType = field.getType();
                    val fieldGenericType = field.getGenericType();

                    if ("_id".equals(fieldName) && fieldType != ObjectId.class) {
                        throw new IllegalStateException("The field '_id' must have type ObjectId.");
                    }

                    val unique = ReflectionUtil.hasAnnotation(field, MongoUnique.class);
                    val useDefault = ReflectionUtil.hasAnnotation(field, MongoUseDefault.class);

                    val mongoField = new MongoMutableField(
                            fieldName,
                            field,
                            unique,
                            useDefault,
                            fieldType,
                            fieldGenericType
                    );

                    fields.put(fieldName, mongoField);

                    if (mongoField.genericType() instanceof ParameterizedType parameterizedType) {
                        TypeUtils.getTypeArguments(parameterizedType)
                                .values().stream()
                                .filter(type -> type instanceof Class<?>)
                                .map(type -> (Class<?>) type)
                                .forEach(aClass -> {
                                    try {
                                        if (schemaHolder != null) {
                                            this.registry.buildModel(aClass, schemaHolder);
                                            return;
                                        }

                                        this.registry.buildType(aClass);
                                    } catch (Exception ignored) {
                                    }
                                });
                    }

                    this.registry.buildEnumOrType(mongoField.type());
                });

        return fields;
    }
}
