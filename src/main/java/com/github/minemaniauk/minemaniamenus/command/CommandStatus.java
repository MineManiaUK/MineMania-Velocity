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

import com.github.minemaniauk.minemaniamenus.User;
import com.github.minemaniauk.minemaniamenus.configuration.ConfigMessages;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>Represents a command's status.</h1>
 * Returned after a command is executed.
 */
public class CommandStatus {

    private boolean hasError = false;
    private boolean hasIncorrectArguments = false;
    private boolean hasDatabaseDisabled = false;
    private boolean hasDatabaseEmpty = false;
    private boolean hasPlayerCommand = false;
    private boolean hasNoPermission = false;
    private boolean hasIsLimited = false;

    private boolean hasStopIncreaseLimit = false;

    /**
     * Used to set error to true.
     *
     * @return This instance.
     */
    public CommandStatus error() {
        this.hasError = true;
        return this;
    }

    /**
     * Used to set incorrect arguments to true.
     *
     * @return This instance.
     */
    public CommandStatus incorrectArguments() {
        this.hasIncorrectArguments = true;
        return this;
    }

    /**
     * Used to set database disabled to true.
     *
     * @return This instance.
     */
    public CommandStatus databaseDisabled() {
        this.hasDatabaseDisabled = true;
        return this;
    }

    /**
     * Used to set database empty to true.
     *
     * @return This instance.
     */
    public CommandStatus databaseEmpty() {
        this.hasDatabaseEmpty = true;
        return this;
    }

    /**
     * Used to set player command to true.
     *
     * @return This instance.
     */
    public CommandStatus playerCommand() {
        this.hasPlayerCommand = true;
        return this;
    }

    /**
     * Used to set no permission to true.
     *
     * @return This instnace.
     */
    public CommandStatus noPermission() {
        this.hasNoPermission = true;
        return this;
    }

    /**
     * Used to set is limited to true.
     *
     * @return This instance.
     */
    public CommandStatus isLimited() {
        this.hasIsLimited = true;
        return this;
    }

    /**
     * Used to stop the limit from increasing.
     *
     * @return This instance.
     */
    public CommandStatus stopIncreaseLimit() {
        this.hasStopIncreaseLimit = true;
        return this;
    }

    /**
     * Used to get if an error occurred and an error
     * message should be sent.
     *
     * @return True if an error occurred.
     */
    public boolean hasError() {
        return this.hasError;
    }

    /**
     * Used to get if the command sender has provided
     * the correct arguments.
     *
     * @return True if the arguments are invalid.
     */
    public boolean hasIncorrectArguments() {
        return this.hasIncorrectArguments;
    }

    /**
     * Used to get if the database is disabled.
     *
     * @return True if the database is disabled;
     */
    public boolean hasDatabaseDisabled() {
        return this.hasDatabaseDisabled;
    }

    /**
     * Used to get if the database was empty.
     *
     * @return True if the database was empty.
     */
    public boolean hasDatabaseEmpty() {
        return this.hasDatabaseEmpty;
    }

    /**
     * Used to get if the command is only for the player to run.
     *
     * @return True if it's a player only command.
     */
    public boolean hasPlayerCommand() {
        return this.hasPlayerCommand;
    }

    /**
     * Used to get if the user doesn't have permission.
     *
     * @return True if the player doesn't have permission.
     */
    public boolean hasNoPermission() {
        return this.hasNoPermission;
    }

    /**
     * Used to check if the user has been limited.
     *
     * @return True if the player has been limited.
     */
    public boolean hasIsLimited() {
        return this.hasIsLimited;
    }

    /**
     * Used to check if the increase of the command limit
     * should be stopped.
     *
     * @return True if the increase of the command limit
     * should be stopped.
     */
    public boolean hasStopIncreaseLimit() {
        return this.hasStopIncreaseLimit;
    }

    /**
     * Used to get the error message.
     *
     * @return The message.
     */
    public String getMessage() {
        if (this.hasError()) return ConfigMessages.getError();
        if (this.hasDatabaseDisabled()) return ConfigMessages.getDatabaseDisabled();
        if (this.hasDatabaseEmpty()) return ConfigMessages.getDatabaseEmpty();
        if (this.hasPlayerCommand()) return ConfigMessages.getPlayerCommand();
        if (this.hasNoPermission()) return ConfigMessages.getNoPermission();
        if (this.hasIsLimited()) return ConfigMessages.getIsLimited();
        return null;
    }
}
