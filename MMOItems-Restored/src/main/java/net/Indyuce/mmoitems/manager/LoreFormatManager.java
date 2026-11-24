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
      File var1 = new File(MMOItems.plugin.getDataFolder() + "/language/lore-formats");

      for (File var5 : var1.listFiles()) {
         try {
            YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
            Validate.isTrue(var6.isList("lore-format"), "Invalid lore-format! (" + var5.getName() + ")");
            this.formats.put(var5.getName().substring(0, var5.getName().length() - 4), var6.getStringList("lore-format"));
         } catch (IllegalArgumentException var8) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load layout '" + var5.getName() + "': " + var8.getMessage());
         }
      }

      FileConfiguration var9 = new ConfigFile("tooltips").getConfig();

      for (String var11 : var9.getKeys(false)) {
         try {
            TooltipTexture var12 = new TooltipTexture(var9.getConfigurationSection(var11));
            this.tooltips.put(var12.getId(), var12);
         } catch (Exception var7) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load tooltip '" + var11 + "': " + var7.getMessage());
         }
      }
   }

   public boolean hasFormat(@NotNull String var1) {
      return this.formats.containsKey(var1);
   }

   @NotNull
   public Collection<List<String>> getFormats() {
      return this.formats.values();
   }

   public boolean hasTooltip(@NotNull String var1) {
      return this.tooltips.containsKey(var1);
   }

   @NotNull
   public Collection<TooltipTexture> getTooltips() {
      return this.tooltips.values();
   }

   @Nullable
   public TooltipTexture getTooltip(@NotNull String var1) {
      return this.tooltips.get(var1);
   }

   @NotNull
   public List<String> getFormat(@NotNull MMOItem var1) {
      if (var1.hasData(ItemStats.LORE_FORMAT)) {
         List var2 = this.formats.get(var1.getData(ItemStats.LORE_FORMAT).toString());
         if (var2 != null) {
            return var2;
         }
      }

      if (var1.getType().getLoreFormat() != null) {
         List var3 = this.formats.get(var1.getType().getLoreFormat());
         if (var3 != null) {
            return var3;
         }
      }

      return MMOItems.plugin.getLanguage().getDefaultLoreFormat();
   }

   @Deprecated
   @NotNull
   public List<String> getFormat(@NotNull String... var1) {
      for (String var5 : var1) {
         List var6 = this.formats.get(var5);
         if (var6 != null) {
            return var6;
         }
      }

      return MMOItems.plugin.getLanguage().getDefaultLoreFormat();
   }
}
