package io.lumine.mythic.core.volatilecode.v1_21_R5;

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
import io.lumine.mythic.core.volatilecode.v1_21_R5.attributes.ImmutableAttributeMap;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.EnumDirection.EnumAxis;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange.a;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.PlayerChunkMap.EntityTracker;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemCooldown;
import net.minecraft.world.item.ItemCooldown.Info;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardTeamBase.EnumNameTagVisibility;
import net.minecraft.world.scores.ScoreboardTeamBase.EnumTeamPush;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.craftbukkit.v1_21_R5.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R5.attribute.CraftAttribute;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftItem;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftChatMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VolatileEntityHandlerImpl implements VolatileEntityHandler {
   private static final AtomicInteger ENTITY_COUNTER = (AtomicInteger)Reflector.getValue(Entity.class, "c");
   private static final Reflector<AttributeMapBase> refAttributeMap = new Reflector<>(AttributeMapBase.class, "a");
   private static Reflector<Entity> refEntity = new Reflector<>(Entity.class, "by", "bz", "az");
   private static final DataWatcherObject<Byte> SHARED_FLAGS = new DataWatcherObject(0, DataWatcherRegistry.a);
   private static final Cache<String, IChatBaseComponent> NAME_CACHE = CacheBuilder.newBuilder()
      .maximumSize(512L)
      .expireAfterAccess(5L, TimeUnit.MINUTES)
      .build();
   private static Field entityTickList;

   public VolatileEntityHandlerImpl(VolatileCodeHandler handler) {
   }

   @Override
   public void initTeams(AbstractPlayer player) {
      for (GlowColor color : GlowColor.values()) {
         RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), IRegistryCustom.b);
         buf.a(color.teamName());
         buf.l(0);
         ComponentSerialization.d.encode(buf, IChatBaseComponent.b(color.teamName()));
         buf.l(3);
         buf.a(EnumNameTagVisibility.a);
         buf.a(EnumTeamPush.a);
         buf.a(color);
         ComponentSerialization.d.encode(buf, IChatBaseComponent.i());
         ComponentSerialization.d.encode(buf, IChatBaseComponent.i());
         buf.a(List.<String>of(), (net.minecraft.network.codec.StreamEncoder<net.minecraft.network.PacketDataSerializer, String>)PacketDataSerializer::a);
         PacketPlayOutScoreboardTeam teamPacket = (PacketPlayOutScoreboardTeam)PacketPlayOutScoreboardTeam.a.decode(buf);
         ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle().g.b(teamPacket);
      }
   }

   @Override
   public void setGlowColor(AbstractEntity target, @NotNull GlowColor color, AbstractPlayer player) {
      RegistryFriendlyByteBuf teamBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), IRegistryCustom.b);
      teamBuf.a(color.teamName());
      teamBuf.l(3);
      if (target.isPlayer()) {
         teamBuf.a(List.<String>of(target.asPlayer().getName()), (net.minecraft.network.codec.StreamEncoder<net.minecraft.network.PacketDataSerializer, String>)PacketDataSerializer::a);
      } else {
         teamBuf.a(List.<String>of(target.getUniqueId().toString()), (net.minecraft.network.codec.StreamEncoder<net.minecraft.network.PacketDataSerializer, String>)PacketDataSerializer::a);
      }

      PacketPlayOutScoreboardTeam teamsPacket = (PacketPlayOutScoreboardTeam)PacketPlayOutScoreboardTeam.a.decode(teamBuf);
      ((CraftPlayer)player.getBukkitEntity()).getHandle().g.b(teamsPacket);
   }

   @Override
   public void setGlowing(AbstractEntity target, boolean glowing, AbstractPlayer receiver) {
      Entity entity = ((CraftEntity)target.getBukkitEntity()).getHandle();
      byte previous = !entity.au().a() ? 0 : (Byte)entity.au().a(SHARED_FLAGS);
      byte b = (byte)(glowing ? previous | 64 : previous);
      PacketPlayOutEntityMetadata glowPacket = new PacketPlayOutEntityMetadata(target.getEntityId(), List.of(c.a(SHARED_FLAGS, b)));
      ((CraftPlayer)receiver.getBukkitEntity()).getHandle().g.b(glowPacket);
   }

   @Override
   public int getNextEntityId() {
      return ENTITY_COUNTER == null ? 0 : ENTITY_COUNTER.incrementAndGet();
   }

   @Override
   public void setCustomName(AbstractEntity entity, String name) {
      Entity le = ((CraftEntity)entity.getBukkitEntity()).getHandle();

      try {
         le.b((IChatBaseComponent)NAME_CACHE.get(name, () -> CraftChatMessage.fromJSON((String)GsonComponentSerializer.gson().serialize(Text.parse(name)))));
      } catch (ExecutionException var5) {
         le.b(CraftChatMessage.fromJSON((String)GsonComponentSerializer.gson().serialize(Text.parse(name))));
      }
   }

   @Override
   public void registerAttribute(AbstractEntity entity, Attribute attribute) {
      EntityLiving livingEntity = ((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
      AttributeMapBase attributeMap = livingEntity.fg();
      Map<Holder<AttributeBase>, AttributeModifiable> attributes = (Map<Holder<AttributeBase>, AttributeModifiable>)refAttributeMap.get(attributeMap, "a");
      AttributeBase nmsAttribute = CraftAttribute.bukkitToMinecraft(attribute);
      Holder<AttributeBase> attributeHolder = CraftAttribute.bukkitToMinecraftHolder(attribute);
      AttributeModifiable attributeInstance = new AttributeModifiable(attributeHolder, AttributeModifiable::a);
      attributes.put(attributeHolder, attributeInstance);
   }

   @Override
   public void stopUsingItem(AbstractEntity entity) {
      EntityLiving living = ((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
      living.fL();
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

         EntityLiving target = ((CraftLivingEntity)aTarget.getBukkitEntity()).getHandle();
         if (target instanceof EntityPlayer serverPlayer
            && reason.d() instanceof EntityHuman attacker
            && attacker.cK().equals(target.cK())
            && !serverPlayer.a(attacker)) {
            reason = new DamageSource(reason.l(), null, null, null);
         }

         AttributeModifier mod = null;
         boolean res = false;

         try {
            if (data.getPreventsKnockback()) {
               mod = new AttributeModifier(UUID.randomUUID(), "mythic$kbresist", 1.0, Operation.ADD_NUMBER);
               ((CraftLivingEntity)aTarget.getBukkitEntity()).getAttribute(Attribute.KNOCKBACK_RESISTANCE).addModifier(mod);
            }

            res = target.b(reason, (float)damage);
         } finally {
            if (data.getPreventsImmunity()) {
               aTarget.setNoDamageTicks(0);
            }

            if (((CraftEntity)caster.getEntity().getBukkitEntity()).getHandle() instanceof EntityLiving livingEntity) {
               livingEntity.C(target);
            }

            if (mod != null) {
               ((CraftLivingEntity)aTarget.getBukkitEntity()).getAttribute(Attribute.KNOCKBACK_RESISTANCE).removeModifier(mod);
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
      DamageSources damageSources = source.ai().al();
      DamageSource src = damageSources.p();
      switch (data.getDamageCause()) {
         case CONTACT:
            src = damageSources.k();
            break;
         case ENTITY_ATTACK:
            if (source instanceof EntityHuman player) {
               src = damageSources.a(player);
            } else {
               src = damageSources.b(source instanceof EntityLiving living ? living : null);
            }
            break;
         case ENTITY_SWEEP_ATTACK:
            if (source instanceof EntityHuman player) {
               src = damageSources.a(player).knownCause(DamageCause.ENTITY_SWEEP_ATTACK);
            } else {
               src = damageSources.b(source instanceof EntityLiving living ? living : null).knownCause(DamageCause.ENTITY_SWEEP_ATTACK);
            }
            break;
         case PROJECTILE:
            src = damageSources.b(
               source,
               source.getBukkitEntity() instanceof Projectile projectile && projectile.getShooter() instanceof org.bukkit.entity.Entity
                  ? ((CraftEntity)((Projectile)source.getBukkitEntity()).getShooter()).getHandle()
                  : null
            );
            break;
         case SUFFOCATION:
            src = damageSources.g();
            break;
         case FALL:
            src = damageSources.l();
            break;
         case FIRE:
            src = damageSources.a();
            break;
         case FIRE_TICK:
            src = damageSources.d();
            break;
         case MELTING:
            src = damageSources.d().knownCause(DamageCause.MELTING);
            break;
         case LAVA:
            src = damageSources.e();
            break;
         case DROWNING:
            src = damageSources.i();
            break;
         case BLOCK_EXPLOSION:
            if (source instanceof EntityTNTPrimed tntPrimed) {
               if (tntPrimed.projectileSource != null && tntPrimed.projectileSource instanceof Entity projectileEntity) {
                  src = damageSources.d(tntPrimed, projectileEntity);
               } else {
                  src = damageSources.d(tntPrimed, null);
               }
            } else {
               src = damageSources.d(source, source);
            }
            break;
         case ENTITY_EXPLOSION:
            src = damageSources.d(source, source);
            break;
         case VOID:
            src = damageSources.o();
            break;
         case LIGHTNING:
            src = damageSources.c();
         case SUICIDE:
         default:
            break;
         case STARVATION:
            src = damageSources.j();
            break;
         case POISON:
            src = damageSources.q().knownCause(DamageCause.POISON);
            break;
         case MAGIC:
            src = damageSources.q();
            break;
         case WITHER:
            src = damageSources.r();
            break;
         case FALLING_BLOCK:
            src = damageSources.a(source);
            break;
         case THORNS:
            src = damageSources.d(source);
            break;
         case DRAGON_BREATH:
            src = damageSources.s();
            break;
         case FLY_INTO_WALL:
            src = damageSources.n();
            break;
         case HOT_FLOOR:
            src = damageSources.f();
            break;
         case CRAMMING:
            src = damageSources.h();
            break;
         case DRYOUT:
            src = damageSources.t();
            break;
         case FREEZE:
            src = damageSources.v();
            break;
         case SONIC_BOOM:
            src = damageSources.e(source);
      }

      if (src.c() == null && src.d() == null) {
         src = new DamageSource(src.l(), source, source).knownCause(data.getDamageCause());
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
         EntityLiving el = (EntityLiving)((CraftEntity)entity.getBukkitEntity()).getHandle();
         return el.fD();
      }
   }

   @Override
   public void setEntityAbsorptionHearts(AbstractEntity entity, float value) {
      if (entity.isLiving()) {
         EntityLiving el = (EntityLiving)((CraftEntity)entity.getBukkitEntity()).getHandle();
         AttributeModifiable attr = el.h(GenericAttributes.s);
         if (attr == null) {
            this.registerAttribute(entity, Attribute.MAX_ABSORPTION);
            attr = el.h(GenericAttributes.s);
         }

         if (value > attr.b()) {
            attr.a(value);
         }

         el.E(value);
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
      e.a(x, y, z, yaw, pitch);
      if (entity.isPlayer()) {
         this.playerConnectionTeleport(entity, x, y, z, yaw, pitch, noRotation, noGravity);
      }

      if (e.ai() instanceof WorldServer) {
      }
   }

   @Override
   public void setPlayerRotation(AbstractPlayer entity, float yaw, float pitch, boolean relative) {
      EntityPlayer me = ((CraftPlayer)entity.getBukkitEntity()).getHandle();
      float yRot = relative ? me.dP() + yaw : yaw;
      float xRot = relative ? me.dR() + pitch : pitch;
      me.g.b(new ClientboundPlayerRotationPacket(yRot, xRot));
   }

   private void playerConnectionTeleport(AbstractEntity entity, double x, double y, double z, float yaw, float pitch, boolean noRotation, boolean noGravity) {
      EntityPlayer me = ((CraftPlayer)entity.getBukkitEntity()).getHandle();
      int id = me.ar();
      Set<Relative> set = new HashSet<>();
      boolean dismountVehicle = false;
      if (noRotation) {
         pitch = 0.0F;
         yaw = 0.0F;
         set.add(Relative.e);
         set.add(Relative.d);
      }

      if (noGravity) {
         set.add(Relative.b);
         y = 0.0;
      }

      Vec3D delta = new Vec3D(0.0, 0.0, 0.0);
      PositionMoveRotation positionMoveRotation = new PositionMoveRotation(new Vec3D(x, y, z), delta, yaw, pitch);
      PacketPlayOutPosition packet = PacketPlayOutPosition.a(id, positionMoveRotation, set);
      me.g.b(packet);
   }

   @Override
   public boolean isEntityInMotion(AbstractEntity entity, boolean exact) {
      if (entity.isLiving()) {
         EntityLiving e = (EntityLiving)((CraftEntity)entity.getBukkitEntity()).getHandle();
         Vec3D position = e.dv();
         if (exact) {
            if (e.X != position.a() || e.Y != position.b() || e.Z != position.c()) {
               return true;
            }
         } else {
            int x = Numbers.floor(e.X);
            int y = Numbers.floor(e.Y);
            int z = Numbers.floor(e.Z);
            int pX = Numbers.floor(position.a());
            int pY = Numbers.floor(position.b());
            int pZ = Numbers.floor(position.c());
            if (x != pX || y != pY || z != pZ) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public AbstractVector getEntityMotion(AbstractEntity entity) {
      if (entity.isLiving()) {
         EntityLiving e = (EntityLiving)((CraftEntity)entity.getBukkitEntity()).getHandle();
         Vec3D position = e.dv();
         double x = position.a() - e.X;
         double y = position.b() - e.Y;
         double z = position.c() - e.Z;
         return new AbstractVector(x, y, z);
      } else {
         return new AbstractVector(0, 0, 0);
      }
   }

   @Override
   public void setHitBox(AbstractEntity target, double width, double height) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity ent = ((CraftEntity)entity).getHandle();
      AxisAlignedBB bb = new AxisAlignedBB(
         ent.dC() - width / 2.0, ent.dE(), ent.dI() - width / 2.0, ent.dC() + width / 2.0, ent.dE() + height, ent.dI() + width / 2.0
      );
      ent.a(bb);
      refEntity.set(ent, "by", new EntitySize((float)width, (float)height, (float)(height * 0.85), EntityAttachments.a((float)width, (float)height), true));
      refEntity.set(ent, "bz", (float)(height * 0.85));
   }

   @Override
   public void setItemPosition(AbstractEntity target, AbstractLocation ol) {
      if (BukkitAdapter.adapt(target) instanceof Item item) {
         EntityItem ei = ((CraftItem)item).getHandle();
         ei.g(ol.getX(), ol.getY(), ol.getZ());
      }
   }

   @Override
   public void sendEntityTeleportPacket(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      int id = me.ar();
      Set<Relative> set = new HashSet<>();
      set.add(Relative.a);
      set.add(Relative.b);
      set.add(Relative.c);
      set.add(Relative.e);
      set.add(Relative.d);
      Vec3D delta = new Vec3D(0.0, 0.0, 0.0);
      PositionMoveRotation positionMoveRotation = new PositionMoveRotation(me.dv(), delta, target.getLocation().getPitch(), target.getLocation().getYaw());
      PacketPlayOutPosition packet = PacketPlayOutPosition.a(id, positionMoveRotation, set);
      entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 32.0, 32.0, 32.0).forEach(e -> {
         if (e instanceof Player) {
            ((CraftPlayer)e).getHandle().g.b(packet);
         }
      });
   }

   @Override
   public void setEntityRotation(AbstractEntity target, float pitch, float yaw) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      me.v(yaw);
      me.r(yaw);
      me.s(yaw);
      me.w(pitch);
   }

   @Override
   public void setArmorStandNoGravity(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      if (entity.getType() == EntityType.ARMOR_STAND) {
         EntityArmorStand as = (EntityArmorStand)((CraftEntity)entity).getHandle();
         as.g(true);
      }
   }

   @Override
   public void sendGameStateChange(AbstractPlayer target, int state, int skybox) {
      Player player = BukkitAdapter.adapt(target);
      PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(new a(state), skybox);
      ((CraftPlayer)player).getHandle().g.b(packet);
   }

   @Override
   public void forcePlayCredits(AbstractPlayer target, float f) {
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      me.g.b(new PacketPlayOutGameStateChange(new a(4), f));
   }

   @Override
   public void forceCloseWindow(AbstractPlayer target) {
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      me.g.b(new PacketPlayOutCloseWindow(0));
   }

   @Override
   public void setPlayerWorldBorder(AbstractPlayer target, AbstractLocation center, int radius) {
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      WorldBorder border;
      if (radius == -1) {
         border = ep.y().F_();
      } else {
         border = new WorldBorder();
         border.world = ep.y().F_().world;
         border.d(center.getX(), center.getZ());
         border.a((double)radius);
         border.c(1);
      }

      ep.g.b(new ClientboundSetBorderCenterPacket(border));
      ep.g.b(new ClientboundSetBorderWarningDistancePacket(border));
      ep.g.b(new ClientboundSetBorderSizePacket(border));
   }

   @Override
   public void setPlayerTickRate(AbstractPlayer target, float tickRate, boolean frozen) {
      Player player = BukkitAdapter.adapt(target);
      ((CraftPlayer)player).getHandle().g.b(new ClientboundTickingStatePacket(tickRate, frozen));
   }

   @Override
   public void resetPlayerTickRate(AbstractPlayer target) {
      Player player = BukkitAdapter.adapt(target);
      MinecraftServer.getServer().aP().a(((CraftPlayer)player).getHandle());
   }

   @Override
   public boolean setEntityTicking(AbstractEntity target, boolean ticking) {
      Entity entity = ((CraftEntity)target.getBukkitEntity()).getHandle();
      WorldServer level = (WorldServer)entity.ai();
      if (!ticking || !entity.dU() && level.f(entity.dx())) {
         if (ServerVersion.isFolia()) {
            return FoliaEntityTicking.setTicking(level, entity, Entity.class, ticking);
         } else {
            if (entityTickList == null) {
               for (Field field : WorldServer.class.getDeclaredFields()) {
                  if (field.getType() == EntityTickList.class) {
                     field.setAccessible(true);
                     entityTickList = field;
                  }
               }
            }

            try {
               EntityTickList list = (EntityTickList)entityTickList.get(level);
               if (ticking) {
                  list.a(entity);
                  return true;
               } else if (!list.c(entity)) {
                  return false;
               } else {
                  list.b(entity);
                  return true;
               }
            } catch (IllegalAccessException var9) {
               throw new IllegalStateException(var9);
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

      Player player = BukkitAdapter.adapt(target);
      EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
      net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(stack);
      PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, 0, slot, item);
      entityPlayer.g.b(packet);
   }

   public void hideEntityModel(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      DataWatcher w = me.au();
      w.a(new DataWatcherObject(0, DataWatcherRegistry.a), (byte)32);
      new PacketPlayOutEntityMetadata(me.ar(), w.c());
   }

   @Override
   public void playTotemEffect(AbstractPlayer target, int model) {
      this.sendPlayerFakeInventoryItem(target, ItemFactory.of(Material.TOTEM_OF_UNDYING).model(model).build(), 45);
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(me, (byte)35);
      me.g.b(packet);
      player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.MASTER);
      Schedulers.async().runLater(() -> this.sendPlayerFakeInventoryItem(target, player.getInventory().getItemInOffHand(), 45), 1L);
   }

   @Override
   public void playTotemEffect(AbstractPlayer target, ItemStack item) {
      item.setType(Material.TOTEM_OF_UNDYING);
      this.sendPlayerFakeInventoryItem(target, item, 45);
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(me, (byte)35);
      me.g.b(packet);
      player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.MASTER);
      Schedulers.async().runLater(() -> this.sendPlayerFakeInventoryItem(target, player.getInventory().getItemInOffHand(), 45), 1L);
   }

   @Override
   public void setEntitySpawnReason(AbstractEntity target, SpawnReason reason) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      World world = BukkitAdapter.adapt(target.getWorld());
      Entity entityHandle = ((CraftEntity)entity).getHandle();
      WorldServer worldHandle = ((CraftWorld)world).getHandle();
      worldHandle.addFreshEntity(entityHandle, reason);
   }

   @Override
   public void playFreezeEffect(AbstractPlayer target, int ticks) {
      Player player = (Player)target.getBukkitEntity();
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      me.k(ticks);
      me.g.b(new PacketPlayOutEntityMetadata(me.ar(), me.au().c()));
   }

   @Override
   public void playEntityAnimation(AbstractEntity target, byte effect, Collection<AbstractPlayer> audience) {
      Entity e = ((CraftEntity)target.getBukkitEntity()).getHandle();
      PacketPlayOutAnimation packet = new PacketPlayOutAnimation(e, effect);

      for (AbstractPlayer entity : audience) {
         if (entity.isPlayer()) {
            Player player = (Player)entity.getBukkitEntity();
            EntityPlayer me = ((CraftPlayer)player).getHandle();
            me.g.b(packet);
         }
      }
   }

   @Override
   public void setEntityPose(AbstractEntity target, String poseId) {
      Entity nmsEntity = ((CraftEntity)target.getBukkitEntity()).getHandle();

      try {
         EntityPose pose = poseId == null ? EntityPose.a : EntityPose.valueOf(poseId);
         nmsEntity.b(pose);
      } catch (Throwable var5) {
         var5.printStackTrace();
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
      return (float)nmsEntity.n(pnmsEntity).a(EnumAxis.b);
   }

   @Override
   public Collection<AbstractPlayer> getTrackedPlayers(AbstractEntity entity) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      WorldServer level = (WorldServer)nmsEntity.ai();
      Map<Integer, EntityTracker> map = level.n().a.L;
      EntityTracker tracker = map.get(nmsEntity.ar());
      if (tracker == null) {
         return Collections.emptyList();
      } else {
         Collection<AbstractPlayer> players = Lists.newArrayList();

         for (ServerPlayerConnection nmsPlayer : tracker.f) {
            players.add(BukkitAdapter.adapt(nmsPlayer.o().getBukkitEntity()));
         }

         return players;
      }
   }

   @Override
   public boolean isUsingSpyglass(AbstractPlayer player) {
      org.bukkit.entity.Entity bukkitPlayer = player.getBukkitEntity();
      CraftPlayer craftPlayer = (CraftPlayer)bukkitPlayer;
      EntityPlayer nmsPlayer = craftPlayer.getHandle();
      return nmsPlayer.gR();
   }

   @Override
   public boolean hasDefaultAttributes(EntityType entityType) {
      return AttributeDefaults.b(CraftEntityType.bukkitToMinecraft(entityType));
   }

   @Override
   public BukkitAttributable getDefaultAttributes(EntityType entityType) {
      AttributeProvider supplier = AttributeDefaults.a((net.minecraft.world.entity.EntityTypes)CraftEntityType.bukkitToMinecraft(entityType));
      return new ImmutableAttributeMap(supplier);
   }

   @Override
   public float getItemGroupCooldownPercent(AbstractPlayer player, String namespacedKey) {
      org.bukkit.entity.Entity bukkitEntity = player.getBukkitEntity();
      EntityPlayer nmsEntity = ((CraftPlayer)bukkitEntity).getHandle();
      if (!namespacedKey.contains(":")) {
         namespacedKey = "minecraft:" + namespacedKey;
      }

      MinecraftKey resourceLocation = MinecraftKey.a(namespacedKey);
      ItemCooldown playerItemCooldowns = nmsEntity.gP();
      Info cooldownInstance = (Info)playerItemCooldowns.a.get(resourceLocation);
      if (cooldownInstance != null) {
         float diff = cooldownInstance.b() - cooldownInstance.a();
         float var5 = cooldownInstance.b() - (playerItemCooldowns.b + 0.0F);
         return MathHelper.a(var5 / diff, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   @Override
   public void setItemGroupCooldown(AbstractPlayer player, String namespacedKey, int ticks) {
      org.bukkit.entity.Entity bukkitEntity = player.getBukkitEntity();
      EntityPlayer nmsEntity = ((CraftPlayer)bukkitEntity).getHandle();
      if (!namespacedKey.contains(":")) {
         namespacedKey = "minecraft:" + namespacedKey;
      }

      MinecraftKey resourceLocation = MinecraftKey.a(namespacedKey);
      ItemCooldown playerItemCooldowns = nmsEntity.gP();
      playerItemCooldowns.a(resourceLocation, ticks);
   }

   @Override
   public void removeItemGroupCooldown(AbstractPlayer player, String namespacedKey) {
      org.bukkit.entity.Entity bukkitEntity = player.getBukkitEntity();
      EntityPlayer nmsEntity = ((CraftPlayer)bukkitEntity).getHandle();
      if (!namespacedKey.contains(":")) {
         namespacedKey = "minecraft:" + namespacedKey;
      }

      MinecraftKey resourceLocation = MinecraftKey.a(namespacedKey);
      ItemCooldown playerItemCooldowns = nmsEntity.gP();
      playerItemCooldowns.a(resourceLocation);
   }

   @Override
   public void spawnFakeLightning(AbstractLocation target, double radius) {
      Location location = BukkitAdapter.adapt(target);
      World world = location.getWorld();
      WorldServer worldHandle = ((CraftWorld)world).getHandle();
      EntityLightning entitylightning = (EntityLightning)EntityTypes.ay.a(worldHandle, EntitySpawnReason.n);
      entitylightning.a(true);
      PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(
         entitylightning.ar(), entitylightning.cK(), location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F, EntityTypes.ay, 0, Vec3D.c, 0.0
      );
      double distanceSquared = radius * radius;
      Schedulers.async().run(() -> {
         for (Player player : location.getWorld().getPlayers()) {
            if (location.distanceSquared(player.getLocation()) <= distanceSquared) {
               ((CraftPlayer)player).getHandle().g.b(spawnItem);
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
      NBTTagCompound compound = new NBTTagCompound();
      return VolatileCompoundTag.fromNMSTag(compound);
   }

   @Override
   public AbstractEntity setNBTData(AbstractEntity entity, CompoundTag compoundTag) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      return entity;
   }

   @Override
   public void sendActionBarMessageToPlayer(AbstractPlayer target, String message) {
      Player player = BukkitAdapter.adapt(target);
   }

   @Override
   public boolean shootFrogTongue(AbstractEntity frogEntity, AbstractEntity targetEntity) {
      Entity handle = ((CraftEntity)frogEntity.getBukkitEntity()).getHandle();
      Entity targetHandle = ((CraftEntity)targetEntity.getBukkitEntity()).getHandle();
      if (handle instanceof Frog frog) {
         if (targetHandle instanceof EntityLiving target) {
            frog.eh().a(MemoryModuleType.p, target);
            frog.eh().b(MemoryModuleType.ab);
            frog.G(targetHandle);
            frog.b(EntityPose.j);
            frog.a(SoundEffects.kF, 1.0F, 1.0F);
            Schedulers.sync().runLater(() -> {
               if (frog.bO()) {
                  frog.m();
                  frog.b(EntityPose.a);
                  frog.eh().b(MemoryModuleType.p);
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
}
