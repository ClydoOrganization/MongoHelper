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

import java.nio.file.Path;

public class StringPathCodec implements Codec<Path> {

    @Override
    public Path decode(
            @NotNull final BsonReader reader,
            @NotNull final DecoderContext decoderContext
    ) {
        return Path.of(reader.readString());
    }

    @Override
    public void encode(
            @NotNull final BsonWriter writer,
            @NotNull final Path value,
            @NotNull final EncoderContext encoderContext
    ) {
        writer.writeString(value.toAbsolutePath().normalize().toString());
    }

    @Override
    public Class<Path> getEncoderClass() {
        return Path.class;
    }

}
