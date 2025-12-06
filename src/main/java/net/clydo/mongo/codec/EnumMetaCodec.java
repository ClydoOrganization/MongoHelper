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

import net.clydo.mongo.meta.enm.EnumMeta;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public record EnumMetaCodec<E extends Enum<E>>(
        @NotNull Class<E> clazz,
        @NotNull EnumMeta<E> enumMeta
) implements Codec<E> {

    @Override
    public void encode(
            @NotNull final BsonWriter writer,
            @NotNull final E value,
            @NotNull final EncoderContext encoderContext
    ) {
        writer.writeString(this.enumMeta.encode(value));
    }

    @Override
    public E decode(
            @NotNull final BsonReader reader,
            @NotNull final DecoderContext decoderContext
    ) {
        return this.enumMeta.decode(reader.readString());
    }

    @Override
    public Class<E> getEncoderClass() {
        return this.clazz;
    }

}
