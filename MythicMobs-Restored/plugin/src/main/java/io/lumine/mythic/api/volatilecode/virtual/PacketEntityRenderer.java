package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;

public abstract class PacketEntityRenderer<T extends PacketEntity<?>> implements IPacketEntityRenderer<T> {
   @Getter
   protected final int entityId;
   @Getter
   protected final UUID uniqueId = UUID.randomUUID();
   @Getter
   protected final T wrapper;
   protected double cullingDistanceSq = 25000.0;
   @Setter
   protected Supplier<Collection<AbstractPlayer>> viewers = null;
   @Getter @Setter
   protected TrackedPlayerMap trackedPlayers = new TrackedPlayerMap();
   @Getter @Setter
   protected Boolean renderDataPerPlayer = false;
   @Setter
   protected int mount = -1;
   private static final double POS_T = 8.0;
   private static final float ROT_MIN = 1.40625F;

   public PacketEntityRenderer(T wrapper) {
      this.entityId = MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getNextEntityId();
      this.wrapper = wrapper;
   }

   @Override
   public void spawn(AbstractPlayer target) {
      this.trackedPlayers.put(target, false);
      this.updateRenderedPlayers(true);
   }

   @Override
   public void spawn() {
      this.spawn(() -> MythicBukkit.inst().getBootstrap().getOnlinePlayers());
   }

   @Override
   public void spawn(Supplier<Collection<AbstractPlayer>> playerSupplier) {
      this.viewers = playerSupplier;

      for (AbstractPlayer player : playerSupplier.get()) {
         this.trackedPlayers.put(player, false);
      }

      this.updateRenderedPlayers(true);
   }

   @Override
   public void spawn(AbstractWorld world) {
      this.spawn(() -> world.getPlayers());
   }

   @Override
   public void spawn(Collection<AbstractPlayer> players) {
      for (AbstractPlayer player : players) {
         this.trackedPlayers.put(player, false);
      }

      this.updateRenderedPlayers(true);
   }

   @Override
   public void spawnWithMount(AbstractPlayer target, PacketEntityRenderer mount) {
      ObjectArrayList<Object> packets = new ObjectArrayList();
      packets.addAll(mount.getSpawnPackets(target));
      packets.addAll(this.getSpawnPackets(target));
      packets.add(mount.createPassengerPacket(this.entityId));
      this.setMount(mount.getEntityId());
      this.sendPacket(target, (Collection<Object>)packets);
   }

   @Override
   public void spawnWithMount(Supplier<Collection<AbstractPlayer>> playerSupplier, PacketEntityRenderer mount) {
      this.viewers = playerSupplier;
      mount.setViewers(playerSupplier);

      for (AbstractPlayer player : playerSupplier.get()) {
         this.trackedPlayers.put(player, false);
         mount.getTrackedPlayers().put(player, false);
      }

      this.updateRenderedPlayers(false);
      mount.updateRenderedPlayers(false);
      this.setMount(mount.getEntityId());

      for (Entry<AbstractPlayer, Boolean> tracked : this.trackedPlayers.entrySet()) {
         if (tracked.getValue()) {
            AbstractPlayer target = tracked.getKey();
            ObjectArrayList<Object> packets = new ObjectArrayList();
            packets.addAll(mount.getSpawnPackets(target));
            packets.addAll(this.getSpawnPackets(target));
            packets.add(mount.createPassengerPacket(this.entityId));
            this.sendPacket(tracked.getKey(), (Collection<Object>)packets);
         }
      }
   }

