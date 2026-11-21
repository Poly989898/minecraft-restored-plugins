package com.ticxo.modelengine.v1_20_R3.entity;

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
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import com.ticxo.modelengine.api.nms.impl.TempTrackedEntity;
import com.ticxo.modelengine.api.utils.RaceConditionUtil;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.config.DebugToggle;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.promise.Promise;
import com.ticxo.modelengine.v1_20_R3.NMSFields;
import com.ticxo.modelengine.v1_20_R3.NMSMethods;
import com.ticxo.modelengine.v1_20_R3.entity.controller.BodyRotationControlWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.controller.LookControlWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.controller.MoveControlWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.hitbox.HitboxEntityImpl;
import com.ticxo.modelengine.v1_20_R3.entity.navigation.AmphibiousNavigationWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.navigation.FlyingNavigationWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.navigation.GroundNavigationWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.navigation.WallClimberNavigationWrapper;
import com.ticxo.modelengine.v1_20_R3.entity.navigation.WaterBoundNavigationWrapper;
import com.ticxo.modelengine.v1_20_R3.network.patch.PatchedServerGamePacketListener;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntitySound;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.PlayerChunkMap.EntityTracker;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAreaEffectCloud;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.navigation.NavigationSpider;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.item.EnumAnimation;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.MovingObjectPosition.EnumMovingObjectType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
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
   private EntityArmorStand dummyArmorStand;
   private EntityAreaEffectCloud dummyCloud;

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
   public void setHitbox(Entity entity, @NotNull Hitbox hitbox) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      EntitySize box = new EntitySize((float)hitbox.getMaxWidth(), (float)hitbox.getHeight(), true);
      ReflectionUtils.set(nms, NMSFields.ENTITY_dimensions, box);
      ReflectionUtils.set(nms, NMSFields.ENTITY_eyeHeight, (float)hitbox.getEyeHeight());
      nms.a(box.a(nms.dk()));
   }

   @Override
   public void setStepHeight(Entity entity, double height) {
      EntityUtils.nms(entity).t((float)height);
   }

   @Override
   public double getStepHeight(Entity entity) {
      return EntityUtils.nms(entity).dG();
   }

   @Override
   public void setPosition(Entity entity, double x, double y, double z) {
      EntityUtils.nms(entity).a_(x, y, z);
   }

   @Override
   public void movePassenger(Entity entity, double x, double y, double z) {
      net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
      if (this.dummyArmorStand == null) {
         this.dummyArmorStand = new EntityArmorStand(EntityTypes.d, nmsEntity.dM());
         this.dummyArmorStand.u(true);
      }

      double seatY = y + nmsEntity.k(this.dummyArmorStand);
      nmsEntity.a_(x, seatY, z);
      nmsEntity.g(Vec3D.b);
      nmsEntity.n();
      if (nmsEntity instanceof EntityPlayer player) {
         ReflectionUtils.set(player.c, NMSFields.SERVER_GAME_PACKET_LISTENER_IMPL_clientIsFloating, false);
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
         NetworkUtils.send(player.getUniqueId(), new PacketPlayOutEntityDestroy(new int[]{entity.getEntityId()}));
      }
   }

   @Override
   public void setForcedInvisible(Entity entity, boolean flag) {
      if (this.isForcedInvisible(entity) != flag) {
         net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
         byte data = 0;

         for (int i = 0; i < 8; i++) {
            data = TMath.setBit(data, i, nms.i(i));
         }

         if (flag) {
            this.forceInvisible.add(entity.getUniqueId());
            data = TMath.setBit(data, 5, true);
         } else {
            this.forceInvisible.remove(entity.getUniqueId());
         }

         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         buf.c(entity.getEntityId());
         EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, data);
         buf.k(255);
         PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(buf);
         NetworkUtils.send(ServerInfo.getOnlinePlayers(), packet);
      }
   }

   @Override
   public boolean isForcedInvisible(UUID uuid) {
      return this.forceInvisible.contains(uuid);
   }

   @Override
   public BodyRotationController wrapBodyRotationControl(Entity entity, Supplier<BodyRotationController> def) {
      if (EntityUtils.nms(entity) instanceof EntityInsentient mob) {
         BodyRotationControlWrapper bodyRotationControlWrapper = new BodyRotationControlWrapper(mob);
         return ReflectionUtils.set(mob, NMSFields.MOB_bodyRotationControl, bodyRotationControlWrapper) ? bodyRotationControlWrapper : def.get();
      } else {
         return def.get();
      }
   }

   @Override
   public MoveController wrapMoveController(Entity entity, Supplier<MoveController> def) {
      if (EntityUtils.nms(entity) instanceof EntityInsentient mob) {
         if (mob.K() instanceof MoveController controller) {
            return controller;
         } else {
            MoveControlWrapper controller = new MoveControlWrapper(mob, mob.K());
            return ReflectionUtils.set(mob, NMSFields.MOB_moveControl, controller) ? controller : def.get();
         }
      } else {
         return def.get();
      }
   }

   @Override
   public LookController wrapLookController(Entity entity, Supplier<LookController> def) {
      if (EntityUtils.nms(entity) instanceof EntityInsentient mob) {
         if (mob.I() instanceof LookController controller) {
            return controller;
         } else {
            LookControlWrapper controller = new LookControlWrapper(mob, mob.I());
            return ReflectionUtils.set(mob, NMSFields.MOB_lookControl, controller) ? controller : def.get();
         }
      } else {
         return def.get();
      }
   }

   @Override
   public void wrapNavigation(Entity entity) {
      if (EntityUtils.nms(entity) instanceof EntityInsentient mob) {
         try {
            Field navField = ReflectionUtils.getField(NMSFields.MOB_navigation);
            NavigationAbstract oldNav = mob.N();
            NavigationAbstract newNav;
            if (oldNav instanceof NavigationSpider wallClimberNavigation) {
               newNav = new WallClimberNavigationWrapper(mob, wallClimberNavigation);
            } else if (oldNav instanceof Navigation groundPathNavigation) {
               newNav = new GroundNavigationWrapper(mob, groundPathNavigation);
            } else if (oldNav instanceof NavigationFlying flyingPathNavigation) {
               newNav = new FlyingNavigationWrapper(mob, flyingPathNavigation);
            } else if (oldNav instanceof NavigationGuardian) {
               newNav = new WaterBoundNavigationWrapper(mob);
            } else {
               if (!(oldNav instanceof AmphibiousPathNavigation amphibiousPathNavigation)) {
                  TLogger.warn("Failed to create custom navigation for " + mob.ai() + ": " + mob.cw());
                  TLogger.warn("Reason: Navigation class type is " + oldNav.getClass().getSimpleName() + ".");
                  return;
               }

               newNav = new AmphibiousNavigationWrapper(mob, amphibiousPathNavigation);
            }

            navField.set(mob, newNav);
            PathfinderGoalSelector goalSelector = (PathfinderGoalSelector)ReflectionUtils.getField(NMSFields.MOB_goalSelector).get(mob);
            RaceConditionUtil.wrapConmod(() -> this.replaceNavigation(goalSelector, newNav));
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }
      }
   }

   private void replaceNavigation(PathfinderGoalSelector goalSelector, NavigationAbstract newNav) {
      try {
         for (PathfinderGoalWrapped wrappedGoal : goalSelector.b()) {
            PathfinderGoal goal = wrappedGoal.k();

            for (Field field : goal.getClass().getDeclaredFields()) {
               field = ReflectionUtils.getField(goal.getClass(), field.getName());
               Object f = field.get(goal);
               if (f instanceof NavigationAbstract) {
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
      WorldServer level = ((CraftWorld)location.getWorld()).getHandle();
      HitboxEntityImpl entity = new HitboxEntityImpl(level, bone, subHitbox);
      entity.queueLocation(new Vector3f().set(location.getX(), location.getY(), location.getZ()));
      entity.a_(location.getX(), location.getY(), location.getZ());
      ModelEngineAPI.setRenderCanceled(entity.aj(), true);
      Promise.start(entity.getBukkitEntity()).thenRunSync(() -> {
         level.b(entity);
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
         return EntityUtils.nms(entity).a(damageSource, amount);
      } else {
         throw new RuntimeException("Passed in source is not an NMS DamageSource.");
      }
   }

   @Override
   public EntityHandler.InteractionResult interact(Entity entity, HumanEntity player, EquipmentSlot hand) {
      if (EntityUtils.nms(entity) instanceof EntityLiving livingEntity) {
         EnumInteractionResult result = livingEntity.a(((CraftPlayer)player).getHandle(), hand == EquipmentSlot.HAND ? EnumHand.a : EnumHand.b);

         return switch (result) {
            case a -> EntityHandler.InteractionResult.SUCCESS;
            case b -> EntityHandler.InteractionResult.CONSUME;
            case c -> EntityHandler.InteractionResult.CONSUME_PARTIAL;
            case d -> EntityHandler.InteractionResult.PASS;
            case e -> EntityHandler.InteractionResult.FAIL;
            default -> throw new IncompatibleClassChangeError();
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
      PacketDataSerializer pivotDataBuf = NetworkUtils.createByteBuf();
      pivotDataBuf.c(DynamicHitbox.getPivotId());
      EntityUtils.writeData(pivotDataBuf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(pivotDataBuf, 8, DataWatcherRegistry.d, 0.0F);
      pivotDataBuf.k(255);
      final PacketPlayOutEntityMetadata pivotData = new PacketPlayOutEntityMetadata(pivotDataBuf);
      final PacketPlayOutSpawnEntity hitboxSpawn = new PacketPlayOutSpawnEntity(
         DynamicHitbox.getHitboxId(),
         DynamicHitbox.getHitboxUUID(),
         location.getX(),
         location.getY() - 0.5202,
         location.getZ(),
         0.0F,
         0.0F,
         EntityTypes.aM,
         0,
         Vec3D.b,
         0.0
      );
      PacketDataSerializer hitboxDataBuf = NetworkUtils.createByteBuf();
      hitboxDataBuf.c(DynamicHitbox.getHitboxId());
      EntityUtils.writeData(hitboxDataBuf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(hitboxDataBuf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
      EntityUtils.writeData(hitboxDataBuf, 16, DataWatcherRegistry.b, 2);
      hitboxDataBuf.k(255);
      final PacketPlayOutEntityMetadata hitboxData = new PacketPlayOutEntityMetadata(hitboxDataBuf);
      PacketDataSerializer mountBuf = NetworkUtils.createByteBuf();
      mountBuf.c(DynamicHitbox.getPivotId());
      mountBuf.c(1);
      mountBuf.c(DynamicHitbox.getHitboxId());
      final PacketPlayOutMount mount = new PacketPlayOutMount(mountBuf);
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
      PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[]{DynamicHitbox.getHitboxId(), DynamicHitbox.getPivotId()});
      NetworkUtils.send(hitbox.getPlayer().getUniqueId(), destroy);
   }

   @Override
   public void forceUseItem(Player player, EquipmentSlot hand) {
      ItemStack stack = player.getEquipment().getItem(hand);
      net.minecraft.world.item.ItemStack nmsStack = ((CraftItemStack)stack).handle;
      EntityPlayer nmsPlayer = (EntityPlayer)EntityUtils.nms(player);
      PlayerConnection connection = nmsPlayer.c;
      PacketPlayInBlockPlace useItemPacket = new PacketPlayInBlockPlace(hand == EquipmentSlot.HAND ? EnumHand.a : EnumHand.b, 0);
      useItemPacket.timestamp = System.currentTimeMillis();
      PatchedServerGamePacketListener.handleUseItem(useItemPacket, connection, interactionResult -> {
         if (nmsStack.s() != EnumAnimation.a && interactionResult == EnumInteractionResult.b) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(player.getEntityId());
            EntityUtils.writeData(buf, 8, DataWatcherRegistry.a, (byte)(hand == EquipmentSlot.HAND ? 1 : 3));
            buf.k(255);
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(buf);
            NetworkUtils.send(player.getUniqueId(), packet);
            if (nmsStack.d() instanceof InstrumentItem instrumentItem) {
               Optional<? extends Holder<Instrument>> optional = ReflectionUtils.call(instrumentItem, NMSMethods.INSTRUMENT_ITEM_getInstrument, nmsStack);
               optional.ifPresent(instrumentHolder -> {
                  Instrument instrument = instrumentHolder.a();
                  Holder<SoundEffect> soundEvent = instrument.a();
                  float f = instrument.c() / 16.0F;
                  RandomSource random = ReflectionUtils.get(nmsPlayer.dM(), NMSFields.LEVEL_threadSafeRandom);
                  PacketPlayOutEntitySound soundPacket = new PacketPlayOutEntitySound(soundEvent, SoundCategory.c, nmsPlayer, f, 1.0F, random.g());
                  NetworkUtils.send(player.getUniqueId(), soundPacket);
               });
            }
         }
      });
   }

   @Override
   public float getYRot(Entity entity) {
      return EntityUtils.nms(entity).dC();
   }

   @Override
   public float getYHeadRot(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      return nms instanceof EntityLiving livingEntity ? livingEntity.cp() : nms.dC();
   }

   @Override
   public float getXHeadRot(Entity entity) {
      return EntityUtils.nms(entity).dE();
   }

   @Override
   public float getYBodyRot(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      return nms instanceof EntityLiving livingEntity ? livingEntity.aU : nms.dC();
   }

   @Override
   public void setYRot(Entity entity, float angle) {
      EntityUtils.nms(entity).r(angle);
   }

   @Override
   public void setYHeadRot(Entity entity, float angle) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      if (nms instanceof EntityLiving livingEntity) {
         livingEntity.n(angle);
      } else {
         nms.r(angle);
      }
   }

   @Override
   public void setXHeadRot(Entity entity, float angle) {
      EntityUtils.nms(entity).s(angle);
   }

   @Override
   public void setYBodyRot(Entity entity, float angle) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      if (nms instanceof EntityLiving livingEntity) {
         livingEntity.o(angle);
      } else {
         nms.r(angle);
      }
   }

   @Override
   public void move(Entity entity, double x, double y, double z) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      nms.a(EnumMoveType.a, new Vec3D(x, y, z));
   }

   @Override
   public boolean isWalking(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      if (nms.ah < 1) {
         return false;
      }

      double dX = nms.dr() - nms.ac;
      double dZ = nms.dx() - nms.ae;
      return dX * dX + dZ * dZ > 2.5000003E-7F;
   }

   @Override
   public boolean isStrafing(Entity entity) {
      return false;
   }

   @Override
   public boolean isJumping(Entity entity) {
      if (JUMPING_GETTER != null && EntityUtils.nms(entity) instanceof EntityLiving livingEntity) {
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
      return EntityUtils.nms(entity) instanceof EntityLiving livingEntity ? livingEntity.ev() : 20.0F;
   }

   @Override
   public float getMaxHealth(Entity entity) {
      return EntityUtils.nms(entity) instanceof EntityLiving livingEntity ? livingEntity.eM() : 20.0F;
   }

   @Override
   public Vector3f getPivotOffset(Entity entity) {
      net.minecraft.world.entity.Entity nmsEntity = EntityUtils.nms(entity);
      if (this.dummyCloud == null) {
         this.dummyCloud = new EntityAreaEffectCloud(EntityTypes.c, nmsEntity.dM());
      }

      Vec3D offset = ReflectionUtils.call(nmsEntity, NMSMethods.ENTITY_getPassengerAttachmentPoint, this.dummyCloud, nmsEntity.a(EntityPose.a), 1.0F);
      Vec3D offset2 = this.dummyCloud.m(this.dummyCloud);
      return offset == null ? new Vector3f() : offset.e(offset2).j();
   }

   @Override
   public boolean isRemoved(Entity entity) {
      return EntityUtils.nms(entity).dH();
   }

   @Override
   public int getGlowColor(Entity entity) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      return nms.j_();
   }

   @Override
   public void setDeathTick(Entity entity, int tick) {
      if (EntityUtils.nms(entity) instanceof EntityLiving livingEntity) {
         livingEntity.aM = tick;
      }
   }

   @Override
   public void getBoundingBox(Entity entity, BoundingBox dest) {
      AxisAlignedBB nms = EntityUtils.nms(entity).cH();
      dest.resize(nms.a, nms.b, nms.c, nms.d, nms.e, nms.f);
   }

   @Override
   public TrackedEntity wrapTrackedEntity(Entity entity) {
      WorldServer level = ((CraftWorld)entity.getWorld()).getHandle();
      Int2ObjectMap<EntityTracker> map = level.l().a.K;
      int entityId = entity.getEntityId();
      EntityTracker tracker = map.get(entityId);
      return tracker == null ? new TempTrackedEntity(entity) : new TrackedEntityImpl(entity, () -> {
         WorldServer lvl = ((CraftWorld)entity.getWorld()).getHandle();
         Int2ObjectMap<EntityTracker> m = lvl.l().a.K;
         return m == null ? null : m.get(entity.getEntityId());
      }, tracker);
   }

   @Override
   public boolean shouldCull(Player player, Location eyePosition, BoundingBox box) {
      CraftWorld world = (CraftWorld)player.getWorld();
      Vec3D start = CraftLocation.toVec3D(eyePosition);
      if (!(box.getWidthX() >= this.forceRenderWidth) && !(box.getWidthZ() >= this.forceRenderWidth) && !(box.getHeight() >= this.forceRenderHeight)) {
         int minX = MathHelper.a(box.getMinX());
         int minY = MathHelper.a(box.getMinY());
         int minZ = MathHelper.a(box.getMinZ());
         int maxX = MathHelper.c(box.getMaxX()) - 1;
         int maxY = MathHelper.c(box.getMaxY()) - 1;
         int maxZ = MathHelper.c(box.getMaxZ()) - 1;
         EntityHandler.BoxRelToCam relX = EntityHandler.BoxRelToCam.from(minX, maxX, MathHelper.a(start.c));
         EntityHandler.BoxRelToCam relY = EntityHandler.BoxRelToCam.from(minY, maxY, MathHelper.a(start.d));
         EntityHandler.BoxRelToCam relZ = EntityHandler.BoxRelToCam.from(minZ, maxZ, MathHelper.a(start.e));
         if (relX == EntityHandler.BoxRelToCam.INSIDE && relY == EntityHandler.BoxRelToCam.INSIDE && relZ == EntityHandler.BoxRelToCam.INSIDE) {
            return false;
         }

         if (DebugToggle.isDebugging(DebugToggle.SHOW_CULL_POINTS)) {
            EntityHandler.sweepCullPoints(minX, minY, minZ, maxX, maxY, maxZ, relX, relY, relZ, (x, y, z) -> {
               world.spawnParticle(Particle.REDSTONE, x, y, z, 1, new DustOptions(Color.RED, 0.2F));
               return false;
            });
         }

         return !EntityHandler.sweepCullPoints(minX, minY, minZ, maxX, maxY, maxZ, relX, relY, relZ, (x, y, z) -> isVisible(world, start, new Vec3D(x, y, z)));
      } else {
         return false;
      }
   }

   private static boolean isVisible(CraftWorld world, Vec3D startPos, Vec3D endPos) {
      if (!usePaperClipMethod) {
         return OcclusionClipContext.isClear(world.getHandle(), startPos, endPos);
      }

      MovingObjectPositionBlock nmsHitResult = world.getHandle().a(new OcclusionClipContext(startPos, endPos));
      return nmsHitResult.c() == EnumMovingObjectType.a;
   }

   @Override
   public void readEntityState(Entity entity, EntityState state) {
      net.minecraft.world.entity.Entity nms = EntityUtils.nms(entity);
      state.yRot = nms.dC();
      state.xHeadRot = nms.dE();
      if (nms instanceof EntityLiving living) {
         state.yHeadRot = living.cp();
         state.yBodyRot = living.aU;
         state.health = living.ev();
      } else {
         state.yHeadRot = state.yRot;
         state.yBodyRot = state.yRot;
         state.health = 20.0F;
      }

      boolean walking = false;
      if (nms.ah >= 1) {
         double dX = nms.dr() - nms.ac;
         double dZ = nms.dx() - nms.ae;
         walking = dX * dX + dZ * dZ > 2.5000003E-7F;
      }

      state.walking = walking;
      state.strafing = false;
      state.flying = false;
      state.removed = nms.dH();
      state.jumping = this.isJumping(entity);
      state.glowing = this.isGlowing(entity);
      state.onFire = this.isVisualFire(entity);
      this.getBoundingBox(entity, state.boundingBox);
   }
}
