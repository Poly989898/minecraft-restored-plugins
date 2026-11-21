package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.bukkit.events.MythicModifiedInventoryEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "stealitem",
   aliases = {"steal", "stealitems", "itemsteal"},
   description = "Steals an item from the target and puts it in the mob's hand"
)
public class StealItemMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private final AbstractItemMatcher item;
   private final PlaceholderString itemFilter;
   private final boolean strict;
   private final boolean vanillaOnly;
   private final boolean inverted;
   private final boolean runtime;
   private final Map<String, BukkitItemMatcher> runtimeMatcherCache = new ConcurrentHashMap<>();
   private final PlaceholderInt amount;
   private final PlaceholderFloat dropChance;
   @MythicField(
      name = "onSteal",
      aliases = {"then"},
      version = "5.8",
      description = "Skill to execute on successfully stealing"
   )
   protected Optional<Skill> onStealSkill = Optional.empty();

   public StealItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderInteger(new String[]{"amount", "a"}, 1);
      this.dropChance = mlc.getPlaceholderFloat(new String[]{"dropchance", "dc"}, 0.0F);
      this.itemFilter = mlc.getPlaceholderString(new String[]{"types", "type", "t", "material", "mat", "m", "item", "i"}, null);
      this.strict = mlc.getBoolean(new String[]{"strict", "exact", "e"}, false);
      this.vanillaOnly = mlc.getBoolean(new String[]{"vanillaonly", "vanilla"}, false);
      this.inverted = mlc.getBoolean(new String[]{"inverted", "blacklist", "bl"}, false);
      this.runtime = mlc.getBoolean(new String[]{"runtime", "dynamic", "rt"}, false) || this.itemFilter != null && !this.itemFilter.isStatic();
      if (this.runtime) {
         this.item = null;
      } else {
         this.item = this.getPlugin().getBootstrap().getItemMatcher(mlc);
      }

      String onStealSkillName = mlc.getString(new String[]{"onstealskill", "onsteal", "then"});
      this.getManager().queueSecondPass(() -> {
         if (onStealSkillName != null) {
            this.onStealSkill = this.getManager().getSkill(file, this, onStealSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         BukkitItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().meta(data).entity(target).build());
         if (matcher == null) {
            return SkillResult.INVALID_CONFIG;
         } else {
            Player player = BukkitAdapter.adapt(target.asPlayer());
            if (data.getCaster().getEntity().getBukkitEntity() instanceof LivingEntity mob) {
               if (!mob.getEquipment().getItemInMainHand().getType().isAir()) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Steal CCCC");
                  return SkillResult.CONDITION_FAILED;
               } else {
                  ItemStack stolenItem = this.stealItem(data, target, player.getInventory(), matcher, this.amount.get(data, target));
                  if (stolenItem != null) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Stole item {0}", stolenItem.toString());
                     mob.getEquipment().setItemInMainHand(stolenItem);
                     mob.getEquipment().setItemInMainHandDropChance(this.dropChance.get(data, target));
                     if (this.onStealSkill.isPresent()) {
                        SkillMetadata summonData = data.deepClone();
                        summonData.setEntityTarget(target);
                        summonData.setMetadata(PlaceholderMetadataKey.ITEM_STACK, stolenItem);
                        this.onStealSkill.get().execute(summonData);
                     }
                  } else {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "No stolen it em found");
                  }

                  return SkillResult.SUCCESS;
               }
            } else {
               return SkillResult.INVALID_CONFIG;
            }
         }
      }
   }

   private ItemStack stealItem(SkillMetadata data, AbstractEntity target, Inventory inventory, BukkitItemMatcher matcher, int amountToSteal) {
      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "doing steal");
      List<Integer> slots = IntStream.range(0, inventory.getSize()).boxed().collect(Collectors.toList());
      Collections.shuffle(slots);

      for (int i : slots) {
         ItemStack inventoryItem = inventory.getItem(i);
         if (inventoryItem == null) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Slot {0} is null", i);
         } else {
            if (matcher.matches(inventoryItem)) {
               int slotAmount = inventoryItem.getAmount();
               int stolenAmount = Math.min(slotAmount, amountToSteal);
               ItemStack stolenItem = inventoryItem.clone();
               stolenItem.setAmount(stolenAmount);
               int newAmount = slotAmount - stolenAmount;
               if (newAmount <= 0) {
                  inventory.setItem(i, null);
               } else {
                  inventoryItem.setAmount(newAmount);
               }

               if (target != null && target.isPlayer()) {
                  AbstractPlayer player = target.asPlayer();
                  player.updateInventory();
                  Events.call(new MythicModifiedInventoryEvent(data.getCaster(), BukkitAdapter.adapt(player)));
               }

               return stolenItem;
            }

            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "{0} not match", inventoryItem.toString());
         }
      }

      return null;
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
