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

import net.clydo.mongo.visitor.builder.TypeMetaBuilder;
import net.clydo.mongo.visitor.impl.type.*;
import org.jetbrains.annotations.NotNull;

public class TypeWalker extends ClassWalker {

    public TypeWalker(
            @NotNull final TypeMetaBuilder builder
    ) {
        super(
                new TypeVisitorImpl(builder),
                new TypeFieldVisitorImpl(builder),
                new ConstructorVisitorImpl(builder),
                new AfterLoadMethodVisitorImpl(builder),
                new AccessorMethodVisitorImpl(builder)
        );
    }

}
