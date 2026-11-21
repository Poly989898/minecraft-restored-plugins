package com.ticxo.modelengine.v1_21_R4.parser.model;

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
import com.ticxo.modelengine.v1_21_R4.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R4.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.BulkEntityDataEncoder;
import com.ticxo.modelengine.v1_21_R4.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R4.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Display.BillboardConstraints;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
   private static StreamCodec<RegistryFriendlyByteBuf, ?> CUSTOM_PAYLOAD_CODEC;

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

      if (renderer.hasGlowAudience()) {
         this.updateAudienceGlow(renderer, data.getTracking().keySet(), data.getStartTracking(), this.noCullForcePlayers);
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

   private void updateAudienceGlow(DisplayRenderer renderer, Set<UUID> current, Set<UUID> startTracking, Set<UUID> forceUpdates) {
      for (DisplayBone bone : renderer.getRendered().values()) {
         Set<UUID> audience = bone.getGlowAudience().get();
         if (audience != null) {
            boolean changed = bone.isAudienceGlowDirty();
            if (changed || !startTracking.isEmpty() || !forceUpdates.isEmpty()) {
               boolean glowing = bone.getAudienceGlowing().get();
               int color = bone.getAudienceGlowColor().get();
               Set<UUID> on = new HashSet<>();
               Set<UUID> off = new HashSet<>();
               if (changed) {
                  for (UUID uuid : current) {
                     if (glowing && audience.contains(uuid)) {
                        on.add(uuid);
                     } else {
                        off.add(uuid);
                     }
                  }

                  bone.clearAudienceGlowDirty();
               } else if (glowing) {
                  for (UUID uuid : startTracking) {
                     if (audience.contains(uuid)) {
                        on.add(uuid);
                     }
                  }

                  for (UUID uuid : forceUpdates) {
                     if (audience.contains(uuid)) {
                        on.add(uuid);
                     }
                  }
               }

               if (!on.isEmpty() || !off.isEmpty()) {
                  for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                     int id = boneData.getId();
                     if (!on.isEmpty()) {
                        NetworkUtils.send(on, this.audienceGlowData(id, true, color));
                     }

                     if (!off.isEmpty()) {
                        NetworkUtils.send(off, this.audienceGlowData(id, false, color));
                     }
                  }
               }
            }
         }
      }
   }

   private ClientboundSetEntityDataPacket audienceGlowData(int entityId, boolean glowing, int color) {
      ArrayList<DataValue<?>> entityData = new ArrayList<>(2);
      entityData.add(new DataValue(0, EntityDataSerializers.BYTE, (byte)(glowing ? 96 : 32)));
      if (glowing) {
         entityData.add(new DataValue(22, EntityDataSerializers.INT, color));
      }

      return new ClientboundSetEntityDataPacket(entityId, entityData);
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
         set.add(this.pivotCameraResize(pivot));
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
            set.add(new ClientboundRemoveEntitiesPacket(new IntArrayList(destroy)));
         }

         if (hitboxDestroy != destroy && !hitboxDestroy.isEmpty()) {
            hitboxSet.add(new ClientboundRemoveEntitiesPacket(new IntArrayList(hitboxDestroy)));
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
               RegistryFriendlyByteBuf buf = NetworkUtils.createByteBuf();
               buf.writeUtf("modelengine:bulk_data");
               buf.writeBytes(data);
               Packet<? super ClientGamePacketListener> packet = createCustomPayload(buf);
               if (packet != null) {
                  NetworkUtils.send(uuid, packet);
               }
            }

            NetworkUtils.sendBundled(uuid, fallbackPackets);
         }
      }
   }

   private static Packet<? super ClientGamePacketListener> createCustomPayload(RegistryFriendlyByteBuf buf) {
      try {
         if (CUSTOM_PAYLOAD_CODEC == null) {
            Field field = ClientboundCustomPayloadPacket.class.getField("GAMEPLAY_STREAM_CODEC");
            field.setAccessible(true);
            CUSTOM_PAYLOAD_CODEC = (StreamCodec<RegistryFriendlyByteBuf, ?>)field.get(null);
         }

         return (Packet<? super ClientGamePacketListener>)CUSTOM_PAYLOAD_CODEC.decode(buf);
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
            NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(entityIds));
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
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(entityIds));
      }
   }

   private Packets.PacketSupplier pivotSpawn(DisplayRenderer.Pivot pivot) {
      return pivot.isCameraPivot()
         ? NetworkUtils.createCameraPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get(), pivot.getCameraHeight().get())
         : NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
   }

   private ClientboundSetEntityDataPacket pivotData(DisplayRenderer.Pivot pivot) {
      return pivot.isCameraPivot()
         ? new ClientboundSetEntityDataPacket(pivot.getId(), this.cameraPivotData(pivot.getCameraHeight().get()))
         : new ClientboundSetEntityDataPacket(pivot.getId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
   }

   private List<DataValue<?>> cameraPivotData(float height) {
      return List.of(
         new DataValue(0, EntityDataSerializers.BYTE, (byte)32),
         new DataValue(8, EntityDataSerializers.FLOAT, 0.0F),
         new DataValue(9, EntityDataSerializers.FLOAT, height)
      );
   }

   private ClientboundSetPassengersPacket pivotMount(DisplayRenderer.Pivot pivot) {
      return new ClientboundSetPassengersPacket(EntityContainer.of(pivot.getId(), pivot.getPassengers()));
   }

   private Object pivotTeleport(DisplayRenderer.Pivot pivot) {
      if (!pivot.getPosition().isDirty()) {
         return null;
      } else {
         return pivot.isCameraPivot()
            ? NetworkUtils.createCameraPivotMove(pivot.getId(), pivot.getPositionCodec(), pivot.getPosition().get(), pivot.getCameraHeight().get())
            : NetworkUtils.createPivotMove(pivot.getId(), pivot.getPositionCodec(), pivot.getPosition().get());
      }
   }

   private ClientboundSetEntityDataPacket pivotCameraResize(DisplayRenderer.Pivot pivot) {
      return pivot.isCameraPivot() && pivot.getCameraHeight().isDirty()
         ? new ClientboundSetEntityDataPacket(pivot.getId(), List.of(new DataValue(9, EntityDataSerializers.FLOAT, pivot.getCameraHeight().get())))
         : null;
   }

   private ClientboundAddEntityPacket displaySpawn(DisplayRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
      Vector3f pos = pivot.getPosition().get();
      return new ClientboundAddEntityPacket(boneData.getId(), boneData.getUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
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

   private ClientboundSetEntityDataPacket displayData(
      UUID uuid, @Nullable AnimationLODHandler.LODTracker tracker, DisplayBone.BoneData boneData, boolean force, boolean transformOnly
   ) {
      DisplayBone bone = boneData.getBone();
      byte update = bone.getSnapshotHandler().getUpdate(uuid);
      ArrayList<DataValue<?>> entityData = new ArrayList<>(13);
      if (!force && update != 0) {
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
      }

      int lodTick = tracker == null ? 1 : tracker.getTickDuration().get();
      boolean lodDirty = tracker != null && tracker.getTickDuration().isDirty();
      bone.getStep().ifDirty(flag -> entityData.add(new DataValue(9, EntityDataSerializers.INT, flag ? 0 : lodTick)), force || transformOnly || lodDirty);
      bone.getGlowing().ifDirty(flag -> entityData.add(new DataValue(0, EntityDataSerializers.BYTE, (byte)(flag ? 96 : 32))), force);
      bone.getGlowColor().ifDirty(color -> {
         if (!force || color != -1) {
            entityData.add(new DataValue(22, EntityDataSerializers.INT, color));
         }
      }, force);
      bone.getBrightness().ifDirty(val -> {
         if (!force || val != -1) {
            entityData.add(new DataValue(16, EntityDataSerializers.INT, val));
         }
      }, force);
      bone.getPosition().ifDirty(vector3f -> entityData.add(new DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), force || TMath.getBit(update, 0));
      bone.getScale().ifDirty(vector3f -> entityData.add(new DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), force || TMath.getBit(update, 2));
      bone.getLeftRotation()
         .ifDirty(quaternionf -> entityData.add(new DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), force || TMath.getBit(update, 1));
      bone.getRightRotation().ifDirty(quaternionf -> {
         if (!force || !TMath.isSimilar(quaternionf, IDENTITY_ROTATION)) {
            entityData.add(new DataValue(14, EntityDataSerializers.QUATERNION, quaternionf));
         }
      }, force || TMath.getBit(update, 3));
      bone.getBillboard().ifDirty(billboard -> {
         if (!force || billboard.ordinal() != 0) {
            entityData.add(new DataValue(15, EntityDataSerializers.BYTE, (byte)billboard.ordinal()));
         }
      }, force);
      bone.getVisibility().ifDirty(flag -> entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, flag ? this.viewRange : 0.0F)), force);
      boneData.getModel().ifDirty(itemStack -> entityData.add(new DataValue(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack))), force);
      bone.getDisplay().ifDirty(display -> entityData.add(new DataValue(24, EntityDataSerializers.BYTE, display == null ? 0 : (byte)display.ordinal())), force);
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(boneData.getId(), entityData);
   }

   private ClientboundSetEntityDataPacket displayVisibleData(DisplayBone.BoneData boneData) {
      DisplayBone bone = boneData.getBone();
      if (!bone.isRenderDirty() && !boneData.getModel().isDirty()) {
         return null;
      }

      ArrayList<DataValue<?>> entityData = new ArrayList<>(6);
      bone.getGlowing().ifDirty(flag -> entityData.add(new DataValue(0, EntityDataSerializers.BYTE, (byte)(flag ? 96 : 32))));
      bone.getGlowColor().ifDirty(color -> entityData.add(new DataValue(22, EntityDataSerializers.INT, color)));
      bone.getBrightness().ifDirty(val -> entityData.add(new DataValue(16, EntityDataSerializers.INT, val)));
      bone.getBillboard().ifDirty(billboard -> entityData.add(new DataValue(15, EntityDataSerializers.BYTE, (byte)billboard.ordinal())));
      bone.getVisibility().ifDirty(flag -> entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, flag ? this.viewRange : 0.0F)));
      boneData.getModel().ifDirty(itemStack -> entityData.add(new DataValue(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack))));
      bone.getDisplay().ifDirty(display -> entityData.add(new DataValue(24, EntityDataSerializers.BYTE, display == null ? 0 : (byte)display.ordinal())));
      this.cleanupQueue.add(bone::clearDirty);
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(boneData.getId(), entityData);
   }

   private Packets.PacketSupplier hitboxSpawnPivot(DisplayRenderer.Hitbox hitbox) {
      return hitbox.isPivotVisible() ? NetworkUtils.createPivotSpawn(hitbox.getPivotId(), hitbox.getPivotUuid(), hitbox.getPosition().get()) : null;
   }

   private ClientboundSetEntityDataPacket hitboxDataPivot(DisplayRenderer.Hitbox hitbox) {
      return !hitbox.isPivotVisible() ? null : new ClientboundSetEntityDataPacket(hitbox.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
   }

   private ClientboundAddEntityPacket hitboxSpawn(DisplayRenderer.Hitbox hitbox) {
      if (!hitbox.isHitboxVisible()) {
         return null;
      }

      Vector3f pos = hitbox.getPosition().get();
      return new ClientboundAddEntityPacket(
         hitbox.getHitboxId(), hitbox.getHitboxUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket hitboxData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
      if (hitbox.isHitboxVisible() && (hitbox.isHitboxDirty() || spawn)) {
         ArrayList<DataValue<?>> entityData = new ArrayList<>(4);
         hitbox.getWidth().ifDirty(val -> entityData.add(new DataValue(8, EntityDataSerializers.FLOAT, val)), spawn);
         hitbox.getHeight().ifDirty(val -> entityData.add(new DataValue(9, EntityDataSerializers.FLOAT, val)), spawn);
         return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(hitbox.getHitboxId(), entityData);
      } else {
         return null;
      }
   }

   private ClientboundAddEntityPacket shadowSpawn(DisplayRenderer.Hitbox hitbox) {
      if (!hitbox.isShadowVisible()) {
         return null;
      }

      Vector3f pos = hitbox.getPosition().get();
      return new ClientboundAddEntityPacket(
         hitbox.getShadowId(), hitbox.getShadowUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket shadowData(DisplayRenderer.Hitbox hitbox, boolean spawn) {
      if (hitbox.isShadowVisible() && (hitbox.getShadowRadius().isDirty() || spawn)) {
         ArrayList<DataValue<?>> entityData = new ArrayList<>(2);
         hitbox.getShadowRadius().ifDirty(val -> entityData.add(new DataValue(18, EntityDataSerializers.FLOAT, val)), spawn);
         return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(hitbox.getShadowId(), entityData);
      } else {
         return null;
      }
   }

   private ClientboundAddEntityPacket fireSpawn(DisplayRenderer.Hitbox hitbox, DisplayFire fire) {
      Vector3f pos = hitbox.getPosition().get();
      return new ClientboundAddEntityPacket(fire.getId(), fire.getUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
   }

   private ClientboundSetEntityDataPacket fireData(DisplayFire fire, boolean spawn) {
      if (!fire.isDirty() && !spawn) {
         return null;
      }

      ArrayList<DataValue<?>> entityData = new ArrayList<>(2);
      if (spawn) {
         entityData.add(new DataValue(15, EntityDataSerializers.BYTE, (byte)BillboardConstraints.VERTICAL.ordinal()));
         entityData.add(new DataValue(16, EntityDataSerializers.INT, Brightness.FULL_BRIGHT.pack()));
      }

      fire.getFireModel().ifDirty(stack -> entityData.add(new DataValue(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(stack))), spawn);
      fire.getPosition().ifDirty(vector3f -> entityData.add(new DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), spawn);
      fire.getScale().ifDirty(vector3f -> entityData.add(new DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), spawn);
      fire.getVisible().ifDirty(flag -> entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, flag ? this.viewRange : 0.0F)), spawn);
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(fire.getId(), entityData);
   }

   private Object hitboxTeleport(DisplayRenderer.Hitbox hitbox) {
      if (hitbox.isPivotVisible() && hitbox.getPosition().isDirty()) {
         this.cleanupQueue.add(() -> hitbox.getPosition().clearDirty());
         return NetworkUtils.createPivotMove(hitbox.getPivotId(), hitbox.getPositionCodec(), hitbox.getPosition().get());
      } else {
         return null;
      }
   }

   private ClientboundSetPassengersPacket hitboxMount(DisplayRenderer.Hitbox hitbox) {
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

      return new ClientboundSetPassengersPacket(EntityContainer.of(hitbox.getPivotId(), passengers));
   }
}
