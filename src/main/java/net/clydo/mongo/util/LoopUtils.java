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

import java.util.function.Function;

@UtilityClass
public final class LoopUtils {

    public <T> LoopControl forEach(
            @NotNull final Iterable<T> iterable,
            @NotNull final Function<T, LoopControl> consumer
    ) {
        for (val item : iterable) {
            val control = consumer.apply(item);
            if (control == LoopControl.BREAK) {
                break;
            }
        }
        return LoopControl.CONTINUE;
    }

}
