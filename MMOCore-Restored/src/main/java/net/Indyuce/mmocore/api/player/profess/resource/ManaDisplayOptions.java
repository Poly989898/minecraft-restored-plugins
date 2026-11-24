package net.Indyuce.mmocore.api.player.profess.resource;

import io.lumine.mythic.lib.MythicLib;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class ManaDisplayOptions {
   private final ChatColor full;
   private final ChatColor half;
   private final ChatColor empty;
   private final String name;
   private final String icon;
   private final char barCharacter;
   public static final ManaDisplayOptions DEFAULT = new ManaDisplayOptions(
      ChatColor.AQUA, ChatColor.BLUE, ChatColor.WHITE, "Mana", "■".charAt(0), ChatColor.BLUE + "★"
   );

   public ChatColor getFull() {
      return this.full;
   }

   public ChatColor getHalf() {
      return this.half;
   }

   public ChatColor getEmpty() {
      return this.empty;
   }

   public ManaDisplayOptions(ConfigurationSection config) {
      Validate.notNull(config, "Could not load mana display options");
      this.name = config.getString("name");
      Validate.notNull(this.name, "Could not load mana name");
      Validate.notNull(config.getConfigurationSection("color"), "Could not find mana color config");
      this.full = ChatColor.valueOf(config.getString("color.full", "NO_INPUT"));
      this.half = ChatColor.valueOf(config.getString("color.half", "NO_INPUT"));
      this.empty = ChatColor.valueOf(config.getString("color.empty", "NO_INPUT"));
      String format = config.getString("char", "");
      Validate.notEmpty(format, "Could not load mana bar character");
      this.barCharacter = format.charAt(0);
      this.icon = MythicLib.plugin.parseColors(config.getString("icon", ""));
      Validate.notEmpty(format, "Could not load mana action bar icon");
   }

   public ManaDisplayOptions(ChatColor full, ChatColor half, ChatColor empty, String name, char barCharacter, String icon) {
      Validate.notNull(full, "Color cannot be null");
      Validate.notNull(half, "Color cannot be null");
      Validate.notNull(empty, "Color cannot be null");
      Validate.notNull(name, "Name cannot be null");
      Validate.notNull(barCharacter, "Bar character cannot be null");
      this.full = full;
      this.half = half;
      this.empty = empty;
      this.name = name;
      this.barCharacter = barCharacter;
      this.icon = icon;
   }

   public String getName() {
      return this.name;
   }

   public String getIcon() {
      return this.icon;
   }

   public String generateBar(double mana, double max) {
      StringBuilder format = new StringBuilder();
      double ratio = 20.0 * mana / max;

      for (double j = 1.0; j < 20.0; j++) {
         format.append(ratio >= j ? this.full : (ratio >= j - 0.5 ? this.half : this.empty)).append(this.barCharacter);
      }

      return format.toString();
   }
}
