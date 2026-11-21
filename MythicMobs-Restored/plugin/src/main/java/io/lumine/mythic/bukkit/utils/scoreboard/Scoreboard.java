package io.lumine.mythic.bukkit.utils.scoreboard;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

@NonnullByDefault
public interface Scoreboard {
   ScoreboardTeam createTeam(String str, String str2, boolean flag);

   default ScoreboardTeam createTeam(String id, String title) {
      return this.createTeam(id, title, true);
   }

   default ScoreboardTeam createTeam(String title, boolean autoSubscribe) {
      return this.createTeam(Long.toHexString(System.nanoTime()), title, autoSubscribe);
   }

   default ScoreboardTeam createTeam(String title) {
      return this.createTeam(title, true);
   }

   @Nullable
   ScoreboardTeam getTeam(String str);

   boolean removeTeam(String str);

   ScoreboardObjective createObjective(String str, String str2, DisplaySlot displaySlot, boolean flag);

   default ScoreboardObjective createObjective(String id, String title, DisplaySlot displaySlot) {
      return this.createObjective(id, title, displaySlot, true);
   }

   default ScoreboardObjective createObjective(String title, DisplaySlot displaySlot, boolean autoSubscribe) {
      return this.createObjective(Long.toHexString(System.nanoTime()), title, displaySlot, autoSubscribe);
   }

   default ScoreboardObjective createObjective(String title, DisplaySlot displaySlot) {
      return this.createObjective(title, displaySlot, true);
   }

   @Nullable
   ScoreboardObjective getObjective(String str);

   boolean removeObjective(String str);

   ScoreboardTeam createPlayerTeam(Player player, String str, String str2, boolean flag);

   default ScoreboardTeam createPlayerTeam(Player player, String id, String title) {
      return this.createPlayerTeam(player, id, title, true);
   }

   default ScoreboardTeam createPlayerTeam(Player player, String title, boolean autoSubscribe) {
      return this.createPlayerTeam(player, Long.toHexString(System.nanoTime()), title, autoSubscribe);
   }

   default ScoreboardTeam createPlayerTeam(Player player, String title) {
      return this.createPlayerTeam(player, title, true);
   }

   @Nullable
   ScoreboardTeam getPlayerTeam(Player player, String str);

   boolean removePlayerTeam(Player player, String str);

   ScoreboardObjective createPlayerObjective(Player player, String str, String str2, DisplaySlot displaySlot, boolean flag);

   default ScoreboardObjective createPlayerObjective(Player player, String id, String title, DisplaySlot displaySlot) {
      return this.createPlayerObjective(player, id, title, displaySlot, true);
   }

   default ScoreboardObjective createPlayerObjective(Player player, String title, DisplaySlot displaySlot, boolean autoSubscribe) {
      return this.createPlayerObjective(player, Long.toHexString(System.nanoTime()), title, displaySlot, autoSubscribe);
   }

   default ScoreboardObjective createPlayerObjective(Player player, String title, DisplaySlot displaySlot) {
      return this.createPlayerObjective(player, title, displaySlot, true);
   }

   @Nullable
   ScoreboardObjective getPlayerObjective(Player player, String str);

   boolean removePlayerObjective(Player player, String str);
}
