package io.lumine.mythic.bukkit.utils.holograms;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersion;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class BukkitHologramFactory implements HologramFactory {
   @Nonnull
   @Override
   public Hologram newHologram(@Nonnull Position position, @Nonnull List<String> lines) {
      return new BukkitHologramFactory.BukkitHologram(position, lines);
   }

   private static final class BukkitHologram implements Hologram {
      private static final Method SET_CAN_TICK;
      private Position position;
      private final List<String> lines = new ArrayList<>();
      private final List<ArmorStand> spawnedEntities = new ArrayList<>();
      private boolean spawned = false;
      private CompositeTerminable listeners = null;
      private Consumer<Player> clickCallback = null;

      BukkitHologram(Position position, List<String> lines) {
         this.position = Objects.requireNonNull(position, "position");
         this.updateLines(lines);
      }

      private Position getNewLinePosition() {
         if (this.spawnedEntities.isEmpty()) {
            return this.position;
         } else {
            ArmorStand last = this.spawnedEntities.get(this.spawnedEntities.size() - 1);
            return Position.of(last.getLocation()).subtract(0.0, 0.25, 0.0);
         }
      }

      @Override
      public void spawn() {
         int linesSize = this.lines.size();
         int spawnedSize = this.spawnedEntities.size();
         if (linesSize < spawnedSize) {
            int diff = spawnedSize - linesSize;

            for (int i = 0; i < diff; i++) {
               ArmorStand as = this.spawnedEntities.remove(this.spawnedEntities.size() - 1);
               as.remove();
            }
         }

         for (int i = 0; i < this.lines.size(); i++) {
            String line = this.lines.get(i);
            if (i >= this.spawnedEntities.size()) {
               Location loc = this.getNewLinePosition().toLocation();
               Chunk chunk = loc.getChunk();
               if (!chunk.isLoaded()) {
                  chunk.load();
               }

               loc.getWorld().getNearbyEntities(loc, 1.0, 1.0, 1.0).forEach(e -> {
                  if (e.getType() == EntityType.ARMOR_STAND && locationsEqual(e.getLocation(), loc)) {
                     e.remove();
                  }
               });
               ArmorStand as;
               if (ServerVersion.isPaper()) {
                  as = loc.getWorld().spawn(loc, ArmorStand.class, ass -> {
                     ass.setSmall(true);
                     ass.setMarker(true);
                     ass.setArms(false);
                     ass.setBasePlate(false);
                     ass.setGravity(false);
                     ass.setVisible(false);
                     ass.setCustomName(line);
                     ass.setCustomNameVisible(true);
                  });
               } else {
                  as = loc.getWorld().spawn(loc, ArmorStand.class);
                  as.setSmall(true);
                  as.setMarker(true);
                  as.setArms(false);
                  as.setBasePlate(false);
                  as.setGravity(false);
                  as.setVisible(false);
                  as.setCustomName(line);
                  as.setCustomNameVisible(true);
               }

               if (MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_9)) {
                  as.setAI(false);
                  as.setCollidable(false);
                  as.setInvulnerable(true);
                  if (SET_CAN_TICK != null) {
                     try {
                        SET_CAN_TICK.invoke(as, false);
                     } catch (Exception ex) {
                        ex.printStackTrace();
                     }
                  }
               }

               this.spawnedEntities.add(as);
            } else {
               ArmorStand asx = this.spawnedEntities.get(i);
               if (asx.getCustomName() == null || !asx.getCustomName().equals(line)) {
                  asx.setCustomName(line);
               }
            }
         }

         if (this.listeners == null && this.clickCallback != null) {
            this.setClickCallback(this.clickCallback);
         }

         this.spawned = true;
      }

      @Override
      public void despawn() {
         this.spawnedEntities.forEach(Entity::remove);
         this.spawnedEntities.clear();
         this.spawned = false;
         if (this.listeners != null) {
            this.listeners.closeAndReportException();
         }

         this.listeners = null;
      }

      @Override
      public boolean isSpawned() {
         if (!this.spawned) {
            return false;
         } else {
            for (ArmorStand stand : this.spawnedEntities) {
               if (!stand.isValid()) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public void updatePosition(@Nonnull Position position) {
         Objects.requireNonNull(position, "position");
         if (!this.position.equals(position)) {
            this.position = position;
            if (!this.isSpawned()) {
               this.spawn();
            } else {
               double offset = 0.0;

               for (ArmorStand as : this.getSpawnedEntities()) {
                  as.teleport(position.toLocation().add(0.0, offset, 0.0));
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

         List<String> ret = lines.stream().map(Text::colorize).collect(Collectors.toList());
         if (!this.lines.equals(ret)) {
            this.lines.clear();
            this.lines.addAll(ret);
         }
      }

      @Override
      public void setClickCallback(@Nullable Consumer<Player> clickCallback) {
         if (clickCallback == null) {
            if (this.listeners != null) {
               this.listeners.closeAndReportException();
            }

            this.clickCallback = null;
            this.listeners = null;
         } else {
            this.clickCallback = clickCallback;
            if (this.listeners == null) {
               this.listeners = CompositeTerminable.create();
               Events.subscribe(PlayerInteractAtEntityEvent.class).filter(e -> e.getRightClicked() instanceof ArmorStand).handler(e -> {
                  Player p = e.getPlayer();
                  ArmorStand as = (ArmorStand)e.getRightClicked();

                  for (ArmorStand spawned : this.spawnedEntities) {
                     if (spawned.equals(as)) {
                        e.setCancelled(true);
                        this.clickCallback.accept(p);
                        return;
                     }
                  }
               }).bindWith(this.listeners);
               Events.subscribe(EntityDamageByEntityEvent.class)
                  .filter(e -> e.getEntity() instanceof ArmorStand)
                  .filter(e -> e.getDamager() instanceof Player)
                  .handler(e -> {
                     Player p = (Player)e.getDamager();
                     ArmorStand as = (ArmorStand)e.getEntity();

                     for (ArmorStand spawned : this.spawnedEntities) {
                        if (spawned.equals(as)) {
                           e.setCancelled(true);
                           this.clickCallback.accept(p);
                           return;
                        }
                     }
                  })
                  .bindWith(this.listeners);
            }
         }
      }

      @Override
      public void close() {
         this.despawn();
      }

      @Override
      public boolean isClosed() {
         return !this.spawned;
      }

      @Nonnull
      public JsonObject serialize() {
         return JsonBuilder.object().add("position", this.position).add("lines", JsonBuilder.array().addStrings(this.lines).build()).build();
      }

      private static boolean locationsEqual(Location l1, Location l2) {
         return Double.doubleToLongBits(l1.getX()) == Double.doubleToLongBits(l2.getX())
            && Double.doubleToLongBits(l1.getY()) == Double.doubleToLongBits(l2.getY())
            && Double.doubleToLongBits(l1.getZ()) == Double.doubleToLongBits(l2.getZ());
      }

      @Override
      public Position getPosition() {
         return this.position;
      }

      public List<ArmorStand> getSpawnedEntities() {
         return this.spawnedEntities;
      }

      static {
         Method setCanTick = null;

         try {
            setCanTick = ArmorStand.class.getDeclaredMethod("setCanTick", boolean.class);
         } catch (Throwable e) {
         }

         SET_CAN_TICK = setCanTick;
      }
   }
}
