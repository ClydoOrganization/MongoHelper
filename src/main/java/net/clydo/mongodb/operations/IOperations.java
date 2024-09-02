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

package net.clydo.mongodb.operations;

import net.clydo.mongodb.loader.classes.values.MongoMutableField;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface IOperations<M> {
    @NotNull String firstUniqueFieldName();

    @NotNull HashMap<String, MongoMutableField> fields();

    Object[] getFieldValues(HashMap<String, MongoMutableField> fields, @NotNull M @NotNull [] data, @NotNull String fieldName);

    Object getFieldValue(@NotNull HashMap<String, MongoMutableField> fields, @NotNull M datum, @NotNull String fieldName);
}
