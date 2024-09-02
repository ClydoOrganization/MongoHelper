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
 */

package net.clydo.mongodb.loader.classes.values;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public record MongoMutableField(
        String fieldName,
        Field field,
        boolean unique
) {
    public Object get(Object object) {
        try {
            return this.field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object object, Object value) {
        try {
            this.field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract(pure = true)
    public @NotNull Class<?> fieldType() {
        return this.field.getType();
    }
}
