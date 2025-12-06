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

package net.clydo.mongo.meta.model.index;

import lombok.val;
import net.clydo.clytil.Validates;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class IndexMetaMap {

    private final static IndexMeta ID_INDEX = new IdIndexMeta();

    private final Map<String, IndexMeta> indices;
    private final Map<String, IndexMeta> uniques;

    public IndexMetaMap() {
        this.indices = new LinkedHashMap<>();
        this.uniques = new LinkedHashMap<>();

        this.indices.put(ID_INDEX.getIndexName(), ID_INDEX);
        this.uniques.put(ID_INDEX.getName(), ID_INDEX);
    }

    public void put(
            @NotNull final Class<?> clazz,
            @NotNull final IndexMeta indexMeta
    ) {
        val indexName = indexMeta.getIndexName();
        if (this.indices.put(indexName, indexMeta) != null) {
            throw new IllegalStateException("Duplicate @OrmUnique/OrmModelUnique/OrmModelIndex map: '" + indexName + "' in class " + clazz.getName());
        }

        val name = indexMeta.getName();
        if (name != null) {
            if (this.uniques.put(name, indexMeta) != null) {
                throw new IllegalStateException("Duplicate @OrmUnique/OrmModelUnique name: '" + name + "' in class " + clazz.getName());
            }
        }
    }

    @NotNull
    public IndexMeta getUnique(
            @NotNull final String indexName
    ) {
        return Validates.requireMsg(
                this.uniques.get(indexName),
                "No @OrmUnique/OrmModelUnique mapping found for index name '" + indexName
        );
    }

    @NotNull
    public Collection<IndexMeta> indices() {
        return this.indices.values();
    }

    @NotNull
    public Collection<IndexMeta> uniques() {
        return this.uniques.values();
    }

}
