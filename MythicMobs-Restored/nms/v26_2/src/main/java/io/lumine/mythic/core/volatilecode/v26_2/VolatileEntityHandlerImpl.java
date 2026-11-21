package io.lumine.mythic.core.volatilecode.v26_2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.attributes.BukkitAttributable;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import io.lumine.mythic.core.volatilecode.FoliaEntityTicking;
import io.lumine.mythic.core.volatilecode.v26_2.attributes.ImmutableAttributeMap;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket.Type;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.TeamColor;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team.Visibility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VolatileEntityHandlerImpl implements VolatileEntityHandler {
   private static final AtomicInteger ENTITY_COUNTER = (AtomicInteger)Reflector.getValue(ServerLevel.class, "ENTITY_COUNTER");
   private static final Reflector<AttributeMap> refAttributeMap = new Reflector<>(AttributeMap.class, "attributes");
   private static Reflector<Entity> refEntity = new Reflector<>(Entity.class, "dimensions", "eyeHeight", "DATA_SHARED_FLAGS_ID");
   private static final Reflector<ItemCooldowns> refItemCooldowns = new Reflector<>(ItemCooldowns.class, "cooldowns", "tickCount");
   private static final EntityDataAccessor<Byte> SHARED_FLAGS = new EntityDataAccessor(0, EntityDataSerializers.BYTE);
   private static final Cache<String, Component> NAME_CACHE = CacheBuilder.newBuilder().maximumSize(512L).expireAfterAccess(5L, TimeUnit.MINUTES).build();
   private static Field entityTickList;

   public VolatileEntityHandlerImpl(VolatileCodeHandler handler) {
   }

   @Override
   public void initTeams(AbstractPlayer player) {
      for (GlowColor color : GlowColor.values()) {
         RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
         buf.writeUtf(color.teamName());
         buf.writeByte(0);
         Parameters.STREAM_CODEC
            .encode(
               buf,
               new Parameters(
                  Component.literal(color.teamName()),
                  Component.empty(),
                  Component.empty(),
                  Visibility.ALWAYS,
                  CollisionRule.ALWAYS,
                  Optional.ofNullable(TeamColor.byName(color.getName())),
                  (byte)3
               )
            );
         buf.writeCollection(List.<String>of(), FriendlyByteBuf::writeUtf);
         ClientboundSetPlayerTeamPacket teamPacket = ClientboundSetPlayerTeamPacket.STREAM_CODEC.decode(buf);
         ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle().connection.send(teamPacket);
      }
   }

   @Override
   public void setGlowColor(AbstractEntity target, @NotNull GlowColor color, AbstractPlayer player) {
      RegistryFriendlyByteBuf teamBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
      teamBuf.writeUtf(color.teamName());
      teamBuf.writeByte(3);
      if (target.isPlayer()) {
         teamBuf.writeCollection(List.of(target.asPlayer().getName()), FriendlyByteBuf::writeUtf);
      } else {
         teamBuf.writeCollection(List.of(target.getUniqueId().toString()), FriendlyByteBuf::writeUtf);
      }

      ClientboundSetPlayerTeamPacket teamsPacket = ClientboundSetPlayerTeamPacket.STREAM_CODEC.decode(teamBuf);
      ((CraftPlayer)player.getBukkitEntity()).getHandle().connection.send(teamsPacket);
   }

   @Override
   public void setGlowing(AbstractEntity target, boolean glowing, AbstractPlayer receiver) {
      Entity entity = ((CraftEntity)target.getBukkitEntity()).getHandle();
      byte previous = !entity.getEntityData().isDirty() ? 0 : entity.getEntityData().get(SHARED_FLAGS);
      byte b = (byte)(glowing ? previous | 64 : previous);
      ClientboundSetEntityDataPacket glowPacket = new ClientboundSetEntityDataPacket(target.getEntityId(), List.of(DataValue.create(SHARED_FLAGS, b)));
      ((CraftPlayer)receiver.getBukkitEntity()).getHandle().connection.send(glowPacket);
   }

   @Override
   public int getNextEntityId() {
      return ENTITY_COUNTER == null ? 0 : ENTITY_COUNTER.incrementAndGet();
   }

   @Override
   public void setCustomName(AbstractEntity entity, String name) {
      Entity le = ((CraftEntity)entity.getBukkitEntity()).getHandle();

      try {
         le.setCustomName(NAME_CACHE.get(name, () -> CraftChatMessage.fromJSON(GsonComponentSerializer.gson().serialize(Text.parse(name)))));
      } catch (ExecutionException e) {
         le.setCustomName(CraftChatMessage.fromJSON(GsonComponentSerializer.gson().serialize(Text.parse(name))));
      }
   }

   @Override
   public void registerAttribute(AbstractEntity entity, org.bukkit.attribute.Attribute attribute) {
      LivingEntity livingEntity = ((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
      AttributeMap attributeMap = livingEntity.getAttributes();
      Map<Holder<Attribute>, AttributeInstance> attributes = (Map<Holder<Attribute>, AttributeInstance>)refAttributeMap.get(attributeMap, "attributes");
      Holder<Attribute> attributeHolder = CraftAttribute.bukkitToMinecraftHolder(attribute);
      AttributeInstance attributeInstance = new AttributeInstance(attributeHolder, AttributeInstance::getAttribute);
      attributes.put(attributeHolder, attributeInstance);
   }

   @Override
   public void stopUsingItem(AbstractEntity entity) {
      LivingEntity living = ((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
      living.releaseUsingItem();
   }

   @Override
   public boolean doDamage(DamageMetadata data, AbstractEntity aTarget) {
      SkillCaster caster = data.getDamager();
      double damage = data.getAmount();
      DamageSource reason = this.getDamageSource(data);
      if (aTarget == null) {
         return false;
      } else if (!aTarget.isDamageable()) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Damage cancelled: target is not damageable");
         return false;
      } else {
         caster.setUsingDamageSkill(true);
         caster.getEntity().setMetadata("doing-skill-damage", true);
         aTarget.setMetadata("skill-damage", data);
         if (caster instanceof ActiveMob am) {
            am.setLastDamageSkillAmount(damage);
         }

         LivingEntity target = ((CraftLivingEntity)aTarget.getBukkitEntity()).getHandle();
         if (target instanceof ServerPlayer serverPlayer
            && reason.getEntity() instanceof Player attacker
            && attacker.getUUID().equals(target.getUUID())
            && !serverPlayer.canHarmPlayer(attacker)) {
            reason = new DamageSource(reason.typeHolder(), null, null, null);
         }

         AttributeModifier mod = null;
         boolean res = false;

         try {
            if (data.getPreventsKnockback()) {
               mod = new AttributeModifier(UUID.randomUUID(), "mythic$kbresist", 1.0, Operation.ADD_NUMBER);
               ((CraftLivingEntity)aTarget.getBukkitEntity()).getAttribute(org.bukkit.attribute.Attribute.KNOCKBACK_RESISTANCE).addModifier(mod);
            }

            res = target.hurtOrSimulate(reason, (float)damage);
         } finally {
            if (data.getPreventsImmunity()) {
               aTarget.setNoDamageTicks(0);
            }

            if (((CraftEntity)caster.getEntity().getBukkitEntity()).getHandle() instanceof LivingEntity livingEntity) {
               livingEntity.setLastHurtMob(target);
            }

            if (mod != null) {
               ((CraftLivingEntity)aTarget.getBukkitEntity()).getAttribute(org.bukkit.attribute.Attribute.KNOCKBACK_RESISTANCE).removeModifier(mod);
            }

            caster.getEntity().removeMetadata("doing-skill-damage");
            caster.setUsingDamageSkill(false);
            aTarget.removeMetadata("skill-damage");
         }

         return res;
      }
   }

   private DamageSource getDamageSource(DamageMetadata data) {
      Entity source = ((CraftEntity)data.getDamager().getEntity().getBukkitEntity()).getHandle();
      DamageSources damageSources = source.level().damageSources();
      DamageSource src = damageSources.generic();
      switch (data.getDamageCause()) {
         case CONTACT:
            src = damageSources.cactus();
            break;
         case ENTITY_ATTACK:
            if (source instanceof Player player) {
               src = damageSources.playerAttack(player);
            } else {
               src = damageSources.mobAttack(source instanceof LivingEntity living ? living : null);
            }
            break;
         case ENTITY_SWEEP_ATTACK:
            if (source instanceof Player player) {
               src = damageSources.playerAttack(player).knownCause(DamageCause.ENTITY_SWEEP_ATTACK);
            } else {
               src = damageSources.mobAttack(source instanceof LivingEntity living ? living : null).knownCause(DamageCause.ENTITY_SWEEP_ATTACK);
            }
            break;
         case PROJECTILE:
            src = damageSources.thrown(
               source,
               source.getBukkitEntity() instanceof Projectile projectile && projectile.getShooter() instanceof org.bukkit.entity.Entity
                  ? ((CraftEntity)((Projectile)source.getBukkitEntity()).getShooter()).getHandle()
                  : null
            );
            break;
         case SUFFOCATION:
            src = damageSources.inWall();
            break;
         case FALL:
            src = damageSources.fall();
            break;
         case FIRE:
            src = damageSources.inFire();
            break;
         case FIRE_TICK:
            src = damageSources.onFire();
            break;
         case MELTING:
            src = damageSources.onFire().knownCause(DamageCause.MELTING);
            break;
         case LAVA:
            src = damageSources.lava();
            break;
         case DROWNING:
            src = damageSources.drown();
            break;
         case BLOCK_EXPLOSION:
            if (source instanceof PrimedTnt tntPrimed) {
               if (tntPrimed.projectileSource != null && tntPrimed.projectileSource instanceof Entity projectileEntity) {
                  src = damageSources.explosion(tntPrimed, projectileEntity);
               } else {
                  src = damageSources.explosion(tntPrimed, null);
               }
            } else {
               src = damageSources.explosion(source, source);
            }
            break;
         case ENTITY_EXPLOSION:
            src = damageSources.explosion(source, source);
            break;
         case VOID:
            src = damageSources.fellOutOfWorld();
            break;
         case LIGHTNING:
            src = damageSources.lightningBolt();
         case SUICIDE:
         default:
            break;
         case STARVATION:
            src = damageSources.starve();
            break;
         case POISON:
            src = damageSources.magic().knownCause(DamageCause.POISON);
            break;
         case MAGIC:
            src = damageSources.magic();
            break;
         case WITHER:
            src = damageSources.wither();
            break;
         case FALLING_BLOCK:
            src = damageSources.fallingBlock(source);
            break;
         case THORNS:
            src = damageSources.thorns(source);
            break;
         case DRAGON_BREATH:
            src = damageSources.dragonBreath();
            break;
         case FLY_INTO_WALL:
            src = damageSources.flyIntoWall();
            break;
         case HOT_FLOOR:
            src = damageSources.hotFloor();
            break;
         case CRAMMING:
            src = damageSources.cramming();
            break;
         case DRYOUT:
            src = damageSources.dryOut();
            break;
         case FREEZE:
            src = damageSources.freeze();
            break;
         case SONIC_BOOM:
            src = damageSources.sonicBoom(source);
      }

      if (src.getDirectEntity() == null && src.getEntity() == null) {
         src = new DamageSource(src.typeHolder(), source, source).knownCause(data.getDamageCause());
      }

      VolatileDamageSource returnSource = VolatileDamageSource.of(src);
      if (data.getIgnoresArmor()) {
         returnSource.ignoreArmor(true);
      }

      if (data.getIgnoreEnchantments()) {
         returnSource.ignoreEnchantments(true);
      }

      returnSource.ignoreShield(data.getBoolean("ignore_shield"));
      returnSource.damagesHelmet(data.getBoolean("damages_helmet"));
      returnSource.ignoreCooldown(data.getPreventsImmunity());
      returnSource.ignoreEffects(data.getBoolean("ignore_effects"));
      returnSource.noAnger(data.getBoolean("no_anger"));
      returnSource.ignoreResistance(data.getBoolean("ignore_resistance"));
      return returnSource;
   }

   @Override
   public float getEntityAbsorptionHearts(AbstractEntity entity) {
      if (!entity.isLiving()) {
         return 0.0F;
      } else {
         LivingEntity el = (LivingEntity)((CraftEntity)entity.getBukkitEntity()).getHandle();
         return el.getAbsorptionAmount();
      }
   }

   @Override
   public void setEntityAbsorptionHearts(AbstractEntity entity, float value) {
      if (entity.isLiving()) {
         LivingEntity el = (LivingEntity)((CraftEntity)entity.getBukkitEntity()).getHandle();
         AttributeInstance attr = el.getAttribute(Attributes.MAX_ABSORPTION);
         if (attr == null) {
            this.registerAttribute(entity, org.bukkit.attribute.Attribute.MAX_ABSORPTION);
            attr = el.getAttribute(Attributes.MAX_ABSORPTION);
         }

         if (value > attr.getBaseValue()) {
            attr.setBaseValue(value);
         }

         el.setAbsorptionAmount(value);
      }
   }

   @Override
   public void setLocation(AbstractEntity entity, AbstractLocation location) {
   }

   @Override
   public void setLocation(AbstractEntity entity, double x, double y, double z, float yaw, float pitch) {
   }

   @Override
   public void setLocation(AbstractEntity entity, double x, double y, double z, float yaw, float pitch, boolean noRotation, boolean noGravity) {
      Entity e = ((CraftEntity)entity.getBukkitEntity()).getHandle();
      e.absSnapTo(x, y, z, yaw, pitch);
      if (entity.isPlayer()) {
         this.playerConnectionTeleport(entity, x, y, z, yaw, pitch, noRotation, noGravity);
      }

      if (e.level() instanceof ServerLevel) {
      }
   }

   @Override
   public void setPlayerRotation(AbstractPlayer entity, float yaw, float pitch, boolean relative) {
      ServerPlayer me = ((CraftPlayer)entity.getBukkitEntity()).getHandle();
      me.connection.send(new ClientboundPlayerRotationPacket(yaw, relative, pitch, relative));
   }

   private void playerConnectionTeleport(AbstractEntity entity, double x, double y, double z, float yaw, float pitch, boolean noRotation, boolean noGravity) {
      ServerPlayer me = ((CraftPlayer)entity.getBukkitEntity()).getHandle();
      int id = me.getId();
      Set<Relative> set = new HashSet<>();
      boolean dismountVehicle = false;
      if (noRotation) {
         pitch = 0.0F;
         yaw = 0.0F;
         set.add(Relative.X_ROT);
         set.add(Relative.Y_ROT);
      }

      if (noGravity) {
         set.add(Relative.Y);
         y = 0.0;
      }

      Vec3 delta = new Vec3(0.0, 0.0, 0.0);
      PositionMoveRotation positionMoveRotation = new PositionMoveRotation(new Vec3(x, y, z), delta, yaw, pitch);
      ClientboundPlayerPositionPacket packet = ClientboundPlayerPositionPacket.of(id, positionMoveRotation, set);
      me.connection.send(packet);
   }

   @Override
   public boolean isEntityInMotion(AbstractEntity entity, boolean exact) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
         return Schedulers.of(entity.getBukkitEntity()).supply(() -> this.isEntityInMotion(entity, exact)).join();
      } else {
         if (entity.isLiving()) {
            LivingEntity e = (LivingEntity)((CraftEntity)entity.getBukkitEntity()).getHandle();
            Vec3 position = e.position();
            if (exact) {
               if (e.xo != position.x() || e.yo != position.y() || e.zo != position.z()) {
                  return true;
               }
            } else {
               int x = Numbers.floor(e.xo);
               int y = Numbers.floor(e.yo);
               int z = Numbers.floor(e.zo);
               int pX = Numbers.floor(position.x());
               int pY = Numbers.floor(position.y());
               int pZ = Numbers.floor(position.z());
               if (x != pX || y != pY || z != pZ) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public AbstractVector getEntityMotion(AbstractEntity entity) {
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
         return Schedulers.of(entity.getBukkitEntity()).supply(() -> this.getEntityMotion(entity)).join();
      } else if (entity.isLiving()) {
         LivingEntity e = (LivingEntity)((CraftEntity)entity.getBukkitEntity()).getHandle();
         Vec3 position = e.position();
         double x = position.x() - e.xo;
         double y = position.y() - e.yo;
         double z = position.z() - e.zo;
         return new AbstractVector(x, y, z);
      } else {
         return new AbstractVector(0, 0, 0);
      }
   }

   @Override
   public void setHitBox(AbstractEntity target, double width, double height) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity ent = ((CraftEntity)entity).getHandle();
      AABB bb = new AABB(
         ent.getX() - width / 2.0, ent.getY(), ent.getZ() - width / 2.0, ent.getX() + width / 2.0, ent.getY() + height, ent.getZ() + width / 2.0
      );
      ent.setBoundingBox(bb);
      refEntity.set(
         ent,
         "dimensions",
         new EntityDimensions((float)width, (float)height, (float)(height * 0.85), EntityAttachments.createDefault((float)width, (float)height), true)
      );
      refEntity.set(ent, "eyeHeight", (float)(height * 0.85));
   }

   @Override
   public void setItemPosition(AbstractEntity target, AbstractLocation ol) {
      if (BukkitAdapter.adapt(target) instanceof Item item) {
         ItemEntity ei = ((CraftItem)item).getHandle();
         ei.absSnapTo(ol.getX(), ol.getY(), ol.getZ());
      }
   }

   @Override
   public void sendEntityTeleportPacket(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      int id = me.getId();
      Set<Relative> set = new HashSet<>();
      set.add(Relative.X);
      set.add(Relative.Y);
      set.add(Relative.Z);
      set.add(Relative.X_ROT);
      set.add(Relative.Y_ROT);
      Vec3 delta = new Vec3(0.0, 0.0, 0.0);
      PositionMoveRotation positionMoveRotation = new PositionMoveRotation(me.position(), delta, target.getLocation().getPitch(), target.getLocation().getYaw());
      ClientboundPlayerPositionPacket packet = ClientboundPlayerPositionPacket.of(id, positionMoveRotation, set);
      entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 32.0, 32.0, 32.0).forEach(e -> {
         if (e instanceof org.bukkit.entity.Player) {
            ((CraftPlayer)e).getHandle().connection.send(packet);
         }
      });
   }

   @Override
   public void setEntityRotation(AbstractEntity target, float pitch, float yaw) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Runnable run = () -> {
         Entity me = ((CraftEntity)entity).getHandle();
         me.setYRot(yaw);
         me.setYHeadRot(yaw);
         me.setYBodyRot(yaw);
         me.setXRot(pitch);
      };
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         Schedulers.of(entity).run(run);
      } else {
         run.run();
      }
   }

   @Override
   public void setArmorStandNoGravity(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      if (entity.getType() == EntityType.ARMOR_STAND) {
         ArmorStand as = (ArmorStand)((CraftEntity)entity).getHandle();
         as.setNoGravity(true);
      }
   }

   @Override
   public void sendGameStateChange(AbstractPlayer target, int state, int skybox) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ClientboundGameEventPacket packet = new ClientboundGameEventPacket(new Type(state), skybox);
      ((CraftPlayer)player).getHandle().connection.send(packet);
   }

   @Override
   public void forcePlayCredits(AbstractPlayer target, float f) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ServerPlayer me = ((CraftPlayer)player).getHandle();
      me.connection.send(new ClientboundGameEventPacket(new Type(4), f));
   }

   @Override
   public void forceCloseWindow(AbstractPlayer target) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ServerPlayer me = ((CraftPlayer)player).getHandle();
      me.connection.send(new ClientboundContainerClosePacket(0));
   }

   @Override
   public void setPlayerWorldBorder(AbstractPlayer target, AbstractLocation center, int radius) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ServerPlayer ep = ((CraftPlayer)player).getHandle();
      WorldBorder border;
      if (radius == -1) {
         border = ep.level().getWorldBorder();
      } else {
         border = new WorldBorder();
         border.setCenter(center.getX(), center.getZ());
         border.setSize(radius);
         border.setWarningBlocks(1);
      }

      ep.connection.send(new ClientboundSetBorderCenterPacket(border));
      ep.connection.send(new ClientboundSetBorderWarningDistancePacket(border));
      ep.connection.send(new ClientboundSetBorderSizePacket(border));
   }

   @Override
   public void setPlayerTickRate(AbstractPlayer target, float tickRate, boolean frozen) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ((CraftPlayer)player).getHandle().connection.send(new ClientboundTickingStatePacket(tickRate, frozen));
   }

   @Override
   public void resetPlayerTickRate(AbstractPlayer target) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      MinecraftServer.getServer().tickRateManager().updateJoiningPlayer(((CraftPlayer)player).getHandle());
   }

   @Override
   public boolean setEntityTicking(AbstractEntity target, boolean ticking) {
      Entity entity = ((CraftEntity)target.getBukkitEntity()).getHandle();
      ServerLevel level = (ServerLevel)entity.level();
      if (!ticking || !entity.isRemoved() && level.isPositionEntityTicking(entity.blockPosition())) {
         if (ServerVersion.isFolia()) {
            return FoliaEntityTicking.setTicking(level, entity, Entity.class, ticking);
         } else {
            if (entityTickList == null) {
               for (Field field : ServerLevel.class.getDeclaredFields()) {
                  if (field.getType() == EntityTickList.class) {
                     field.setAccessible(true);
                     entityTickList = field;
                  }
               }
            }

            try {
               EntityTickList list = (EntityTickList)entityTickList.get(level);
               if (ticking) {
                  list.add(entity);
                  return true;
               } else if (!list.contains(entity)) {
                  return false;
               } else {
                  list.remove(entity);
                  return true;
               }
            } catch (IllegalAccessException e) {
               throw new IllegalStateException(e);
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public void sendPlayerFakeInventoryItem(AbstractPlayer target, ItemStack stack, int slot) {
      if (slot < 9) {
         slot += 36;
      }

      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ServerPlayer entityPlayer = ((CraftPlayer)player).getHandle();
      net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(stack);
      ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(0, 0, slot, item);
      entityPlayer.connection.send(packet);
   }

   public void hideEntityModel(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      SynchedEntityData w = me.getEntityData();
      w.set(new EntityDataAccessor(0, EntityDataSerializers.BYTE), (byte)32);
      new ClientboundSetEntityDataPacket(me.getId(), w.getNonDefaultValues());
   }

   @Override
   public void playTotemEffect(AbstractPlayer target, int model) {
      this.sendPlayerFakeInventoryItem(target, ItemFactory.of(Material.TOTEM_OF_UNDYING).model(model).build(), 45);
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ServerPlayer me = ((CraftPlayer)player).getHandle();
      ClientboundEntityEventPacket packet = new ClientboundEntityEventPacket(me, (byte)35);
      me.connection.send(packet);
      player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.MASTER);
      Schedulers.async().runLater(() -> this.sendPlayerFakeInventoryItem(target, player.getInventory().getItemInOffHand(), 45), 1L);
   }

   @Override
   public void playTotemEffect(AbstractPlayer target, ItemStack item) {
      item.setType(Material.TOTEM_OF_UNDYING);
      this.sendPlayerFakeInventoryItem(target, item, 45);
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
      ServerPlayer me = ((CraftPlayer)player).getHandle();
      ClientboundEntityEventPacket packet = new ClientboundEntityEventPacket(me, (byte)35);
      me.connection.send(packet);
      player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.MASTER);
      Schedulers.async().runLater(() -> this.sendPlayerFakeInventoryItem(target, player.getInventory().getItemInOffHand(), 45), 1L);
   }

   @Override
   public void setEntitySpawnReason(AbstractEntity target, SpawnReason reason) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      World world = BukkitAdapter.adapt(target.getWorld());
      Entity entityHandle = ((CraftEntity)entity).getHandle();
      ServerLevel worldHandle = ((CraftWorld)world).getHandle();
      worldHandle.addFreshEntity(entityHandle, reason);
   }

   @Override
   public void playFreezeEffect(AbstractPlayer target, int ticks) {
      org.bukkit.entity.Player player = (org.bukkit.entity.Player)target.getBukkitEntity();
      ServerPlayer me = ((CraftPlayer)player).getHandle();
      me.setTicksFrozen(ticks);
      me.connection.send(new ClientboundSetEntityDataPacket(me.getId(), me.getEntityData().getNonDefaultValues()));
   }

   @Override
   public void playEntityAnimation(AbstractEntity target, byte effect, Collection<AbstractPlayer> audience) {
      Entity e = ((CraftEntity)target.getBukkitEntity()).getHandle();
      ClientboundAnimatePacket packet = new ClientboundAnimatePacket(e, effect);

      for (AbstractPlayer entity : audience) {
         if (entity.isPlayer()) {
            org.bukkit.entity.Player player = (org.bukkit.entity.Player)entity.getBukkitEntity();
            ServerPlayer me = ((CraftPlayer)player).getHandle();
            me.connection.send(packet);
         }
      }
   }

   @Override
   public void setEntityPose(AbstractEntity target, String poseId) {
      Entity nmsEntity = ((CraftEntity)target.getBukkitEntity()).getHandle();

      try {
         Pose pose = poseId == null ? Pose.STANDING : Pose.valueOf(poseId);
         nmsEntity.setPose(pose);
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   @Override
   public double getPassengerRidingOffset(AbstractEntity entity) {
      return this.getPassengerRidingOffset(entity, entity);
   }

   @Override
   public float getPassengerRidingOffset(AbstractEntity entity, AbstractEntity passenger) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      org.bukkit.entity.Entity pbukkitEntity = passenger.getBukkitEntity();
      Entity pnmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      return (float)nmsEntity.getPassengerRidingPosition(pnmsEntity).get(Axis.Y);
   }

   @Override
   public Collection<AbstractPlayer> getTrackedPlayers(AbstractEntity entity) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      ServerLevel level = (ServerLevel)nmsEntity.level();
      Map<Integer, TrackedEntity> map = level.getChunkSource().chunkMap.entityMap;
      TrackedEntity tracker = map.get(nmsEntity.getId());
      if (tracker == null) {
         return Collections.emptyList();
      } else {
         Collection<AbstractPlayer> players = Lists.newArrayList();

         for (ServerPlayerConnection nmsPlayer : tracker.seenBy) {
            players.add(BukkitAdapter.adapt(nmsPlayer.getPlayer().getBukkitEntity()));
         }

         return players;
      }
   }

   @Override
   public boolean isUsingSpyglass(AbstractPlayer player) {
      org.bukkit.entity.Entity bukkitPlayer = player.getBukkitEntity();
      CraftPlayer craftPlayer = (CraftPlayer)bukkitPlayer;
      ServerPlayer nmsPlayer = craftPlayer.getHandle();
      return nmsPlayer.isScoping();
   }

   @Override
   public boolean hasDefaultAttributes(EntityType entityType) {
      return DefaultAttributes.hasSupplier(CraftEntityType.bukkitToMinecraft(entityType));
   }

   @Override
   public BukkitAttributable getDefaultAttributes(EntityType entityType) {
      AttributeSupplier supplier = DefaultAttributes.getSupplier((net.minecraft.world.entity.EntityType)CraftEntityType.bukkitToMinecraft(entityType));
      return new ImmutableAttributeMap(supplier);
   }

   @Override
   public float getItemGroupCooldownPercent(AbstractPlayer player, String namespacedKey) {
      org.bukkit.entity.Entity bukkitEntity = player.getBukkitEntity();
      ServerPlayer nmsEntity = ((CraftPlayer)bukkitEntity).getHandle();
      if (!namespacedKey.contains(":")) {
         namespacedKey = "minecraft:" + namespacedKey;
      }

      Identifier resourceLocation = Identifier.parse(namespacedKey);
      ItemCooldowns playerItemCooldowns = nmsEntity.getCooldowns();
      Map<?, ?> cooldowns = (Map<?, ?>)refItemCooldowns.get(playerItemCooldowns, "cooldowns");
      Object cooldownInstance = cooldowns.get(resourceLocation);
      if (cooldownInstance != null) {
         int startTime = (Integer)Reflector.getValue(cooldownInstance, true, "startTime");
         int endTime = (Integer)Reflector.getValue(cooldownInstance, true, "endTime");
         int tickCount = refItemCooldowns.getInt(playerItemCooldowns, "tickCount");
         float diff = endTime - startTime;
         float value = endTime - (tickCount + 0.0F);
         return Mth.clamp(value / diff, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   @Override
   public void setItemGroupCooldown(AbstractPlayer player, String namespacedKey, int ticks) {
      org.bukkit.entity.Entity bukkitEntity = player.getBukkitEntity();
      ServerPlayer nmsEntity = ((CraftPlayer)bukkitEntity).getHandle();
      if (!namespacedKey.contains(":")) {
         namespacedKey = "minecraft:" + namespacedKey;
      }

      Identifier resourceLocation = Identifier.parse(namespacedKey);
      ItemCooldowns playerItemCooldowns = nmsEntity.getCooldowns();
      playerItemCooldowns.addCooldown(resourceLocation, ticks);
   }

   @Override
   public void removeItemGroupCooldown(AbstractPlayer player, String namespacedKey) {
      org.bukkit.entity.Entity bukkitEntity = player.getBukkitEntity();
      ServerPlayer nmsEntity = ((CraftPlayer)bukkitEntity).getHandle();
      if (!namespacedKey.contains(":")) {
         namespacedKey = "minecraft:" + namespacedKey;
      }

      Identifier resourceLocation = Identifier.parse(namespacedKey);
      ItemCooldowns playerItemCooldowns = nmsEntity.getCooldowns();
      playerItemCooldowns.removeCooldown(resourceLocation);
   }

   @Override
   public void spawnFakeLightning(AbstractLocation target, double radius) {
      Location location = BukkitAdapter.adapt(target);
      World world = location.getWorld();
      ServerLevel worldHandle = ((CraftWorld)world).getHandle();
      LightningBolt entitylightning = EntityTypes.LIGHTNING_BOLT.create(worldHandle, EntitySpawnReason.COMMAND);
      entitylightning.setVisualOnly(true);
      ClientboundAddEntityPacket spawnItem = new ClientboundAddEntityPacket(
         entitylightning.getId(),
         entitylightning.getUUID(),
         location.getX(),
         location.getY(),
         location.getZ(),
         0.0F,
         0.0F,
         EntityTypes.LIGHTNING_BOLT,
         0,
         Vec3.ZERO,
         0.0
      );
      double distanceSquared = radius * radius;
      Schedulers.async().run(() -> {
         for (org.bukkit.entity.Player player : location.getWorld().getPlayers()) {
            if (location.distanceSquared(player.getLocation()) <= distanceSquared) {
               ((CraftPlayer)player).getHandle().connection.send(spawnItem);
               player.playSound(location, "entity.lightning_bolt.impact", 1.0F, 1.0F);
               player.playSound(location, "entity.lightning_bolt.thunder", 10000.0F, 63.0F);
            }
         }
      });
   }

   @Override
   public AbstractEntity addNBTData(AbstractEntity entity, String key, Tag value) {
      CompoundTag compound = this.getNBTData(entity).createBuilder().put(key, value).build();
      this.setNBTData(entity, compound);
      return entity;
   }

   @Override
   public CompoundTag getNBTData(AbstractEntity entity) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      net.minecraft.nbt.CompoundTag compound = new net.minecraft.nbt.CompoundTag();
      return VolatileCompoundTag.fromNMSTag(compound);
   }

   @Override
   public AbstractEntity setNBTData(AbstractEntity entity, CompoundTag compoundTag) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      return entity;
   }

   @Override
   public void sendActionBarMessageToPlayer(AbstractPlayer target, String message) {
      org.bukkit.entity.Player player = BukkitAdapter.adapt(target);
   }

   @Override
   public boolean shootFrogTongue(AbstractEntity frogEntity, AbstractEntity targetEntity) {
      Entity handle = ((CraftEntity)frogEntity.getBukkitEntity()).getHandle();
      Entity targetHandle = ((CraftEntity)targetEntity.getBukkitEntity()).getHandle();
      if (handle instanceof Frog frog) {
         if (targetHandle instanceof LivingEntity target) {
            frog.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
            frog.getBrain().eraseMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS);
            frog.setTongueTarget(targetHandle);
            frog.setPose(Pose.USING_TONGUE);
            frog.playSound(SoundEvents.FROG_TONGUE, 1.0F, 1.0F);
            Schedulers.sync().runLater(() -> {
               if (frog.isAlive()) {
                  frog.eraseTongueTarget();
                  frog.setPose(Pose.STANDING);
                  frog.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
               }
            }, 10L);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean canBeHitByProjectile(AbstractEntity entity) {
      Entity handle = ((CraftEntity)entity.getBukkitEntity()).getHandle();
      return handle.canBeHitByProjectile();
   }
}
