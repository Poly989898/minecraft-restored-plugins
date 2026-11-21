package io.lumine.mythic.core.drops;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ICommandDrop;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.drops.ILocationDrop;
import io.lumine.mythic.api.drops.IMessagingDrop;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.drops.droppables.ExperienceDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@ToString
public class LootBag {
   private final DropMetadata metadata;
   private List<Drop> lootTable = new ArrayList<>();
   private Map<Class, Drop> lootTableIntangible = new HashMap<>();

   public LootBag(DropMetadata meta) {
      this.metadata = meta;
   }

   public Collection<Drop> getDrops() {
      List<Drop> drops = new ArrayList<>(this.lootTable.size() + this.lootTableIntangible.size());
      drops.addAll(this.lootTable);
      drops.addAll(this.lootTableIntangible.values());
      return drops;
   }

   @Deprecated
   public LootBag add(Drop type) {
      return this.add(null, type);
   }

   public LootBag add(DropMetadata data, Drop type) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Adding Drop to LootBag: {0}", type.getLine());
      if (type instanceof IIntangibleDrop || type instanceof ExperienceDrop) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Adding Intangible Drop");
         this.lootTableIntangible.merge(type.getClass(), type, (o, n) -> o.addAmount(n));
      } else if (type instanceof ICommandDrop) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Adding Command Drop");
         this.lootTable.add(type);
      } else if (type instanceof IMultiDrop multiDrop) {
         int amount = (int)type.getAmount();
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Adding {0} of MultiDrop", amount);

         for (int i = 0; i < amount; i++) {
            this.add(data, multiDrop.get(this.metadata));
         }
      } else if (type instanceof IItemDrop) {
         if (type.getAmount() < 1.0) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Cannot drop < 0 of item, skipping: {0}", type.getLine());
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Adding {0} of item drop: {1}", type.getAmount(), type.getLine());
            this.lootTable.add(type);
         }
      } else if (type instanceof CustomDrop customDrop) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "-- Adding Custom Drop from API");
         if (!customDrop.getDrop().isPresent()) {
            return this;
         }

         type.rollAmount(data);
         if (customDrop.getDrop().get() instanceof IItemDrop && type.getAmount() < 1.0) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Cannot drop < 0 of item, skipping: {0}", type.getLine());
            return this;
         }

         this.lootTable.add(type);
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Adding {0} of drop: {1}", type.getAmount(), type.getLine());
         this.lootTable.add(type);
      }

      return this;
   }

   public LootBag add(DropMetadata data, LootBag other) {
      for (Drop type : other.getLootTableIntangible().values()) {
         this.lootTableIntangible.merge(type.getClass(), type, (o, n) -> o.addAmount(n));
      }

      for (Drop type : other.getLootTable()) {
         type.rollAmount(data);
         this.lootTable.add(type);
      }

      return this;
   }

   public Collection<AbstractEntity> drop(AbstractLocation target) {
      return this.drop(target, true);
   }

   public Collection<AbstractEntity> drop(AbstractLocation target, boolean naturally) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Calling the drop method passing in an AbstractLocation only.");
      Collection<AbstractEntity> drops = Lists.newArrayList();
      Location loc = BukkitAdapter.adapt(target);

      for (Drop type : this.getDrops()) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Drop type {0} {1}", type.getLine(), type.getClass());
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop customDrop) {
            if (customDrop.getDrop().isEmpty()) {
               continue;
            }

            drop = customDrop.getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Calling drop with type of IItemDrop.");
            AbstractItemStack stack = itemDrop.getDrop(this.metadata, amount);
            ItemStack bukkitDropItem = BukkitAdapter.adapt(stack);
            if (bukkitDropItem == null) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drop produced no item, skipping: {0}", type.getLine());
            } else {
               Item itemEntity;
               if (naturally) {
                  itemEntity = loc.getWorld().dropItemNaturally(loc, bukkitDropItem);
               } else {
                  itemEntity = loc.getWorld().dropItem(loc, bukkitDropItem);
               }

               if (itemEntity != null) {
                  drops.add(BukkitAdapter.adapt(itemEntity));
               }
            }
         } else if (drop instanceof ILocationDrop iLocationDrop) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Calling drop with type of ILocationDrop.");
            iLocationDrop.drop(target, this.metadata, amount);
         } else if (drop instanceof ICommandDrop iCommandDrop) {
            DropMetadata dropMetadata = this.metadata;
            dropMetadata.setAmount((int)amount);
            iCommandDrop.dispatchCommand(null, dropMetadata, amount);
         }
      }

      return drops;
   }

   @Nullable
   public Collection<AbstractEntity> putInContainer(AbstractLocation target) {
      Collection<AbstractEntity> drops = Lists.newArrayList();
      Location loc = BukkitAdapter.adapt(target);
      if (!(loc.getBlock().getState() instanceof InventoryHolder container)) {
         return this.drop(target);
      } else {
         for (Drop type : this.getDrops()) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Drop type {0} {1}", type.getLine(), type.getClass());
            double amount = type.getAmount();
            IDrop drop;
            if (type instanceof CustomDrop customDrop) {
               if (customDrop.getDrop().isEmpty()) {
                  continue;
               }

               drop = customDrop.getDrop().get();
            } else {
               drop = type;
            }

            if (drop instanceof IItemDrop itemDrop) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Calling drop with type of IItemDrop.");
               AbstractItemStack stack = itemDrop.getDrop(this.metadata, amount);
               ItemStack containerItem = BukkitAdapter.adapt(stack);
               if (containerItem == null) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drop produced no item, skipping: {0}", type.getLine());
               } else {
                  container.getInventory().addItem(new ItemStack[]{containerItem});
               }
            } else if (drop instanceof ILocationDrop iLocationDrop) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Calling drop with type of ILocationDrop.");
               iLocationDrop.drop(target, this.metadata, amount);
            } else if (drop instanceof ICommandDrop iCommandDrop) {
               DropMetadata dropMetadata = this.metadata;
               dropMetadata.setAmount((int)amount);
               iCommandDrop.dispatchCommand(null, dropMetadata, amount);
            }
         }

         return drops;
      }
   }

   public void dropForEntityDeathEvent(AbstractLocation location, AbstractPlayer target, EntityDeathEvent event) {
   }

   public void give(AbstractPlayer player) {
      this.give(player, true);
   }

   public void give(AbstractPlayer player, boolean sendMessages) {
      this.give(player, sendMessages, (p, failed) -> {
         for (ItemStack fd : failed.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), fd, droppedItem -> {
               droppedItem.setCanMobPickup(false);
               droppedItem.setOwner(p.getUniqueId());
               droppedItem.setThrower(p.getUniqueId());
            });
         }
      });
   }

   public void give(AbstractPlayer player, boolean sendMessages, BiConsumer<Player, HashMap<Integer, ItemStack>> onFullInventory) {
      this.give(player, sendMessages, (p, itemStack) -> p.getInventory().addItem(new ItemStack[]{itemStack}), onFullInventory);
   }

   public void give(
      AbstractPlayer player,
      boolean sendMessages,
      BiFunction<Player, ItemStack, HashMap<Integer, ItemStack>> giveAction,
      BiConsumer<Player, HashMap<Integer, ItemStack>> onFullInventory
   ) {
      Player p = BukkitAdapter.adapt(player);
      Map<IMessagingDrop, Double> messagingDrops = new HashMap<>();

      for (Drop type : this.getDrops()) {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop customDrop) {
            if (customDrop.getDrop().isEmpty()) {
               continue;
            }

            drop = customDrop.getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Giving Item {0} with amount {1}", type.getLine(), type.getAmount());
            AbstractItemStack stack = itemDrop.getDrop(this.metadata, amount);
            ItemStack is = BukkitAdapter.adapt(stack);
            if (is == null) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drop produced no item, skipping: {0}", type.getLine());
               continue;
            }

            HashMap<Integer, ItemStack> failed = giveAction.apply(p, is);
            if (failed != null && failed.size() > 0) {
               onFullInventory.accept(p, failed);
            }
         } else if (drop instanceof ExperienceDrop) {
            p.giveExp((int)amount);
         } else if (drop instanceof IIntangibleDrop intangibleDrop) {
            intangibleDrop.giveDrop(player, this.metadata, amount);
         } else if (drop instanceof ICommandDrop commandDrop) {
            DropMetadata dropMetadata = this.metadata;
            dropMetadata.setAmount((int)amount);
            commandDrop.dispatchCommand(player, dropMetadata, amount);
         }

         if (drop instanceof IMessagingDrop) {
            messagingDrops.merge((IMessagingDrop)type, amount, (n, o) -> n + o);
         }
      }

      if (messagingDrops.size() > 0 && sendMessages) {
         for (Entry<IMessagingDrop, Double> m : messagingDrops.entrySet()) {
            String message = m.getKey().getRewardMessage(this.metadata, m.getValue());
            if (message != null) {
               player.sendMessage(message);
            }
         }
      }
   }

   public void equip(AbstractEntity entity) {
      AbstractItemStack hand = null;
      AbstractItemStack offhand = null;
      AbstractItemStack head = null;
      AbstractItemStack chest = null;
      AbstractItemStack legs = null;
      AbstractItemStack feet = null;

      for (Drop type : this.getDrops()) {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop customDrop) {
            if (customDrop.getDrop().isEmpty()) {
               continue;
            }

            drop = customDrop.getDrop().get();
         } else {
            drop = type;
         }

         try {
            if (drop instanceof IItemDrop itemDrop && type.getEquipSlot() != null) {
               EquipSlot slot = type.getEquipSlot();
               switch (slot.getBukkitSlot()) {
                  case HAND:
                     hand = itemDrop.getDrop(this.metadata, amount);
                     break;
                  case FEET:
                     feet = itemDrop.getDrop(this.metadata, amount);
                     break;
                  case LEGS:
                     legs = itemDrop.getDrop(this.metadata, amount);
                     break;
                  case CHEST:
                     chest = itemDrop.getDrop(this.metadata, amount);
                     break;
                  case HEAD:
                     head = itemDrop.getDrop(this.metadata, amount);
                     break;
                  case OFF_HAND:
                     offhand = itemDrop.getDrop(this.metadata, amount);
               }
            }
         } catch (Exception e) {
            MythicLogger.handleMinorError(e);
         }
      }

      if (head != null) {
         entity.equipItemHead(head);
         EquipSlot.HEAD.preventDrop(entity);
      }

      if (chest != null) {
         entity.equipItemChest(chest);
         EquipSlot.CHEST.preventDrop(entity);
      }

      if (legs != null) {
         entity.equipItemLegs(legs);
         EquipSlot.LEGS.preventDrop(entity);
      }

      if (feet != null) {
         entity.equipItemFeet(feet);
         EquipSlot.FEET.preventDrop(entity);
      }

      if (hand != null) {
         entity.equipItemMainHand(hand);
         EquipSlot.HAND.preventDrop(entity);
      }

      if (offhand != null) {
         entity.equipItemOffHand(offhand);
         EquipSlot.OFFHAND.preventDrop(entity);
      }
   }

   public void equipOrGive(AbstractPlayer player) {
      boolean sendMessages = true;
      Player p = BukkitAdapter.adapt(player);
      AbstractItemStack hand = null;
      AbstractItemStack offhand = null;
      AbstractItemStack head = null;
      AbstractItemStack chest = null;
      AbstractItemStack legs = null;
      AbstractItemStack feet = null;
      Map<IMessagingDrop, Double> messagingDrops = new HashMap<>();
      Iterator iterator = this.getDrops().iterator();

      while (true) {
         Drop type;
         IDrop drop;
         double amount;
         while (true) {
            if (!iterator.hasNext()) {
               if (messagingDrops.size() > 0 && sendMessages) {
                  for (Entry<IMessagingDrop, Double> m : messagingDrops.entrySet()) {
                     String message = m.getKey().getRewardMessage(this.metadata, m.getValue());
                     if (message != null) {
                        player.sendMessage(message);
                     }
                  }
               }

               if (head != null) {
                  player.equipItemHead(head);
               }

               if (chest != null) {
                  player.equipItemChest(chest);
               }

               if (legs != null) {
                  player.equipItemLegs(legs);
               }

               if (feet != null) {
                  player.equipItemFeet(feet);
               }

               if (hand != null) {
                  player.equipItemMainHand(hand);
               }

               if (offhand != null) {
                  player.equipItemOffHand(offhand);
               }

               return;
            }

            type = (Drop)iterator.next();
            amount = type.getAmount();
            if (type instanceof CustomDrop customDrop) {
               if (customDrop.getDrop().isEmpty()) {
                  continue;
               }

               drop = customDrop.getDrop().get();
               break;
            }

            drop = type;
            break;
         }

         if (type instanceof IItemDrop itemDrop) {
            if (type.getEquipSlot() != null) {
               EquipSlot slot = type.getEquipSlot();
               switch (slot.getBukkitSlot()) {
                  case HAND:
                     hand = itemDrop.getDrop(this.metadata, amount);
                     continue;
                  case FEET:
                     feet = itemDrop.getDrop(this.metadata, amount);
                     continue;
                  case LEGS:
                     legs = itemDrop.getDrop(this.metadata, amount);
                     continue;
                  case CHEST:
                     chest = itemDrop.getDrop(this.metadata, amount);
                     continue;
                  case HEAD:
                     head = itemDrop.getDrop(this.metadata, amount);
                     continue;
                  case OFF_HAND:
                     offhand = itemDrop.getDrop(this.metadata, amount);
                     continue;
               }
            }

            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Giving Item {0} with amount {1}", type.getLine(), type.getAmount());
            AbstractItemStack stack = itemDrop.getDrop(this.metadata, amount);
            ItemStack is = BukkitAdapter.adapt(stack);
            if (is == null) {
               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drop produced no item, skipping: {0}", type.getLine());
               continue;
            }

            HashMap<Integer, ItemStack> failed = p.getInventory().addItem(new ItemStack[]{is});
            if (failed != null && failed.size() > 0) {
               for (ItemStack fd : failed.values()) {
                  p.getWorld().dropItemNaturally(p.getLocation(), fd, droppedItem -> {
                     droppedItem.setCanMobPickup(false);
                     droppedItem.setOwner(p.getUniqueId());
                     droppedItem.setThrower(p.getUniqueId());
                  });
               }
            }
         } else if (drop instanceof ExperienceDrop) {
            p.giveExp(Numbers.floor(amount));
         } else if (drop instanceof IIntangibleDrop intangibleDrop) {
            intangibleDrop.giveDrop(player, this.metadata, amount);
         } else if (drop instanceof ICommandDrop iCommandDrop) {
            DropMetadata dropMetadata = this.metadata;
            dropMetadata.setAmount((int)amount);
            iCommandDrop.dispatchCommand(player, dropMetadata, amount);
         }

         if (drop instanceof IMessagingDrop iMessagingDrop) {
            messagingDrops.merge(iMessagingDrop, amount, (n, o) -> n + o);
         }
      }
   }

   public AbstractItemStack getSingleItem() {
      ArrayList<Drop> drops = Lists.newArrayList(this.getDrops());
      Collections.shuffle(drops);

      for (Drop type : drops) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Drop type {0} {1}", type.getLine(), type.getClass());
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop customDrop) {
            if (customDrop.getDrop().isEmpty()) {
               continue;
            }

            drop = customDrop.getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Calling drop with type of IItemDrop.");
            return itemDrop.getDrop(this.metadata, amount);
         }
      }

      return null;
   }

   public int size() {
      return this.lootTable.size();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof LootBag other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$metadata = this.getMetadata();
         Object other$metadata = other.getMetadata();
         if (this$metadata == null ? other$metadata == null : this$metadata.equals(other$metadata)) {
            Object this$lootTable = this.getLootTable();
            Object other$lootTable = other.getLootTable();
            if (this$lootTable == null ? other$lootTable == null : this$lootTable.equals(other$lootTable)) {
               Object this$lootTableIntangible = this.getLootTableIntangible();
               Object other$lootTableIntangible = other.getLootTableIntangible();
               return this$lootTableIntangible == null ? other$lootTableIntangible == null : this$lootTableIntangible.equals(other$lootTableIntangible);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof LootBag;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      Object $lootTable = this.getLootTable();
      result = result * 59 + ($lootTable == null ? 43 : $lootTable.hashCode());
      Object $lootTableIntangible = this.getLootTableIntangible();
      return result * 59 + ($lootTableIntangible == null ? 43 : $lootTableIntangible.hashCode());
   }
}
