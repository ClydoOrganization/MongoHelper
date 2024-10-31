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
import net.clydo.mongodb.loader.classes.ClassCacheLoader;
import net.clydo.mongodb.loader.classes.values.ClassCacheValue;
import net.clydo.mongodb.loader.classes.values.MongoModelValue;
import net.clydo.mongodb.loader.classes.values.MongoTypeValue;
import net.clydo.mongodb.loader.enums.EnumCacheLoader;
import net.clydo.mongodb.loader.enums.values.MongoEnumValue;
import net.clydo.mongodb.schematic.MongoSchemaHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LoaderRegistry {
    private final HashMap<Class<?>, CacheValue> cache;

    private final EnumCacheLoader enumCacheLoader;
    private final ClassCacheLoader classCacheLoader;

    public LoaderRegistry() {
        this.enumCacheLoader = new EnumCacheLoader();
        this.classCacheLoader = new ClassCacheLoader(this);

        this.cache = new HashMap<>();
    }

    public <T> void buildEnumOrType(@NotNull Class<T> clazz) {
        if (Enum.class.isAssignableFrom(clazz)) {
            this.buildEnum(clazz);
        } else {
            this.buildType(clazz);
        }
    }

    public <C> MongoModelValue<C> buildModel(Class<C> clazz, MongoSchemaHolder schemaHolder) {
        val mongoModel = this.classCacheLoader.buildModel(clazz, schemaHolder);
        if (mongoModel == null) {
            return null;
        }

        this.cache.put(clazz, mongoModel);
        return mongoModel;
    }

    public <C> MongoTypeValue<C> buildType(Class<C> clazz) {
        val mongoType = this.classCacheLoader.buildType(clazz);
        if (mongoType == null) {
            return null;
        }

        this.cache.put(clazz, mongoType);
        return mongoType;
    }

    public <C, E extends Enum<E>> MongoEnumValue<E> buildEnum(Class<C> clazz) {
        final MongoEnumValue<E> value = this.enumCacheLoader.buildUnsafe(clazz);
        this.cache.put(clazz, value);
        return value;
    }

    public <T> ClassCacheValue<?> getModelOrType(Class<T> clazz) {
        var value = this.cache.get(clazz);

        if (value == null) {
            value = this.buildType(clazz);
        }

        if (value instanceof ClassCacheValue<?> classCacheValue)
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

        throw new NullPointerException("Model not found for '" + clazz.getSimpleName() + "'. Ensure it is added in the 'newSchema' method.");
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
