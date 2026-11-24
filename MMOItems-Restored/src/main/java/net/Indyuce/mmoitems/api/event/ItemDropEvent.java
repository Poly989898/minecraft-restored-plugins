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

   public ItemDropEvent(LivingEntity entity, List<ItemStack> items, CustomBlock customBlock) {
      this(entity, items, ItemDropEvent.DropCause.CUSTOM_BLOCK, null, null, null, customBlock);
   }

   public ItemDropEvent(LivingEntity entity, List<ItemStack> items, Block block) {
      this(entity, items, ItemDropEvent.DropCause.NORMAL_BLOCK, block, null, null, null);
   }

   public ItemDropEvent(LivingEntity entity, List<ItemStack> items, Entity entity2) {
      this(entity, items, ItemDropEvent.DropCause.NORMAL_MONSTER, null, entity2, null, null);
   }

   @Deprecated
   public ItemDropEvent(LivingEntity entity, List<ItemStack> items, ItemDropEvent.DropCause dropCause, String text) {
      this(entity, items, ItemDropEvent.DropCause.MYTHIC_MOB, null, null, text, null);
   }

   private ItemDropEvent(LivingEntity entity, List<ItemStack> items, ItemDropEvent.DropCause dropCause, Block block, Entity entity2, String text, CustomBlock customBlock) {
      this.player = entity;
      this.cause = dropCause;
      this.drops = items;
      this.block = block;
      this.entity = entity2;
      this.mythicMobName = text;
      this.customBlock = customBlock;
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

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
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
