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

package net.clydo.mongo.visitor.builder;

import lombok.Getter;
import lombok.Setter;
import net.clydo.mongo.meta.model.ModelMeta;
import net.clydo.mongo.meta.model.ModelMetaImpl;
import net.clydo.mongo.meta.model.index.IndexMeta;
import net.clydo.mongo.meta.model.index.IndexMetaMap;
import org.jetbrains.annotations.NotNull;

public class ModelMetaBuilder extends TypeMetaBuilder {

    @Setter
    @Getter
    private String collectionName;
    private IndexMetaMap indexMetaMap;

    public void addIndex(
            @NotNull final Class<?> clazz,
            @NotNull final IndexMeta indexMeta
    ) {
        this.indexMetaMap.put(clazz, indexMeta);
    }

    @Override
    public void reset() {
        super.reset();
        this.collectionName = null;
        this.indexMetaMap = new IndexMetaMap();
    }

    @NotNull
    @Override
    public ModelMeta<?> build() {
        return (ModelMeta<?>) super.build();
    }

    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ModelMeta<?> buildInternal() {
        return new ModelMetaImpl(
                this.collectionName,
                this.fieldMetaMap,
                this.indexMetaMap,
                this.constructorMeta,
                this.afterLoad
        );
    }

}
