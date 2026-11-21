package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "jaylawl",
   name = "wearing",
   aliases = {"iswearing", "wielding", "iswielding"},
   version = "4.5",
   description = "Tests what the target entity has equipped."
)
public class WearingCondition extends SkillCondition implements IEntityCondition, ISkillMetaCondition {
   private final PlaceholderString itemFilter;
   private final boolean strict;
   private final boolean vanillaOnly;
   private final boolean inverted;
   private final boolean runtime;
   private final Map<String, AbstractItemMatcher> runtimeMatcherCache = new ConcurrentHashMap<>();
   @MythicField(
      name = "armorslot",
      aliases = {"slot", "s"},
      description = "The item slot to check",
      defValue = "HEAD"
   )
   private EquipSlot slot;
   private final String slotName;
   @MythicField(
      name = "types",
      aliases = {"types", "type", "t", "material", "mat", "m", "item", "i"},
      description = "A list of materials to check"
   )
   private AbstractItemMatcher matcher;

   public WearingCondition(String line, MythicLineConfig mlc) {
      super(line);
      String s = mlc.getString(new String[]{"armorslot", "slot", "s"}, "HEAD").toUpperCase();
      this.slotName = s;
      this.slot = EquipSlot.of(s);
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
      AbstractItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().entity(e).build());
      return this.checkWearing(e, matcher);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getCaster() != null && meta.getCaster().getEntity() != null) {
         AbstractEntity entity = meta.getCaster().getEntity();
         AbstractItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().meta(meta).entity(entity).build());
         return this.checkWearing(entity, matcher);
      } else {
         return false;
      }
   }

   private boolean checkWearing(AbstractEntity e, AbstractItemMatcher matcher) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking WEARING Condition...");
      if (matcher == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Matcher is null, something went wrong on load");
         return false;
      } else {
         if (this.slot == EquipSlot.NONE) {
            EquipSlot resolvedSlot = EquipSlot.of(this.slotName);
            if (resolvedSlot == EquipSlot.NONE) {
               MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Invalid slot supplied");
               return false;
            }

            this.slot = resolvedSlot;
         }

         AbstractItemStack slotitem = this.slot.get(e).orElse(null);
         slotitem = (AbstractItemStack)(slotitem == null ? BukkitAdapter.adapt(new ItemStack(Material.AIR)) : slotitem);
         if (matcher.matches(slotitem)) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "+ Items are similar, returning true");
            return true;
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "+ Items are not similar, returning false");
            return false;
         }
      }
   }

   private AbstractItemMatcher getMatcher(PlaceholderContext context) {
      if (!this.runtime) {
         return this.matcher;
      } else if (this.itemFilter == null) {
         return null;
      } else {
         String resolvedFilter = this.itemFilter.get(context);
         if (resolvedFilter != null && !resolvedFilter.isBlank()) {
            String filter = sanitizeFilter(resolvedFilter);
            if (containsUnresolvedPlaceholder(filter)) {
               return null;
            } else {
               String cacheKey = filter.toLowerCase();

               try {
                  return this.runtimeMatcherCache.computeIfAbsent(cacheKey, key -> new BukkitItemMatcher(filter, this.strict, this.vanillaOnly, this.inverted));
               } catch (IllegalArgumentException e) {
                  MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Failed to build runtime item matcher for filter: {0}", filter);
                  return null;
               }
            }
         } else {
            return null;
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

   private static boolean containsUnresolvedPlaceholder(String filter) {
      return filter.indexOf(60) >= 0 && filter.indexOf(62) > filter.indexOf(60);
   }
}
