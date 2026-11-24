package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.itemtype.ItemType;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.block.BlockType;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestrictionManager implements MMOCoreManager {
   private final Map<String, RestrictionManager.ToolPermissions> map = new HashMap<>();
   @Nullable
   private RestrictionManager.ToolPermissions defaultPermissions;

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.map.clear();
         this.defaultPermissions = null;
      }

      FileConfiguration config = new ConfigFile("restrictions").getConfig();

      for (String key : config.getKeys(false)) {
         try {
            this.register(new RestrictionManager.ToolPermissions(config.getConfigurationSection(key)));
         } catch (IllegalArgumentException exception) {
            MMOCore.log(Level.WARNING, "Could not load perm set '" + key + "': " + exception.getMessage());
         }
      }

      for (RestrictionManager.ToolPermissions perms : this.map.values()) {
         try {
            perms.getPostLoadAction().performAction();
         } catch (IllegalArgumentException exception) {
            MMOCore.log(Level.WARNING, "Could not post-load perm set '" + perms.getTool().display() + "': " + exception.getMessage());
         }
      }
   }

   public void register(RestrictionManager.ToolPermissions perms) {
      this.map.put(perms.getTool().display(), perms);
      if (perms.isDefault()) {
         Validate.isTrue(this.defaultPermissions == null, "There is already a default tool permission set");
         this.defaultPermissions = perms;
      }
   }

   @Nullable
   public RestrictionManager.ToolPermissions getPermissions(ItemStack item) {
      String mapKey = ItemType.fromItemStack(item).display();
      RestrictionManager.ToolPermissions found = this.map.get(mapKey);
      return found == null ? this.defaultPermissions : found;
   }

   public boolean checkPermissions(ItemStack item, BlockType block) {
      RestrictionManager.ToolPermissions perms = this.getPermissions(item);
      return perms != null && perms.canMine(block);
   }

   private String formatId(String str) {
      return UtilityMethods.enumName(str.replace("?", ".").replace("%", "."));
   }

   public class ToolPermissions implements PreloadedObject {
      private final Set<BlockType> mineable = new HashSet<>();
      private final ItemType tool;
      private final boolean defaultSet;
      private RestrictionManager.ToolPermissions parent;
      private final PostLoadAction postLoadAction = new PostLoadAction(configx -> {
         if (configx.contains("parent")) {
            String parentFormat = RestrictionManager.this.formatId(configx.getString("parent"));
            this.parent = Objects.requireNonNull(RestrictionManager.this.map.get(parentFormat), "Could not find parent with ID '" + parentFormat + "'");
         }

         if (configx.contains("can-mine")) {
            for (String key : configx.getStringList("can-mine")) {
               this.mineable.add(MMOCore.plugin.loadManager.loadBlockType(new MMOLineConfig(key)));
            }
         }
      });

      public ToolPermissions(ConfigurationSection config) {
         this.postLoadAction.cacheConfig(config);
         this.tool = ItemType.fromString(config.getName());
         this.defaultSet = config.getBoolean("default");
      }

      @NotNull
      public PostLoadAction getPostLoadAction() {
         return this.postLoadAction;
      }

      public boolean canMine(@NotNull BlockType type) {
         RestrictionManager.ToolPermissions parent;
         return this.mineable.contains(type) || (parent = this.getParent()) != null && parent.canMine(type);
      }

      public RestrictionManager.ToolPermissions getParent() {
         return this.parent != null ? this.parent : (this.defaultSet ? null : RestrictionManager.this.defaultPermissions);
      }

      public ItemType getTool() {
         return this.tool;
      }

      public boolean isDefault() {
         return this.defaultSet;
      }
   }
}
