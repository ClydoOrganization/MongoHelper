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

package net.clydo.mongodb.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@UtilityClass
public class MongoUtil {
    public <T> T make(@NotNull Supplier<T> supplier) {
        return supplier.get();
    }

    public <T> BsonDocument toBsonDocument(@NotNull CodecRegistry codecRegistry, Class<T> type, T instance) {
        val document = new BsonDocument();
        val writer = new BsonDocumentWriter(document);
        codecRegistry.get(type).encode(writer, instance, EncoderContext.builder().build());
        return document;
    }
}
