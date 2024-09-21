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

package net.clydo.mongodb.util;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class DevUtil {
    private long startTime;
    private boolean profiling = false;

    public void push() {
        if (profiling) {
            System.out.println("DevUtil Started before");
        }
        DevUtil.profiling = true;
        DevUtil.startTime = System.currentTimeMillis();
    }

    public void pop() {
        val elapsedTime = System.currentTimeMillis() - DevUtil.startTime;
        System.out.println("Elapsed Time : " + elapsedTime + "ms");
        DevUtil.profiling = false;
    }
}
