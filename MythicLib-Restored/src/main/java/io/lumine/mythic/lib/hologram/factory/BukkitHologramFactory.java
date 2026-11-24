package io.lumine.mythic.lib.hologram.factory;

import com.google.common.base.Preconditions;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import io.lumine.mythic.lib.listener.option.GameIndicators;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BukkitHologramFactory implements HologramFactory {
   public BukkitHologramFactory() {
      Validate.isTrue(MythicLib.plugin.getVersion().isAbove(1, 19, 4), "Text displays are only available on 1.19.4+");
   }

   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new BukkitHologramFactory.HologramImpl(loc, lines);
   }

   private static final class HologramImpl extends Hologram {
      private final List<String> lines = new ArrayList<>();
      private final List<TextDisplay> spawnedEntities = new ArrayList<>();
      private Location loc;
      private boolean spawned = false;
      private static final double LINE_OFFSET = 0.25;
      private static final double EPSILON = 1.0E-5;

      HologramImpl(@NotNull Location loc, @NotNull List<String> lines) {
         this.loc = Objects.requireNonNull(loc, "Location cannot be null").clone();
         this.updateLines(lines);
         this.spawn();
      }

      @Override
      public List<String> getLines() {
         return this.lines;
      }

      private void spawn() {
         Location clone = this.loc.clone();

         for (String line : this.lines) {
            TextDisplay as = (TextDisplay)clone.getWorld().spawn(clone, TextDisplay.class);
            as.setBillboard(Billboard.CENTER);
            this.spawnedEntities.add(as);
            as.setText(line);
            clone.subtract(0.0, 0.25, 0.0);
         }

         this.spawned = true;
      }

      @Override
      public void despawn() {
         this.spawnedEntities.forEach(Entity::remove);
         this.spawnedEntities.clear();
         this.spawned = false;
      }

      @Override
      public boolean isSpawned() {
         return this.spawned;
      }

      @Override
      public void updateLocation(@NotNull Location newLoc) {
         Validate.isTrue(this.spawned, "Hologram is not spawned");
         if (!(this.loc.distanceSquared(newLoc) < 1.0E-5)) {
            this.loc = newLoc.clone();
            Location clone = this.loc.clone();

            for (TextDisplay textDisplay : this.getSpawnedEntities()) {
               textDisplay.teleport(clone);
               clone.subtract(0.0, 0.25, 0.0);
            }
         }
      }

      @Override
      public void flyOut(@NotNull GameIndicators settings, @NotNull Vector dir) {
         for (TextDisplay td : this.getSpawnedEntities()) {
            td.setTeleportDuration((int)settings.tickPeriod);
         }

         super.flyOut(settings, dir);
      }

      @Override
      public void updateLines(@Nonnull List<String> lines) {
         Objects.requireNonNull(lines, "lines");
         Preconditions.checkArgument(!lines.isEmpty(), "Lines cannot be empty");

         for (String line : lines) {
            Preconditions.checkArgument(line != null, "Null line");
         }

         this.lines.clear();
         this.lines.addAll(lines);
      }

      @Override
      public Location getLocation() {
         return this.loc;
      }

      public List<TextDisplay> getSpawnedEntities() {
         return this.spawnedEntities;
      }
   }
}
