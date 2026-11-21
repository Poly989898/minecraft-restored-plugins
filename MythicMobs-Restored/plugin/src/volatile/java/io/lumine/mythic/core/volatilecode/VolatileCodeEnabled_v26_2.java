package io.lumine.mythic.core.volatilecode;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileSpawningHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.VectorUtils;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileAIHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileBlockHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileCompatibilityHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileCompoundTag;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileEntityHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileItemHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatilePacketHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileParticleHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileSpawningHandlerImpl;
import io.lumine.mythic.core.volatilecode.v26_2.VolatileWorldHandlerImpl;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftChicken;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class VolatileCodeEnabled_v26_2 implements VolatileCodeHandler {
   private final VolatileAIHandler AIHandler = new VolatileAIHandlerImpl(this);
   private final VolatileBlockHandler blockHandler = new VolatileBlockHandlerImpl(this);
   private final VolatileCompatibilityHandler compatibilityHandler = new VolatileCompatibilityHandlerImpl(this);
   private final VolatileEntityHandler entityHandler = new VolatileEntityHandlerImpl(this);
   private final VolatileItemHandler itemHandler = new VolatileItemHandlerImpl(this);
   private final VolatilePacketHandler packetHandler = new VolatilePacketHandlerImpl(this);
   private final VolatileParticleHandler particleHandler = new VolatileParticleHandlerImpl(this);
   private final VolatileSpawningHandler spawningHandler = new VolatileSpawningHandlerImpl(this);
   private final VolatileWorldHandler worldHandler = new VolatileWorldHandlerImpl(this);
   private static final Set<EntityType> BAD_CONTROLLER_LOOK = EnumSet.of(
      EntityType.BAT,
      EntityType.ENDERMITE,
      EntityType.ENDER_DRAGON,
      EntityType.GHAST,
      EntityType.HORSE,
      EntityType.MAGMA_CUBE,
      EntityType.POLAR_BEAR,
      EntityType.SILVERFISH,
      EntityType.SLIME
   );

   @Override
   public CompoundTag createCompoundTag(Map<String, Tag> value) {
      return new VolatileCompoundTag(value);
   }

   @Override
   public Set<AbstractEntity> getEntitiesBySelector(SkillCaster am, String targetSelector) {
      return null;
   }

   @Override
   public void CreateFireworksExplosion(Location location, boolean flicker, boolean trail, int type, int[] colors, int[] fadeColors, int flightDuration) {
   }

   @Override
   public void setChickenHostile(Chicken c) {
      net.minecraft.world.entity.animal.chicken.Chicken chicken = ((CraftChicken)c).getHandle();
      chicken.setAggressive(true);
   }

   @Override
   public boolean doDamage(ActiveMob mob, AbstractEntity t, float amount) {
      CraftLivingEntity caster = (CraftLivingEntity)mob.getEntity().getBukkitEntity();
      CraftLivingEntity target = (CraftLivingEntity)t.getBukkitEntity();
      return target.getHandle().hurtOrSimulate(caster.getHandle().level().damageSources().mobAttack(caster.getHandle()), amount);
   }

   @Override
   public double getAbsorptionHearts(LivingEntity entity) {
      return ((CraftLivingEntity)entity).getHandle().getAbsorptionAmount();
   }

   @Override
   public void saveSkinData(Player player, String name) {
   }

   @Override
   public float getItemRecharge(Player player) {
      ServerPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.getAttackStrengthScale(0.0F);
   }

   @Override
   public boolean getItemRecharging(Player player) {
      ServerPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.getAttackStrengthScale(0.0F) < 1.0F;
   }

   @Override
   public void doEffectArmSwing(AbstractEntity entity) {
      Entity e = ((CraftEntity)entity.getBukkitEntity()).getHandle();
      e.level().broadcastEntityEvent(e, (byte)0);
   }

   @Override
   public void lookAt(AbstractEntity entity, float yaw, float pitch) {
      Entity handle = ((CraftEntity)BukkitAdapter.adapt(entity)).getHandle();
      yaw = VectorUtils.clampYaw(yaw);
      handle.setXRot(pitch);
      this.setHeadYaw(entity, yaw);
      handle.setYHeadRot(yaw);
   }

   @Override
   public void lookAtLocation(AbstractEntity entity, AbstractLocation to, boolean headOnly, boolean immediate) {
      Entity handle = ((CraftEntity)BukkitAdapter.adapt(entity)).getHandle();
      if (!immediate && !headOnly && !BAD_CONTROLLER_LOOK.contains(handle.getBukkitEntity().getType()) && handle instanceof Mob) {
         ((Mob)handle).getLookControl().setLookAt(to.getX(), to.getY(), to.getZ());
      } else {
         AbstractLocation fromLocation = entity.getLocation();
         double xDiff = to.getX() - fromLocation.getX();
         double yDiff = to.getY() - fromLocation.getY();
         double zDiff = to.getZ() - fromLocation.getZ();
         double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
         double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);
         double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
         double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0;
         if (zDiff < 0.0) {
            yaw += Math.abs(180.0 - yaw) * 2.0;
         }

         yaw -= 90.0;
         if (headOnly) {
            this.setHeadYaw(entity, (float)yaw);
         } else {
            this.lookAt(entity, (float)yaw, (float)pitch);
         }
      }
   }

   @Override
   public void lookAtEntity(AbstractEntity entity, AbstractEntity to, boolean headOnly, boolean immediate) {
      Entity handle = ((CraftEntity)BukkitAdapter.adapt(entity)).getHandle();
      Entity target = ((CraftEntity)BukkitAdapter.adapt(to)).getHandle();
      if (BAD_CONTROLLER_LOOK.contains(handle.getBukkitEntity().getType())) {
         if (to.isLiving()) {
            this.lookAtLocation(entity, to.getEyeLocation(), headOnly, immediate);
         } else {
            this.lookAtLocation(entity, to.getLocation(), headOnly, immediate);
         }
      } else if (handle instanceof Mob insentient) {
         insentient.getLookControl().setLookAt(target);
      }
   }

   @Override
   public void sendResourcePack(AbstractPlayer player, String url, String hash) {
      Player p = (Player)player.getBukkitEntity();
      byte[] h = hash.getBytes();
      p.setResourcePack(url, h);
   }

   @Override
   public void applyPhysics(Block target) {
      Location location = target.getLocation();
      ServerLevel world = ((CraftWorld)location.getWorld()).getHandle();
      BlockPos blockposition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
      BlockState iblockdata = world.getBlockState(blockposition);
      net.minecraft.world.level.block.Block block = iblockdata.getBlock();
      world.updateNeighborsAt(blockposition, block);
   }

   @Generated
   @Override
   public VolatileAIHandler getAIHandler() {
      return this.AIHandler;
   }

   @Generated
   @Override
   public VolatileBlockHandler getBlockHandler() {
      return this.blockHandler;
   }

   @Generated
   @Override
   public VolatileCompatibilityHandler getCompatibilityHandler() {
      return this.compatibilityHandler;
   }

   @Generated
   @Override
   public VolatileEntityHandler getEntityHandler() {
      return this.entityHandler;
   }

   @Generated
   @Override
   public VolatileItemHandler getItemHandler() {
      return this.itemHandler;
   }

   @Generated
   @Override
   public VolatilePacketHandler getPacketHandler() {
      return this.packetHandler;
   }

   @Generated
   @Override
   public VolatileParticleHandler getParticleHandler() {
      return this.particleHandler;
   }

   @Generated
   @Override
   public VolatileSpawningHandler getSpawningHandler() {
      return this.spawningHandler;
   }

   @Generated
   @Override
   public VolatileWorldHandler getWorldHandler() {
      return this.worldHandler;
   }
}
