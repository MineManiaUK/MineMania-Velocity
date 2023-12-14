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

package com.github.minemaniauk.minemaniamenus.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of command aliases.
 */
public class CommandAliases {

    private final List<String> aliases = new ArrayList<>();

    /**
     * Used to append an alias to the list.
     *
     * @param alias The name of the command alias.
     * @return The instance of the aliases.
     */
    public CommandAliases append(String alias) {
        this.aliases.add(alias);

        return this;
    }

    /**
     * Used to append multiple aliases to the list.
     *
     * @param aliases A list of aliases.
     * @return The instance of the aliases.
     */
    public CommandAliases append(List<String> aliases) {
        this.aliases.addAll(aliases);

        return this;
    }

    /**
     * Used to get the aliases as a list.
     *
     * @return The list of aliases.
     */
    public List<String> get() {
        return this.aliases;
    }

    /**
     * Used to check if a name is contained in this list.
     *
     * @param name The name of the command.
     * @return True if it is an alias.
     */
    public boolean contains(@NotNull String name) {
        return this.aliases.contains(name);
    }
}
