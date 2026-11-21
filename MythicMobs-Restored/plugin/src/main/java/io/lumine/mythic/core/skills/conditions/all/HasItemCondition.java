package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "Phil",
   name = "HasItem",
   aliases = {},
   description = "Checks the inventory for this item"
)
public class HasItemCondition extends SkillCondition implements IEntityCondition, ILocationCondition, ISkillMetaCondition {
   private final PlaceholderString itemFilter;
   private final boolean strict;
   private final boolean vanillaOnly;
   private final boolean inverted;
   private final boolean runtime;
   private final Map<String, AbstractItemMatcher> runtimeMatcherCache = new ConcurrentHashMap<>();
   private final Map<String, RangedInt> runtimeAmountCache = new ConcurrentHashMap<>();
   @MythicField(
      name = "types",
      aliases = {"types", "type", "t", "material", "mat", "m", "item", "i"},
      description = "A list of materials to check"
   )
   private AbstractItemMatcher matcher;
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount to check for"
   )
   private final PlaceholderString amountPlaceholder;
   private final RangedInt staticAmount;

   public HasItemCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amountPlaceholder = mlc.getPlaceholderString(new String[]{"amount", "a"}, ">0");
      this.staticAmount = this.amountPlaceholder != null && this.amountPlaceholder.isStatic() ? parseAmount(this.amountPlaceholder.get()) : null;
      this.itemFilter = mlc.getPlaceholderString(new String[]{"types", "type", "t", "material", "mat", "m", "item", "i"}, null, this.conditionVar);
      this.strict = mlc.getBoolean(new String[]{"strict", "exact", "e"}, false);
      this.vanillaOnly = mlc.getBoolean(new String[]{"vanillaonly", "vanilla"}, false);
      this.inverted = mlc.getBoolean(new String[]{"inverted", "blacklist", "bl"}, false);
      this.runtime = mlc.getBoolean(new String[]{"runtime", "dynamic", "rt"}, false) || this.itemFilter != null && !this.itemFilter.isStatic();
      if (!this.runtime) {
         this.matcher = getPlugin().getBootstrap().getItemMatcher(mlc, this.conditionVar);
      }
   }

   @Override
   public boolean check(AbstractEntity e) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking HASITEM Condition...");
      if (!e.isPlayer()) {
         return false;
      } else {
         PlaceholderContext context = PlaceholderContext.builder().entity(e).build();
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(e.getBukkitEntity())
            ? Schedulers.of(e.getBukkitEntity()).supply(() -> this.checkPlayerInventory(e, context)).join()
            : this.checkPlayerInventory(e, context);
      }
   }

   @Override
   public boolean check(AbstractLocation l) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking HASITEM Condition...");
      Location location = BukkitAdapter.adapt(l);
      PlaceholderContext context = PlaceholderContext.builder().location(l).build();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
         Boolean result = Schedulers.of(location).supply(() -> this.checkContainerInventory(location, context)).join();
         return result != null ? result : false;
      } else {
         return this.checkContainerInventory(location, context);
      }
   }

   private boolean checkContainerInventory(Location location, PlaceholderContext context) {
      if (location.getBlock().getState() instanceof Container container) {
         return this.checkInventory(container.getInventory(), this.getMatcher(context), this.getAmount(context));
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Location " + location + " is not a container, returning FALSE");
         return false;
      }
   }

   @Override
   public boolean check(SkillMetadata data) {
      if (data.getCaster() != null && data.getCaster().getEntity() != null && data.getCaster().getEntity().isPlayer()) {
         AbstractEntity entity = data.getCaster().getEntity();
         PlaceholderContext context = PlaceholderContext.builder().meta(data).entity(entity).build();
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())
            ? Schedulers.of(entity.getBukkitEntity()).supply(() -> this.checkPlayerInventory(entity, context)).join()
            : this.checkPlayerInventory(entity, context);
      } else {
         return false;
      }
   }

   private boolean checkPlayerInventory(AbstractEntity entity, PlaceholderContext context) {
      Player player = BukkitAdapter.adapt(entity.asPlayer());
      return this.checkInventory(player.getInventory(), this.getMatcher(context), this.getAmount(context));
   }

   private boolean checkInventory(Inventory inventory, AbstractItemMatcher matcher, RangedInt amount) {
      if (matcher == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Matcher is null, something went wrong on load");
         return false;
      } else if (amount == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Amount could not be resolved, returning FALSE");
         return false;
      } else {
         int count = 0;

         for (ItemStack item : inventory.getContents()) {
            if (item != null && matcher.matches(BukkitAdapter.adapt(item))) {
               count += item.getAmount();
            }
         }

         return amount.equals(count);
      }
   }

   private RangedInt getAmount(PlaceholderContext context) {
      if (this.staticAmount != null) {
         return this.staticAmount;
      } else if (this.amountPlaceholder == null) {
         return null;
      } else {
         String resolved = this.amountPlaceholder.get(context);
         return resolved != null && !resolved.trim().isEmpty() ? this.runtimeAmountCache.computeIfAbsent(resolved.trim(), HasItemCondition::parseAmount) : null;
      }
   }

   private static RangedInt parseAmount(String value) {
      try {
         return new RangedInt(value);
      } catch (Exception e) {
         return null;
      }
   }

   private AbstractItemMatcher getMatcher(PlaceholderContext context) {
      if (!this.runtime) {
         return this.matcher;
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
