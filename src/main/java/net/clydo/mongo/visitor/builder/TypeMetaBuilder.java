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

package net.clydo.mongo.visitor.builder;

import lombok.Getter;
import lombok.Setter;
import net.clydo.clytil.reflect.MethodInvoker;
import net.clydo.mongo.meta.type.TypeMeta;
import net.clydo.mongo.meta.type.TypeMetaImpl;
import net.clydo.mongo.meta.type.constructor.ConstructorMeta;
import net.clydo.mongo.meta.type.field.FieldMeta;
import net.clydo.mongo.meta.type.field.FieldMetaMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TypeMetaBuilder extends MetaBuilder<TypeMeta<?>> {

    protected FieldMetaMap fieldMetaMap;
    @Setter
    protected ConstructorMeta<?> constructorMeta;
    @Setter
    protected MethodInvoker<Object, Void> afterLoad;

    protected Map<String, Method> setters;
    protected Map<String, Method> getters;

    public void addField(
            @NotNull final Class<?> clazz,
            @NotNull final FieldMeta fieldMeta
    ) {
        this.fieldMetaMap.put(clazz, fieldMeta);
    }

    public void addSetter(
            @NotNull final Class<?> clazz,
            @NotNull final String fieldName,
            @NotNull final Method setter
    ) {
        if (this.setters.put(fieldName, setter) != null) {
            throw new IllegalStateException("Duplicate @OrmSetter name: '" + fieldName + "' in class " + clazz.getName());
        }
    }

    public void addGetter(
            @NotNull final Class<?> clazz,
            @NotNull final String fieldName,
            @NotNull final Method getter
    ) {
        if (this.getters.put(fieldName, getter) != null) {
            throw new IllegalStateException("Duplicate @OrmGetter name: '" + fieldName + "' in class " + clazz.getName());
        }
    }

    @Override
    public void reset() {
        this.fieldMetaMap = new FieldMetaMap();
        this.setters = new HashMap<>();
        this.getters = new HashMap<>();
        this.constructorMeta = null;
        this.afterLoad = null;
    }

    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TypeMeta<?> buildInternal() {
        return new TypeMetaImpl(
                this.fieldMetaMap,
                this.constructorMeta,
                this.afterLoad
        );
    }

}
