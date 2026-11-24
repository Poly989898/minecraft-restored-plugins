package io.lumine.mythic.lib.comp.placeholder;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.manager.StatManager;
import io.lumine.mythic.lib.util.DefenseFormula;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {
   @NotNull
   public String getIdentifier() {
      return "mythiclib";
   }

   @NotNull
   public String getAuthor() {
      return "Indyuce";
   }

   @NotNull
   public String getVersion() {
      return MythicLib.plugin.getDescription().getVersion();
   }

   public String onPlaceholderRequest(Player player, @NotNull String params) {
      if (params.startsWith("space_")) {
         return UtilityMethods.getFontSpace(Integer.parseInt(params.substring(6)));
      } else if (player == null) {
         return null;
      } else if (params.startsWith("defense_damage_reduction")) {
         double defenseStat = MMOPlayerData.get(player).getStatMap().getStat("DEFENSE");
         double damageReduction = 100.0 - DefenseFormula.calculateDamage(false, defenseStat, 100.0);
         return MythicLib.plugin.getMMOConfig().decimal.format(damageReduction);
      } else if (params.startsWith("raw_stat_")) {
         String stat = UtilityMethods.enumName(params.substring(9));
         return String.valueOf(MMOPlayerData.get(player).getStatMap().getInstance(stat).getFinal());
      } else if (params.startsWith("stat_")) {
         String stat = UtilityMethods.enumName(params.substring(5));
         return StatManager.format(stat, MMOPlayerData.get(player));
      } else if (params.startsWith("cooldown_")) {
         String key = params.substring(9);
         return MythicLib.plugin.getMMOConfig().decimal.format(MMOPlayerData.get(player).getCooldownMap().getCooldown(key));
      } else {
         return null;
      }
   }
}
