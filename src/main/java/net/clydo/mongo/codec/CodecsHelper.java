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

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.clydo.clytil.Inits;
import net.clydo.clytil.reflect.Reflects;
import org.bson.*;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.BsonTypeCodecMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.UuidHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;

@UtilityClass
public class CodecsHelper {

    @Getter
    private static final BsonTypeClassMap defaultBsonTypeClassMap = Inits.of(() -> {
        try {
            val field = Reflects.getField(BsonTypeClassMap.class, "DEFAULT_BSON_TYPE_CLASS_MAP");
            field.setAccessible(true);
            return (BsonTypeClassMap) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    });

    @Nullable
    Object readValue(
            @NotNull final BsonReader reader,
            @NotNull final CodecRegistry registry,
            @NotNull final BsonTypeCodecMap bsonTypeCodecMap,
            @NotNull final DecoderContext decoderContext,
            @NotNull final Transformer valueTransformer,
            @Nullable final UuidRepresentation uuidRepresentation,
            @Nullable final Type type
    ) {
        val bsonType = reader.getCurrentBsonType();
        if (bsonType == BsonType.NULL) {
            reader.readNull();
            return null;
        }

        var codec = type != null
                ? getCodec(registry, type)
                : null;

        if (codec == null) {
            codec = bsonTypeCodecMap.get(bsonType);

            if (bsonType == BsonType.BINARY) {
                val binarySubType = reader.peekBinarySubType();
                codec = getBinarySubTypeCodec(
                        reader,
                        uuidRepresentation,
                        registry,
                        binarySubType,
                        codec
                );
            }
        }

        if (codec == null) {
            return null;
        }

        val value = valueTransformer.transform(codec.decode(reader, decoderContext));
        if (value == null) {
            return null;
        }
        return type instanceof Class<?> caster
                ? caster.cast(value)
                : value;
    }

    Codec<?> getBinarySubTypeCodec(
            @NotNull final BsonReader reader,
            @Nullable final UuidRepresentation uuidRepresentation,
            @NotNull final CodecRegistry registry,
            final byte binarySubType,
            final Codec<?> fallback
    ) {
        if (binarySubType == BsonBinarySubType.VECTOR.getValue()) {
            val vectorCodec = registry.get(BinaryVector.class, registry);
            if (vectorCodec != null) {
                return vectorCodec;
            }
        } else if (reader.peekBinarySize() == 16) {
            switch (binarySubType) {
                case 3:
                    if (UuidHelper.isLegacyUUID(uuidRepresentation)) {
                        return registry.get(UUID.class);
                    }
                    break;
                case 4:
                    if (uuidRepresentation == UuidRepresentation.STANDARD) {
                        return registry.get(UUID.class);
                    }
                    break;
                default:
                    break;
            }
        }

        return fallback;
    }

    public Codec<?> getCodec(
            @NotNull final CodecRegistry registry,
            @NotNull final Type type
    ) {
        if (type instanceof Class<?> clazz) {
            return registry.get(clazz);
        } else if (type instanceof ParameterizedType parameterizedType) {
            val clazz = (Class<?>) parameterizedType.getRawType();
            val typeArguments = Arrays.asList(parameterizedType.getActualTypeArguments());
            return registry.get(clazz, typeArguments);
        }

        throw new CodecConfigurationException("Unsupported generic type of container: " + type);
    }

}
