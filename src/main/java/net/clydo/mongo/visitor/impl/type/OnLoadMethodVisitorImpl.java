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
import net.clydo.clytil.reflect.MethodInvokers;
import net.clydo.mongo.annotations.OrmOnLoad;
import net.clydo.mongo.util.LoopControl;
import net.clydo.mongo.visitor.MethodVisitor;
import net.clydo.mongo.visitor.builder.TypeMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class OnLoadMethodVisitorImpl extends BaseVisitor<TypeMetaBuilder> implements MethodVisitor {

    public OnLoadMethodVisitorImpl(
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

        val ormOnLoad = Annotations.get(method, OrmOnLoad.class);
        if (ormOnLoad != null) {
            if (this.builder.getOnLoad() != null) {
                throw new IllegalStateException("Only one method can be annotated with @OrmOnLoad in class " + clazz.getName());
            }

            this.builder.setOnLoad(
                    MethodInvokers.of(clazz, method)
            );
        }

        return LoopControl.CONTINUE;
    }

}
