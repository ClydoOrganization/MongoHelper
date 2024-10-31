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

import com.mongodb.DocumentToDBRefTransformer;
import lombok.val;
import net.clydo.mongodb.MongoHelpers;
import net.clydo.mongodb.loader.LoaderRegistry;
import net.clydo.mongodb.schematic.MongoSchemaHelper;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ClassCodecProvider implements CodecProvider {
    private static final DocumentToDBRefTransformer TRANSFORMER = new DocumentToDBRefTransformer();

    private final MongoSchemaHelper schemaHelper;
    private final LoaderRegistry registry;

    public ClassCodecProvider(final MongoSchemaHelper schemaHelper, final LoaderRegistry registry) {
        this.schemaHelper = schemaHelper;
        this.registry = registry;
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return this.get(clazz, Collections.emptyList(), registry);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
        if (Enum.class.isAssignableFrom(clazz)) {
            val encoder = this.registry.getEnum(clazz);
            if (encoder == null) {
                return null;
            }
            return (Codec<T>) new EnumCodec(clazz, encoder);
        }

        val typeHolder = this.registry.getModelOrType(clazz);
        if (typeHolder == null) {
            return null;
        }

        return new TypeCodec<>(registry, MongoHelpers.getDefaultBsonTypeClassMap(), TRANSFORMER, clazz, typeHolder, this.schemaHelper, this.registry);
    }
}
