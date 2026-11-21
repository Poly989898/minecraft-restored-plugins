package com.ticxo.modelengine.v1_20_R1.parser.model;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.nms.network.ModPlayerTracker;
import com.ticxo.modelengine.api.utils.data.PooledCollection;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_20_R1.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.BulkEntityDataEncoder;
import com.ticxo.modelengine.v1_20_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R1.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.b;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Display.BillboardConstraints;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DisplayParser implements ModelRendererParser<DisplayRenderer> {
   private static final Quaternionf IDENTITY_ROTATION = new Quaternionf();
   private final Set<UUID> noCullPlayers = new HashSet<>();
   private final Set<UUID> noCullForcePlayers = new HashSet<>();
   private final Set<UUID> movementOnlyPlayers = new HashSet<>();
   private final Set<UUID> culledPlayers = new HashSet<>();
   private final List<Runnable> cleanupQueue = new ArrayList<>();
   private ModeledEntity modeledEntity;
   private float viewRange = 4096.0F;
   private Object pivotMove;
   private Object hitboxMove;
   private static Constructor<?> CUSTOM_PAYLOAD_CONSTRUCTOR;

   public void sendToClients(DisplayRenderer renderer) {
      this.noCullPlayers.clear();
      this.noCullForcePlayers.clear();
      this.movementOnlyPlayers.clear();
      this.culledPlayers.clear();
      this.cleanupQueue.clear();
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      this.modeledEntity = renderer.getActiveModel().getModeledEntity();
      this.viewRange = renderer.getActiveModel().getViewRange();
      this.pivotMove = this.pivotTeleport(renderer.getPivot());
      this.hitboxMove = this.hitboxTeleport(renderer.getHitbox());

      for (Entry<UUID, CullType> entry : data.getTracking().entrySet()) {
         switch (entry.getValue()) {
            case NO_CULL:
               if (renderer.pollFullUpdate(entry.getKey())) {
                  this.noCullForcePlayers.add(entry.getKey());
               } else {
                  this.noCullPlayers.add(entry.getKey());
               }
               break;
            case MOVEMENT_ONLY:
               renderer.pushFullUpdate(entry.getKey());
               this.movementOnlyPlayers.add(entry.getKey());
               break;
            case CULLED:
               this.culledPlayers.add(entry.getKey());
         }
      }

      if (renderer.pollFirstSpawn()) {
         HashSet<UUID> set = new HashSet<>((int)((this.movementOnlyPlayers.size() + this.noCullPlayers.size() + this.noCullForcePlayers.size()) / 0.75F) + 1);
         set.addAll(this.movementOnlyPlayers);
         set.addAll(this.noCullPlayers);
         set.addAll(this.noCullForcePlayers);
         this.spawn(set, renderer);
      } else {
         Set<UUID> spawning = data.getStartTracking();
         this.spawn(spawning, renderer);
         this.updateRealtime(excluding(this.movementOnlyPlayers, spawning), renderer, true, false);
         this.updateRealtime(excluding(this.noCullPlayers, spawning), renderer, false, false);
         this.updateRealtime(excluding(this.noCullForcePlayers, spawning), renderer, false, true);
         this.updateCulled(this.culledPlayers, renderer);
         this.remove(data.getStopTracking(), renderer);
      }

      this.noCullPlayers.clear();
      this.noCullForcePlayers.clear();
      this.movementOnlyPlayers.clear();
      this.culledPlayers.clear();
      renderer.getPivot().clearDirty();
      renderer.getHitbox().clearDirty();
      PooledCollection<DisplayFire> fireDisplay = renderer.getHitbox().getFireDisplay();
      if (!fireDisplay.getCreated().isEmpty()) {
         fireDisplay.getCreated().clear();
      }

      if (!fireDisplay.getReleased().isEmpty()) {
         fireDisplay.getReleased().clear();
      }

      this.cleanupQueue.forEach(Runnable::run);
      this.cleanupQueue.clear();
      this.pivotMove = null;
      this.hitboxMove = null;
   }

   private static Set<UUID> excluding(Set<UUID> targets, Set<UUID> excluded) {
      if (!targets.isEmpty() && !excluded.isEmpty()) {
         HashSet<UUID> filtered = new HashSet<>(targets);
         filtered.removeAll(excluded);
         return filtered;
      } else {
         return targets;
      }
   }

   public void destroy(DisplayRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, DisplayRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         DisplayRenderer.Pivot pivot = renderer.getPivot();
         set.add(this.pivotSpawn(pivot));
         set.add(this.pivotData(pivot));

         for (DisplayBone bone : renderer.getRendered().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               set.add(this.displaySpawn(pivot, boneData));
               set.add(this.displayData(boneData, true, false));
            }
         }

         for (DisplayBone bone : renderer.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               set.add(this.displaySpawn(pivot, boneData));
               set.add(this.displayData(boneData, true, false));
            }
         }

         set.add(this.pivotMount(pivot));
         BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
         if (base instanceof BukkitEntity bukkitEntity && bukkitEntity.getOriginal() instanceof Player owner && targets.contains(owner.getUniqueId())) {
            NetworkUtils.sendBundled(owner.getUniqueId(), set);
         }

         DisplayRenderer.Hitbox hitbox = renderer.getHitbox();
         set.add(this.hitboxSpawnPivot(hitbox));
         set.add(this.hitboxDataPivot(hitbox));
         set.add(this.hitboxSpawn(hitbox));
         set.add(this.hitboxData(hitbox, true));
         set.add(this.shadowSpawn(hitbox));
         set.add(this.shadowData(hitbox, true));
         if (hitbox.isFireVisible()) {
            hitbox.getFireDisplay().getInUse().forEach(displayFire -> {
               set.add(this.fireSpawn(hitbox, displayFire));
               set.add(this.fireData(displayFire, true));
            });
         }

         set.add(this.hitboxMount(hitbox));
         NetworkUtils.sendBundled(targets, set, player -> !player.getUniqueId().equals(base.getUUID()));
      }
   }

   private void updateRealtime(Set<UUID> targets, DisplayRenderer renderer, boolean movementOnly, boolean transformOnly) {
      if (!targets.isEmpty()) {
         Set<UUID> modTargets = null;
         Set<UUID> vanillaTargets = targets;

         for (UUID uuid : targets) {
            if (ModPlayerTracker.hasClientMod(uuid)) {
               if (modTargets == null) {
                  modTargets = new HashSet<>();
                  vanillaTargets = new HashSet<>(targets);
               }

               modTargets.add(uuid);
               vanillaTargets.remove(uuid);
            }
         }

         Packets set = new Packets();
         Packets boneSet = modTargets != null ? new Packets() : set;
         HashSet<Integer> destroy = new HashSet<>();
         DisplayRenderer.Pivot pivot = renderer.getPivot();
         set.add(this.pivotMove);
         if (!movementOnly) {
            for (DisplayBone bone : renderer.getRendered().values()) {
               UpdateScheme<DisplayBone.BoneData> scheme = bone.getModelUpdateScheme();

               for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                  switch (scheme.getUpdateMode(boneData)) {
                     case NONE:
                     case UPDATE:
                        boneSet.add(this.displayData(boneData, false, transformOnly));
                  }
               }

               if (scheme.hasUpdate()) {
                  scheme.getAdded().forEach(boneData -> {
                     set.add(this.displaySpawn(pivot, boneData));
                     set.add(this.displayData(boneData, true, false));
                  });
                  scheme.getRemoved().forEach(boneData -> destroy.add(boneData.getId()));
               }
            }
         } else {
            for (DisplayBone bone : renderer.getRendered().values()) {
               UpdateScheme<DisplayBone.BoneData> scheme = bone.getModelUpdateScheme();

               for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                  switch (scheme.getUpdateMode(boneData)) {
                     case NONE:
                     case UPDATE:
                        boneSet.add(this.displayVisibleData(boneData));
                  }
               }

               if (scheme.hasUpdate()) {
                  scheme.getAdded().forEach(boneData -> {
                     set.add(this.displaySpawn(pivot, boneData));
                     set.add(this.displayData(boneData, true, false));
                  });
                  scheme.getRemoved().forEach(boneData -> destroy.add(boneData.getId()));
               }
            }
         }

         for (DisplayBone bone : renderer.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               set.add(this.displaySpawn(pivot, boneData));
               set.add(this.displayData(boneData, true, false));
            }
         }

         renderer.getDestroyQueue().forEach((s, bones) -> bones.getModel().forEach((uuid, boneData) -> destroy.add(boneData.getId())));
         if (pivot.getPassengers().isDirty()) {
            set.add(this.pivotMount(pivot));
         }

         BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
         UUID owner = base instanceof BukkitEntity bukkitEntity && bukkitEntity.getOriginal() instanceof Player && targets.contains(base.getUUID())
            ? base.getUUID()
            : null;
         Packets hitboxSet = owner == null ? set : new Packets();
         HashSet<Integer> hitboxDestroy = owner == null ? destroy : new HashSet<>();
         DisplayRenderer.Hitbox hitbox = renderer.getHitbox();
         AtomicBoolean updateHitboxMount = new AtomicBoolean(false);
         hitboxSet.add(this.hitboxMove);
         if (hitbox.getHitboxVisible().isDirty()) {
            if (hitbox.isHitboxVisible()) {
               hitboxSet.add(this.hitboxSpawn(hitbox));
               hitboxSet.add(this.hitboxData(hitbox, true));
               updateHitboxMount.set(true);
            } else {
               hitboxDestroy.add(hitbox.getHitboxId());
            }
         } else {
            hitboxSet.add(this.hitboxData(hitbox, false));
         }

         if (hitbox.getShadowVisible().isDirty()) {
            if (hitbox.isShadowVisible()) {
               hitboxSet.add(this.shadowSpawn(hitbox));
               hitboxSet.add(this.shadowData(hitbox, true));
               updateHitboxMount.set(true);
            } else {
               hitboxDestroy.add(hitbox.getShadowId());
            }
         } else {
            hitboxSet.add(this.shadowData(hitbox, false));
         }

         PooledCollection<DisplayFire> fireDisplay = hitbox.getFireDisplay();
         if (hitbox.getFireVisible().isDirty()) {
            if (hitbox.isFireVisible()) {
               fireDisplay.getInUse().forEach(displayFire -> {
                  updateHitboxMount.set(true);
                  hitboxSet.add(this.fireSpawn(hitbox, displayFire));
                  hitboxSet.add(this.fireData(displayFire, true));
               });
            } else {
               fireDisplay.getAll().forEach(displayFire -> hitboxDestroy.add(displayFire.getId()));
            }
         } else {
            fireDisplay.processAll(displayFire -> {
               updateHitboxMount.set(true);
               hitboxSet.add(this.fireSpawn(hitbox, displayFire));
               hitboxSet.add(this.fireData(displayFire, true));
            }, displayFire -> hitboxSet.add(this.fireData(displayFire, false)), displayFire -> hitboxDestroy.add(displayFire.getId()));
         }

         if (!fireDisplay.getCreated().isEmpty()) {
            fireDisplay.getCreated().clear();
         }

         if (!fireDisplay.getReleased().isEmpty()) {
            fireDisplay.getReleased().clear();
         }

         if (updateHitboxMount.get()) {
            hitboxSet.add(this.hitboxMount(hitbox));
         }

         if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.d(destroy.size());
            destroy.forEach(buf::d);
            set.add(new PacketPlayOutEntityDestroy(buf));
         }

         if (hitboxDestroy != destroy && !hitboxDestroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.d(hitboxDestroy.size());
            hitboxDestroy.forEach(buf::d);
            hitboxSet.add(new PacketPlayOutEntityDestroy(buf));
         }

         if (modTargets != null) {
            NetworkUtils.sendBundled(targets, set);
            NetworkUtils.sendBundled(vanillaTargets, boneSet);
            this.sendBulkBoneData(modTargets, renderer, movementOnly, transformOnly);
         } else {
            NetworkUtils.sendBundled(targets, set);
         }

         if (hitboxSet != set) {
            NetworkUtils.sendBundled(targets, hitboxSet, player -> !player.getUniqueId().equals(owner));
         }
      }
   }

   private void sendBulkBoneData(Set<UUID> modTargets, DisplayRenderer renderer, boolean movementOnly, boolean transformOnly) {
      if (!movementOnly) {
         for (UUID uuid : modTargets) {
            BulkEntityDataEncoder encoder = new BulkEntityDataEncoder();
            Packets fallbackPackets = new Packets();

            for (DisplayBone bone : renderer.getRendered().values()) {
               UpdateScheme<DisplayBone.BoneData> scheme = bone.getModelUpdateScheme();

               for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                  switch (scheme.getUpdateMode(boneData)) {
                     case NONE:
                     case UPDATE:
                        if (boneData.getModel().isDirty()) {
                           fallbackPackets.add((Packets.PacketSupplier)u -> this.displayData(u, null, boneData, false, transformOnly));
                        } else if (!boneData.getBone().isSkippable()) {
                           this.encodeBoneData(encoder, uuid, null, boneData, false, transformOnly);
                        } else {
                           AnimationLODHandler.LODTracker tracker = this.modeledEntity.getAnimationLodHandler().tick(uuid);
                           if (!tracker.isCanSkip()) {
                              this.encodeBoneData(encoder, uuid, tracker, boneData, false, transformOnly);
                           }
                        }
                  }
               }
            }

            if (encoder.getEntryCount() > 0) {
               byte[] data = encoder.build();
               PacketDataSerializer buf = NetworkUtils.createByteBuf();
               buf.a("modelengine:bulk_data");
               buf.writeBytes(data);
               Packet<? super PacketListenerPlayOut> packet = createCustomPayload(buf);
               if (packet != null) {
                  NetworkUtils.send(uuid, packet);
               }
            }

            NetworkUtils.sendBundled(uuid, fallbackPackets);
         }
      }
   }

   private static Packet<? super PacketListenerPlayOut> createCustomPayload(PacketDataSerializer buf) {
      try {
         if (CUSTOM_PAYLOAD_CONSTRUCTOR == null) {
            CUSTOM_PAYLOAD_CONSTRUCTOR = PacketPlayOutCustomPayload.class.getDeclaredConstructor(PacketDataSerializer.class);
            CUSTOM_PAYLOAD_CONSTRUCTOR.setAccessible(true);
         }

         return (Packet<? super PacketListenerPlayOut>)CUSTOM_PAYLOAD_CONSTRUCTOR.newInstance(buf);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   private void encodeBoneData(
      BulkEntityDataEncoder encoder,
      UUID uuid,
      @Nullable AnimationLODHandler.LODTracker tracker,
      DisplayBone.BoneData boneData,
      boolean force,
      boolean transformOnly
   ) {
      DisplayBone bone = boneData.getBone();
      if (force || transformOnly || bone.isDirty() || boneData.getModel().isDirty()) {
         byte update = bone.getSnapshotHandler().getUpdate(uuid);
         boolean wouldWriteTransform = force || update != 0;
         boolean wouldWriteRender = force || bone.isRenderDirty();
         if (wouldWriteTransform || wouldWriteRender) {
            BulkEntityDataEncoder.EntryWriter entry = encoder.startEntry(boneData.getId());
            bone.getPosition().ifDirty(v -> entry.translation(new Vector3f(v)), force || TMath.getBit(update, 0));
            bone.getLeftRotation().ifDirty(q -> entry.leftRotation(new Quaternionf(q)), force || TMath.getBit(update, 1));
            bone.getScale().ifDirty(v -> entry.scale(new Vector3f(v)), force || TMath.getBit(update, 2));
            bone.getRightRotation().ifDirty(q -> entry.rightRotation(new Quaternionf(q)), force || TMath.getBit(update, 3));
            int lodTick = tracker == null ? 1 : tracker.getTickDuration().get();
            boolean lodDirty = tracker != null && tracker.getTickDuration().isDirty();
            bone.getStep().ifDirty(flag -> entry.transformDuration(flag ? 0 : lodTick), force || transformOnly || lodDirty);
            bone.getGlowing().ifDirty(flag -> bone.getGlowColor().ifDirty(color -> entry.glowData((byte)(flag ? 96 : 32), color), true), force);
            bone.getBrightness().ifDirty(entry::brightness, force);
            boolean hasBillboard = bone.getBillboard().isDirty() || force;
            boolean hasVisibility = bone.getVisibility().isDirty() || force;
            boolean hasDisplayType = bone.getDisplay().isDirty() || force;
            if (hasBillboard || hasVisibility || hasDisplayType) {
               entry.renderData(
                  (byte)bone.getBillboard().get().ordinal(),
                  bone.getVisibility().get() ? this.viewRange : 0.0F,
                  bone.getDisplay().get() == null ? 0 : (byte)bone.getDisplay().get().ordinal()
               );
            }

            entry.finish();
         }
      }
   }

   private void updateCulled(Set<UUID> targets, DisplayRenderer renderer) {
      if (!targets.isEmpty()) {
         IntArrayList entityIds = new IntArrayList();
         Map<String, DisplayBone> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            destroy.values()
               .stream()
               .mapMultiToInt((bones, intConsumer) -> bones.getModel().forEach((uuid, boneData) -> intConsumer.accept(boneData.getId())))
               .forEach(entityIds::add);
         }

         if (!entityIds.isEmpty()) {
            NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(entityIds));
         }
      }
   }

   private void remove(Set<UUID> targets, DisplayRenderer renderer) {
      if (!targets.isEmpty()) {
         IntArrayList entityIds = new IntArrayList();
         renderer.getRendered().forEach((s, bone) -> {
            targets.forEach(uuid -> bone.getSnapshotHandler().remove(uuid));
            bone.getModel().forEach((integer, boneData) -> entityIds.add(boneData.getId()));
         });
         entityIds.add(renderer.getPivot().getId());
         entityIds.add(renderer.getHitbox().getPivotId());
         entityIds.add(renderer.getHitbox().getHitboxId());
         entityIds.add(renderer.getHitbox().getShadowId());
         renderer.getHitbox().getFireDisplay().getAll().forEach(displayFire -> entityIds.add(displayFire.getId()));
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(entityIds));
      }
   }

   private Packets.PacketSupplier pivotSpawn(DisplayRenderer.Pivot pivot) {
      return pivot.isOverridden() ? null : NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
   }

   private PacketPlayOutEntityMetadata pivotData(DisplayRenderer.Pivot pivot) {
      if (pivot.isOverridden()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(pivot.getId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.writeByte(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutMount pivotMount(DisplayRenderer.Pivot pivot) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(pivot.getId());
      buf.d(pivot.getPassengers().size());
      pivot.getPassengers().forEach(buf::d);
      return new PacketPlayOutMount(buf);
   }

   private Object pivotTeleport(DisplayRenderer.Pivot pivot) {
      return !pivot.getPosition().isDirty() ? null : NetworkUtils.createPivotMove(pivot.getId(), pivot.getPositionCodec(), pivot.getPosition().get());
   }

   private PacketPlayOutSpawnEntity displaySpawn(DisplayRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
      Vector3f pos = pivot.getPosition().get();
      return new PacketPlayOutSpawnEntity(boneData.getId(), boneData.getUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ae, 0, Vec3D.b, 0.0);
   }

   private Packets.PacketSupplier displayData(DisplayBone.BoneData boneData, boolean force, boolean transformOnly) {
      DisplayBone bone = boneData.getBone();
      if (!force && !transformOnly && !bone.isDirty() && !boneData.getModel().isDirty()) {
         return null;
      } else {
         this.cleanupQueue.add(bone::clearDirty);
         this.cleanupQueue.add(() -> boneData.getModel().clearDirty());
         if (force) {
            return uuid -> this.displayData(uuid, this.modeledEntity.getAnimationLodHandler().tick(uuid), boneData, true, transformOnly);
         } else {
            return !bone.isSkippable()
               ? uuid -> this.displayData(uuid, null, boneData, false, transformOnly)
               : NetworkUtils.lodWrapper(this.modeledEntity, (uuid, tracker) -> this.displayData(uuid, tracker, boneData, false, transformOnly));
         }
      }
   }

   private PacketPlayOutEntityMetadata displayData(
      UUID uuid, @Nullable AnimationLODHandler.LODTracker tracker, DisplayBone.BoneData boneData, boolean force, boolean transformOnly
   ) {
      DisplayBone bone = boneData.getBone();
      byte update = bone.getSnapshotHandler().getUpdate(uuid);
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(boneData.getId());
      if (!force && update != 0) {
         EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
      }

      int lodTick = tracker == null ? 1 : tracker.getTickDuration().get();
      boolean lodDirty = tracker != null && tracker.getTickDuration().isDirty();
      bone.getStep().ifDirty(flag -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, flag ? 0 : lodTick), force || transformOnly || lodDirty);
      bone.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag ? 96 : 32)), force);
      bone.getGlowColor().ifDirty(color -> {
         if (!force || color != -1) {
            EntityUtils.writeData(buf, 21, DataWatcherRegistry.b, color);
         }
      }, force);
      bone.getBrightness().ifDirty(val -> {
         if (!force || val != -1) {
            EntityUtils.writeData(buf, 15, DataWatcherRegistry.b, val);
         }
      }, force);
      bone.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 10, DataWatcherRegistry.A, vector3f), force || TMath.getBit(update, 0));
      bone.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), force || TMath.getBit(update, 2));
      bone.getLeftRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.B, quaternionf), force || TMath.getBit(update, 1));
      bone.getRightRotation().ifDirty(quaternionf -> {
         if (!force || !TMath.isSimilar(quaternionf, IDENTITY_ROTATION)) {
            EntityUtils.writeData(buf, 13, DataWatcherRegistry.B, quaternionf);
         }
      }, force || TMath.getBit(update, 3));
      bone.getBillboard().ifDirty(billboard -> {
         if (!force || billboard.ordinal() != 0) {
            EntityUtils.writeData(buf, 14, DataWatcherRegistry.a, (byte)billboard.ordinal());
         }
      }, force);
      bone.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.d, flag ? this.viewRange : 0.0F), force);
      boneData.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.h, CraftItemStack.asNMSCopy(itemStack)), force);
      bone.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.a, display == null ? 0 : (byte)display.ordinal()), force);
      buf.writeByte(255);
      this.cleanupQueue.add(bone::clearDirty);
      this.cleanupQueue.add(() -> boneData.getModel().clearDirty());
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutEntityMetadata displayVisibleData(DisplayBone.BoneData boneData) {
      DisplayBone bone = boneData.getBone();
      if (!bone.isRenderDirty()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(boneData.getId());
      bone.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag ? 96 : 32)));
      bone.getGlowColor().ifDirty(color -> EntityUtils.writeData(buf, 21, DataWatcherRegistry.b, color));
      bone.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 15, DataWatcherRegistry.b, val));
      bone.getBillboard().ifDirty(billboard -> EntityUtils.writeData(buf, 14, DataWatcherRegistry.a, (byte)billboard.ordinal()));
      bone.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.d, flag ? this.viewRange : 0.0F));
      boneData.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.h, CraftItemStack.asNMSCopy(itemStack)));
      bone.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.a, display == null ? 0 : (byte)display.ordinal()));
      buf.writeByte(255);
      this.cleanupQueue.add(bone::clearDirty);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private Packets.PacketSupplier hitboxSpawnPivot(DisplayRenderer.Hitbox hitbox) {
      return hitbox.isPivotVisible() ? NetworkUtils.createPivotSpawn(hitbox.getPivotId(), hitbox.getPivotUuid(), hitbox.getPosition().get()) : null;
   }

   private PacketPlayOutEntityMetadata hitboxDataPivot(DisplayRenderer.Hitbox hitbox) {
      if (!hitbox.isPivotVisible()) {
         return null;
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(hitbox.getPivotId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.writeByte(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutSpawnEntity hitboxSpawn(DisplayRenderer.Hitbox hitbox) {
      if (!hitbox.isHitboxVisible()) {
         return null;
      }

      Vector3f pos = hitbox.getPosition().get();
      return new PacketPlayOutSpawnEntity(hitbox.getHitboxId(), hitbox.getHitboxUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ab, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata hitboxData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
      if (hitbox.isHitboxVisible() && (hitbox.isHitboxDirty() || spawn)) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         buf.d(hitbox.getHitboxId());
         hitbox.getWidth().ifDirty(val -> EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, val), spawn);
         hitbox.getHeight().ifDirty(val -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.d, val), spawn);
         buf.writeByte(255);
         return new PacketPlayOutEntityMetadata(buf);
      } else {
         return null;
      }
   }

   private PacketPlayOutSpawnEntity shadowSpawn(DisplayRenderer.Hitbox hitbox) {
      if (!hitbox.isShadowVisible()) {
         return null;
      }

      Vector3f pos = hitbox.getPosition().get();
      return new PacketPlayOutSpawnEntity(hitbox.getShadowId(), hitbox.getShadowUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ae, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata shadowData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
      if (hitbox.isShadowVisible() && (hitbox.getShadowRadius().isDirty() || spawn)) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         buf.d(hitbox.getShadowId());
         hitbox.getShadowRadius().ifDirty(val -> EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, val), spawn);
         buf.writeByte(255);
         return new PacketPlayOutEntityMetadata(buf);
      } else {
         return null;
      }
   }

   private PacketPlayOutSpawnEntity fireSpawn(DisplayRenderer.Hitbox hitbox, DisplayFire fire) {
      Vector3f pos = hitbox.getPosition().get();
      return new PacketPlayOutSpawnEntity(fire.getId(), fire.getUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ae, 0, Vec3D.b, 0.0);
   }

   private PacketPlayOutEntityMetadata fireData(DisplayFire fire, boolean spawn) {
      if (!fire.isDirty() && !spawn) {
         return null;
      }

      ArrayList<b<?>> entityData = new ArrayList<>(2);
      if (spawn) {
         entityData.add(new b(14, DataWatcherRegistry.a, (byte)BillboardConstraints.b.ordinal()));
         entityData.add(new b(15, DataWatcherRegistry.b, Brightness.c.a()));
      }

      fire.getFireModel().ifDirty(stack -> entityData.add(new b(22, DataWatcherRegistry.h, CraftItemStack.asNMSCopy(stack))), spawn);
      fire.getPosition().ifDirty(vector3f -> entityData.add(new b(10, DataWatcherRegistry.A, vector3f)), spawn);
      fire.getScale().ifDirty(vector3f -> entityData.add(new b(11, DataWatcherRegistry.A, vector3f)), spawn);
      fire.getVisible().ifDirty(flag -> entityData.add(new b(16, DataWatcherRegistry.d, flag ? this.viewRange : 0.0F)), spawn);
      return new PacketPlayOutEntityMetadata(fire.getId(), entityData);
   }

   private Object hitboxTeleport(DisplayRenderer.Hitbox hitbox) {
      if (hitbox.isPivotVisible() && hitbox.getPosition().isDirty()) {
         this.cleanupQueue.add(() -> hitbox.getPosition().clearDirty());
         return NetworkUtils.createPivotMove(hitbox.getPivotId(), hitbox.getPositionCodec(), hitbox.getPosition().get());
      } else {
         return null;
      }
   }

   private PacketPlayOutMount hitboxMount(DisplayRenderer.Hitbox hitbox) {
      if (!hitbox.isPivotVisible()) {
         return null;
      }

      boolean hitboxVisible = hitbox.isHitboxVisible();
      boolean shadowVisible = hitbox.isShadowVisible();
      ArrayList<Integer> passengers = new ArrayList<>(2 + hitbox.getFireDisplay().getInUse().size());
      if (hitboxVisible) {
         passengers.add(hitbox.getHitboxId());
      }

      if (shadowVisible) {
         passengers.add(hitbox.getShadowId());
      }

      if (hitbox.isFireVisible()) {
         hitbox.getFireDisplay().getInUse().forEach(displayFire -> passengers.add(displayFire.getId()));
      }

      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.d(hitbox.getPivotId());
      buf.d(passengers.size());
      passengers.forEach(buf::d);
      return new PacketPlayOutMount(buf);
   }
}
