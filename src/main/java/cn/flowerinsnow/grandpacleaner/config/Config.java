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
package cn.flowerinsnow.grandpacleaner.config;

import cc.carm.lib.configuration.core.Configuration;
import cc.carm.lib.configuration.core.annotation.ConfigPath;
import cc.carm.lib.configuration.core.annotation.HeaderComment;
import cc.carm.lib.configuration.core.value.type.ConfiguredList;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;
import cc.carm.lib.mineconfiguration.bukkit.value.ConfiguredMessageList;

public class Config implements Configuration {
    @HeaderComment("总开关")
    @ConfigPath("enable")
    public final ConfiguredValue<Boolean> enable = ConfiguredValue.of(Boolean.class, true);

    @HeaderComment({"清理周期", "单位：秒"})
    @ConfigPath("period")
    public final ConfiguredValue<Integer> period = ConfiguredValue.of(Integer.class, 300);

    @HeaderComment({"清理前 X 秒提醒", "单位：秒"})
    @ConfigPath("notify-on")
    public final ConfiguredList<Integer> notifyOn = ConfiguredList.of(
            Integer.class,
            180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1
    );

    @HeaderComment("排除物品列表")
    @ConfigPath("exclude-list")
    public final ConfiguredList<String> excludeList = ConfiguredList.of(
            String.class,
            "dragon_egg",
            "shulker_box", "white_shulker_box", "orange_shulker_box", "magenta_shulker_box", "light_blue_shulker_box", "yellow_shulker_box", "lime_shulker_box", "pink_shulker_box", "gray_shulker_box", "light_gray_shulker_box", "cyan_shulker_box", "purple_shulker_box", "blue_shulker_box", "brown_shulker_box", "green_shulker_box", "red_shulker_box", "black_shulker_box"
    );

    @HeaderComment({"每次使用命令推迟的时间", "单位：秒"})
    @ConfigPath("delay-time")
    public final ConfiguredValue<Integer> delayTime = ConfiguredValue.of(Integer.class, 60);

    @ConfigPath("messages")
    public final Messages messages = new Messages();

    public static class Messages implements Configuration {
        @ConfigPath("notify")
        public final ConfiguredMessageList<String> notify = ConfiguredMessageList.asStrings()
                .defaults("&7[&b!&7]&b地上的掉落物将在 &e%(countdown)&b 秒后被清除")
                .params("countdown")
                .build();

        @ConfigPath("cleaned")
        public final ConfiguredMessageList<String> cleaned = ConfiguredMessageList.asStrings()
                .defaults("&7[&b!&7]&b清除了 &e%(count)&b 个掉落物")
                .params("count")
                .build();

        @ConfigPath("command")
        public Command command = new Command();

        public static class Command implements Configuration {
            @ConfigPath("console")
            public final ConfiguredMessageList<String> console = ConfiguredMessageList.ofStrings("&7[&c!&7]&c必须是一名玩家才能这么做");

            @ConfigPath("reload")
            public Reload reload = new Reload();

            public static class Reload implements Configuration {
                @ConfigPath("success")
                public final ConfiguredMessageList<String> success = ConfiguredMessageList.ofStrings("&7[&b!&7]&b重载完成");
                @ConfigPath("failed")
                public final ConfiguredMessageList<String> failed = ConfiguredMessageList.ofStrings("&7[&c!&7]&c重载失败");
            }

            @ConfigPath("delayed")
            public final ConfiguredMessageList<String> delayed = ConfiguredMessageList.ofStrings("&7[&b!&7]&b本次掉落物清理已被推迟");
        }
    }
}
