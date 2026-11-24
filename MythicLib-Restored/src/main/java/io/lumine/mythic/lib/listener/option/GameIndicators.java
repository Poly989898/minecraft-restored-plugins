package io.lumine.mythic.lib.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.IndicatorDisplayEvent;
import io.lumine.mythic.lib.hologram.Hologram;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class GameIndicators implements Listener {
   private final String format;
   private final DecimalFormat decimalFormat;
   private final boolean move;
   public final double radialVelocity;
   public final double gravity;
   public final double initialUpwardVelocity;
   public final double entityHeightPercent;
   public final double yOffset;
   public final double rOffset;
   public final double entityWidthPercent;
   public final long lifespan;
   public final long tickPeriod;
   protected static final Random RANDOM = new Random();

   public GameIndicators(ConfigurationSection config) {
      this.decimalFormat = MythicLib.plugin.getMMOConfig().newDecimalFormat(config.getString("decimal-format"));
      this.format = config.getString("format");
      this.radialVelocity = config.getDouble("radial-velocity", 1.0);
      this.gravity = config.getDouble("gravity", 1.0);
      this.initialUpwardVelocity = config.getDouble("initial-upward-velocity", 1.0);
      this.entityHeightPercent = config.getDouble("entity-height-percent", 0.75);
      this.entityWidthPercent = config.getDouble("entity-width-percent", 0.75);
      this.yOffset = config.getDouble("y-offset", 0.1);
      this.rOffset = config.getDouble("r-offset", 0.1);
      this.move = config.getBoolean("move", true);
      this.lifespan = config.getLong("lifespan", 20L);
      this.tickPeriod = config.getLong("tick-period", 3L);
   }

   @NotNull
   public String formatNumber(double d) {
      return this.decimalFormat.format(d);
   }

   @NotNull
   public String getRaw() {
      return this.format;
   }

   public void displayIndicator(@NotNull Entity entity, @NotNull String message, @NotNull Vector dir, @NotNull IndicatorDisplayEvent.IndicatorType type) {
      IndicatorDisplayEvent called = new IndicatorDisplayEvent(entity, message, type);
      Bukkit.getPluginManager().callEvent(called);
      if (!called.isCancelled()) {
         double a = RANDOM.nextDouble() * 2.0 * Math.PI;
         double width = (entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ()) / 2.0;
         double r = this.rOffset + width * this.entityWidthPercent;
         double h = this.yOffset + entity.getHeight() * this.entityHeightPercent;
         Location loc = entity.getLocation().add(Math.cos(a) * r, h, Math.sin(a) * r);
         Hologram holo = Hologram.create(loc, Collections.singletonList(MythicLib.plugin.parseColors(called.getMessage())));
         if (!this.move) {
            Bukkit.getScheduler().runTaskLater(MythicLib.plugin, holo::despawn, this.lifespan);
         } else {
            holo.flyOut(this, dir);
         }
      }
   }
}
