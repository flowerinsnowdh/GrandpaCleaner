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

import cn.flowerinsnow.grandpacleaner.GrandpaCleanerPlugin;
import cn.flowerinsnow.grandpacleaner.feature.RecycleBinGUI;
import cn.flowerinsnow.grandpacleaner.task.CleanTask;
import cn.flowerinsnow.grandpacleaner.util.LogUtil;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandGrandpaCleaner {
    public static void register(@NotNull GrandpaCleanerPlugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            commands.register(
                    Commands.literal("grandpacleaner")
                            .then(
                                    Commands.literal("reload")
                                            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("grandpacleaner.reload"))
                                            .executes(context -> {
                                                try {
                                                    plugin.reload();
                                                    plugin.getConfiguration().messages.command.reload.success.send(context.getSource().getSender());
                                                    return Command.SINGLE_SUCCESS;
                                                } catch (Exception e) {
                                                    LogUtil.throwing(plugin.getLogger(), e);
                                                    plugin.getConfiguration().messages.command.reload.failed.send(context.getSource().getSender());
                                                    return 0;
                                                }
                                            })
                            )
                            .then(
                                    Commands.literal("recycle")
                                            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("grandpacleaner.recycle") && plugin.getConfiguration().enable.getNotNull())
                                            .executes(context -> {
                                                RecycleBinGUI gui = RecycleBinGUI.ofRecycleBin(plugin.getCleanTask().getRecycleBin(), plugin.getCleanTask().recycleBinLock);
                                                gui.init();
                                                if (context.getSource().getExecutor() instanceof Player player) {
                                                    gui.openGUI(player);
                                                } else {
                                                    plugin.getConfiguration().messages.command.console.send(context.getSource().getSender());
                                                }
                                                return Command.SINGLE_SUCCESS;
                                            })
                            )
                            .then(
                                    Commands.literal("delay")
                                            .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("grandpacleaner.delay") && plugin.getConfiguration().enable.getNotNull())
                                            .executes(contenxt -> {
                                                CleanTask task = plugin.getCleanTask();
                                                task.setCountdown(task.getCountdown() + plugin.getConfiguration().delayTime.getNotNull());
                                                plugin.getConfiguration().messages.command.delayed.broadcast();
                                                return Command.SINGLE_SUCCESS;
                                            })
                            )
                            .build()
            );
        });
    }
}
