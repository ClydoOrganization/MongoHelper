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

package net.clydo.mongo.visitor.impl.type;

import lombok.val;
import net.clydo.clytil.reflect.Annotations;
import net.clydo.mongo.annotations.OrmField;
import net.clydo.mongo.meta.type.field.AccessorResolver;
import net.clydo.mongo.meta.type.field.FieldMetaImpl;
import net.clydo.mongo.util.LoopControl;
import net.clydo.mongo.visitor.FieldVisitor;
import net.clydo.mongo.visitor.builder.TypeMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class TypeFieldVisitorImpl extends BaseVisitor<TypeMetaBuilder> implements FieldVisitor {

    public TypeFieldVisitorImpl(
            @NotNull final TypeMetaBuilder builder
    ) {
        super(builder);
    }

    @NotNull
    @Override
    public LoopControl visitField(
            @NotNull final Class<?> clazz,
            @NotNull final Field field
    ) {
        val ormField = Annotations.get(field, OrmField.class);
        if (ormField == null) {
            return LoopControl.CONTINUE;
        }

        val name = ormField.value();
        val type = field.getType();

        if ("_id".equals(name) && type != ObjectId.class) {
            throw new IllegalStateException(String.format(
                    "Field '_id' must be of type %s, but found: %s",
                    ObjectId.class.getName(),
                    type.getName()
            ));
        }

        this.builder.addField(
                clazz,
                new FieldMetaImpl(
                        name,
                        new AccessorResolver(clazz, field)
                )
        );

        return LoopControl.CONTINUE;
    }

}
