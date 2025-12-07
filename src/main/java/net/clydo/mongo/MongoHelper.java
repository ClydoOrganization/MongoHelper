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

package net.clydo.mongo;

import com.mongodb.client.MongoClient;
import net.clydo.mongo.operations.OperationsGroup;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface MongoHelper {

    @NotNull
    @Contract("_ -> new")
    static MongoHelper create(
            @NotNull final MongoClient client
    ) {
        return new MongoHelperImpl(client);
    }

    void register(
            @NotNull final OrmSchematic schematic
    );

    <S extends OrmSchematic> void register(
            @NotNull final Class<S> clazz
    );

    @NotNull
    <M> OperationsGroup<M> get(
            @NotNull final Class<M> clazz
    );

}
