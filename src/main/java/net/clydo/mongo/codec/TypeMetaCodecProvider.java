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
import net.clydo.clytil.Nulls;
import net.clydo.mongo.registry.ModelMetaRegistry;
import net.clydo.mongo.registry.TypeMetaRegistry;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TypeMetaCodecProvider(
        @NotNull ModelMetaRegistry modelRegistry,
        @NotNull TypeMetaRegistry typeRegistry
) implements CodecProvider {

    @Override
    @Nullable
    public <T> Codec<T> get(
            @NotNull final Class<T> clazz,
            @NotNull final CodecRegistry registry
    ) {
        val modelMeta = this.modelRegistry.getNullable(clazz);

        val typeMeta = Nulls.orGet(modelMeta, () -> this.typeRegistry.getNullable(clazz));
        if (typeMeta == null) {
            return null;
        }

        return new TypeMetaCodec<>(clazz, typeMeta, registry, CodecsHelper.getDefaultBsonTypeClassMap());
    }

}
