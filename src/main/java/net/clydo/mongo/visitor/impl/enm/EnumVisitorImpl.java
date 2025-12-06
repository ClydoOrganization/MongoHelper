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

package net.clydo.mongo.visitor.impl.enm;

import net.clydo.clytil.Validates;
import net.clydo.clytil.reflect.Annotations;
import net.clydo.mongo.annotations.OrmEnum;
import net.clydo.mongo.visitor.ClassVisitor;
import net.clydo.mongo.visitor.builder.EnumMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;

public class EnumVisitorImpl extends BaseVisitor<EnumMetaBuilder> implements ClassVisitor {

    public EnumVisitorImpl(
            @NotNull final EnumMetaBuilder builder
    ) {
        super(builder);
    }

    @Override
    public void visitClass(
            @NotNull final Class<?> clazz
    ) {
        Validates.require(clazz, "clazz");
        Annotations.require(clazz, OrmEnum.class);

        if (!clazz.isEnum()) {
            throw new IllegalStateException(
                    "The class '" + clazz.getName() + "' is annotated with @OrmEnum but is not an enum."
            );
        }
    }

}
