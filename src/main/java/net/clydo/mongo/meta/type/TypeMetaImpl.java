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

package net.clydo.mongo.meta.type;

import net.clydo.clytil.reflect.MethodInvoker;
import net.clydo.mongo.meta.type.constructor.ConstructorMeta;
import net.clydo.mongo.meta.type.field.FieldMetaMap;
import org.jetbrains.annotations.NotNull;

public record TypeMetaImpl<T>(
        @NotNull FieldMetaMap fieldMetaMap,
        @NotNull ConstructorMeta<T> constructor,
        @NotNull MethodInvoker<T, Void> afterLoad
) implements TypeMeta<T> {
}
