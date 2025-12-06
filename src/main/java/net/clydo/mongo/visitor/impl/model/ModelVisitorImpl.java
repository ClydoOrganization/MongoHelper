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

package net.clydo.mongo.visitor.impl.model;

import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.clytil.reflect.Annotations;
import net.clydo.mongo.annotations.OrmModel;
import net.clydo.mongo.visitor.ClassVisitor;
import net.clydo.mongo.visitor.builder.ModelMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;

public class ModelVisitorImpl extends BaseVisitor<ModelMetaBuilder> implements ClassVisitor {

    public ModelVisitorImpl(
            @NotNull final ModelMetaBuilder builder
    ) {
        super(builder);
    }

    @Override
    public void visitClass(
            @NotNull final Class<?> clazz
    ) {
        Validates.require(clazz, "clazz");
        val ormModel = Annotations.require(clazz, OrmModel.class);

        this.builder.setCollectionName(ormModel.value());
    }

}