   @Override
   public void spawnWithMount(Collection<AbstractPlayer> players, PacketEntityRenderer mount) {
      for (AbstractPlayer player : players) {
         this.trackedPlayers.put(player, false);
         mount.getTrackedPlayers().put(player, false);
      }

      this.updateRenderedPlayers(false);
      mount.updateRenderedPlayers(false);
      this.setMount(mount.getEntityId());

      for (Entry<AbstractPlayer, Boolean> tracked : this.trackedPlayers.entrySet()) {
         if (tracked.getValue()) {
            AbstractPlayer target = tracked.getKey();
            ObjectArrayList<Object> packets = new ObjectArrayList();
            packets.addAll(mount.getSpawnPackets(target));
            packets.addAll(this.getSpawnPackets(target));
            packets.add(mount.createPassengerPacket(this.entityId));
            this.sendPacket(tracked.getKey(), (Collection<Object>)packets);
         }
      }
   }

   @Override
   public void spawnWithMount(Supplier<Collection<AbstractPlayer>> playerSupplier, AbstractPlayer mount) {
      this.viewers = playerSupplier;

      for (AbstractPlayer player : playerSupplier.get()) {
         this.trackedPlayers.put(player, false);
      }

      this.updateRenderedPlayers(true);
      this.setMount(mount.getEntityId());

      for (Entry<AbstractPlayer, Boolean> tracked : this.trackedPlayers.entrySet()) {
         if (tracked.getValue()) {
            AbstractPlayer target = tracked.getKey();
            ObjectArrayList<Object> packets = new ObjectArrayList();
            packets.addAll(this.getSpawnPackets(target));
            packets.add(this.createMountPacket(this.entityId));
            this.sendPacket(tracked.getKey(), (Collection<Object>)packets);
         }
      }
   }

   @Override
   public void spawnWithMount(AbstractPlayer mount) {
      this.spawnWithMount(() -> mount.getWorld().getPlayers(), mount);
   }

   @Override
   public void setCullingDistance(int blocks) {
      this.cullingDistanceSq = Math.pow(blocks, 2.0);
   }

   @Override
   public void updateRenderedPlayers(boolean sendSpawnPacket) {
      if (this.viewers != null) {
         Collection<AbstractPlayer> viewerCollection = this.viewers.get();
         this.trackedPlayers.retainViewers(viewerCollection);

         for (AbstractPlayer player : viewerCollection) {
            this.trackedPlayers.addIfAbsent(player);
         }
      }

      Vector3dc location = this.wrapper.getLocation().peek();
      double locX = location.x();
      double locZ = location.z();
      AbstractWorld entityWorld = this.wrapper.getWorld();
      AbstractPlayer[] keys = this.trackedPlayers.keys;
      boolean[] vals = this.trackedPlayers.vals;
      int n = this.trackedPlayers.count;
      Object removePacket = null;

      for (int i = 0; i < n; i++) {
         AbstractPlayer player = keys[i];
         if (player != null) {
            AbstractLocation playerLoc = player.getLocation();
            if (!entityWorld.equals(playerLoc.getWorld())) {
               vals[i] = false;
            } else {
               double dx = playerLoc.getX() - locX;
               double dz = playerLoc.getZ() - locZ;
               double distSq = dx * dx + dz * dz;
               if (vals[i]) {
                  if (distSq > this.cullingDistanceSq) {
                     if (removePacket == null) {
                        removePacket = this.createRemovePacket();
                     }

                     this.sendPacket(player, removePacket);
                     vals[i] = false;
                  }
               } else if (distSq <= this.cullingDistanceSq) {
                  if (sendSpawnPacket) {
                     this.create(player);
                  }

                  vals[i] = true;
               }
            }
         }
      }
   }

   public void sendPacket(Collection<AbstractPlayer> players, Collection<Object> packets) {
      if (!packets.isEmpty()) {
         ObjectArrayList<Object> validPackets = new ObjectArrayList();

         for (Object packet : packets) {
            if (packet != null) {
               validPackets.add(packet);
            }
         }

         if (!validPackets.isEmpty()) {
            Object packetx = validPackets.size() == 1 ? validPackets.get(0) : this.bundlePackets(validPackets);

            for (AbstractPlayer player : players) {
               this.sendPacket(player, packetx);
            }
         }
      }
   }

