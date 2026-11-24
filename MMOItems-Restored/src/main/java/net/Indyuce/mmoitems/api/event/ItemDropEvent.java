package net.Indyuce.mmoitems.api.event;

import java.util.List;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemDropEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private boolean cancelled;
   private final ItemDropEvent.DropCause cause;
   private final List<ItemStack> drops;
   private final LivingEntity player;
   private final Block block;
   private final Entity entity;
   private final String mythicMobName;
   private final CustomBlock customBlock;

   public ItemDropEvent(LivingEntity var1, List<ItemStack> var2, CustomBlock var3) {
      this(var1, var2, ItemDropEvent.DropCause.CUSTOM_BLOCK, null, null, null, var3);
   }

   public ItemDropEvent(LivingEntity var1, List<ItemStack> var2, Block var3) {
      this(var1, var2, ItemDropEvent.DropCause.NORMAL_BLOCK, var3, null, null, null);
   }

   public ItemDropEvent(LivingEntity var1, List<ItemStack> var2, Entity var3) {
      this(var1, var2, ItemDropEvent.DropCause.NORMAL_MONSTER, null, var3, null, null);
   }

   @Deprecated
   public ItemDropEvent(LivingEntity var1, List<ItemStack> var2, ItemDropEvent.DropCause var3, String var4) {
      this(var1, var2, ItemDropEvent.DropCause.MYTHIC_MOB, null, null, var4, null);
   }

   private ItemDropEvent(LivingEntity var1, List<ItemStack> var2, ItemDropEvent.DropCause var3, Block var4, Entity var5, String var6, CustomBlock var7) {
      this.player = var1;
      this.cause = var3;
      this.drops = var2;
      this.block = var4;
      this.entity = var5;
      this.mythicMobName = var6;
      this.customBlock = var7;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public List<ItemStack> getDrops() {
      return this.drops;
   }

   public ItemDropEvent.DropCause getCause() {
      return this.cause;
   }

   public LivingEntity getWhoDropped() {
      return this.player;
   }

   public Block getMinedBlock() {
      return this.block;
   }

   public Entity getKilledEntity() {
      return this.entity;
   }

   public String getKilledMythicMobName() {
      return this.mythicMobName;
   }

   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public enum DropCause {
      NORMAL_BLOCK,
      NORMAL_MONSTER,
      @Deprecated
      MYTHIC_MOB,
      CUSTOM_BLOCK;
   }
}
