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

package net.clydo.mongo.visitor.impl.enm;

import lombok.val;
import net.clydo.clytil.Validates;
import net.clydo.clytil.reflect.Annotations;
import net.clydo.clytil.reflect.Fields;
import net.clydo.mongo.annotations.OrmMap;
import net.clydo.mongo.util.LoopControl;
import net.clydo.mongo.visitor.FieldVisitor;
import net.clydo.mongo.visitor.builder.EnumMetaBuilder;
import net.clydo.mongo.visitor.impl.BaseVisitor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class EnumFieldVisitorImpl extends BaseVisitor<EnumMetaBuilder> implements FieldVisitor {

    public EnumFieldVisitorImpl(
            @NotNull final EnumMetaBuilder builder
    ) {
        super(builder);
    }

    @NotNull
    @Override
    public LoopControl visitField(
            @NotNull final Class<?> clazz,
            @NotNull final Field field
    ) {
        Validates.require(clazz, "clazz");
        Validates.require(field, "field");

        val ormMap = Annotations.get(field, OrmMap.class);
        if (!field.isEnumConstant()) {
            if (ormMap != null) {
                throw new IllegalStateException(
                        "Field " + field + " is annotated with @OrmMap but is not an enum constant."
                );
            }
            return LoopControl.CONTINUE;
        }

        val mapped = ormMap != null
                ? ormMap.value()
                : field.getName();
        val constant = Fields.get(clazz, field, null);

        this.builder.put(clazz, mapped, constant);

        return LoopControl.CONTINUE;
    }

}
