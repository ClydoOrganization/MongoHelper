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
import net.clydo.clytil.Validates;
import net.clydo.clytil.reflect.Annotations;
import net.clydo.mongo.annotations.OrmGetter;
import net.clydo.mongo.annotations.OrmSetter;
import net.clydo.mongo.meta.type.field.AccessorResolver;
import net.clydo.mongo.meta.type.field.FieldMetaImpl;
import net.clydo.mongo.util.LoopControl;
import net.clydo.mongo.visitor.MethodVisitor;
import net.clydo.mongo.visitor.builder.TypeMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class AccessorMethodVisitorImpl extends BaseVisitor<TypeMetaBuilder> implements MethodVisitor {

    public AccessorMethodVisitorImpl(
            @NotNull final TypeMetaBuilder builder
    ) {
        super(builder);
    }

    @NotNull
    @Override
    public LoopControl visitMethod(
            @NotNull final Class<?> clazz,
            @NotNull final Method method
    ) {
        Validates.require(clazz, "clazz");
        Validates.require(method, "method");

        val ormSetter = Annotations.get(method, OrmSetter.class);
        if (ormSetter != null) {
            this.validateOrmAccessor(clazz, method, ormSetter.value(), OrmSetter.class);

            this.builder.addSetter(
                    clazz,
                    ormSetter.value(),
                    method
            );
        }

        val ormGetter = Annotations.get(method, OrmGetter.class);
        if (ormGetter != null) {
            this.validateOrmAccessor(clazz, method, ormGetter.value(), OrmGetter.class);

            this.builder.addGetter(
                    clazz,
                    ormGetter.value(),
                    method
            );
        }

        return LoopControl.CONTINUE;
    }

    @Override
    public void visitMethodsEnd(
            @NotNull final Class<?> clazz
    ) {
        val fieldMetaMap = this.builder.getFieldMetaMap();
        val setters = this.builder.getSetters();
        val getters = this.builder.getGetters();

        Stream.concat(setters.keySet().stream(), getters.keySet().stream())
                .distinct()
                .forEach((fieldName) -> {
                    val fieldMeta = fieldMetaMap.getNullable(fieldName);

                    @Nullable
                    val setter = setters.get(fieldName);
                    @Nullable
                    val getter = getters.get(fieldName);

                    if (setter == null && getter == null) {
                        return;
                    }

                    if (fieldMeta != null && fieldMeta.getResolver().isFieldOnly()) {
                        fieldMetaMap.remove(fieldName);
                    }

                    val resolver = new AccessorResolver(clazz, fieldMeta, setter, getter);

                    this.builder.addField(
                            clazz,
                            new FieldMetaImpl(
                                    fieldName,
                                    resolver
                            )
                    );
                });
    }

    private <A extends Annotation> void validateOrmAccessor(
            @NotNull final Class<?> clazz,
            @NotNull final Method method,
            @NotNull final String name,
            @NotNull final Class<A> annotationClass
    ) {
        val paramCount = method.getParameterCount();
        val returnType = method.getReturnType();
        val annotationName = "@" + annotationClass.getSimpleName();
        val isSetter = annotationClass == OrmSetter.class;

        if (paramCount != (isSetter ? 1 : 0)) {
            throw new IllegalStateException(String.format(
                    "Method %s annotated with %s must have %s parameter(s), but found %d in class %s (name: '%s')",
                    method,
                    annotationName,
                    isSetter ? "exactly one" : "no",
                    paramCount,
                    clazz.getName(),
                    name
            ));
        }
        if (returnType.equals(void.class) != isSetter) {
            throw new IllegalStateException(String.format(
                    "Method %s annotated with %s must have a %s return type, but returns %s in class %s (name: '%s')",
                    method,
                    annotationName,
                    isSetter ? "void" : "\b",
                    isSetter ? returnType.getName() : "void",
                    clazz.getName(),
                    name
            ));
        }
    }

}
