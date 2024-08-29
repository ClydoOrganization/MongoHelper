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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MongoHelper.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2024 ClydoNetwork
 *
 */

package net.clydo.mongodb.loader.enums;

import net.clydo.mongodb.annotations.MongoEnum;
import net.clydo.mongodb.loader.enums.values.MongoEnumValue;
import net.clydo.mongodb.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

public class EnumCacheLoader {
    @SuppressWarnings("unchecked")
    public <T, E extends Enum<E>> MongoEnumValue<E> buildUnsafe(Class<T> clazz) {
        return build((Class<E>) clazz);
    }

    private <E extends Enum<E>> @NotNull MongoEnumValue<E> build(Class<E> clazz) {
        ReflectionUtil.validateAnnotation(clazz, MongoEnum.class);

        return MongoEnumValue.of(clazz);
    }
}
