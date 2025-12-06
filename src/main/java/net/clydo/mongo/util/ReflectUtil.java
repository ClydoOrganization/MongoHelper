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

package net.clydo.mongo.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class ReflectUtil {

    @NotNull
    public <T> List<T> collect(
            @NotNull final Class<?> clazz,
            @NotNull final Function<Class<?>, T[]> function
    ) {
        val result = new ArrayList<T>();
        for (val c : ReflectUtil.collectSuperClasses(clazz)) {
            Collections.addAll(result, function.apply(c));
        }
        return result;
    }

    @NotNull
    public List<Class<?>> collectSuperClasses(
            @NotNull final Class<?> clazz
    ) {
        val superClasses = new ArrayList<Class<?>>();
        for (var current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
            superClasses.add(current);
            Collections.addAll(superClasses, current.getInterfaces());
        }
        Collections.reverse(superClasses);
        return superClasses;
    }


}
