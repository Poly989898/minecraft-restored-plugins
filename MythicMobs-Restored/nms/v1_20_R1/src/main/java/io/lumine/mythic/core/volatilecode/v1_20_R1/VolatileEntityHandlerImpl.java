package io.lumine.mythic.core.volatilecode.v1_20_R1;

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
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange.a;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcher.b;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.PlayerChunkMap.EntityTracker;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VolatileEntityHandlerImpl implements VolatileEntityHandler {
   private static final AtomicInteger ENTITY_COUNTER = (AtomicInteger)Reflector.getValue(Entity.class, "d");
   private static final Reflector<AttributeMapBase> refAttributeMap = new Reflector<>(AttributeMapBase.class, "b");
   private static Reflector<Entity> refEntity = new Reflector<>(Entity.class, "bb", "bc", "am");
   private static final DataWatcherObject<Byte> SHARED_FLAGS = new DataWatcherObject(0, DataWatcherRegistry.a);

   public VolatileEntityHandlerImpl(VolatileCodeHandler handler) {
   }

   @Override
   public void initTeams(AbstractPlayer player) {
      for (GlowColor color : GlowColor.values()) {
         PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer());
         buf.a(color.teamName());
         buf.writeByte(0);
         buf.a(IChatBaseComponent.b(color.teamName()));
         buf.writeByte(3);
         buf.a("always");
         buf.a("always");
         buf.a(color);
         buf.a(IChatBaseComponent.h());
         buf.a(IChatBaseComponent.h());
         buf.a(List.<String>of(), (net.minecraft.network.PacketDataSerializer.b<String>)PacketDataSerializer::a);
         PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam(buf);
         ((CraftPlayer)BukkitAdapter.adapt(player)).getHandle().c.a(teamPacket);
      }
   }

   @Override
   public void setGlowColor(AbstractEntity target, @NotNull GlowColor color, AbstractPlayer player) {
      PacketDataSerializer teamBuf = new PacketDataSerializer(Unpooled.buffer());
      teamBuf.a(color.teamName());
      teamBuf.writeByte(3);
      if (target.isPlayer()) {
         teamBuf.a(List.<String>of(target.asPlayer().getName()), (net.minecraft.network.PacketDataSerializer.b<String>)PacketDataSerializer::a);
      } else {
         teamBuf.a(List.<String>of(target.getUniqueId().toString()), (net.minecraft.network.PacketDataSerializer.b<String>)PacketDataSerializer::a);
      }

      PacketPlayOutScoreboardTeam teamsPacket = new PacketPlayOutScoreboardTeam(teamBuf);
      ((CraftPlayer)player.getBukkitEntity()).getHandle().c.a(teamsPacket);
   }

   @Override
   public void setGlowing(AbstractEntity target, boolean glowing, AbstractPlayer receiver) {
      Entity entity = ((CraftEntity)target.getBukkitEntity()).getHandle();
      byte previous = entity.aj().d() ? 0 : (Byte)entity.aj().b(SHARED_FLAGS);
      byte b = (byte)(glowing ? previous | 64 : previous);
      PacketPlayOutEntityMetadata glowPacket = new PacketPlayOutEntityMetadata(target.getEntityId(), List.of(net.minecraft.network.syncher.DataWatcher.b.a(SHARED_FLAGS, b)));
      ((CraftPlayer)receiver.getBukkitEntity()).getHandle().c.a(glowPacket);
   }

   @Override
   public int getNextEntityId() {
      return ENTITY_COUNTER == null ? 0 : ENTITY_COUNTER.incrementAndGet();
   }

   @Override
   public void setCustomName(AbstractEntity entity, String name) {
      Entity le = ((CraftEntity)entity.getBukkitEntity()).getHandle();
      le.b(CraftChatMessage.fromJSON((String)GsonComponentSerializer.gson().serialize(Text.parse(name))));
   }

   @Override
   public void registerAttribute(AbstractEntity entity, Attribute attribute) {
      EntityLiving livingEntity = ((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
      AttributeMapBase attributeMap = livingEntity.eM();
      Map<AttributeBase, AttributeModifiable> attributes = (Map<AttributeBase, AttributeModifiable>)refAttributeMap.get(attributeMap, "b");
      AttributeBase nmsAttribute = CraftAttributeMap.toMinecraft(attribute);
      AttributeModifiable attributeInstance = new AttributeModifiable(nmsAttribute, AttributeModifiable::a);
      attributes.put(nmsAttribute, attributeInstance);
   }

   @Override
   public void stopUsingItem(AbstractEntity entity) {
      EntityLiving living = ((CraftLivingEntity)entity.getBukkitEntity()).getHandle();
      living.fn();
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
            && attacker.ct().equals(target.ct())
            && !serverPlayer.a(attacker)) {
            reason = new DamageSource(reason.k());
         }

         AttributeModifier mod = null;
         boolean res = false;

         try {
            if (data.getPreventsKnockback()) {
               mod = new AttributeModifier(UUID.randomUUID(), "mythic$kbresist", 1.0, Operation.ADD_NUMBER);
               ((CraftLivingEntity)aTarget.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(mod);
            }

            res = target.a(reason, (float)damage);
         } finally {
            if (data.getPreventsImmunity()) {
               aTarget.setNoDamageTicks(0);
            }

            if (mod != null) {
               ((CraftLivingEntity)aTarget.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(mod);
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
      DamageSources damageSources = source.dI().ag();
      DamageSource src = damageSources.n();
      switch (data.getDamageCause()) {
         case CONTACT:
            src = damageSources.j();
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
               src = damageSources.a(player).sweep();
            } else {
               src = damageSources.b(source instanceof EntityLiving living ? living : null).sweep();
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
            src = damageSources.f();
            break;
         case FALL:
            src = damageSources.k();
            break;
         case FIRE:
            src = damageSources.a();
            break;
         case FIRE_TICK:
            src = damageSources.c();
            break;
         case MELTING:
            src = damageSources.melting;
            break;
         case LAVA:
            src = damageSources.d();
            break;
         case DROWNING:
            src = damageSources.h();
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
            src = damageSources.m();
            break;
         case LIGHTNING:
            src = damageSources.b();
         case SUICIDE:
         default:
            break;
         case STARVATION:
            src = damageSources.i();
            break;
         case POISON:
            src = damageSources.poison;
            break;
         case MAGIC:
            src = damageSources.o();
            break;
         case WITHER:
            src = damageSources.p();
            break;
         case FALLING_BLOCK:
            src = damageSources.a(source);
            break;
         case THORNS:
            src = damageSources.d(source);
            break;
         case DRAGON_BREATH:
            src = damageSources.q();
            break;
         case FLY_INTO_WALL:
            src = damageSources.l();
            break;
         case HOT_FLOOR:
            src = damageSources.e();
            break;
         case CRAMMING:
            src = damageSources.g();
            break;
         case DRYOUT:
            src = damageSources.r();
            break;
         case FREEZE:
            src = damageSources.t();
            break;
         case SONIC_BOOM:
            src = damageSources.e(source);
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
         return el.ff();
      }
   }

   @Override
   public void setEntityAbsorptionHearts(AbstractEntity entity, float value) {
      if (entity.isLiving()) {
         EntityLiving el = (EntityLiving)((CraftEntity)entity.getBukkitEntity()).getHandle();
         el.y(value);
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

      if (e.dI() instanceof WorldServer) {
      }
   }

   @Override
   public void setPlayerRotation(AbstractPlayer entity, float yaw, float pitch, boolean relative) {
      EntityPlayer me = ((CraftPlayer)entity.getBukkitEntity()).getHandle();
      Set<RelativeMovement> set = new HashSet<>();
      if (relative) {
         set.add(RelativeMovement.e);
         set.add(RelativeMovement.d);
      }

      set.add(RelativeMovement.a);
      set.add(RelativeMovement.b);
      set.add(RelativeMovement.c);
      me.c.a(new PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, set, 0));
   }

   private void playerConnectionTeleport(AbstractEntity entity, double x, double y, double z, float yaw, float pitch, boolean noRotation, boolean noGravity) {
      EntityPlayer me = ((CraftPlayer)entity.getBukkitEntity()).getHandle();
      Set<RelativeMovement> set = new HashSet<>();
      boolean dismountVehicle = false;
      if (noRotation) {
         pitch = 0.0F;
         yaw = 0.0F;
         set.add(RelativeMovement.e);
         set.add(RelativeMovement.d);
      }

      if (noGravity) {
         set.add(RelativeMovement.b);
         y = 0.0;
      }

      me.c.a(new PacketPlayOutPosition(x, y, z, yaw, pitch, set, 0));
   }

   @Override
   public boolean isEntityInMotion(AbstractEntity entity, boolean exact) {
      if (entity.isLiving()) {
         EntityLiving e = (EntityLiving)((CraftEntity)entity.getBukkitEntity()).getHandle();
         Vec3D position = e.dg();
         if (exact) {
            if (e.J != position.a() || e.K != position.b() || e.L != position.c()) {
               return true;
            }
         } else {
            int x = Numbers.floor(e.J);
            int y = Numbers.floor(e.K);
            int z = Numbers.floor(e.L);
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
         Vec3D position = e.dg();
         double x = position.a() - e.J;
         double y = position.b() - e.K;
         double z = position.c() - e.L;
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
         ent.dn() - width / 2.0, ent.dp(), ent.dt() - width / 2.0, ent.dn() + width / 2.0, ent.dp() + height, ent.dt() + width / 2.0
      );
      ent.a(bb);
      refEntity.set(ent, "bb", new EntitySize((float)width, (float)height, true));
      refEntity.set(ent, "bc", (float)(height * 0.8));
   }

   @Override
   public void setItemPosition(AbstractEntity target, AbstractLocation ol) {
      if (BukkitAdapter.adapt(target) instanceof Item item) {
         EntityItem ei = (EntityItem)((CraftItem)item).getHandle();
         ei.h(ol.getX(), ol.getY(), ol.getZ());
      }
   }

   @Override
   public void sendEntityTeleportPacket(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(me);
      entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 32.0, 32.0, 32.0).forEach(e -> {
         if (e instanceof Player) {
            ((CraftPlayer)e).getHandle().c.a(tp);
         }
      });
   }

   @Override
   public void setEntityRotation(AbstractEntity target, float pitch, float yaw) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      me.a_(yaw);
      me.n(yaw);
      me.o(yaw);
      me.b_(pitch);
   }

   @Override
   public void setArmorStandNoGravity(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      if (entity.getType() == EntityType.ARMOR_STAND) {
         EntityArmorStand as = (EntityArmorStand)((CraftEntity)entity).getHandle();
         as.e(true);
      }
   }

   @Override
   public void sendGameStateChange(AbstractPlayer target, int state, int skybox) {
      Player player = BukkitAdapter.adapt(target);
      PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(new a(state), skybox);
      ((CraftPlayer)player).getHandle().c.a(packet);
   }

   @Override
   public void forcePlayCredits(AbstractPlayer target, float f) {
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      me.c.a(new PacketPlayOutGameStateChange(new a(4), f));
   }

   @Override
   public void forceCloseWindow(AbstractPlayer target) {
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      me.c.a(new PacketPlayOutCloseWindow(0));
   }

   @Override
   public void setPlayerWorldBorder(AbstractPlayer target, AbstractLocation center, int radius) {
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      WorldBorder border;
      if (radius == -1) {
         border = ep.dI().w_();
      } else {
         border = new WorldBorder();
         border.world = ep.dI().w_().world;
         border.c(center.getX(), center.getZ());
         border.a((double)radius);
         border.c(1);
      }

      ep.c.a(new ClientboundSetBorderCenterPacket(border));
      ep.c.a(new ClientboundSetBorderWarningDistancePacket(border));
      ep.c.a(new ClientboundSetBorderSizePacket(border));
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
      entityPlayer.c.a(packet);
   }

   public void hideEntityModel(AbstractEntity target) {
      org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
      Entity me = ((CraftEntity)entity).getHandle();
      DataWatcher w = me.aj();
      w.b(new DataWatcherObject(0, DataWatcherRegistry.a), (byte)32);
      new PacketPlayOutEntityMetadata(me.af(), w.c());
   }

   @Override
   public void playTotemEffect(AbstractPlayer target, int model) {
      this.sendPlayerFakeInventoryItem(target, ItemFactory.of(Material.TOTEM_OF_UNDYING).model(model).build(), 45);
      Player player = BukkitAdapter.adapt(target);
      EntityPlayer me = ((CraftPlayer)player).getHandle();
      PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(me, (byte)35);
      me.c.a(packet);
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
      me.c.a(new PacketPlayOutEntityMetadata(me.af(), me.aj().c()));
   }

   @Override
   public void playEntityAnimation(AbstractEntity target, byte effect, Collection<AbstractPlayer> audience) {
      Entity e = ((CraftEntity)target.getBukkitEntity()).getHandle();
      PacketPlayOutAnimation packet = new PacketPlayOutAnimation(e, effect);

      for (AbstractPlayer entity : audience) {
         if (entity.isPlayer()) {
            Player player = (Player)entity.getBukkitEntity();
            EntityPlayer me = ((CraftPlayer)player).getHandle();
            me.c.a(packet);
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
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      return nmsEntity.bx();
   }

   @Override
   public Collection<AbstractPlayer> getTrackedPlayers(AbstractEntity entity) {
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      Entity nmsEntity = ((CraftEntity)bukkitEntity).getHandle();
      WorldServer level = (WorldServer)nmsEntity.dI();
      Map<Integer, EntityTracker> map = level.k().a.K;
      EntityTracker tracker = map.get(nmsEntity.af());
      if (tracker == null) {
         return Collections.emptyList();
      } else {
         Collection<AbstractPlayer> players = Lists.newArrayList();

         for (ServerPlayerConnection nmsPlayer : tracker.f) {
            players.add(BukkitAdapter.adapt(nmsPlayer.f().getBukkitEntity()));
         }

         return players;
      }
   }

   @Override
   public boolean isUsingSpyglass(AbstractPlayer player) {
      org.bukkit.entity.Entity bukkitPlayer = player.getBukkitEntity();
      CraftPlayer craftPlayer = (CraftPlayer)bukkitPlayer;
      EntityPlayer nmsPlayer = craftPlayer.getHandle();
      return nmsPlayer.gl();
   }

   @Override
   public void spawnFakeLightning(AbstractLocation target, double radius) {
      Location location = BukkitAdapter.adapt(target);
      World world = location.getWorld();
      WorldServer worldHandle = ((CraftWorld)world).getHandle();
      EntityLightning entitylightning = (EntityLightning)EntityTypes.ai.a(worldHandle);
      entitylightning.a(Vec3D.b(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
      entitylightning.a(true);
      PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(entitylightning);
      double distanceSquared = radius * radius;
      Schedulers.async().run(() -> {
         for (Player player : location.getWorld().getPlayers()) {
            if (location.distanceSquared(player.getLocation()) <= distanceSquared) {
               ((CraftPlayer)player).getHandle().c.a(spawnItem);
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
            frog.dK().a(MemoryModuleType.o, target);
            frog.dK().b(MemoryModuleType.Z);
            frog.a(targetHandle);
            frog.b(EntityPose.j);
            frog.a(SoundEffects.iL, 1.0F, 1.0F);
            Schedulers.sync().runLater(() -> {
               if (frog.bs()) {
                  frog.q();
                  frog.b(EntityPose.a);
                  frog.dK().b(MemoryModuleType.o);
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