   public void sendPacket(Object... packets) {
      Object single = null;
      int count = 0;

      for (Object packet : packets) {
         if (packet != null) {
            single = packet;
            count++;
         }
      }

      if (count != 0) {
         Object packetx;
         if (count == 1) {
            packetx = single;
         } else {
            ObjectArrayList<Object> valid = new ObjectArrayList(count);

            for (Object p : packets) {
               if (p != null) {
                  valid.add(p);
               }
            }

            packetx = this.bundlePackets(valid);
         }

         AbstractPlayer[] keys = this.trackedPlayers.keys;
         boolean[] vals = this.trackedPlayers.vals;
         int n = this.trackedPlayers.count;

         for (int i = 0; i < n; i++) {
            if (vals[i]) {
               AbstractPlayer player = keys[i];
               if (player != null) {
                  this.sendPacket(player, packetx);
               }
            }
         }
      }
   }

   private void sendUpdatePackets(Object p1, Object p2, Object p3) {
      Object single = null;
      int count = 0;
      if (p1 != null) {
         single = p1;
         count++;
      }

      if (p2 != null) {
         single = p2;
         count++;
      }

      if (p3 != null) {
         single = p3;
         count++;
      }

      if (count != 0) {
         Object packet;
         if (count == 1) {
            packet = single;
         } else {
            ObjectArrayList<Object> valid = new ObjectArrayList(count);
            if (p1 != null) {
               valid.add(p1);
            }

            if (p2 != null) {
               valid.add(p2);
            }

            if (p3 != null) {
               valid.add(p3);
            }

            packet = this.bundlePackets(valid);
         }

         AbstractPlayer[] keys = this.trackedPlayers.keys;
         boolean[] vals = this.trackedPlayers.vals;
         int n = this.trackedPlayers.count;

         for (int i = 0; i < n; i++) {
            if (vals[i]) {
               AbstractPlayer player = keys[i];
               if (player != null) {
                  this.sendPacket(player, packet);
               }
            }
         }
      }
   }

   public void sendPacket(AbstractPlayer player, Collection<Object> packets) {
      this.sendPacket(ObjectArrayList.of(new AbstractPlayer[]{player}), packets);
   }

   public abstract void sendPacket(AbstractPlayer abstractPlayer, @NotNull Object obj);

   @Override
   public void update() {
      Object packetTeleport = null;
      Object packetVelocity = null;
      if (this.mount < 0) {
         AbstractLocation base = this.wrapper.previousLocation;
         Vector3dc pos = this.wrapper.location.peek();
         double dx = pos.x() - base.getX();
         double dy = pos.y() - base.getY();
         double dz = pos.z() - base.getZ();
         float yaw = this.wrapper.yaw.peekFloat();
         float pitch = this.wrapper.pitch.peekFloat();
         boolean absPos = dx > 8.0 || dx < -8.0 || dy > 8.0 || dy < -8.0 || dz > 8.0 || dz < -8.0;
         boolean relPos = dx * dx + dy * dy + dz * dz >= MythicBukkit.inst().getConfiguration().getPacketMinimumPositionDelta();
         boolean minRotDelta = Math.abs(yaw - base.getYaw()) >= 1.40625F || Math.abs(pitch - base.getPitch()) >= 1.40625F;
         if (!absPos) {
            if (minRotDelta && relPos) {
               packetTeleport = this.createRelativePosRotPacket(dx, dy, dz, yaw, pitch);
            } else if (relPos) {
               packetTeleport = this.createRelativePosPacket(dx, dy, dz);
            } else if (minRotDelta) {
               packetTeleport = this.createRelativeRotPacket(yaw, pitch);
            }
         } else {
            packetTeleport = this.createTeleportPacket(pos.x(), pos.y(), pos.z(), yaw, pitch);
         }

         if (this.wrapper.velocity.isDirty()) {
            AbstractVector velocity = this.wrapper.velocity.get();
            packetVelocity = this.createVelocityPacket(velocity.getX(), velocity.getY(), velocity.getZ());
         }

         Vector3dc newPos = !absPos && !relPos ? base.toVector3d() : this.wrapper.location.get();
         float newYaw = !absPos && !minRotDelta ? base.getYaw() : this.wrapper.yaw.getFloat();
         float newPitch = !absPos && !minRotDelta ? base.getPitch() : this.wrapper.pitch.getFloat();
         this.wrapper.previousLocation = AbstractLocation.from(newPos, newYaw, newPitch);
      }

      if (this.renderDataPerPlayer) {
         for (AbstractPlayer player : this.trackedPlayers.keySet()) {
            Object packetData = this.createDataPacket(player, false);
            if (packetTeleport != null) {
               this.sendPacket(player, packetTeleport);
            }

            if (packetData != null) {
               this.sendPacket(player, packetData);
            }

            if (packetVelocity != null) {
               this.sendPacket(player, packetVelocity);
            }
         }
      } else {
         this.sendUpdatePackets(this.createDataPacket(false), packetTeleport, packetVelocity);
      }
   }

