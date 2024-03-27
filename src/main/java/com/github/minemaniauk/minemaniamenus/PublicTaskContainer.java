/*
 * MineManiaMenus
 * Used for interacting with the database and message broker.
 *
 * Copyright (C) 2023  MineManiaUK Staff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.minemaniauk.minemaniamenus;

import com.github.kerbity.kerb.task.TaskContainer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PublicTaskContainer extends TaskContainer {

    private static PublicTaskContainer instance;

    public @NotNull TaskContainer runTask(@NotNull Runnable runnable, @NotNull Duration duration, @NotNull String identifier) {
        return super.runTask(runnable, duration, identifier);
    }

    public @NotNull TaskContainer stopTask(@NotNull String identifier) {
        return super.stopTask(identifier);
    }

    public static @NotNull PublicTaskContainer getInstance() {
        if (PublicTaskContainer.instance == null) {
            PublicTaskContainer.instance = new PublicTaskContainer();
        }

        return new PublicTaskContainer();
    }
}
