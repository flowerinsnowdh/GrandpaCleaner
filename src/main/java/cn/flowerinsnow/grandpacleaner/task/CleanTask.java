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
package cn.flowerinsnow.grandpacleaner.task;

import cn.flowerinsnow.grandpacleaner.Main;
import cn.flowerinsnow.grandpacleaner.config.Config;
import cn.flowerinsnow.grandpacleaner.util.AtomicCounter;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Consumer;

public final class CleanTask implements Consumer<ScheduledTask> {
    @NotNull private final Main plugin;
    private final int period;
    @NotNull private final List<Integer> notifyOn;
    @NotNull private final List<String> excludeList;
    @NotNull private final Config.Messages messages;

    private int countdown;
    @NotNull public final Object lock = new Object();
    private int taskCounter;

    public CleanTask(@NotNull Main plugin, int period, @NotNull List<Integer> notifyOn, @NotNull List<String> excludeList, @NotNull Config.Messages messages) {
        this.plugin = plugin;
        this.period = period;
        this.notifyOn = Objects.requireNonNull(notifyOn);
        this.excludeList = Objects.requireNonNull(excludeList);
        this.messages = Objects.requireNonNull(messages);
        this.countdown = period;
    }

    @Contract(pure = true)
    public synchronized @Range(from = 0L, to = Integer.MAX_VALUE) int countdown() {
        return this.countdown;
    }

    @Contract("_ -> this")
    public synchronized @NotNull CleanTask countdown(@Range(from = 0L, to = Integer.MAX_VALUE) int countdown) {
        this.countdown = countdown;
        return this;
    }

    @Contract("-> this")
    public synchronized @NotNull CleanTask doCountdown() {
        this.countdown--;
        return this;
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
        if (this.notifyOn.contains(this.countdown())) {
            this.messages.notify.sendToAll(Bukkit.getOnlinePlayers(), this.countdown());
        }
        if (this.countdown() <= 0) {
            this.countdown(this.period);
            AtomicCounter itemThreadCounter = AtomicCounter.create(instance -> {
                CleanTask.this.messages.cleaned.item.sendToAll(Bukkit.getOnlinePlayers(), instance.extraCounter());
            });
            // 获取所有已加载的区块
            Bukkit.getWorlds().stream().flatMap(world -> Arrays.stream(world.getLoadedChunks()))
                    .forEach(chunk -> {
                        // 在每个已加载的区块的调度器上做清理
                        itemThreadCounter.countUp();
                        Bukkit.getRegionScheduler().run(CleanTask.this.plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), task -> {
                            try {
                                List<Item> list = Arrays.stream(chunk.getEntities()).filter(Item.class::isInstance)
                                        .map(Item.class::cast)
                                        .filter(item -> !CleanTask.this.plugin.getConfiguration().excludeList.getNotNull().contains(item.getItemStack().getType().key().value()))
                                        .toList();
                                list.forEach(Item::remove);
                                itemThreadCounter.extraCountUp(list.size());
                            } finally {
                                itemThreadCounter.countDown();
                            }
                        });
                    });
            itemThreadCounter.submitOver();

            AtomicCounter entityThreadCounter = AtomicCounter.create(instance -> {
                CleanTask.this.messages.cleaned.entities.sendToAll(Bukkit.getOnlinePlayers(), instance.extraCounter());
            });
            Bukkit.getWorlds().stream().flatMap(world -> Arrays.stream(world.getLoadedChunks()))
                    .forEach(chunk -> {
                        entityThreadCounter.countUp();
                        Bukkit.getRegionScheduler().run(CleanTask.this.plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), task -> {
                            try {
                                List<Mob> list = Arrays.stream(chunk.getEntities())
                                        .filter(Mob.class::isInstance)
                                        .map(Mob.class::cast)
                                        .filter(m -> {
                                            net.minecraft.world.entity.Mob mob = ((CraftMob) m).getHandle();
                                            return !mob.isPersistenceRequired() && !mob.requiresCustomPersistence() && mob.removeWhenFarAway(Double.MAX_VALUE);
                                        }).toList();
                                if (list.size() > CleanTask.this.plugin.getConfiguration().chunkLimit.getNotNull()) {
                                    list.forEach(Mob::remove);
                                    entityThreadCounter.extraCountUp(list.size());
                                }
                            } finally {
                                entityThreadCounter.countDown();
                            }
                        });
                    });
            entityThreadCounter.submitOver();
        } else {
            this.doCountdown();
        }
    }

    private void handleCleanOver() {

    }
}
