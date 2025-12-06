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

import com.mongodb.DocumentToDBRefTransformer;
import lombok.val;
import net.clydo.clytil.Primitives;
import net.clydo.mongo.meta.type.TypeMeta;
import net.clydo.mongo.meta.type.field.FieldMeta;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypeMetaCodec<T> implements Codec<T> {

    private static final DocumentToDBRefTransformer TRANSFORMER = new DocumentToDBRefTransformer();

    private final Class<T> clazz;
    private final TypeMeta<T> typeMeta;
    private final CodecRegistry codecRegistry;
    private final BsonTypeCodecMap bsonTypeCodecMap;

    public TypeMetaCodec(
            @NotNull final Class<T> clazz,
            @NotNull final TypeMeta<T> type,
            @NotNull final CodecRegistry codecRegistry,
            @NotNull final BsonTypeClassMap bsonTypeClassMap
    ) {
        this.clazz = clazz;
        this.typeMeta = type;
        this.codecRegistry = codecRegistry;
        this.bsonTypeCodecMap = new BsonTypeCodecMap(bsonTypeClassMap, codecRegistry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(
            @NotNull final BsonWriter writer,
            @NotNull final T owner,
            @NotNull final EncoderContext encoderContext
    ) {
        writer.writeStartDocument();

        val fields = this.typeMeta.fieldMetaMap();

        for (val name : fields.keys()) {
            val mongoField = fields.get(name);

            val value = mongoField.get(owner);
            if (value == null) {
                writer.writeNull(name);
                continue;
            }

            val genericType = Primitives.wrap(mongoField.getGenericType());

            writer.writeName(name);
            val codec = (Codec<Object>) CodecsHelper.getCodec(this.codecRegistry, genericType);
            encoderContext.encodeWithChildContext(codec, writer, value);
        }

        writer.writeEndDocument();
    }

    @Override
    public T decode(
            @NotNull final BsonReader reader,
            @NotNull final DecoderContext decoderContext
    ) {
        val constructor = this.typeMeta.constructor();
        val onLoad = this.typeMeta.onLoad();

        val requiredFields = constructor.requiredFields();
        if (requiredFields != null && !requiredFields.isEmpty()) {
            val values = new Object[requiredFields.size()];

            this.walk(
                    reader,
                    decoderContext,
                    (fieldMeta, value) -> {
                        val name = fieldMeta.getName();
                        if (!requiredFields.contains(name)) {
                            return;
                        }

                        val index = requiredFields.indexOf(name);
                        values[index] = value;
                    }
            );

            val result = constructor.newInstance(values);
            if (onLoad != null) {
                onLoad.invoke(result);
            }
            return result;
        } else {
            val result = constructor.newInstance();

            this.walk(
                    reader,
                    decoderContext,
                    (fieldMeta, value) -> fieldMeta.set(result, value)
            );

            if (onLoad != null) {
                onLoad.invoke(result);
            }
            return result;
        }
    }

    private void walk(
            @NotNull final BsonReader reader,
            @NotNull final DecoderContext decoderContext,
            @NotNull final FieldVisitor visitor
    ) {
        reader.readStartDocument();

        val fields = this.typeMeta.fieldMetaMap();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            if (reader.getCurrentBsonType() == BsonType.NULL) {
                reader.readNull();
                continue;
            }

            val name = reader.readName();
            val mongoField = fields.getByAliasNullable(name);

            val type = mongoField != null
                    ? Primitives.wrap(mongoField.getGenericType())
                    : null;

            val value = CodecsHelper.readValue(
                    reader,
                    this.codecRegistry,
                    this.bsonTypeCodecMap,
                    decoderContext,
                    TRANSFORMER,
                    null,
                    type
            );

            if (mongoField != null) {
                visitor.visit(mongoField, value);
            }
        }

        reader.readEndDocument();
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.clazz;
    }

    @FunctionalInterface
    private interface FieldVisitor {

        void visit(
                @NotNull final FieldMeta fieldMeta,
                @Nullable final Object value
        );

    }

}
