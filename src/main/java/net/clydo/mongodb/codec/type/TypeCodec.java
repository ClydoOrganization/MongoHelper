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
 *
 */

package net.clydo.mongodb.codec.type;

import lombok.val;
import net.clydo.mongodb.codec.CodecsHelper;
import net.clydo.mongodb.loader.classes.values.ClassCacheValue;
import net.clydo.mongodb.util.Primitives;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.assertions.Assertions;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class TypeCodec<T> implements Codec<T> {
    private final CodecRegistry registry;
    private final ClassCacheValue typeHolder;
    private final Class<T> clazz;
    private final Supplier<T> supplier;
    private final BsonTypeCodecMap bsonTypeCodecMap;
    private final Transformer transformer;

    @Contract(pure = true)
    public TypeCodec(CodecRegistry registry, final BsonTypeClassMap bsonTypeClassMap, Transformer transformer, @NotNull Class<T> clazz, @NotNull ClassCacheValue typeHolder) {
        this.registry = registry;
        this.clazz = clazz;
        this.typeHolder = typeHolder;
        this.bsonTypeCodecMap = new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry);
        this.transformer = transformer;

        Supplier<T> supplier;
        try {
            val constructor = clazz.getDeclaredConstructor();
            supplier = () -> {
                try {
                    return (T) constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new CodecConfigurationException(String.format("Can not invoke no-args constructor for class %s", clazz),
                            e);
                }
            };
        } catch (NoSuchMethodException e) {
            supplier = () -> {
                throw new CodecConfigurationException(String.format("No no-args constructor for class %s", clazz), e);
            };
        }
        this.supplier = supplier;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        val result = this.supplier.get();
        reader.readStartDocument();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            val fieldName = reader.readName();

            if (reader.getCurrentBsonType() == BsonType.NULL) {
                reader.readNull();
            } else {
                Class<?> type = null;
                val mongoFieldHolder = this.typeHolder.fields().get(fieldName);

                if (mongoFieldHolder != null) {
                    type = Primitives.wrap(mongoFieldHolder.fieldType());
                }

                val value = CodecsHelper.readValue(reader, decoderContext, this.bsonTypeCodecMap, null, this.registry, this.transformer, type);

                if (mongoFieldHolder != null) {
                    mongoFieldHolder.set(result, type.cast(value));
                }
            }
        }

        reader.readEndDocument();

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(@NotNull BsonWriter writer, T object, EncoderContext encoderContext) {
        writer.writeStartDocument();

        for (val entry : this.typeHolder.fields().entrySet()) {
            val fieldName = entry.getKey();
            if ("_id".equals(fieldName)) {
                continue;
            }

            val fieldHolder = entry.getValue();
            val field = fieldHolder.field();

            writer.writeName(fieldName);
            val value = fieldHolder.get(object);
            if (value == null) {
                writer.writeNull();
            } else {
                val codec = (Codec<Object>) CodecsHelper.getCodec(this.registry, Primitives.wrap(field.getType()));
                encoderContext.encodeWithChildContext(codec, writer, value);
            }
        }

        writer.writeEndDocument();
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.clazz;
    }
}
