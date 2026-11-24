package net.Indyuce.mmocore.loot;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LootBuilder {
   private final PlayerData player;
   private final List<ItemStack> loot = new ArrayList<>();
   private double capacity;
   public static double DEFAULT_CAPACITY = 100.0;

   public LootBuilder(PlayerData player) {
      this(player, DEFAULT_CAPACITY);
   }

   public LootBuilder(@NotNull PlayerData player, double capacity) {
      this.player = player;
      this.capacity = capacity;
   }

   public PlayerData getEntity() {
      return this.player;
   }

   public List<ItemStack> getLoot() {
      return this.loot;
   }

   public double getCapacity() {
      return this.capacity;
   }

   public void addLoot(ItemStack item) {
      this.loot.add(item);
   }

   public void addLoot(List<? extends ItemStack> items) {
      this.loot.addAll(items);
   }

   public void reduceCapacity(double value) {
      this.capacity = Math.max(0.0, this.capacity - value);
   }
}
