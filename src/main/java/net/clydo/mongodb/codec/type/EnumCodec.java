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
 * Copyright (C) 2024 ClydoNetwork
 */

package net.clydo.mongodb.codec.type;

import net.clydo.mongodb.loader.enums.values.MongoEnumValue;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public final class EnumCodec<T extends Enum<T>> implements Codec<T> {
    private final Class<T> clazz;
    private final MongoEnumValue<T> enumHolder;

    public EnumCodec(final Class<T> clazz, final MongoEnumValue<T> enumHolder) {
        this.clazz = clazz;
        this.enumHolder = enumHolder;
    }

    @Override
    public T decode(final @NotNull BsonReader reader, final DecoderContext decoderContext) {
        return this.enumHolder.decode(reader.readString());
    }

    @Override
    public void encode(final @NotNull BsonWriter writer, final T value, final EncoderContext encoderContext) {
        writer.writeString(this.enumHolder.encode(value));
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }
}
