package com.ticxo.modelengine.v1_21_R7.entity;

import ca.spottedleaf.moonrise.patches.entity_tracker.EntityTrackerEntity;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.interaction.DynamicHitbox;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.type.SubHitbox;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.EntityState;
import com.ticxo.modelengine.api.nms.entity.HitboxEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.impl.TempTrackedEntity;
import com.ticxo.modelengine.api.utils.RaceConditionUtil;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.promise.Promise;
import com.ticxo.modelengine.v1_21_R7.NMSFields;
import com.ticxo.modelengine.v1_21_R7.NMSMethods;
import com.ticxo.modelengine.v1_21_R7.entity.controller.BodyRotationControlWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.controller.LookControlWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.controller.MoveControlWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.hitbox.HitboxEntityImpl;
import com.ticxo.modelengine.v1_21_R7.entity.navigation.AmphibiousNavigationWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.navigation.FlyingNavigationWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.navigation.GroundNavigationWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.navigation.WallClimberNavigationWrapper;
import com.ticxo.modelengine.v1_21_R7.entity.navigation.WaterBoundNavigationWrapper;
import com.ticxo.modelengine.v1_21_R7.network.patch.PatchedServerGamePacketListener;
import com.ticxo.modelengine.v1_21_R7.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.Packets;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult.Fail;
import net.minecraft.world.InteractionResult.Pass;
import net.minecraft.world.InteractionResult.Success;
import net.minecraft.world.InteractionResult.TryEmptyHandInteraction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EntityHandlerImpl implements EntityHandler {
   private static boolean usePaperClipMethod;
   private static final MethodHandle JUMPING_GETTER = createJumpingGetter();
   private final Set<UUID> forceInvisible = new HashSet<>();
   private double forceRenderWidth;
   private double forceRenderHeight;
   private ArmorStand dummyArmorStand;
   private AreaEffectCloud dummyCloud;

   private static MethodHandle createJumpingGetter() {
      Field field = ReflectionUtils.getField(NMSFields.LIVING_ENTITY_jumping);
      if (field == null) {
         return null;
      }

      try {
         return MethodHandles.lookup().unreflectGetter(field);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         return null;
      }
   }

   public EntityHandlerImpl() {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(this::updateConfig);
   }

   @Override
   public void updateConfig() {
      this.forceRenderWidth = ConfigProperty.BLOCK_CULL_IGNORE_SIZE_WIDTH.getDouble();
      this.forceRenderHeight = ConfigProperty.BLOCK_CULL_IGNORE_SIZE_HEIGHT.getDouble();
      usePaperClipMethod = ConfigProperty.BLOCK_CULL_USE_PAPER_CLIP.getBoolean();
   }

   @Override
   public int getNextEntityId() {
      return CraftMagicNumbers.INSTANCE.nextEntityId();
   }

   @Override
   public int getEntityId(Entity entity) {
      return EntityUtils.nms(entity).getId();
   }

   @Override
   public void setHitbox(Entity entity, @NotNull Hitbox hitbox) {
      float width = (float)hitbox.getMaxWidth();
      float height = (float)hitbox.getHeight();
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      EntityDimensions box = new EntityDimensions(width, height, (float)hitbox.getEyeHeight(), EntityAttachments.createDefault(width, height), true);
      ReflectionUtils.set(nms, NMSFields.ENTITY_dimensions, box);
      ReflectionUtils.set(nms, NMSFields.ENTITY_eyeHeight, (float)hitbox.getEyeHeight());
      nms.setBoundingBox(box.makeBoundingBox(nms.position()));
   }

   @Override
   public void setStepHeight(Entity entity, double height) {
      if (EntityUtils.nms(entity) instanceof LivingEntity living) {
         living.getAttributes().registerAttribute(Attributes.STEP_HEIGHT);
         Objects.requireNonNull(living.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(height);
      }
   }

   @Override
   public double getStepHeight(Entity entity) {
      return EntityUtils.nms(entity).maxUpStep();
   }

   @Override
   public void setPosition(Entity entity, double x, double y, double z) {
      EntityUtils.nms(entity).setPos(x, y, z);
   }

   @Override
   public void movePassenger(Entity entity, double x, double y, double z) {
      net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
      if (this.dummyArmorStand == null) {
         this.dummyArmorStand = new ArmorStand(EntityType.ARMOR_STAND, nmsEntity.level());
         this.dummyArmorStand.setMarker(true);
      }

      double seatY = y - nmsEntity.getVehicleAttachmentPoint(this.dummyArmorStand).y;
      nmsEntity.setPos(x, seatY, z);
      nmsEntity.setDeltaMovement(Vec3.ZERO);
      nmsEntity.resetFallDistance();
      if (nmsEntity instanceof ServerPlayer player) {
         ReflectionUtils.set(player.connection, NMSFields.SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating, false);
      }
   }

   @Override
   public void forceSpawn(BaseEntity<?> entity, Player player) {
      if (player != null) {
         if (entity.getData() instanceof BukkitEntityData bukkitEntityData) {
            bukkitEntityData.getTracked().sendPairingData(player);
         }
      }
   }

   @Override
   public void forceDespawn(BaseEntity<?> entity, Player player) {
      if (player != null) {
         NetworkUtils.send(player.getUniqueId(), new ClientboundRemoveEntitiesPacket(new int[]{entity.getEntityId()}));
      }
   }

   @Override
   public void setForcedInvisible(Entity entity, boolean flag) {
      if (this.isForcedInvisible(entity) != flag) {
         net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
         byte data = 0;

         for (int i = 0; i < 8; i++) {
            data = TMath.setBit(data, i, nms.getSharedFlag(i));
         }

         if (flag) {
            this.forceInvisible.add(entity.getUniqueId());
            data = TMath.setBit(data, 5, true);
         } else {
            this.forceInvisible.remove(entity.getUniqueId());
         }

         ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
            this.getEntityId(entity), List.of(new DataValue(0, EntityDataSerializers.BYTE, data))
         );
         NetworkUtils.send(ServerInfo.getOnlinePlayers(), packet);
      }
   }

   @Override
   public boolean isForcedInvisible(UUID uuid) {
      return this.forceInvisible.contains(uuid);
   }

   @Override
   public BodyRotationController wrapBodyRotationControl(Entity entity, Supplier<BodyRotationController> def) {
      if (EntityUtils.nms(entity) instanceof Mob mob) {
         BodyRotationControlWrapper bodyRotationControlWrapper = new BodyRotationControlWrapper(mob);
         return ReflectionUtils.set(mob, NMSFields.MOB_bodyRotationControl, bodyRotationControlWrapper) ? bodyRotationControlWrapper : def.get();
      } else {
         return def.get();
      }
   }

   @Override
   public MoveController wrapMoveController(Entity entity, Supplier<MoveController> def) {
      if (EntityUtils.nms(entity) instanceof Mob mob) {
         if (mob.getMoveControl() instanceof MoveController controller) {
            return controller;
         } else {
            MoveControlWrapper controller = new MoveControlWrapper(mob, mob.getMoveControl());
            return ReflectionUtils.set(mob, NMSFields.MOB_moveControl, controller) ? controller : def.get();
         }
      } else {
         return def.get();
      }
   }

   @Override
   public LookController wrapLookController(Entity entity, Supplier<LookController> def) {
      if (EntityUtils.nms(entity) instanceof Mob mob) {
         if (mob.getLookControl() instanceof LookController controller) {
            return controller;
         } else {
            LookControlWrapper controller = new LookControlWrapper(mob, mob.getLookControl());
            return ReflectionUtils.set(mob, NMSFields.MOB_lookControl, controller) ? controller : def.get();
         }
      } else {
         return def.get();
      }
   }

   @Override
   public void wrapNavigation(Entity entity) {
      if (EntityUtils.nms(entity) instanceof Mob mob) {
         try {
            Field navField = ReflectionUtils.getField(NMSFields.MOB_navigation);
            PathNavigation oldNav = mob.getNavigation();
            PathNavigation newNav;
            switch (oldNav) {
               case WallClimberNavigation wallClimberNavigation:
                  newNav = new WallClimberNavigationWrapper(mob, wallClimberNavigation);
                  break;
               case GroundPathNavigation groundPathNavigation:
                  newNav = new GroundNavigationWrapper(mob, groundPathNavigation);
                  break;
               case FlyingPathNavigation flyingPathNavigation:
                  newNav = new FlyingNavigationWrapper(mob, flyingPathNavigation);
                  break;
               case WaterBoundPathNavigation waterBoundPathNavigation:
                  newNav = new WaterBoundNavigationWrapper(mob);
                  break;
               case AmphibiousPathNavigation amphibiousPathNavigation:
                  newNav = new AmphibiousNavigationWrapper(mob, amphibiousPathNavigation);
                  break;
               default:
                  TLogger.warn("Failed to create custom navigation for " + mob.getType() + ": " + mob.getUUID());
                  TLogger.warn("Reason: Navigation class type is " + oldNav.getClass().getSimpleName() + ".");
                  return;
            }

            navField.set(mob, newNav);
            GoalSelector goalSelector = (GoalSelector)ReflectionUtils.getField(NMSFields.MOB_goalSelector).get(mob);
            RaceConditionUtil.wrapConmod(() -> this.replaceNavigation(goalSelector, newNav));
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
   }

   private void replaceNavigation(GoalSelector goalSelector, PathNavigation newNav) {
      try {
         for (WrappedGoal wrappedGoal : goalSelector.getAvailableGoals()) {
            Goal goal = wrappedGoal.getGoal();

            for (Field field : goal.getClass().getDeclaredFields()) {
               field = ReflectionUtils.getField(goal.getClass(), field.getName());
               Object f = field.get(goal);
               if (f instanceof PathNavigation) {
                  field.set(goal, newNav);
               }
            }
         }
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   @Override
   public HitboxEntity createHitbox(Location location, ModelBone bone, SubHitbox subHitbox) {
      World bukkitWorld = location.getWorld();
      if (bukkitWorld == null) {
         if (bone.getActiveModel().getModeledEntity().getBase().getOriginal() instanceof Entity entity) {
            bukkitWorld = entity.getWorld();
         }

         if (bukkitWorld == null) {
            return null;
         }
      }

      ServerLevel level = ((CraftWorld)bukkitWorld).getHandle();
      HitboxEntityImpl entity = new HitboxEntityImpl(level, bone, subHitbox);
      entity.queueLocation(new Vector3f().set(location.getX(), location.getY(), location.getZ()));
      entity.setPos(location.getX(), location.getY(), location.getZ());
      ModelEngineAPI.setRenderCanceled(entity.getId(), true);
      Promise.start(entity.getBukkitEntity()).thenRunSync(() -> {
         level.addFreshEntity(entity);
         ModelEngineAPI.getInteractionTracker().addHitbox(entity);
      });
      return entity;
   }

   @Nullable
   @Override
   public HitboxEntity castHitbox(Entity entity) {
      return EntityUtils.nms(entity) instanceof HitboxEntity hitbox ? hitbox : null;
   }

   @Override
   public boolean hurt(Entity entity, Object source, float amount) {
      if (source instanceof DamageSource damageSource) {
         net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
         if (nmsEntity.level() instanceof ServerLevel worldserver) {
            return nmsEntity.hurtServer(worldserver, damageSource, amount);
         }
      }

      throw new RuntimeException("Passed in source is not an NMS DamageSource.");
   }

   @Override
   public EntityHandler.InteractionResult interact(Entity entity, HumanEntity player, EquipmentSlot hand) {
      if (EntityUtils.nms(entity) instanceof LivingEntity livingEntity) {
         net.minecraft.world.InteractionResult result = livingEntity.interact(
            ((CraftPlayer)player).getHandle(), hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND
         );

         return switch (result) {
            case Success success -> success.equals(net.minecraft.world.InteractionResult.SUCCESS_SERVER)
               ? EntityHandler.InteractionResult.SUCCESS
               : (
                  success.equals(net.minecraft.world.InteractionResult.SUCCESS)
                     ? EntityHandler.InteractionResult.CONSUME_PARTIAL
                     : EntityHandler.InteractionResult.CONSUME
               );
            case TryEmptyHandInteraction ignored -> EntityHandler.InteractionResult.SUCCESS_NO_ITEM_USED;
            case Pass ignored -> EntityHandler.InteractionResult.PASS;
            case Fail ignored -> EntityHandler.InteractionResult.FAIL;
            default -> throw new MatchException(null, null);
         };
      } else {
         return EntityHandler.InteractionResult.FAIL;
      }
   }

   @Override
   public void spawnDynamicHitbox(DynamicHitbox hitbox) {
      Vector location = hitbox.getPositionTracker().get();
      final Packets.PacketSupplier pivotSpawn = NetworkUtils.createPivotSpawn(
         DynamicHitbox.getPivotId(), DynamicHitbox.getPivotUUID(), location.toVector3f().add(0.0F, -0.5202F, 0.0F)
      );
      final ClientboundSetEntityDataPacket pivotData = new ClientboundSetEntityDataPacket(
         DynamicHitbox.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA
      );
      final ClientboundAddEntityPacket hitboxSpawn = new ClientboundAddEntityPacket(
         DynamicHitbox.getHitboxId(),
         DynamicHitbox.getHitboxUUID(),
         location.getX(),
         location.getY() - 0.5202,
         location.getZ(),
         0.0F,
         0.0F,
         EntityType.SLIME,
         0,
         Vec3.ZERO,
         0.0
      );
      final ClientboundSetEntityDataPacket hitboxData = new ClientboundSetEntityDataPacket(DynamicHitbox.getHitboxId(), EntityUtils.DEFAULT_SLIME_DATA);
      final ClientboundSetPassengersPacket mount = new ClientboundSetPassengersPacket(
         EntityContainer.of(DynamicHitbox.getPivotId(), DynamicHitbox.getHitboxId())
      );
      NetworkUtils.sendBundled(Set.of(hitbox.getPlayer().getUniqueId()), new Packets() {
         {
            this.add(pivotSpawn);
            this.add(pivotData);
            this.add(hitboxSpawn);
            this.add(hitboxData);
            this.add(mount);
         }
      });
   }

   @Override
   public void updateDynamicHitbox(DynamicHitbox hitbox) {
      Vector3f vector = hitbox.getPositionTracker().get().toVector3f().add(0.0F, -0.5202F, 0.0F);
      NetworkUtils.send(
         hitbox.getPlayer().getUniqueId(), NetworkUtils.createPivotTeleport(DynamicHitbox.getPivotId(), vector).supply(hitbox.getPlayer().getUniqueId())
      );
   }

   @Override
   public void destroyDynamicHitbox(DynamicHitbox hitbox) {
      ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(new int[]{DynamicHitbox.getHitboxId(), DynamicHitbox.getPivotId()});
      NetworkUtils.send(hitbox.getPlayer().getUniqueId(), destroy);
   }

   @Override
   public void forceUseItem(Player player, EquipmentSlot hand) {
      ItemStack stack = player.getEquipment().getItem(hand);
      net.minecraft.world.item.ItemStack nmsStack = ((CraftItemStack)stack).handle;
      ServerPlayer nmsPlayer = (ServerPlayer)EntityUtils.nms(player);
      ServerGamePacketListenerImpl connection = nmsPlayer.connection;
      ServerboundUseItemPacket useItemPacket = new ServerboundUseItemPacket(
         hand == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, 0, 0.0F, 0.0F
      );
      useItemPacket.timestamp = System.currentTimeMillis();
      PatchedServerGamePacketListener.handleUseItem(
         useItemPacket,
         connection,
         interactionResult -> {
            if (nmsStack.getUseAnimation() != ItemUseAnimation.NONE && interactionResult == net.minecraft.world.InteractionResult.CONSUME) {
               ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
                  player.getEntityId(), List.of(new DataValue(8, EntityDataSerializers.BYTE, (byte)(hand == EquipmentSlot.HAND ? 1 : 3)))
               );
               NetworkUtils.send(player.getUniqueId(), packet);
               if (nmsStack.getItem() instanceof InstrumentItem instrumentItem) {
                  Optional<? extends Holder<Instrument>> optional = ReflectionUtils.call(
                     instrumentItem, NMSMethods.INSTRUMENT_ITEM_getInstrument, nmsStack, nmsPlayer.registryAccess()
                  );
                  optional.ifPresent(
                     instrumentHolder -> {
                        Instrument instrument = instrumentHolder.value();
                        Holder<SoundEvent> soundEvent = instrument.soundEvent();
                        float f = instrument.range() / 16.0F;
                        RandomSource random = ReflectionUtils.get(nmsPlayer.level(), NMSFields.LEVEL_threadSafeRandom);
                        ClientboundSoundEntityPacket soundPacket = new ClientboundSoundEntityPacket(
                           soundEvent, SoundSource.RECORDS, nmsPlayer, f, 1.0F, random.nextLong()
                        );
                        NetworkUtils.send(player.getUniqueId(), soundPacket);
                     }
                  );
               }
            }
         }
      );
   }

   @Override
   public float getYRot(Entity entity) {
      return EntityUtils.nms(entity).getYRot();
   }

   @Override
   public float getYHeadRot(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      return nms instanceof LivingEntity livingEntity ? livingEntity.getYHeadRot() : nms.getYRot();
   }

   @Override
   public float getXHeadRot(Entity entity) {
      return EntityUtils.nms(entity).getXRot();
   }

   @Override
   public float getYBodyRot(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      return nms instanceof LivingEntity livingEntity ? livingEntity.yBodyRot : nms.getYRot();
   }

   @Override
   public void setYRot(Entity entity, float angle) {
      EntityUtils.nms(entity).setYRot(angle);
   }

   @Override
   public void setYHeadRot(Entity entity, float angle) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      if (nms instanceof LivingEntity livingEntity) {
         livingEntity.setYHeadRot(angle);
      } else {
         nms.setYRot(angle);
      }
   }

   @Override
   public void setXHeadRot(Entity entity, float angle) {
      EntityUtils.nms(entity).setXRot(angle);
   }

   @Override
   public void setYBodyRot(Entity entity, float angle) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      if (nms instanceof LivingEntity livingEntity) {
         livingEntity.setYBodyRot(angle);
      } else {
         nms.setYRot(angle);
      }
   }

   @Override
   public void move(Entity entity, double x, double y, double z) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      nms.move(MoverType.SELF, new Vec3(x, y, z));
   }

   @Override
   public boolean isWalking(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      if (nms.tickCount < 1) {
         return false;
      }

      double dX = nms.getX() - nms.xOld;
      double dZ = nms.getZ() - nms.zOld;
      return dX * dX + dZ * dZ > 2.5000003E-7F;
   }

   @Override
   public boolean isStrafing(Entity entity) {
      return false;
   }

   @Override
   public boolean isJumping(Entity entity) {
      if (JUMPING_GETTER != null && EntityUtils.nms(entity) instanceof LivingEntity livingEntity) {
         try {
            return (boolean)JUMPING_GETTER.invokeExact(livingEntity);
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }

      return false;
   }

   @Override
   public boolean isFlying(Entity entity) {
      return false;
   }

   @Override
   public float getHealth(Entity entity) {
      return EntityUtils.nms(entity) instanceof LivingEntity livingEntity ? livingEntity.getHealth() : 20.0F;
   }

   @Override
   public float getMaxHealth(Entity entity) {
      return EntityUtils.nms(entity) instanceof LivingEntity livingEntity ? livingEntity.getMaxHealth() : 20.0F;
   }

   @Override
   public Vector3f getPivotOffset(Entity entity) {
      net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
      if (this.dummyCloud == null) {
         this.dummyCloud = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, nmsEntity.level());
      }

      Vec3 offset = ReflectionUtils.call(
         nmsEntity, NMSMethods.ENTITY_getPassengerAttachmentPoint, this.dummyCloud, nmsEntity.getDimensions(Pose.STANDING), 1.0F
      );
      Vec3 offset2 = this.dummyCloud.getPassengerRidingPosition(this.dummyCloud);
      return offset == null ? new Vector3f() : offset.add(offset2).toVector3f();
   }

   @Override
   public Float getCameraEyeHeight(Entity entity) {
      net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
      return nmsEntity.getDimensions(Pose.STANDING).eyeHeight();
   }

   @Override
   public Float getCameraPivotHeight(Entity entity) {
      net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
      EntityDimensions dimensions = nmsEntity.getDimensions(nmsEntity.getPose());
      if (this.dummyCloud == null) {
         this.dummyCloud = new AreaEffectCloud(EntityType.AREA_EFFECT_CLOUD, nmsEntity.level());
      }

      Vec3 attachment = ReflectionUtils.call(nmsEntity, NMSMethods.ENTITY_getPassengerAttachmentPoint, this.dummyCloud, dimensions, 1.0F);
      return attachment == null ? null : (float)(dimensions.eyeHeight() - attachment.y);
   }

   @Override
   public boolean isRemoved(Entity entity) {
      return EntityUtils.nms(entity).isRemoved();
   }

   @Override
   public int getGlowColor(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      return nms.getTeamColor();
   }

   @Override
   public Set<UUID> getCollidableExemptions(Entity entity) {
      return EntityUtils.nms(entity) instanceof LivingEntity living ? living.collidableExemptions : null;
   }

   @Override
   public Component getCustomName(Entity entity) {
      net.minecraft.network.chat.Component name = EntityUtils.nms(entity).getCustomName();
      return name == null ? null : PaperAdventure.asAdventure(name);
   }

   @Override
   public void setDeathTick(Entity entity, int tick) {
      if (EntityUtils.nms(entity) instanceof LivingEntity livingEntity) {
         livingEntity.deathTime = tick;
      }
   }

   private static Int2ObjectMap<TrackedEntity> getEntityMap(ChunkMap chunkMap) {
      return chunkMap.entityMap;
   }

   @Override
   public com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity wrapTrackedEntity(Entity entity) {
      if (!ServerInfo.IS_FOLIA && !ServerInfo.IS_PAPER) {
         ServerLevel level = ((CraftWorld)entity.getWorld()).getHandle();
         Int2ObjectMap<TrackedEntity> map = getEntityMap(level.getChunkSource().chunkMap);
         if (map == null) {
            return new TempTrackedEntity(entity);
         }

         int entityId = entity.getEntityId();
         TrackedEntity tracker = map.get(entityId);
         return tracker == null ? new TempTrackedEntity(entity) : new TrackedEntityImpl(entity, () -> {
            ServerLevel lvl = ((CraftWorld)entity.getWorld()).getHandle();
            Int2ObjectMap<TrackedEntity> m = getEntityMap(lvl.getChunkSource().chunkMap);
            return m == null ? null : m.get(entity.getEntityId());
         }, tracker);
      } else {
         if (EntityUtils.nms(entity) instanceof EntityTrackerEntity trackerEntity) {
            TrackedEntity tracker = trackerEntity.moonrise$getTrackedEntity();
            if (tracker != null) {
               return new TrackedEntityImpl(
                  entity, () -> EntityUtils.nms(entity) instanceof EntityTrackerEntity te ? te.moonrise$getTrackedEntity() : null, tracker
               );
            }
         }

         return new TempTrackedEntity(entity);
      }
   }

   @Override
   public boolean isGlowing(Entity entity) {
      return EntityUtils.nms(entity).getSharedFlag(6);
   }

   @Override
   public boolean isVisualFire(Entity entity) {
      return EntityUtils.nms(entity).getSharedFlag(0);
   }

   @Override
   public int getFireTicks(Entity entity) {
      return this.isVisualFire(entity) ? 1 : 0;
   }

   @Override
   public BoundingBox getBoundingBox(Entity entity) {
      AABB nms = EntityUtils.nms(entity).getBoundingBox();
      return new BoundingBox(nms.minX, nms.minY, nms.minZ, nms.maxX, nms.maxY, nms.maxZ);
   }

   @Override
   public void getBoundingBox(Entity entity, BoundingBox dest) {
      AABB nms = EntityUtils.nms(entity).getBoundingBox();
      dest.resize(nms.minX, nms.minY, nms.minZ, nms.maxX, nms.maxY, nms.maxZ);
   }

   @Override
   public void readEntityState(Entity entity, EntityState state) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      state.yRot = nms.getYRot();
      state.xHeadRot = nms.getXRot();
      if (nms instanceof LivingEntity living) {
         state.yHeadRot = living.getYHeadRot();
         state.yBodyRot = living.yBodyRot;
         state.health = living.getHealth();
      } else {
         state.yHeadRot = state.yRot;
         state.yBodyRot = state.yRot;
         state.health = 20.0F;
      }

      boolean walking = false;
      if (nms.tickCount >= 1) {
         double dX = nms.getX() - nms.xOld;
         double dZ = nms.getZ() - nms.zOld;
         walking = dX * dX + dZ * dZ > 2.5000003E-7F;
      }

      state.walking = walking;
      state.strafing = false;
      state.flying = false;
      boolean jumping = false;
      if (JUMPING_GETTER != null && nms instanceof LivingEntity livingEntity) {
         try {
            jumping = (boolean)JUMPING_GETTER.invokeExact(livingEntity);
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }

      state.jumping = jumping;
      state.removed = nms.isRemoved();
      state.glowing = nms.getSharedFlag(6);
      state.onFire = nms.getSharedFlag(0);
      AABB box = nms.getBoundingBox();
      state.boundingBox.resize(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
   }

   @Override
   public void getLocation(Entity entity, Location location) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      Vec3 pos = nms.position();
      location.setWorld(nms.level().getWorld());
      location.setX(pos.x);
      location.setY(pos.y);
      location.setZ(pos.z);
      location.setYaw(nms.getYRot());
      location.setPitch(nms.getXRot());
   }

   @Override
   public boolean shouldCull(Player player, Location eyePosition, BoundingBox box) {
      CraftWorld world = (CraftWorld)player.getWorld();
      Vec3 start = CraftLocation.toVec3(eyePosition);
      if (!(box.getWidthX() >= this.forceRenderWidth) && !(box.getWidthZ() >= this.forceRenderWidth) && !(box.getHeight() >= this.forceRenderHeight)) {
         int minX = Mth.floor(box.getMinX());
         int minY = Mth.floor(box.getMinY());
         int minZ = Mth.floor(box.getMinZ());
         int maxX = Mth.ceil(box.getMaxX()) - 1;
         int maxY = Mth.ceil(box.getMaxY()) - 1;
         int maxZ = Mth.ceil(box.getMaxZ()) - 1;
         EntityHandler.BoxRelToCam relX = EntityHandler.BoxRelToCam.from(minX, maxX, Mth.floor(start.x));
         EntityHandler.BoxRelToCam relY = EntityHandler.BoxRelToCam.from(minY, maxY, Mth.floor(start.y));
         EntityHandler.BoxRelToCam relZ = EntityHandler.BoxRelToCam.from(minZ, maxZ, Mth.floor(start.z));
         if (relX == EntityHandler.BoxRelToCam.INSIDE && relY == EntityHandler.BoxRelToCam.INSIDE && relZ == EntityHandler.BoxRelToCam.INSIDE) {
            return false;
         }

         if (DebugToggle.isDebugging(DebugToggle.SHOW_CULL_POINTS)) {
            EntityHandler.sweepCullPoints(minX, minY, minZ, maxX, maxY, maxZ, relX, relY, relZ, (x, y, z) -> {
               world.spawnParticle(Particle.DUST, x, y, z, 1, new DustOptions(Color.RED, 0.2F));
               return false;
            });
         }

         return !EntityHandler.sweepCullPoints(minX, minY, minZ, maxX, maxY, maxZ, relX, relY, relZ, (x, y, z) -> isVisible(world, start, new Vec3(x, y, z)));
      } else {
         return false;
      }
   }

   private static boolean isVisible(CraftWorld world, Vec3 startPos, Vec3 endPos) {
      if (ServerInfo.IS_FOLIA && !Bukkit.isPrimaryThread()) {
         return true;
      }

      if (!usePaperClipMethod) {
         return OcclusionClipContext.isClear(world.getHandle(), startPos, endPos);
      }

      BlockHitResult nmsHitResult = world.getHandle().clip(new OcclusionClipContext(startPos, endPos));
      return nmsHitResult.getType() == Type.MISS;
   }
}
