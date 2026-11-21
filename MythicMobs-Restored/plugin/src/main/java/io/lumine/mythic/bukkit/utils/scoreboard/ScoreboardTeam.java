package io.lumine.mythic.bukkit.utils.scoreboard;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.Objects;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@NonnullByDefault
public interface ScoreboardTeam {
   String getId();

   boolean shouldAutoSubscribe();

   void setAutoSubscribe(boolean flag);

   String getDisplayName();

   void setDisplayName(String str);

   String getPrefix();

   void setPrefix(String str);

   String getSuffix();

   void setSuffix(String str);

   boolean isAllowFriendlyFire();

   void setAllowFriendlyFire(boolean flag);

   boolean isCanSeeFriendlyInvisibles();

   void setCanSeeFriendlyInvisibles(boolean flag);

   ScoreboardTeam.NameTagVisibility getNameTagVisibility();

   void setNameTagVisibility(ScoreboardTeam.NameTagVisibility nameTagVisibility);

   ScoreboardTeam.CollisionRule getCollisionRule();

   void setCollisionRule(ScoreboardTeam.CollisionRule collisionRule);

   ChatColor getColor();

   void setColor(ChatColor chatColor);

   boolean addPlayer(String str);

   boolean removePlayer(String str);

   boolean hasPlayer(String str);

   default boolean addPlayer(Player player) {
      return this.addPlayer(Objects.requireNonNull(player, "player").getName());
   }

   default boolean removePlayer(Player player) {
      return this.removePlayer(Objects.requireNonNull(player, "player").getName());
   }

   default boolean hasPlayer(Player player) {
      return this.hasPlayer(Objects.requireNonNull(player, "player").getName());
   }

   Set<String> getPlayers();

   void subscribe(Player player);

   void unsubscribe(Player player);

   void unsubscribe(Player player, boolean flag);

   void unsubscribeAll();

   public static enum CollisionRule {
      ALWAYS("always"),
      NEVER("never"),
      PUSH_OTHER_TEAMS("pushOtherTeams"),
      PUSH_OWN_TEAM("pushOwnTeam");

      private final String protocolName;

      private CollisionRule(String protocolName) {
         this.protocolName = protocolName;
      }

      public String getProtocolName() {
         return this.protocolName;
      }
   }

   public static enum NameTagVisibility {
      ALWAYS("always"),
      NEVER("never"),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam");

      private final String protocolName;

      private NameTagVisibility(String protocolName) {
         this.protocolName = protocolName;
      }

      public String getProtocolName() {
         return this.protocolName;
      }
   }
}
