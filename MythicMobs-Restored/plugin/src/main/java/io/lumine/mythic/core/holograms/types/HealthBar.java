package io.lumine.mythic.core.holograms.types;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.holograms.HologramExecutor;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.UUID;
import org.bukkit.ChatColor;

public class HealthBar implements Terminable {
   private final ActiveMob activemob;
   private final HologramExecutor manager;
   private IHologram hologram;
   private float yOffset;
   private TerminableRegistry components = TerminableRegistry.create();

   public HealthBar(HologramExecutor manager, ActiveMob am) {
      this.manager = manager;
      this.activemob = am;
      this.yOffset = am.getType().getConfig().getFloat("HealthBar.Offset", manager.getPlugin().getConfiguration().getHologramHealthBarOffset());
      this.yOffset = this.yOffset + manager.getPlugin().getConfiguration().getGlobalMountedHologramOffset();
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
         PacketTextDisplay.PacketTextDisplayBuilder options = PacketTextDisplay.create()
            .lineWidth(200)
            .backgroundColor(manager.getPlugin().getConfiguration().getHologramHealthBarBackgroundColor())
            .billboard(manager.getPlugin().getConfiguration().getHologramHealthBarBillboarding())
            .scale(manager.getPlugin().getConfiguration().getHologramHealthBarScale())
            .brightnessBitshifted(-1)
            .yOffset(this.yOffset);
         this.hologram = manager.createHologram(this.activemob.getEntity(), this.getLine(), options);
      } else {
         this.yOffset = (float)(this.yOffset + am.getEntity().getNameplateMountOffset());
         this.hologram = manager.createHologram(
            "#TempHealthBar" + this.activemob.getUniqueId().toString(), this.activemob.getEntity().getLocation(), this.getLine()
         );
      }

      this.components.accept(this.hologram);
      this.components.accept(Events.subscribe(MythicMobDeathEvent.class).handler(event -> {
         UUID uuid = event.getEntity().getUniqueId();
         if (this.activemob.getUniqueId().equals(uuid)) {
            this.terminate();
         }
      }));
      this.components.accept(Events.subscribe(MythicMobDespawnEvent.class).handler(event -> {
         UUID uuid = event.getEntity().getUniqueId();
         if (this.activemob.getUniqueId().equals(uuid)) {
            this.terminate();
         }
      }));
      this.components.accept(Schedulers.async().runRepeating(() -> this.check(), 1L, 10L));
      this.components.accept(Schedulers.async().runRepeating(() -> this.teleport(), 1L, 1L));
   }

   public boolean check() {
      if (!this.activemob.isDead()
         && this.activemob.getEntity().isValid()
         && this.activemob.getEntity().getHealth() != this.activemob.getEntity().getMaxHealth()) {
         return true;
      } else {
         this.terminate();
         return false;
      }
   }

   public void teleport() {
      AbstractLocation location = this.getLocation();
      if (location == null) {
         this.terminate();
      } else {
         this.hologram.teleport(this.getLocation());
      }
   }

   @Override
   public void close() {
      this.components.closeAndReportException();
   }

   private AbstractLocation getLocation() {
      return this.activemob.getEntity().getEyeLocation().add(0.0, this.yOffset, 0.0);
   }

   public void update() {
      this.hologram.setText(this.getLine());
   }

   private String getLine() {
      int hp = (int)this.activemob.getEntity().getHealth();
      double percent = this.activemob.getEntity().getHealth() / this.activemob.getEntity().getMaxHealth();
      String sHP = String.valueOf(hp);
      int hplength = sHP.length();
      int length = this.manager.getPlugin().getConfiguration().getHologramHealthBarLength();
      int gray = (int)(length * percent);
      StringBuilder line = new StringBuilder().append(ChatColor.DARK_RED).append("[");

      for (int i = 0; i < length; i++) {
         if (i < gray) {
            line.append(ChatColor.RED);
         } else {
            line.append(ChatColor.DARK_GRAY);
         }

         if (i >= (length - hplength) / 2 && i < (length + hplength) / 2) {
            line.append(ChatColor.DARK_RED).append(sHP.charAt(i - (length - hplength) / 2));
         } else {
            line.append("|");
         }
      }

      return line.append(ChatColor.DARK_RED).append("]").toString();
   }
}
