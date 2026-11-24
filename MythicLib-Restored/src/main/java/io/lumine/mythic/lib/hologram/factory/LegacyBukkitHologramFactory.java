package io.lumine.mythic.lib.hologram.factory;

import com.google.common.base.Preconditions;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class LegacyBukkitHologramFactory implements HologramFactory {
   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new LegacyBukkitHologramFactory.HologramImpl(loc, lines);
   }

   static final class HologramImpl extends Hologram {
      private static final Method SET_CAN_TICK;
      private Location loc;
      private final List<String> lines = new ArrayList<>();
      private final List<ArmorStand> spawnedEntities = new ArrayList<>();
      private boolean spawned = false;

      HologramImpl(Location loc, List<String> lines) {
         this.loc = Objects.requireNonNull(loc, "position");
         this.updateLines(lines);
         this.spawn();
      }

      @Override
      public List<String> getLines() {
         return this.lines;
      }

      private Location getNewLinePosition() {
         if (this.spawnedEntities.isEmpty()) {
            return this.loc;
         }

         ArmorStand last = this.spawnedEntities.get(this.spawnedEntities.size() - 1);
         return last.getLocation().subtract(0.0, 0.25, 0.0);
      }

      public void spawn() {
         int linesSize = this.lines.size();
         int spawnedSize = this.spawnedEntities.size();
         if (linesSize < spawnedSize) {
            int i = spawnedSize - linesSize;

            for (int j = 0; j < i; j++) {
               ArmorStand as = this.spawnedEntities.remove(this.spawnedEntities.size() - 1);
               as.remove();
            }
         }

         for (int i = 0; i < this.lines.size(); i++) {
            String line = this.lines.get(i);
            if (i >= this.spawnedEntities.size()) {
               Location loc = this.getNewLinePosition();
               Chunk chunk = loc.getChunk();
               if (!chunk.isLoaded()) {
                  chunk.load();
               }

               loc.getWorld().getNearbyEntities(loc, 1.0, 1.0, 1.0).forEach(e -> {
                  if (e.getType() == EntityType.ARMOR_STAND && locationsEqual(e.getLocation(), loc)) {
                     e.remove();
                  }
               });
               ArmorStand as = (ArmorStand)loc.getWorld().spawn(loc, ArmorStand.class);
               as.setSmall(true);
               as.setMarker(true);
               as.setArms(false);
               as.setBasePlate(false);
               as.setGravity(false);
               as.setVisible(false);
               as.setCustomName(line);
               as.setCustomNameVisible(true);
               as.setAI(false);
               as.setCollidable(false);
               as.setInvulnerable(true);
               if (SET_CAN_TICK != null) {
                  try {
                     SET_CAN_TICK.invoke(as, false);
                  } catch (Exception var9) {
                     var9.printStackTrace();
                  }
               }

               this.spawnedEntities.add(as);
            } else {
               ArmorStand as = this.spawnedEntities.get(i);
               if (as.getCustomName() == null || !as.getCustomName().equals(line)) {
                  as.setCustomName(line);
               }
            }
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
         if (!this.spawned) {
            return false;
         }

         for (ArmorStand stand : this.spawnedEntities) {
            if (!stand.isValid()) {
               return false;
            }
         }

         return true;
      }

      @Override
      public void updateLocation(@NotNull Location loc) {
         Objects.requireNonNull(loc, "position");
         if (!this.loc.equals(loc)) {
            this.loc = loc;
            if (!this.isSpawned()) {
               this.spawn();
            } else {
               double offset = 0.0;

               for (ArmorStand as : this.getSpawnedEntities()) {
                  as.teleport(loc.add(0.0, offset, 0.0));
                  offset += 0.25;
               }
            }
         }
      }

      @Override
      public void updateLines(@Nonnull List<String> lines) {
         Objects.requireNonNull(lines, "lines");
         Preconditions.checkArgument(!lines.isEmpty(), "lines cannot be empty");

         for (String line : lines) {
            Preconditions.checkArgument(line != null, "null line");
         }

         this.lines.clear();
         this.lines.addAll(lines);
      }

      private static boolean locationsEqual(Location l1, Location l2) {
         return Double.doubleToLongBits(l1.getX()) == Double.doubleToLongBits(l2.getX())
            && Double.doubleToLongBits(l1.getY()) == Double.doubleToLongBits(l2.getY())
            && Double.doubleToLongBits(l1.getZ()) == Double.doubleToLongBits(l2.getZ());
      }

      @Override
      public Location getLocation() {
         return this.loc;
      }

      public List<ArmorStand> getSpawnedEntities() {
         return this.spawnedEntities;
      }

      static {
         Method setCanTick = null;

         try {
            setCanTick = ArmorStand.class.getDeclaredMethod("setCanTick", boolean.class);
         } catch (Throwable var2) {
         }

         SET_CAN_TICK = setCanTick;
      }
   }
}
