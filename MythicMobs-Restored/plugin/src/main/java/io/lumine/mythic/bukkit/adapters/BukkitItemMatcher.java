package io.lumine.mythic.bukkit.adapters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemMatcher;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class BukkitItemMatcher extends AbstractItemMatcher {
   @Getter
   private final Collection<Material> materials = Sets.newHashSet();
   @Getter
   private final Collection<AbstractItemStack> specificItems = Sets.newHashSet();
   @Getter
   private final Collection<Tag<Material>> tags = Sets.newHashSet();
   private boolean strict = false;
   private boolean vanillaOnly = false;
   private boolean inverted = false;

   public BukkitItemMatcher(MythicLineConfig config, String conditionVar) {
      this.strict = config.getBoolean(new String[]{"strict", "exact", "e"}, false);
      this.vanillaOnly = config.getBoolean(new String[]{"vanillaonly", "vanilla"}, false);
      this.inverted = config.getBoolean(new String[]{"inverted", "blacklist", "bl"}, false);
      this.loadFromConfig(config, conditionVar);
      MythicBukkit.inst().getClock().queuePostReload(() -> this.loadFromConfig(config, conditionVar));
   }

   private void loadFromConfig(MythicLineConfig config, String conditionVar) {
      this.materials.clear();
      this.specificItems.clear();
      this.tags.clear();
      List<String> list = config.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "item", "i"}, null, conditionVar);
      if (list != null) {
         for (String line : list) {
            this.parse(line);
         }
      }
   }

   public BukkitItemMatcher(String filter, boolean strict) {
      this.strict = strict;
      String[] split = filter.split(",");

      for (String line : split) {
         this.parse(line);
      }
   }

   public BukkitItemMatcher(Collection<String> filter, boolean strict) {
      this.strict = strict;

      for (String line : filter) {
         this.parse(line);
      }
   }

   public BukkitItemMatcher(String filter, boolean strict, boolean vanillaOnly, boolean inverted) {
      this.strict = strict;
      this.vanillaOnly = vanillaOnly;
      this.inverted = inverted;
      String[] split = filter.split(",");

      for (String line : split) {
         this.parse(line);
      }
   }

   private void parse(String filter) {
      if (filter.startsWith("#")) {
         String raw = filter.substring(1).toLowerCase(Locale.ROOT);
         if (!raw.contains(":")) {
            raw = "minecraft:" + raw;
         }

         NamespacedKey key = NamespacedKey.fromString(raw);
         Tag<Material> tag = Bukkit.getTag("items", key, Material.class);
         if (tag == null) {
            MythicLogger.error("[ItemType] ItemTag '#" + key + "' does not exist");
         } else {
            this.tags.add(tag);
         }
      } else if (filter.contains("*")) {
         String regex = "^" + filter.replace("*", ".*") + "$";
         Pattern pattern = Pattern.compile(regex, 2);

         for (Material m : Material.values()) {
            if (m.isItem() && pattern.matcher(m.name()).matches()) {
               this.materials.add(m);
            }
         }

         for (MythicItem item : MythicBukkit.inst().getItemManager().getItems()) {
            if (pattern.matcher(item.getInternalName()).matches()) {
               this.specificItems.add(item.generateItemStack(1));
            }
         }
      } else {
         String name = filter.contains(":") ? filter.substring(filter.indexOf(58) + 1) : filter;
         Material mat = Material.getMaterial(name.toUpperCase(Locale.ROOT));
         if (mat != null) {
            this.materials.add(mat);
         } else {
            AbstractItemStack custom = MythicBukkit.inst().getBootstrap().createItem(filter);
            if (custom != null) {
               this.specificItems.add(custom);
            } else {
               MythicLogger.error("[ItemType] filter \u201c" + filter + "\u201d did not match any material or custom item");
            }
         }
      }
   }

   @Override
   public boolean matches(AbstractItemStack stack) {
      if (stack instanceof BukkitItemStack bukkitItemStack) {
         if (this.vanillaOnly && bukkitItemStack.getMythicItemType().isPresent()) {
            return false;
         } else if (!this.materials.isEmpty()
            && (!this.strict || bukkitItemStack.getMythicItemType().isEmpty())
            && this.materials.contains(bukkitItemStack.getItemStack().getType())) {
            return true;
         } else {
            if (!this.specificItems.isEmpty()) {
               for (AbstractItemStack item : this.specificItems) {
                  if (item.isSimilar(stack)) {
                     return true;
                  }
               }
            }

            if (!this.tags.isEmpty()) {
               Material material = bukkitItemStack.getItemStack().getType();

               for (Tag<Material> tag : this.tags) {
                  Log.info("[ItemMatcher] Check tag {0}", tag.getKey().toString());
                  if (tag.isTagged(material)) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean matches(ItemStack stack) {
      Material material = stack.getType();
      BukkitItemStack bukkitItemStack = BukkitAdapter.adapt(stack);
      if (this.vanillaOnly && bukkitItemStack.getMythicItemType().isPresent()) {
         return false;
      } else if (!this.materials.isEmpty() && this.materials.contains(material)) {
         return true;
      } else {
         if (!this.tags.isEmpty()) {
            for (Tag<Material> tag : this.tags) {
               if (tag.isTagged(material)) {
                  return true;
               }
            }
         }

         if (!this.specificItems.isEmpty()) {
            for (AbstractItemStack item : this.specificItems) {
               if (item.isSimilar(bukkitItemStack)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public boolean matches(SkillMetadata skillMetadata) {
      Optional<Object> maybeItem = skillMetadata.getMetadata("item");
      if (maybeItem.isPresent()) {
         ItemStack itemStack = (ItemStack)maybeItem.get();
         BukkitItemStack aItemStack = BukkitAdapter.adapt(itemStack);
         return this.matches(aItemStack);
      } else if (skillMetadata.getVariables().has("equip-item")) {
         ItemStack itemStack = (ItemStack)skillMetadata.getVariables().get("equip-item").get();
         BukkitItemStack aItemStack = BukkitAdapter.adapt(itemStack);
         return this.matches(aItemStack);
      } else {
         return false;
      }
   }

   @Override
   public boolean matches(AbstractEntity entity) {
      if (entity.getBukkitEntity() instanceof Item item) {
         ItemStack itemStack = item.getItemStack();
         return this.matches(BukkitAdapter.adapt(itemStack));
      } else {
         return false;
      }
   }
}
