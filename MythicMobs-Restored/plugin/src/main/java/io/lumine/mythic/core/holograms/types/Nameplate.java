package io.lumine.mythic.core.holograms.types;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.holograms.HologramManager;
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
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.UUID;

public class Nameplate implements Terminable {
   private final ActiveMob activemob;
   private final HologramManager manager;
   private IHologram hologram;
   private float yOffset;
   private TerminableRegistry components = TerminableRegistry.create();

   public Nameplate(HologramExecutor manager, ActiveMob am) {
      this.manager = manager;
      this.activemob = am;
      this.yOffset = am.getType().getConfig().getFloat("Nameplate.Offset", manager.getPlugin().getConfiguration().getHologramNameplateOffset());
      this.yOffset = this.yOffset + manager.getPlugin().getConfiguration().getGlobalMountedHologramOffset();
      String scaleStr = am.getType().getConfig().getString("Nameplate.Scale", manager.getPlugin().getConfiguration().getHologramNameplateScale());
      AbstractVector scale = this.parseVector(scaleStr);
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_19_4)) {
         PacketTextDisplay.PacketTextDisplayBuilder options = PacketTextDisplay.create()
            .lineWidth(200)
            .backgroundColor(manager.getPlugin().getConfiguration().getHologramNameplateBackgroundColor())
            .billboard(manager.getPlugin().getConfiguration().getHologramNameplateBillboarding())
            .scale(scale)
            .brightness(15)
            .yOffset(this.yOffset)
            .interpolationDelay(1)
            .interpolationDuration(1);
         if (am.getType().getConfig().getBoolean("Nameplate.Mounted", true)) {
            this.hologram = manager.createHologram(this.activemob.getEntity(), am.getDisplayName(), options);
         } else {
            this.hologram = manager.createHologram(this.activemob.getEntity().getLocation(), am.getDisplayName(), options);
         }
      } else {
         this.yOffset = (float)(this.yOffset + am.getEntity().getNameplateMountOffset());
         this.hologram = manager.createHologram(
            "#TempNameplate" + this.activemob.getUniqueId().toString(), this.activemob.getEntity().getLocation(), am.getDisplayName()
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
      this.components.accept(Schedulers.async().runRepeating(task -> this.check(), 1L, 10L));
      this.components.accept(Schedulers.async().runRepeating(() -> this.teleport(), 1L, 1L));
   }

   public boolean check() {
      if (!this.activemob.isDead() && this.activemob.getEntity().isValid()) {
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
      this.components.terminate();
   }

   private AbstractLocation getLocation() {
      return this.activemob.getEntity().getEyeLocation().add(0.0, this.yOffset, 0.0);
   }

   AbstractVector parseVector(String vectorString) {
      String[] split = vectorString.split(",");
      double x = 0.0;
      double y = 0.0;
      double z = 0.0;
      if (split.length == 3) {
         try {
            x = Double.parseDouble(split[0]);
            y = Double.parseDouble(split[1]);
            z = Double.parseDouble(split[2]);
         } catch (NumberFormatException e) {
            MythicLogger.error("Invalid format nameplate scale for " + vectorString + " in" + this.activemob.getName());
         }
      } else {
         MythicLogger.error("Invalid format nameplate scale for " + vectorString + " in" + this.activemob.getName());
      }

      return new AbstractVector(x, y, z);
   }
}
