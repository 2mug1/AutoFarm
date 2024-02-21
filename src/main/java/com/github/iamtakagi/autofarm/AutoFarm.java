package com.github.iamtakagi.autofarm;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AutoFarm extends JavaPlugin implements Listener {

  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  public void onDisable() {
  }

  @EventHandler
  public void onCropBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();

    final Block block = event.getBlock();

    Material cropBlockType = null;
    Material seedBlockType = null;

    if (block.getType() == Material.WHEAT_SEEDS || block.getType() == Material.WHEAT) {
      cropBlockType = Material.WHEAT;
      seedBlockType = Material.WHEAT_SEEDS;
    } else if (block.getType() == Material.POTATO || block.getType() == Material.POTATOES) {
      cropBlockType = Material.POTATOES;
      seedBlockType = Material.POTATO;
    } else if (block.getType() == Material.CARROT || block.getType() == Material.CARROTS) {
      cropBlockType = Material.CARROTS;
      seedBlockType = Material.CARROT;
    } else if (block.getType() == Material.BEETROOTS) {
      cropBlockType = Material.BEETROOTS;
      seedBlockType = Material.BEETROOT_SEEDS;
    } else if (block.getType() == Material.NETHER_WART) {
      cropBlockType = Material.NETHER_WART;
      seedBlockType = Material.NETHER_WART;
    } else {
      return;
    }

    Ageable ageable = (Ageable) block.getBlockData();

    if (ageable.getAge() == 0) {
      block.getDrops().clear();
      event.setCancelled(true);
    } else if (hasSeed(seedBlockType, player)) {
      removeItems((Inventory) player.getInventory(), seedBlockType, 1);
      final Material finalCropBlockType = cropBlockType;
      Bukkit.getScheduler().runTaskLater(this, () -> block.setType(finalCropBlockType), 1L);

      event.setCancelled(false);
    }
  }

  private boolean hasSeed(Material seedType, Player player) {
    return player.getInventory().containsAtLeast(new ItemStack(seedType), 1)
        || player.getInventory().getItemInOffHand().getType() == seedType;
  }

  private void removeItems(Inventory inventory, Material type, int amount) {
    if (amount <= 0)
      return;
    for (ItemStack item : inventory.getContents()) {
      if (item != null && type == item.getType()) {
        int newAmount = item.getAmount() - amount;
        if (newAmount > 0) {
          item.setAmount(newAmount);
          break;
        }
        inventory.removeItem(item);
        amount = -newAmount;
        if (amount == 0)
          break;
      }
    }
  }
}
