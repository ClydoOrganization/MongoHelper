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

package net.clydo.mongo.visitor.impl.model;

import lombok.val;
import net.clydo.clytil.Nulls;
import net.clydo.mongo.annotations.OrmModelIndex;
import net.clydo.mongo.annotations.OrmModelUnique;
import net.clydo.mongo.annotations.OrmUnique;
import net.clydo.mongo.meta.model.index.NonUniqueIndexMeta;
import net.clydo.mongo.meta.model.index.UniqueIndexMeta;
import net.clydo.mongo.meta.type.field.FieldMeta;
import net.clydo.mongo.util.SortOrder;
import net.clydo.mongo.visitor.ClassVisitor;
import net.clydo.mongo.visitor.builder.ModelMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class IndexVisitorImpl extends BaseVisitor<ModelMetaBuilder> implements ClassVisitor {

    public IndexVisitorImpl(
            @NotNull final ModelMetaBuilder builder
    ) {
        super(builder);
    }

    @Override
    public void visitClassEnd(
            @NotNull final Class<?> clazz
    ) {
        val collectionName = this.builder.getCollectionName();
        val fieldMetaMap = this.builder.getFieldMetaMap();

        for (val field : fieldMetaMap.values()) {
            val ormUnique = field.getResolver().resolveOrmUnique();
            if (ormUnique == null) {
                continue;
            }

            val order = Nulls.mapOrDefault(ormUnique, OrmUnique::sort, SortOrder.ASCENDING);
            val mapped = Nulls.map(ormUnique, OrmUnique::map);

            this.builder.addIndex(
                    clazz,
                    new UniqueIndexMeta(
                            collectionName,
                            order,
                            field.getName(),
                            mapped
                    )
            );
        }

        val ormModelUniques = clazz.getAnnotationsByType(OrmModelUnique.class);
        for (val ormModelUnique : ormModelUniques) {
            val fieldsName = Arrays.stream(ormModelUnique.value())
                    .map(fieldMetaMap::getByAlias)
                    .map(FieldMeta::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            this.builder.addIndex(
                    clazz,
                    new UniqueIndexMeta(
                            collectionName,
                            SortOrder.ASCENDING,
                            fieldsName,
                            ormModelUnique.map(),
                            ormModelUnique.name()
                    )
            );
        }

        val ormModelIndices = clazz.getAnnotationsByType(OrmModelIndex.class);
        for (val ormModelIndex : ormModelIndices) {
            val fieldsName = Arrays.stream(ormModelIndex.value())
                    .map(fieldMetaMap::getByAlias)
                    .map(FieldMeta::getName)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            this.builder.addIndex(
                    clazz,
                    new NonUniqueIndexMeta(
                            collectionName,
                            SortOrder.ASCENDING,
                            fieldsName,
                            ormModelIndex.map()
                    )
            );
        }
    }

}
