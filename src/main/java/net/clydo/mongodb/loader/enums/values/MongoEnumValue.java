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

package net.clydo.mongodb.loader.enums.values;

import lombok.val;
import net.clydo.mongodb.annotations.MongoMapAs;
import net.clydo.mongodb.loader.CacheValue;
import net.clydo.mongodb.util.ReflectionUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public record MongoEnumValue<E extends Enum<E>>(
        Map<E, String> encryption,
        Map<String, E> decryption
) implements CacheValue {

    public E decode(String key) {
        return this.decryption.get(key);
    }

    public String encode(@NotNull E value) {
        return this.encryption.get(value);
    }

    @Deprecated(forRemoval = true)
    public Map<E, String> encryption() {
        return this.encryption;
    }

    @Deprecated(forRemoval = true)
    public Map<String, E> decryption() {
        return this.decryption;
    }

    @Contract("_ -> new")
    public static <E extends Enum<E>> @NotNull MongoEnumValue<E> of(@NotNull Class<E> enumClass) {
        val constantMap = new HashMap<String, E>();
        for (E constant : enumClass.getEnumConstants()) {
            if (constantMap.put(constant.name(), constant) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        val encryption = new HashMap<E, String>();
        val decryption = new HashMap<String, E>();

        for (Field field : enumClass.getDeclaredFields()) {
            if (field.isEnumConstant()) {
                val fieldName = field.getName();
                val mapAs = ReflectionUtil.validateAnnotation(field, MongoMapAs.class).value();
                val value = constantMap.get(fieldName);

                encryption.put(value, mapAs);
                decryption.put(mapAs, value);
            }
        }

        return new MongoEnumValue<>(encryption, decryption);
    }

}
