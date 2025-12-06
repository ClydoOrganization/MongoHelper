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
 * Copyright (C) 2024-2025 ClydoNetwork
 */

package net.clydo.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StringUUIDCodec implements Codec<UUID> {

    @Override
    public UUID decode(
            @NotNull final BsonReader reader,
            @NotNull final DecoderContext decoderContext
    ) {
        return UUID.fromString(reader.readString());
    }

    @Override
    public void encode(
            @NotNull final BsonWriter writer,
            @NotNull final UUID value,
            @NotNull final EncoderContext encoderContext
    ) {
        writer.writeString(value.toString());
    }

    @Override
    public Class<UUID> getEncoderClass() {
        return UUID.class;
    }

}
