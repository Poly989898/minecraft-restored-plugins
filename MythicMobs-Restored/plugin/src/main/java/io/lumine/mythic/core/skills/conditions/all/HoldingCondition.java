package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@MythicCondition(
   author = "Ashijin",
   name = "holding",
   aliases = {},
   description = "Checks if the target is holding a given material"
)
public class HoldingCondition extends SkillCondition implements IEntityCondition, ISkillMetaCondition {
   private final PlaceholderString itemFilter;
   private final boolean strict;
   private final boolean vanillaOnly;
   private final boolean inverted;
   private final boolean runtime;
   private final Map<String, AbstractItemMatcher> runtimeMatcherCache = new ConcurrentHashMap<>();
   @MythicField(
      name = "types",
      aliases = {"types", "type", "t", "material", "mat", "m", "item", "i"},
      description = "A list of materials to check"
   )
   private AbstractItemMatcher matcher;

   public HoldingCondition(String line, MythicLineConfig mlc) {
      super(line);
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
   public boolean check(AbstractEntity target) {
      AbstractItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().entity(target).build());
      return this.checkHolding(target, matcher);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (meta.getCaster() != null && meta.getCaster().getEntity() != null) {
         AbstractEntity target = meta.getCaster().getEntity();
         AbstractItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().meta(meta).entity(target).build());
         return this.checkHolding(target, matcher);
      } else {
         return false;
      }
   }

   private boolean checkHolding(AbstractEntity target, AbstractItemMatcher matcher) {
      if (matcher == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Matcher is null, something went wrong on load");
         return false;
      } else if (!target.isLiving()) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Target is not living entity");
         return false;
      } else {
         Optional<AbstractItemStack> maybeItem = target.getItemMainHand();
         if (maybeItem.isEmpty()) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "No item in hand");
            return false;
         } else {
            AbstractItemStack holding = maybeItem.get();
            return matcher.matches(holding);
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
