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

package net.clydo.mongodb.util;

import lombok.experimental.UtilityClass;
import java.lang.reflect.Type;

@UtilityClass
public final class Primitives {
    public static boolean isPrimitive(Type type) {
        return type instanceof Class<?> clazz && clazz.isPrimitive();
    }

    public static boolean isWrapperType(Type type) {
        return type == Integer.class
                || type == Float.class
                || type == Byte.class
                || type == Double.class
                || type == Long.class
                || type == Character.class
                || type == Boolean.class
                || type == Short.class
                || type == Void.class;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> type) {
        if (type == int.class) return (Class<T>) Integer.class;
        if (type == float.class) return (Class<T>) Float.class;
        if (type == byte.class) return (Class<T>) Byte.class;
        if (type == double.class) return (Class<T>) Double.class;
        if (type == long.class) return (Class<T>) Long.class;
        if (type == char.class) return (Class<T>) Character.class;
        if (type == boolean.class) return (Class<T>) Boolean.class;
        if (type == short.class) return (Class<T>) Short.class;
        if (type == void.class) return (Class<T>) Void.class;
        return type;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> unwrap(Class<T> type) {
        if (type == Integer.class) return (Class<T>) int.class;
        if (type == Float.class) return (Class<T>) float.class;
        if (type == Byte.class) return (Class<T>) byte.class;
        if (type == Double.class) return (Class<T>) double.class;
        if (type == Long.class) return (Class<T>) long.class;
        if (type == Character.class) return (Class<T>) char.class;
        if (type == Boolean.class) return (Class<T>) boolean.class;
        if (type == Short.class) return (Class<T>) short.class;
        if (type == Void.class) return (Class<T>) void.class;
        return type;
    }
}
