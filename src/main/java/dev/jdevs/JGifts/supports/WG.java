package dev.jdevs.JGifts.supports;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static dev.jdevs.JGifts.Christmas.version_mode;
import static dev.jdevs.JGifts.events.FallGifts.saveBlock;
import static dev.jdevs.JGifts.events.FallGifts.saveBlock_12;

public class WG {
    public static RegionManager getRegionManager(Location w) {
        if (version_mode > 12) {
            return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w.getWorld()));
        } else {
            try {
                Method method = WorldGuardPlugin.inst().getClass().getMethod("getRegionManager", World.class);
                return (RegionManager)method.invoke(WorldGuardPlugin.inst(), w.getWorld());
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var4) {
                throw new RuntimeException(var4);
            }
        }
    }

    public static void setBlock(Location block, org.bukkit.block.Block b) {
        if (version_mode > 12) {
            if (saveBlock.get(block) != null) {
                BlockData blockData = saveBlock.get(block);
                String material = blockData.getMaterial().name().toLowerCase();
                String check = block.getBlock().getType().name().toLowerCase();
                if (check.contains("nether") || check.contains("end")) {
                    return;
                }
                if (material.contains("lava") || material.contains("fire") || material.contains("nether") || material.contains("end")) {
                    saveBlock.remove(b.getLocation());
                    b.setBlockData(Material.AIR.createBlockData());
                }
                else {
                    b.setBlockData(blockData);
                    saveBlock.remove(b.getLocation());
                }
            }
        }
        else {
            if (saveBlock_12.get(block) != null) {
                Map.Entry<Material, Byte> entry = saveBlock_12.get(block);
                Material mt = entry.getKey();
                String material = mt.name().toLowerCase();
                String check = block.getBlock().getType().name().toLowerCase();
                if (check.contains("nether") || check.contains("end")) {
                    return;
                }
                if (material.contains("lava") || material.contains("fire")) {
                    b.setType(Material.AIR);
                    saveBlock_12.remove(block);
                    block.getBlock().getState().update();
                }
                else {
                    block.getBlock().setType(mt);
                    try {
                        Class<?>[] parameters = new Class<?>[]{byte.class};
                        Object[] args = new Object[]{entry.getValue()};
                        b.getClass().getMethod("setData", parameters).invoke(b, args);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    block.getBlock().getState().update();
                    saveBlock_12.remove(block);
                }
            }
        }
    }
}