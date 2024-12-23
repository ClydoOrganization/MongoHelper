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

import lombok.val;
import net.clydo.mongodb.annotations.MongoConstructor;
import net.clydo.mongodb.annotations.MongoParameter;
import net.clydo.mongodb.codec.CodecsHelper;
import net.clydo.mongodb.loader.LoaderRegistry;
import net.clydo.mongodb.loader.classes.values.ClassCacheValue;
import net.clydo.mongodb.loader.classes.values.MongoMutableField;
import net.clydo.mongodb.schematic.MongoSchemaHelper;
import net.clydo.mongodb.util.Primitives;
import net.clydo.mongodb.util.ReflectionUtil;
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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

public class TypeCodec<T> implements Codec<T> {
    private final CodecRegistry registry;
    private final ClassCacheValue<?> typeHolder;
    private final Class<T> clazz;
    private final TypeSupplier<T> supplier;
    private final BsonTypeCodecMap bsonTypeCodecMap;
    private final Transformer transformer;
    @Nullable
    private final LinkedList<String> requiredFields;

    @Contract(pure = true)
    public TypeCodec(CodecRegistry registry, final BsonTypeClassMap bsonTypeClassMap, Transformer transformer, @NotNull Class<T> clazz, @NotNull ClassCacheValue<?> typeHolder, MongoSchemaHelper schemaHelper, LoaderRegistry loaderRegistry) {
        this.registry = registry;
        this.clazz = clazz;
        this.typeHolder = typeHolder;
        this.bsonTypeCodecMap = new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry);
        this.transformer = transformer;

        val mongoConstructor = new MongoConstructor[1];
        val constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constr -> (mongoConstructor[0] = ReflectionUtil.getAnnotation(constr, MongoConstructor.class, true)) != null)
                .reduce((a, b) -> {
                    throw new IllegalStateException("More than one constructor with @MongoConstructor annotation found");
                })
                .orElse(null);

        TypeSupplier<T> supplier;
        if (constructor != null) {
            this.requiredFields = new LinkedList<>();
            val fields = this.typeHolder.fields();

            val parameters = constructor.getParameters();
            for (Parameter parameter : parameters) {
                val mongoParameter = ReflectionUtil.getAnnotation(parameter, MongoParameter.class);
                if (mongoParameter == null) {
                    throw new IllegalStateException("All of parameters must be annotated with @MongoParameter");
                }

                val fieldName = mongoParameter.value();

                val mongoFieldHolder = fields.get(fieldName);
                if (mongoFieldHolder == null) {
                    throw new IllegalStateException("No such field: " + fieldName);
                }

                if (!Objects.equals(Primitives.wrap(mongoFieldHolder.genericType()), Primitives.wrap(parameter.getParameterizedType()))) {
                    throw new IllegalStateException(
                            "Mismatch between parameter type and @MongoField field type for field '" + fieldName +
                                    "'. Expected: " + mongoFieldHolder.genericType() + ", but found: " + parameter.getParameterizedType()
                    );
                }

                this.requiredFields.add(fieldName);
            }

            if (mongoConstructor[0] != null && mongoConstructor[0].requiredAll()) {
                val allFieldsMatch = this.requiredFields.size() == fields.size() && this.requiredFields.containsAll(fields.keySet());

                if (!allFieldsMatch) {
                    throw new IllegalStateException(
                            "All fields annotated with @MongoField must also be present as parameters in the constructor and annotated with @MongoParameter."
                    );
                }
            }

            supplier = (args) -> {
                try {
                    //noinspection unchecked
                    return (T) constructor.newInstance(args);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new CodecConfigurationException(String.format("Can not invoke constructor for class %s", clazz), e);
                }
            };
        } else {
            this.requiredFields = null;

            try {
                val noArgsConstructor = clazz.getDeclaredConstructor();
                supplier = (args) -> {
                    try {
                        return (T) noArgsConstructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new CodecConfigurationException(String.format("Can not invoke no-args constructor for class %s", clazz), e);
                    }
                };
            } catch (NoSuchMethodException e) {
                supplier = (args) -> {
                    throw new CodecConfigurationException(String.format("No no-args constructor for class %s", clazz), e);
                };
            }
        }
        this.supplier = supplier;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        final T result;
        if (this.requiredFields != null) {
            val values = new ArrayList<>(Collections.nCopies(this.requiredFields.size(), null));

            while (true) {
                val isDoc = this.processField(reader, decoderContext, (fieldName, value, mongoFieldHolder) -> {
                    if (!this.requiredFields.contains(fieldName)) {
                        return;
                    }

                    val index = this.requiredFields.indexOf(fieldName);
                    values.set(index, value);
                });
                if (isDoc) break;
            }

            result = this.supplier.get(values.toArray());
        } else {
            result = this.supplier.get();

            while (true) {
                val isDoc = this.processField(reader, decoderContext, (fieldName, value, mongoFieldHolder) -> {
                    if (mongoFieldHolder == null) {
                        return;
                    }

                    mongoFieldHolder.set(result, value);
                });
                if (isDoc) break;
            }
        }

        reader.readEndDocument();

        return result;
    }

    private boolean processField(
            @NotNull BsonReader reader,
            DecoderContext decoderContext,
            FieldProcessor processor
    ) {
        val isEndDocument = reader.readBsonType() == BsonType.END_OF_DOCUMENT;
        if (isEndDocument) {
            return true;
        }

        val fieldName = reader.readName();
        val fieldHolder = this.typeHolder.fields().get(fieldName);

        if (reader.getCurrentBsonType() != BsonType.NULL) {
            Type type = null;
            if (fieldHolder != null) {
                type = Primitives.wrap(fieldHolder.genericType());
            }

            val value = CodecsHelper.readValue(
                    reader, this.registry, this.bsonTypeCodecMap, decoderContext, this.transformer, null, type, null
            );

            processor.process(fieldName, this.castIfNeeded(type, value), fieldHolder);
        } else {
            reader.readNull();
        }

        return false;
    }

    private Object castIfNeeded(Type type, Object value) {
        if (type instanceof Class<?> aClass) {
            return aClass.cast(value);
        } else {
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void encode(@NotNull BsonWriter writer, T object, EncoderContext encoderContext) {
        writer.writeStartDocument();

        for (val entry : this.typeHolder.fields().entrySet()) {
            var fieldName = entry.getKey();
            if ("_id".equals(fieldName)) {
                continue;
            }

            val fieldHolder = entry.getValue();

            final Object[] value = {fieldHolder.get(object)};
            if (value[0] != null) {
                val fieldGenericType = Primitives.wrap(fieldHolder.genericType());

                writer.writeName(fieldName);
                val codec = (Codec<Object>) CodecsHelper.getCodec(this.registry, Primitives.wrap(fieldGenericType));

                encoderContext.encodeWithChildContext(codec, writer, value[0]);

                continue;
            }

            writer.writeNull(fieldName);
        }

        writer.writeEndDocument();
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.clazz;
    }

    @FunctionalInterface
    private interface FieldProcessor {
        void process(String fieldName, Object value, MongoMutableField mongoFieldHolder);
    }

    private interface TypeSupplier<T> {
        T get(Object... args);
    }

}
