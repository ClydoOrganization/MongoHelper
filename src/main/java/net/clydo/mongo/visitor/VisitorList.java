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

package net.clydo.mongo.visitor;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VisitorList {

    protected final List<ClassVisitor> classVisitors;
    protected final List<FieldVisitor> fieldVisitors;
    protected final List<MethodVisitor> methodVisitors;
    protected final List<ConstructorVisitor> constructorVisitors;

    public VisitorList(
            @NotNull final List<Visitor> visitors
    ) {
        this.classVisitors = new ArrayList<>();
        this.fieldVisitors = new ArrayList<>();
        this.methodVisitors = new ArrayList<>();
        this.constructorVisitors = new ArrayList<>();

        for (val visitor : visitors) {
            if (visitor instanceof ClassVisitor classVisitor) {
                this.classVisitors.add(classVisitor);
            }
            if (visitor instanceof FieldVisitor fieldVisitor) {
                this.fieldVisitors.add(fieldVisitor);
            }
            if (visitor instanceof MethodVisitor methodVisitor) {
                this.methodVisitors.add(methodVisitor);
            }
            if (visitor instanceof ConstructorVisitor constructorVisitor) {
                this.constructorVisitors.add(constructorVisitor);
            }
        }
    }

}
