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

package net.clydo.mongo.walker;

import net.clydo.mongo.util.LoopUtils;
import net.clydo.mongo.util.ReflectUtil;
import net.clydo.mongo.visitor.Visitor;
import net.clydo.mongo.visitor.VisitorList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClassWalker extends VisitorList {

    public ClassWalker(
            @NotNull final Visitor... visitors
    ) {
        super(List.of(visitors));
    }

    public void walk(
            @NotNull final Class<?> clazz
    ) {
        this.classVisitors.forEach(classVisitor ->
                classVisitor.visitClass(clazz)
        );

        LoopUtils.forEach(
                ReflectUtil.collect(
                        clazz,
                        Class::getDeclaredFields
                ),
                (field) ->
                        LoopUtils.forEach(
                                this.fieldVisitors,
                                fieldVisitor ->
                                        fieldVisitor.visitField(
                                                clazz,
                                                field
                                        )
                        )
        );

        LoopUtils.forEach(
                List.of(clazz.getDeclaredConstructors()),
                (constructor) ->
                        LoopUtils.forEach(
                                this.constructorVisitors,
                                constructorVisitor ->
                                        constructorVisitor.visitConstructor(
                                                clazz,
                                                constructor
                                        )
                        )

        );
        this.constructorVisitors.forEach(constructorVisitor ->
                constructorVisitor.visitConstructorsEnd(clazz)
        );

        LoopUtils.forEach(
                ReflectUtil.collect(
                        clazz,
                        Class::getDeclaredMethods
                ),
                (method) ->
                        LoopUtils.forEach(
                                this.methodVisitors,
                                methodVisitor ->
                                        methodVisitor.visitMethod(
                                                clazz,
                                                method
                                        )
                        )
        );
        this.methodVisitors.forEach(methodVisitor ->
                methodVisitor.visitMethodsEnd(clazz)
        );

        this.classVisitors.forEach(classVisitor ->
                classVisitor.visitClassEnd(clazz)
        );
    }

}
