package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BukkitWorld implements AbstractWorld {
   private transient WeakReference<World> worldRef;
   private UUID uuid;
   private String name;
   private static final Map<Integer, Effect> effects = new HashMap<>();

   public BukkitWorld() {
      World w = Bukkit.getWorld(this.uuid);
      this.worldRef = new WeakReference<>(w);
   }

   public BukkitWorld(World w) {
      this.worldRef = new WeakReference<>(w);
      this.uuid = w.getUID();
      this.name = w.getName();
   }

   public BukkitWorld(String world) {
      World w = Bukkit.getWorld(world);
      this.worldRef = new WeakReference<>(w);
      if (this.worldRef != null && this.worldRef.get() != null) {
         this.uuid = w.getUID();
      }

      this.name = world;
   }

   public World getBukkitWorld() {
      World world = this.worldRef.get();
      if (world == null) {
         world = Bukkit.getWorld(this.name);
         if (world == null) {
            return null;
         }

         this.worldRef = new WeakReference<>(world);
      }

      return world;
   }

   @Override
   public boolean isLoaded() {
      return Bukkit.getWorld(this.uuid) != null;
   }

   @Override
   public Collection<AbstractEntity> getLivingEntities() {
      return MythicBukkit.inst().getBootstrap().getLivingEntities(this);
   }

   @Override
   public String getName() {
      return this.getBukkitWorld() == null ? this.name : this.getBukkitWorld().getName();
   }

   @Override
   public UUID getUniqueId() {
      return this.getBukkitWorld() == null ? this.uuid : this.getBukkitWorld().getUID();
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof BukkitWorld bukkitWorld) {
         return bukkitWorld.getBukkitWorld() == null ? false : bukkitWorld.getBukkitWorld().equals(this.getBukkitWorld());
      } else {
         return other instanceof AbstractWorld abstractWorld ? abstractWorld.getName().equals(this.getName()) : false;
      }
   }

   @Override
   public String toString() {
      return this.getName();
   }

   @Override
   public int hashCode() {
      return this.getBukkitWorld() == null ? -1 : this.getBukkitWorld().hashCode();
   }

   public int getMaxY() {
      return this.getBukkitWorld().getMaxHeight() - 1;
   }

   @Override
   public void createExplosion(AbstractLocation l, float f) {
      if (this.getBukkitWorld() != null) {
         this.getBukkitWorld().createExplosion(BukkitAdapter.adapt(l), f);
      }
   }

   @Override
   public void createExplosion(AbstractLocation l, float yield, boolean fire, boolean blockdamage) {
      if (this.getBukkitWorld() != null) {
         this.getBukkitWorld().createExplosion(l.getX(), l.getY(), l.getZ(), yield, fire, blockdamage);
      }
   }

   @Override
   public void createExplosion(AbstractLocation l, float yield, boolean fire, boolean blockdamage, AbstractEntity source) {
      if (this.getBukkitWorld() != null) {
         Entity bukkitSource = BukkitAdapter.adapt(source);
         if (bukkitSource != null) {
            this.getBukkitWorld().createExplosion(l.getX(), l.getY(), l.getZ(), yield, fire, blockdamage, bukkitSource);
         } else {
            this.getBukkitWorld().createExplosion(l.getX(), l.getY(), l.getZ(), yield, fire, blockdamage);
         }
      }
   }

   @Override
   public List<AbstractPlayer> getPlayers() {
      ObjectList<AbstractPlayer> pl = new ObjectArrayList();

      for (Player p : this.getBukkitWorld().getPlayers()) {
         pl.add(BukkitAdapter.adapt(p));
      }

      return pl;
   }

   @Override
   public int getPlayerCount() {
      return ServerVersion.isPaper() ? this.getBukkitWorld().getPlayerCount() : Bukkit.getOnlinePlayers().size();
   }

   @Override
   public Collection<AbstractPlayer> getPlayersInRadius(AbstractLocation center, double radiusSq) {
      ObjectList<AbstractPlayer> pl = new ObjectArrayList();
      Location location = BukkitAdapter.adapt(center);

      for (Player player : this.getBukkitWorld().getPlayers()) {
         if (player.getLocation().distanceSquared(location) <= radiusSq) {
            pl.add(BukkitAdapter.adapt(player));
         }
      }

      return pl;
   }

   @Override
   public void setStorm(boolean b) {
      this.getBukkitWorld().setStorm(b);
   }

   @Override
   public void setThundering(boolean b) {
      this.getBukkitWorld().setThundering(b);
   }

   @Override
   public void setWeatherDuration(int duration) {
      this.getBukkitWorld().setWeatherDuration(duration);
   }

   @Override
   public boolean playEffect(AbstractLocation location, int type) {
      return this.playEffect(location, type, 0);
   }

   @Override
   public boolean playEffect(AbstractLocation location, int type, int data) {
      World world = this.getBukkitWorld();
      Effect effect = effects.get(type);
      if (effect == null) {
         return false;
      } else {
         world.playEffect(BukkitAdapter.adapt(location), effect, data);
         return true;
      }
   }

   @Override
   public byte getLightLevel(AbstractLocation l) {
      return BukkitAdapter.adapt(l).getBlock().getLightLevel();
   }

   @Override
   public byte getLightLevelFromBlocks(AbstractLocation l) {
      return this.getBukkitWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()).getLightFromBlocks();
   }

   @Override
   public byte getLightLevelFromSky(AbstractLocation l) {
      return this.getBukkitWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()).getLightFromSky();
   }

   @Override
   public AbstractLocation getSpawnLocation() {
      return this.getBukkitWorld() == null ? null : BukkitAdapter.adapt(this.getBukkitWorld().getSpawnLocation());
   }

   @Override
   public AbstractLocation getHighestBlock(AbstractLocation location) {
      Location checkLocation = BukkitAdapter.adapt(location);
      return BukkitAdapter.adapt(this.getBukkitWorld().getHighestBlockAt(checkLocation).getLocation());
   }

   @Override
   public boolean isChunkLoaded(int x, int z) {
      return this.getBukkitWorld() == null ? false : MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().isChunkLoaded(this, x, z);
   }

   @Override
   public int getEntitiesInChunk(int x, int z) {
      return MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getEntitiesInChunk(this, x, z);
   }

   @Override
   public Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation location, double radius) {
      return MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(location, radius, null);
   }

   @Override
   public Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation location, double radius, Predicate<AbstractEntity> predicate) {
      return MythicBukkit.inst().getVolatileCodeHandler().getWorldHandler().getEntitiesNearLocation(location, radius, predicate);
   }

   @Override
   public boolean isLocationLoaded(AbstractLocation abstractLocation) {
      if (this.getBukkitWorld() == null) {
         return false;
      } else {
         Location loc = BukkitAdapter.adapt(abstractLocation);
         int x = loc.getBlockX() >> 4;
         int z = loc.getBlockZ() >> 4;
         return this.getBukkitWorld().isChunkLoaded(x, z);
      }
   }

   @Override
   public List<AbstractPlayer> getPlayersNearLocation(AbstractLocation location, int radius) {
      ObjectList<AbstractPlayer> pl = new ObjectArrayList();
      Location l = BukkitAdapter.adapt(location);

      for (Player p : this.getBukkitWorld().getPlayers()) {
         if (p.getLocation().distanceSquared(l) <= Math.pow(radius, 2.0)) {
            pl.add(BukkitAdapter.adapt(p));
         }
      }

      return pl;
   }

   @Override
   public Collection<AbstractEntity> getEntities() {
      return MythicBukkit.inst().getBootstrap().getEntities(this);
   }

   @Override
   public AbstractBiome getLocationBiome(AbstractLocation abstractLocation) {
      Location l = BukkitAdapter.adapt(abstractLocation);
      return BukkitAdapter.adapt(l.getBlock().getBiome());
   }

   @Override
   public long getFullTime() {
      return this.getBukkitWorld() == null ? 0L : this.getBukkitWorld().getFullTime();
   }

   @Override
   public Optional<AbstractLocation> getSurfaceLocation(AbstractLocation in, int tolerance) {
      Location bLoc = BukkitAdapter.adapt(in).toBlockLocation();
      Block block = bLoc.getBlock();
      Predicate<Block> isAir = b -> b.isEmpty();
      Predicate<Block> isSolid = b -> !b.isEmpty() && b.getType().isSolid();
      if (!isAir.test(block)) {
         Block cur = block;

         for (int climbed = 0; climbed < tolerance && !isAir.test(cur); climbed++) {
            cur = cur.getRelative(BlockFace.UP);
         }

         if (!isAir.test(cur)) {
            return Optional.empty();
         } else {
            Block below = cur.getRelative(BlockFace.DOWN);
            if (!isSolid.test(below)) {
               int dropped = 0;

               Block seek;
               for (seek = below; dropped < tolerance && !isSolid.test(seek); dropped++) {
                  seek = seek.getRelative(BlockFace.DOWN);
               }

               if (!isSolid.test(seek)) {
                  return Optional.empty();
               }

               below = seek;
            }

            AbstractLocation out = in.clone();
            out.setY(below.getY() + 1.0);
            return Optional.of(out);
         }
      } else {
         Block below = block.getRelative(BlockFace.DOWN);
         if (isSolid.test(below)) {
            AbstractLocation out = in.clone();
            out.setY(below.getY() + 1.0);
            return Optional.of(out);
         } else if (isAir.test(below)) {
            Block cur = below;

            for (int dropped = 0; dropped < tolerance && isAir.test(cur); dropped++) {
               cur = cur.getRelative(BlockFace.DOWN);
            }

            if (!isSolid.test(cur)) {
               return Optional.empty();
            } else {
               AbstractLocation out = in.clone();
               out.setY(cur.getY() + 1.0);
               return Optional.of(out);
            }
         } else {
            Block cur = below;

            for (int dropped = 0; dropped < tolerance && !isAir.test(cur) && !isSolid.test(cur); dropped++) {
               cur = cur.getRelative(BlockFace.DOWN);
            }

            if (!isSolid.test(cur)) {
               return Optional.empty();
            } else {
               AbstractLocation out = in.clone();
               out.setY(cur.getY() + 1.0);
               return Optional.of(out);
            }
         }
      }
   }

   static {
      for (Effect effect : Effect.values()) {
         effects.put(effect.getId(), effect);
      }
   }
}
