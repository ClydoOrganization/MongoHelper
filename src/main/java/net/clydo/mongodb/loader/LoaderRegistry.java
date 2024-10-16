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

package net.clydo.mongodb.loader;

import lombok.val;
import net.clydo.mongodb.annotations.MongoType;
import net.clydo.mongodb.loader.classes.ClassCacheLoader;
import net.clydo.mongodb.loader.classes.values.ClassCacheValue;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.loader.classes.values.MongoTypeValue;
import net.clydo.mongodb.loader.enums.EnumCacheLoader;
import net.clydo.mongodb.loader.enums.values.MongoEnumValue;
import net.clydo.mongodb.schematic.MongoSchemaHolder;
import net.clydo.mongodb.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LoaderRegistry {
    private final HashMap<Class<?>, CacheValue> cache;

    private final EnumCacheLoader enumCacheLoader;
    private final ClassCacheLoader classCacheLoader;

    public LoaderRegistry() {
        this.cache = new HashMap<>();
        this.enumCacheLoader = new EnumCacheLoader();
        this.classCacheLoader = new ClassCacheLoader(this);
    }

    public <T> CacheValue build(@NotNull Class<T> clazz) {
        if (clazz.isEnum()) {
            return this.buildEnum(clazz);
        } else {
            val isMongoType = ReflectionUtil.hasAnnotation(clazz, MongoType.class, false);
            if (isMongoType) {
                return this.buildType(clazz);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C> MongoModelValue<C> buildModel(Class<C> clazz, MongoSchemaHolder parent) {
        val value = this.classCacheLoader.build(clazz, parent);
        this.cache.put(clazz, value);
        return (MongoModelValue<C>) value;
    }

    @SuppressWarnings("unchecked")
    public <C> MongoTypeValue<C> buildType(Class<C> clazz) {
        val value = this.classCacheLoader.build(clazz, null);
        this.cache.put(clazz, value);
        return (MongoTypeValue<C>) value;
    }

    public <C, E extends Enum<E>> MongoEnumValue<E> buildEnum(Class<C> clazz) {
        final MongoEnumValue<E> value = this.enumCacheLoader.buildUnsafe(clazz);
        this.cache.put(clazz, value);
        return value;
    }

    public <T> ClassCacheValue getClass(Class<T> clazz) {
        var value = this.cache.get(clazz);

        if (value == null) {
            value = this.buildType(clazz);
        }

        if (value instanceof ClassCacheValue classCacheValue)
            return classCacheValue;

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> MongoTypeValue<T> getType(Class<T> clazz) {
        val value = this.cache.get(clazz);

        if (value instanceof MongoTypeValue<?> modelHolder)
            return (MongoTypeValue<T>) modelHolder;

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> MongoModelValue<T> getModel(Class<T> clazz) {
        val value = this.cache.get(clazz);

        if (value instanceof MongoModelValue<?> modelHolder)
            return (MongoModelValue<T>) modelHolder;

        return null;
    }

    @SuppressWarnings("unchecked")
    public <C, E extends Enum<E>> MongoEnumValue<E> getEnum(Class<C> clazz) {
        var value = this.cache.get(clazz);

        if (value == null) {
            value = this.buildEnum(clazz);
        }

        if (value instanceof MongoEnumValue<?> modelHolder)
            return (MongoEnumValue<E>) modelHolder;

        return null;
    }
}
