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

import cn.flowerinsnow.grandpacleaner.GrandpaCleanerPlugin;
import cn.flowerinsnow.grandpacleaner.config.Config;
import cn.flowerinsnow.grandpacleaner.feature.RecycleBin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CleanTask implements Consumer<ScheduledTask> {
    @NotNull private final GrandpaCleanerPlugin plugin;
    private final int period;
    @NotNull private final List<Integer> notifyOn;
    @NotNull private final List<String> excludeList;
    @NotNull private final Config.Messages messages;

    @NotNull private RecycleBin recycleBin;
    private int countdown;
    @NotNull public final Object recycleBinLock = new Object();
    private int counter;

    public CleanTask(@NotNull GrandpaCleanerPlugin plugin, int period, @NotNull List<Integer> notifyOn, @NotNull List<String> excludeList, @NotNull Config.Messages messages) {
        this.plugin = plugin;
        this.period = period;
        this.notifyOn = Objects.requireNonNull(notifyOn);
        this.excludeList = Objects.requireNonNull(excludeList);
        this.messages = Objects.requireNonNull(messages);
        this.recycleBin = new RecycleBin(new ArrayList<>());
        this.countdown = period;
    }

    public @NotNull RecycleBin getRecycleBin() {
        return this.recycleBin;
    }

    public synchronized int getCountdown() {
        return this.countdown;
    }

    public synchronized void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public synchronized void countdown() {
        this.countdown--;
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
        int countdown = this.getCountdown();
        if (this.notifyOn.contains(countdown)) {
            this.messages.notify.broadcast(countdown);
        }
        if (countdown <= 0) {
            this.setCountdown(this.period);
            this.recycleBin = new RecycleBin(new ArrayList<>());
            Bukkit.getWorlds().stream()
                    .map(World::getLoadedChunks)
                    .flatMap(Arrays::stream)
                    .forEach(chunk -> {
                        // 每启用一个线程就给计数器 +1，每结束一个线程就给计数器 -1，当计数器归零后代表一次清理结束
                        synchronized (CleanTask.this.recycleBinLock) {
                            CleanTask.this.counter++;
                        }
                        Bukkit.getRegionScheduler().run(CleanTask.this.plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), task -> {
                            ArrayList<ItemStack> itemStacks = Arrays.stream(chunk.getEntities())
                                    .filter(entity -> entity instanceof Item)
                                    .map(entity -> (Item) entity)
                                    .filter(item -> !CleanTask.this.excludeList.contains(item.getItemStack().getType().getKey().getKey()))
                                    .collect(
                                            ArrayList::new,
                                            (list, item) -> {
                                                list.add(item.getItemStack().clone());
                                                item.remove();
                                            },
                                            ArrayList::addAll
                                    );
                            synchronized (CleanTask.this.recycleBinLock) {
                                CleanTask.this.recycleBin.items().addAll(itemStacks);
                                if (--CleanTask.this.counter == 0) {
                                    this.messages.cleaned.broadcast(CleanTask.this.recycleBin.items().size());
                                }
                            }
                        });
                    });
        } else {
            this.countdown();
        }
    }
}
