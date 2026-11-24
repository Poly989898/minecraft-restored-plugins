package me.TechsCode.UltraPermissions.commands;

import java.util.ArrayList;
import java.util.List;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;

public class CommandOptions {
   private String world = null;
   private String server = null;
   private String operator;
   private long expiration = 0L;
   private final long secs;

   public CommandOptions(UltraPermissions ultraPermissions, List<String> texts) {
      this(ultraPermissions, texts.toArray(new String[0]));
   }

   public CommandOptions(UltraPermissions ultraPermissions, String[] texts) {
      this.operator = "";
      if (ArrayUtils.contains(texts, "local")) {
         this.server = ultraPermissions.getThisServer().map(NServer::getName).orElse(null);
      }

      if (ArrayUtils.contains(texts, "bungee")) {
         this.server = "BungeeCord";
      }

      this.secs = Tools.getTimeSecondsFromString(String.join(" ", texts));
      if (this.secs != 0L) {
         this.expiration = System.currentTimeMillis() + this.secs * 1000L;
      }

      Bukkit.getWorlds().stream().filter(world -> ArrayUtils.contains(texts, world.getName())).forEach(world -> this.world = world.getName());
   }

   public String getWorld() {
      return this.world;
   }

   public String getServer() {
      return this.server;
   }

   public long getExpiration() {
      return this.expiration;
   }

   public long getSecs() {
      return this.secs;
   }

   public String getCombinedInfo() {
      ArrayList list = new ArrayList();
      if (this.expiration != 0L) {
         list.add(Tools.getTimeString(this.secs));
      }

      if (this.server != null) {
         list.add(this.server);
      }

      if (this.world != null) {
         list.add(this.world);
      }

      return list.isEmpty()
         ? ""
         : Colors.GRAY + " (" + Colors.YELLOW + this.operator + String.join(Colors.DARK_GRAY + " | " + Colors.YELLOW, list) + Colors.GRAY + ")";
   }

   public void setOperator(String text) {
      this.operator = text;
   }

   public String getOperator() {
      return this.operator;
   }
}
