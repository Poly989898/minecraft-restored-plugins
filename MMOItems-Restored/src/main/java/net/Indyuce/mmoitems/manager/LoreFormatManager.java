package net.Indyuce.mmoitems.manager;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.tooltip.TooltipTexture;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoreFormatManager implements Reloadable {
   private final Map<String, List<String>> formats = new HashMap<>();
   private final Map<String, TooltipTexture> tooltips = new HashMap<>();

   @Override
   public void reload() {
      this.formats.clear();
      this.tooltips.clear();
      File file = new File(MMOItems.plugin.getDataFolder() + "/language/lore-formats");

      for (File listFile : file.listFiles()) {
         try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(listFile);
            Validate.isTrue(config.isList("lore-format"), "Invalid lore-format! (" + listFile.getName() + ")");
            this.formats.put(listFile.getName().substring(0, listFile.getName().length() - 4), config.getStringList("lore-format"));
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load layout '" + listFile.getName() + "': " + exception.getMessage());
         }
      }

      FileConfiguration config = new ConfigFile("tooltips").getConfig();

      for (String key : config.getKeys(false)) {
         try {
            TooltipTexture tooltipTexture = new TooltipTexture(config.getConfigurationSection(key));
            this.tooltips.put(tooltipTexture.getId(), tooltipTexture);
         } catch (Exception exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load tooltip '" + key + "': " + exception.getMessage());
         }
      }
   }

   public boolean hasFormat(@NotNull String text) {
      return this.formats.containsKey(text);
   }

   @NotNull
   public Collection<List<String>> getFormats() {
      return this.formats.values();
   }

   public boolean hasTooltip(@NotNull String text) {
      return this.tooltips.containsKey(text);
   }

   @NotNull
   public Collection<TooltipTexture> getTooltips() {
      return this.tooltips.values();
   }

   @Nullable
   public TooltipTexture getTooltip(@NotNull String text) {
      return this.tooltips.get(text);
   }

   @NotNull
   public List<String> getFormat(@NotNull MMOItem mmoItem) {
      if (mmoItem.hasData(ItemStats.LORE_FORMAT)) {
         List list = this.formats.get(mmoItem.getData(ItemStats.LORE_FORMAT).toString());
         if (list != null) {
            return list;
         }
      }

      if (mmoItem.getType().getLoreFormat() != null) {
         List list = this.formats.get(mmoItem.getType().getLoreFormat());
         if (list != null) {
            return list;
         }
      }

      return MMOItems.plugin.getLanguage().getDefaultLoreFormat();
   }

   @Deprecated
   @NotNull
   public List<String> getFormat(@NotNull String... texts) {
      for (String text : texts) {
         List list = this.formats.get(text);
         if (list != null) {
            return list;
         }
      }

      return MMOItems.plugin.getLanguage().getDefaultLoreFormat();
   }
}
