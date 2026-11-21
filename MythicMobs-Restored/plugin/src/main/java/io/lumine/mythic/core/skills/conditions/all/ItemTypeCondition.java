package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitItemMatcher;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@MythicCondition(
   author = "Ashijin",
   name = "itemType",
   aliases = {},
   description = "Tests the item type that triggered the skill"
)
public class ItemTypeCondition extends SkillCondition implements ISkillMetaCondition, IEntityCondition {
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

   public ItemTypeCondition(String line, MythicLineConfig mlc) {
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
   public boolean check(SkillMetadata meta) {
      AbstractItemMatcher matcher = this.getMatcher(this.getMetaContext(meta));
      return matcher == null ? false : matcher.matches(meta);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      AbstractItemMatcher matcher = this.getMatcher(PlaceholderContext.builder().entity(entity).build());
      if (matcher == null) {
         return false;
      } else {
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())
            ? Schedulers.of(entity.getBukkitEntity()).supply(() -> matcher.matches(entity)).join()
            : matcher.matches(entity);
      }
   }

   private PlaceholderContext getMetaContext(SkillMetadata meta) {
      PlaceholderContext.Builder builder = PlaceholderContext.builder().meta(meta);
      if (meta.getCaster() != null && meta.getCaster().getEntity() != null) {
         builder.entity(meta.getCaster().getEntity());
      }

      return builder.build();
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
