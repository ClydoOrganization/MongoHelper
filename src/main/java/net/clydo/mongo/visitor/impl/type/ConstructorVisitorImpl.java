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

package net.clydo.mongo.visitor.impl.type;

import lombok.val;
import net.clydo.clytil.Primitives;
import net.clydo.clytil.Validates;
import net.clydo.clytil.reflect.Annotations;
import net.clydo.clytil.reflect.Constructors;
import net.clydo.mongo.annotations.OrmConstructor;
import net.clydo.mongo.annotations.OrmParameter;
import net.clydo.mongo.meta.type.constructor.ConstructorMetaImpl;
import net.clydo.mongo.util.LoopControl;
import net.clydo.mongo.visitor.ConstructorVisitor;
import net.clydo.mongo.visitor.builder.TypeMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class ConstructorVisitorImpl extends BaseVisitor<TypeMetaBuilder> implements ConstructorVisitor {

    public ConstructorVisitorImpl(
            @NotNull final TypeMetaBuilder builder
    ) {
        super(builder);
    }

    @NotNull
    @Override
    public LoopControl visitConstructor(
            @NotNull final Class<?> clazz,
            @NotNull final Constructor<?> constructor
    ) {
        Validates.require(clazz, "clazz");
        Validates.require(constructor, "constructor");

        val ormConstructor = Annotations.get(constructor, OrmConstructor.class);
        if (ormConstructor == null) {
            return LoopControl.CONTINUE;
        }

        if (this.builder.getConstructorMeta() != null) {
            throw new IllegalStateException("Only one constructor can be annotated with @OrmConstructor in class " + clazz.getName());
        }

        val requiredFields = this.validateParameters(clazz, constructor, ormConstructor);

        this.builder.setConstructorMeta(new ConstructorMetaImpl<>(
                Constructors.of(
                        clazz,
                        constructor.getParameterTypes()
                ),
                requiredFields
        ));

        return LoopControl.CONTINUE;
    }

    @Override
    public void visitConstructorsEnd(
            @NotNull final Class<?> clazz
    ) {
        val annotated = this.builder.getConstructorMeta();
        val selected = annotated == null
                ? Constructors.of(clazz)
                : annotated;

        val requiredFields = annotated != null
                ? annotated.requiredFields()
                : null;

        this.builder.setConstructorMeta(new ConstructorMetaImpl<>(
                selected,
                requiredFields
        ));
    }

    @NotNull
    private List<String> validateParameters(
            @NotNull final Class<?> clazz,
            @NotNull final Constructor<?> constructor,
            @NotNull final OrmConstructor ormConstructor
    ) {
        val fieldMetaMap = this.builder.getFieldMetaMap();

        val requiredFields = new LinkedList<String>();

        for (val parameter : constructor.getParameters()) {
            val ormParameter = Annotations.get(parameter, OrmParameter.class);
            if (ormParameter == null) {
                requiredFields.add(null);
                continue;
            }

            val name = ormParameter.value();
            val fieldMeta = fieldMetaMap.get(name);

            val expectedType = Primitives.wrap(fieldMeta.getGenericType());
            val actualType = Primitives.wrap(parameter.getParameterizedType());

            if (!Objects.equals(expectedType, actualType)) {
                throw new IllegalStateException(
                        "Type mismatch for field '" + name + "' bound to constructor parameter.\n" +
                                " - Expected type: " + expectedType.getTypeName() + "\n" +
                                " - Actual constructor parameter type: " + actualType.getTypeName()
                );
            }

            if (requiredFields.contains(name)) {
                throw new IllegalStateException(
                        "Duplicate @OrmParameter name: '" + name + "' in class " + clazz.getName()
                );
            }

            requiredFields.add(name);
        }

        if (ormConstructor.strict()) {
            val fieldNames = fieldMetaMap.keys();

            val allFieldsMatch = requiredFields.size() == fieldMetaMap.size()
                    && requiredFields.containsAll(fieldNames);

            if (!allFieldsMatch) {
                val missingFields = new HashSet<>(fieldNames);
                requiredFields.forEach(missingFields::remove);

                throw new IllegalStateException(
                        clazz + " constructor does not cover all fields annotated with @OrmField.\n" +
                                " - Expected all @OrmField fields to have corresponding @OrmParameter constructor parameters.\n" +
                                " - Missing in constructor: " + missingFields
                );
            }
        }

        return requiredFields;
    }

}
