/* Copyright (C) 2025 flowerinsnow
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
package cn.flowerinsnow.grandpacleaner.command;

import cn.flowerinsnow.grandpacleaner.Main;
import cn.flowerinsnow.grandpacleaner.task.CleanTask;
import cn.flowerinsnow.grandpacleaner.util.LogUtil;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class CommandGrandpaCleaner {
    public static void register(@NotNull Main plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            commands.register(
                    Commands.literal("grandpacleaner")
                            .then(Commands.literal("reload")
                                            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("grandpacleaner.reload"))
                                            .executes(context -> {
                                                try {
                                                    plugin.reload();
                                                    plugin.getConfiguration().messages.command.reload.success.sendTo(context.getSource().getSender());
                                                } catch (Exception e) {
                                                    LogUtil.throwing(plugin.getLogger(), e);
                                                    plugin.getConfiguration().messages.command.reload.failed.sendTo(context.getSource().getSender());
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            })
                            ).then(
                                    Commands.literal("delay")
                                            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("grandpacleaner.delay") && plugin.getConfiguration().enable.getNotNull())
                                            .executes(contenxt -> {
                                                CleanTask task = plugin.getCleanTask();
                                                task.countdown(task.countdown() + plugin.getConfiguration().delayTime.getNotNull());
                                                plugin.getConfiguration().messages.command.delayed.sendToAll(Bukkit.getOnlinePlayers());
                                                return Command.SINGLE_SUCCESS;
                                            })
                            ).build()
            );
        });
    }
}
