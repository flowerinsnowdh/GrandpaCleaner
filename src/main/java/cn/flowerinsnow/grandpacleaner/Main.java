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
package cn.flowerinsnow.grandpacleaner;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.mineconfiguration.bukkit.MineConfiguration;
import cc.carm.lib.mineconfiguration.bukkit.source.BukkitSource;
import cn.flowerinsnow.grandpacleaner.command.CommandGrandpaCleaner;
import cn.flowerinsnow.grandpacleaner.config.Config;
import cn.flowerinsnow.grandpacleaner.task.CleanTask;
import cn.flowerinsnow.grandpacleaner.util.LogUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin {
    private ConfigurationHolder<BukkitSource> configProvider;
    private Config config;

    private CleanTask cleanTask;
    private ScheduledTask cleanTaskTask;

    @Override
    public void onLoad() {
        this.configProvider = MineConfiguration.from(this, "config.yml");
        this.configProvider.initialize(this.config = new Config());
    }

    @Override
    public void onEnable() {
        try {
            this.reload();
        } catch (Exception e) {
            LogUtil.throwing(this.getLogger(), e);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        CommandGrandpaCleaner.register(this);
    }

    @Override
    public void onDisable() {
        if (this.cleanTaskTask != null) {
            this.cleanTaskTask.cancel();
            this.cleanTaskTask = null;
        }
    }

    public @NotNull Config getConfiguration() {
        return this.config;
    }

    public @NotNull CleanTask getCleanTask() {
        return this.cleanTask;
    }

    public void reload() throws Exception {
        this.configProvider.reload();

        if (this.cleanTaskTask != null) {
            this.cleanTaskTask.cancel();
            this.cleanTaskTask = null;
            this.cleanTask = null;
        }

        if (this.config.enable.getNotNull()) {
            this.cleanTask = new CleanTask(this, this.config.period.getNotNull(), this.config.notifyOn, this.config.excludeList, this.config.messages);
            this.cleanTaskTask = this.getServer().getGlobalRegionScheduler().runAtFixedRate(this, this.cleanTask, 1L, 20L);
        }
    }
}