   private void create(AbstractPlayer target) {
      Collection<Object> packets = this.getSpawnPackets(target);
      if (this.mount > 0) {
         packets.add(this.createMountPacket(this.mount));
      }

      this.sendPacket(target, packets);
   }

   @Override
   public void destroy() {
      Object destroyPacket = this.createRemovePacket();

      for (AbstractPlayer player : this.trackedPlayers.keySet()) {
         this.sendPacket(player, destroyPacket);
      }

      this.trackedPlayers.clear();
   }

   @Override
   public void destroy(AbstractPlayer player) {
      this.trackedPlayers.put(player, false);
      Object destroyPacket = this.createRemovePacket();
      this.sendPacket(player, destroyPacket);
   }

   @Override
   public void mountEntity(AbstractEntity entity) {
      int entityId;
      if (!ServerVersion.isFolia() || entity.getBukkitEntity() == null) {
         entityId = entity.getBukkitEntity().getEntityId();
      } else if (Bukkit.isPrimaryThread() && FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
         entityId = entity.getBukkitEntity().getEntityId();
      } else {
         entityId = Schedulers.of(entity.getBukkitEntity()).supply(() -> entity.getBukkitEntity().getEntityId()).join();
      }

      Object packet = this.createMountPacket(entityId);
      if (packet != null) {
         this.mount = entityId;
         this.sendPacket(packet);
      }
   }

   @Override
   public void addPassenger(PacketEntityRenderer entity) {
      Object packet = this.createPassengerPacket(entity.getEntityId());
      if (packet != null) {
         entity.setMount(this.entityId);
         this.sendPacket(packet);
      }
   }

   protected short encodePos(double delta) {
      return (short)(delta * 4096.0);
   }

   protected byte encodeRot(float rot) {
      return (byte)(rot / 360.0F * 255.0F);
   }

   protected abstract Object bundlePackets(Object obj);

   protected abstract Collection<Object> getSpawnPackets(AbstractPlayer abstractPlayer);

   protected abstract Object createSpawnPacket();

   protected abstract Object createDataPacket(boolean flag);

   protected Object createDataPacket(AbstractPlayer player, boolean force) {
      return this.createDataPacket(force);
   }

   protected abstract Object createRemovePacket();

   protected abstract Object createVelocityPacket(double value, double value2, double value3);

   protected abstract Object createTeleportPacket(double value, double value2, double value3, float value4, float value5);

   protected abstract Object createRelativePosPacket(double value, double value2, double value3);

   protected abstract Object createRelativeRotPacket(float value, float value2);

   protected abstract Object createRelativePosRotPacket(double value, double value2, double value3, float value4, float value5);

   protected Object createMountPacket(int entityId) {
      return null;
   }

   protected Object createPassengerPacket(int entityId) {
      return null;
   }
}
