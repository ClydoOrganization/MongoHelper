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
import net.clydo.mongodb.loader.classes.values.MongoMutableField;
import net.clydo.mongodb.loader.CacheValue;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.loader.classes.values.MongoTypeValue;
import net.clydo.mongodb.schematic.MongoSchemaHolder;
import net.clydo.mongodb.util.ReflectionUtil;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ClassCacheLoader {
    private final LoaderRegistry registry;

    public ClassCacheLoader(LoaderRegistry registry) {
        this.registry = registry;
    }

    public <C> CacheValue build(Class<C> clazz, MongoSchemaHolder parent) {
        val mongoModel = ReflectionUtil.getAnnotation(clazz, MongoModel.class);

        val fields = this.createFields(clazz);

        return this.createHolder(clazz, mongoModel, fields, parent);
    }

    @Contract("_, _, _, _ -> new")
    private <C> @Unmodifiable @NotNull CacheValue createHolder(Class<C> clazz, MongoModel mongoModel, HashMap<String, MongoMutableField> fields, MongoSchemaHolder parent) {
        if (mongoModel != null && parent != null) {
            return MongoModelValue.of(clazz, fields, mongoModel.value(), parent);
        } else {
            return MongoTypeValue.of(clazz, fields);
        }
    }

    protected <M> @NotNull HashMap<String, MongoMutableField> createFields(@NotNull Class<M> clazz) {
        val fields = new HashMap<String, MongoMutableField>();

        for (Field field : clazz.getDeclaredFields()) {
            val mongoFieldInfo = ReflectionUtil.getAnnotation(field, MongoField.class);

            if (mongoFieldInfo != null) {
                val fieldName = mongoFieldInfo.value();
                val fieldType = field.getType();

                if ("_id".equals(fieldName) && fieldType != ObjectId.class) {
                    throw new IllegalStateException("");
                }

                field.setAccessible(true);

                val unique = ReflectionUtil.hasAnnotation(field, MongoUnique.class);
                val useFallback = ReflectionUtil.hasAnnotation(field, MongoUseFallback.class);

                val fieldHolder = new MongoMutableField(
                        fieldName,
                        field,
                        unique,
                        useFallback
                );
                fields.put(
                        fieldName,
                        fieldHolder
                );

                this.registry.build(fieldHolder.fieldType());
            }
        }

        return fields;
    }
}
