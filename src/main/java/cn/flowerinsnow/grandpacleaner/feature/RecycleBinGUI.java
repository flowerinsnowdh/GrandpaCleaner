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
package cn.flowerinsnow.grandpacleaner.feature;

import cc.carm.lib.easyplugin.gui.GUIItem;
import cc.carm.lib.easyplugin.gui.GUIType;
import cc.carm.lib.easyplugin.gui.paged.AutoPagedGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RecycleBinGUI extends AutoPagedGUI {
    @NotNull private final RecycleBin recycleBin;
    @NotNull private final Object recycleBinLock;
    private RecycleBinGUI(@NotNull RecycleBin recycleBin, @NotNull Object recycleBinLock) {
        super(
                GUIType.SIX_BY_NINE, "回收站",
                new int[]{
                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                        9, 10, 11, 12, 13, 14, 15, 16, 17,
                        18, 19, 20, 21, 22, 23, 24, 25, 26,
                        27, 28, 29, 30, 31, 32, 33, 34, 35,
                        36, 37, 38, 39, 40, 41, 42, 43, 44,
                        45, 46, 47, 48, 49, 50, 51
                }
        );
        this.recycleBin = recycleBin;
        this.recycleBinLock = recycleBinLock;

        this.setPreviousPageSlot(52);
        {
            ItemStack previousPageUI = new ItemStack(Material.OAK_FENCE);
            ItemMeta meta = previousPageUI.getItemMeta();
            meta.displayName(Component.text("上一页"));
            previousPageUI.setItemMeta(meta);
            this.setPreviousPageUI(previousPageUI);
        }
        this.setNoPreviousPageUI(null);

        this.setNextPageSlot(53);
        {
            ItemStack setNextPageUI = new ItemStack(Material.OAK_FENCE);
            ItemMeta meta = setNextPageUI.getItemMeta();
            meta.displayName(Component.text("下一页"));
            setNextPageUI.setItemMeta(meta);
            this.setNextPageUI(setNextPageUI);
        }
        this.setNoNextPageUI(null);
    }

    public void init() {
        synchronized (this.recycleBinLock) {
            this.recycleBin.items().forEach(it -> RecycleBinGUI.this.addItem(new GUIItem(it) {
                @Override
                public void onClick(Player clicker, ClickType type) {
                    clicker.getInventory().addItem(this.getDisplay().clone());
                }
            }));
        }
    }

    public static @NotNull RecycleBinGUI ofRecycleBin(@NotNull RecycleBin recycleBin, @NotNull Object recycleBinLock) {
        Objects.requireNonNull(recycleBin);
        Objects.requireNonNull(recycleBin);
        return new RecycleBinGUI(recycleBin, recycleBinLock);
    }
}
