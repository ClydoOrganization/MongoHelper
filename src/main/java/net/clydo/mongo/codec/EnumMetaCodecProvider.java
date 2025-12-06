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

package net.clydo.mongo.codec;

import lombok.val;
import net.clydo.mongo.registry.EnumMetaRegistry;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record EnumMetaCodecProvider(
        @NotNull EnumMetaRegistry enumRegistry
) implements CodecProvider {

    @Override
    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Codec<T> get(
            @NotNull final Class<T> clazz,
            @NotNull final CodecRegistry registry
    ) {
        if (!Enum.class.isAssignableFrom(clazz)) {
            return null;
        }

        val enumMeta = this.enumRegistry.getNullable(clazz);
        if (enumMeta == null) {
            return null;
        }

        return (Codec<T>) new EnumMetaCodec(clazz, enumMeta);
    }

}
