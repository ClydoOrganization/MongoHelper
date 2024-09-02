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

package net.clydo.mongodb.codec;

import lombok.Getter;
import net.clydo.mongodb.codec.uuid.StringUUIDCodec;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.codecs.BsonTypeCodecMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CodecsHelper {
    @Getter
    private static final CodecRegistry defaultCodecRegistry = CodecRegistries.fromProviders(List.of(
            CodecRegistries.fromCodecs(
                    new StringUUIDCodec()
            )
    ));

    public static @Nullable Object readValue(
            final @NotNull BsonReader reader,
            final DecoderContext decoderContext,
            final BsonTypeCodecMap bsonTypeCodecMap,
            final UuidRepresentation uuidRepresentation,
            final CodecRegistry registry,
            final Transformer valueTransformer,
            final Class<?> type
    ) {
        BsonType bsonType = reader.getCurrentBsonType();
        if (bsonType == BsonType.NULL) {
            reader.readNull();
            return null;
        }

        Codec<?> codec = null;
        if (type != null) {
            codec = getCodec(registry, type);
        }
        if (codec == null) {
            codec = bsonTypeCodecMap.get(bsonType);

            if (uuidRepresentation != null) {
                if (bsonType == BsonType.BINARY && reader.peekBinarySize() == 16) {
                    switch (reader.peekBinarySubType()) {
                        case 3:
                            if (uuidRepresentation == UuidRepresentation.JAVA_LEGACY
                                    || uuidRepresentation == UuidRepresentation.C_SHARP_LEGACY
                                    || uuidRepresentation == UuidRepresentation.PYTHON_LEGACY) {
                                codec = registry.get(UUID.class);
                            }
                            break;
                        case 4:
                            if (uuidRepresentation == UuidRepresentation.STANDARD) {
                                codec = registry.get(UUID.class);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        if (codec == null) {
            return null;
        }
        return valueTransformer.transform(codec.decode(reader, decoderContext));
    }

    public static Codec<?> getCodec(final CodecRegistry codecRegistry, final Type type) {
        if (type instanceof Class) {
            return codecRegistry.get((Class<?>) type);
        } else if (type instanceof ParameterizedType parameterizedType) {
            return codecRegistry.get((Class<?>) parameterizedType.getRawType(), Arrays.asList(parameterizedType.getActualTypeArguments()));
        } else {
            throw new CodecConfigurationException("Unsupported generic type of container: " + type);
        }
    }
}
