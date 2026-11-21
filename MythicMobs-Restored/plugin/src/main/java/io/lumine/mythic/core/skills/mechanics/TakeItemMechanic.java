package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.variables.MetaVariableMechanic;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "takeitem",
   aliases = {"take", "takeitems", "itemtake"},
   description = "Takes an item from the target"
)
public class TakeItemMechanic extends MetaVariableMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private final AbstractItemMatcher item;
   private final PlaceholderString itemFilter;
   private final boolean strict;
   private final boolean vanillaOnly;
   private final boolean inverted;
   private final boolean runtime;
   private final Map<String, BukkitItemMatcher> runtimeMatcherCache = new ConcurrentHashMap<>();
   private final PlaceholderInt amount;
   private boolean exact;

   public TakeItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.itemFilter = mlc.getPlaceholderString(new String[]{"types", "type", "t", "material", "mat", "m", "item", "i"}, null);
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
      this.strict = mlc.getBoolean(new String[]{"strict", "exact", "e"}, false);
      this.vanillaOnly = mlc.getBoolean(new String[]{"vanillaonly", "vanilla"}, false);
      this.inverted = mlc.getBoolean(new String[]{"inverted", "blacklist", "bl"}, false);
      this.runtime = mlc.getBoolean(new String[]{"runtime", "dynamic", "rt"}, false) || this.itemFilter != null && !this.itemFilter.isStatic();
      if (this.runtime) {
         this.item = null;
      } else {
         this.item = this.getPlugin().getBootstrap().getItemMatcher(mlc);
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Player player = BukkitAdapter.adapt(target.asPlayer());
         if (this.variableName != null) {
            PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(target).build();
            this.removeVariableItem(context, player.getInventory(), this.amount.get(data, target));
         } else {
            BukkitItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().meta(data).entity(target).build());
            if (matcher == null) {
               return SkillResult.INVALID_CONFIG;
            }

            this.removeItem(data, target, player.getInventory(), matcher, this.amount.get(data, target));
         }

         return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      Block block = location.getBlock();
      if (block.getState() instanceof Container container) {
         if (this.variableName != null) {
            PlaceholderContext context = PlaceholderContext.builder().meta(data).location(target).build();
            this.removeVariableItem(context, container.getInventory(), this.amount.get(data));
         } else {
            BukkitItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().meta(data).location(target).build());
            if (matcher == null) {
               return SkillResult.INVALID_CONFIG;
            }

            this.removeItem(data, null, container.getInventory(), matcher, this.amount.get(data));
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   private void removeItem(SkillMetadata data, AbstractEntity target, Inventory inventory, BukkitItemMatcher matcher, int amountToRemove) {
      for (int i = 0; i < inventory.getSize(); i++) {
         ItemStack inventoryItem = inventory.getItem(i);
         if (inventoryItem != null && matcher.matches(inventoryItem)) {
            int slotAmount = this.executeRemoval(data, target, inventoryItem, amountToRemove);
            amountToRemove = Math.max(0, amountToRemove - slotAmount);
            if (amountToRemove == 0) {
               break;
            }
         }
      }
   }

   private void removeVariableItem(PlaceholderContext context, Inventory inventory, int amountToRemove) {
      if (this.getVariable(context) instanceof ItemVariable itemVariable) {
         ItemStack targetItem = itemVariable.getValueClone().getItemStack();
         targetItem.setAmount(1);

         for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem != null && targetItem.isSimilar(inventoryItem)) {
               int slotAmount = this.executeRemoval(context.meta(), context.entity(), inventoryItem, amountToRemove);
               amountToRemove = Math.max(0, amountToRemove - slotAmount);
               if (amountToRemove == 0) {
                  break;
               }
            }
         }
      }
   }

   private int executeRemoval(PlaceholderMeta data, AbstractEntity target, ItemStack inventoryItem, int amountToRemove) {
      int slotAmount = inventoryItem.getAmount();
      int newAmount = Math.max(0, slotAmount - amountToRemove);
      if (newAmount == 0) {
         this.getPlugin().getVolatileCodeHandler().getItemHandler().destroyItem(inventoryItem);
         if (target != null && target.isPlayer()) {
            AbstractPlayer player = target.asPlayer();
            player.updateInventory();
            Events.call(new MythicModifiedInventoryEvent(data.getCaster(), BukkitAdapter.adapt(player)));
         }
      } else {
         inventoryItem.setAmount(newAmount);
      }

      return slotAmount;
   }

   private BukkitItemMatcher getMatcher(PlaceholderContext context) {
      if (!this.runtime) {
         return this.item instanceof BukkitItemMatcher bukkitItemMatcher ? bukkitItemMatcher : null;
      } else if (this.itemFilter == null) {
         return null;
      } else {
         String resolvedFilter = this.itemFilter.get(context);
         if (isInvalidRuntimeFilter(resolvedFilter)) {
            return null;
         } else {
            String filter = sanitizeFilter(resolvedFilter);
            if (isInvalidRuntimeFilter(filter)) {
               return null;
            } else {
               String cacheKey = filter.toLowerCase();

               try {
                  return this.runtimeMatcherCache.computeIfAbsent(cacheKey, key -> new BukkitItemMatcher(filter, this.strict, this.vanillaOnly, this.inverted));
               } catch (IllegalArgumentException e) {
                  return null;
               }
            }
         }
      }
   }

   private static String sanitizeFilter(String filter) {
      String sanitized = filter.trim();
      if (sanitized.length() >= 2 && sanitized.startsWith("\"") && sanitized.endsWith("\"")) {
         sanitized = sanitized.substring(1, sanitized.length() - 1).trim();
      }

      return sanitized;
   }

   private static boolean isInvalidRuntimeFilter(String filter) {
      if (filter == null) {
         return true;
      } else {
         String trimmed = filter.trim();
         if (trimmed.isEmpty()) {
            return true;
         } else {
            return !trimmed.equalsIgnoreCase("UNDEFINED") && !trimmed.equalsIgnoreCase("NULL")
               ? trimmed.indexOf(60) >= 0 && trimmed.indexOf(62) > trimmed.indexOf(60)
               : true;
         }
      }
   }
}
