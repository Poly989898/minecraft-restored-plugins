package io.lumine.mythic.lib.glow.provided;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.glow.GlowModule;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class MythicGlowModule implements GlowModule {
   private final Map<ChatColor, Team> scoreboardTeams = new HashMap<>();
   private static final NamespacedKey COLOR_TAG_PATH = new NamespacedKey(MythicLib.plugin, "glow_color");
   private int counter;

   @Override
   public void enable() {
      Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

      for (ChatColor color : ChatColor.values()) {
         if (color.isColor()) {
            String teamName = this.getTeamName(color);
            Team team = scoreboard.getTeam(teamName) == null ? scoreboard.registerNewTeam(teamName) : scoreboard.getTeam(teamName);
            team.setColor(color);
            this.scoreboardTeams.put(color, team);
         }
      }
   }

   private String getTeamName(ChatColor color) {
      Validate.isTrue(color.isColor(), "Not a color");
      return "ml_glow_" + this.counter++;
   }

   @Override
   public void disable() {
      this.scoreboardTeams.forEach((color, team) -> team.unregister());
   }

   @Override
   public void setGlowing(Entity entity, ChatColor color) {
      Validate.isTrue(color.isColor(), "Not a color");
      this.scoreboardTeams.get(color).addEntry(entity.getUniqueId().toString());
      entity.getPersistentDataContainer().set(COLOR_TAG_PATH, PersistentDataType.STRING, color.name());
      entity.setGlowing(true);
   }

   @Override
   public void disableGlowing(Entity entity) {
      entity.getPersistentDataContainer().remove(COLOR_TAG_PATH);
      entity.setGlowing(false);
   }
}
