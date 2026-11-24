package me.TechsCode.UltraPermissions.permissionlogger;

import org.bukkit.entity.Player;

public class LoggedPermission {
   private final Player p;
   private final String permission;
   private final boolean outcome;
   private final long time;
   private final String source;

   public LoggedPermission(Player player, String permission, boolean outcome, long time, String source) {
      this.p = player;
      this.permission = permission;
      this.outcome = outcome;
      this.time = time;
      this.source = source;
   }

   public Player getPlayer() {
      return this.p;
   }

   public String getPermission() {
      return this.permission;
   }

   public boolean getOutcome() {
      return this.outcome;
   }

   public String getSource() {
      return this.source;
   }

   public long getTime() {
      return this.time;
   }
}
