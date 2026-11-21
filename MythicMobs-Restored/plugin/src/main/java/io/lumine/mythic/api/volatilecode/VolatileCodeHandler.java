package io.lumine.mythic.api.volatilecode;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileSpawningHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface VolatileCodeHandler {
   static MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   default VolatileAIHandler getAIHandler() {
      return null;
   }

   default VolatileBlockHandler getBlockHandler() {
      return null;
   }

   default VolatileCompatibilityHandler getCompatibilityHandler() {
      return null;
   }

   default VolatileEntityHandler getEntityHandler() {
      return null;
   }

   default VolatileItemHandler getItemHandler() {
      return null;
   }

   default VolatilePacketHandler getPacketHandler() {
      return null;
   }

   default VolatileParticleHandler getParticleHandler() {
      return null;
   }

   default VolatileSpawningHandler getSpawningHandler() {
      return null;
   }

   default VolatileWorldHandler getWorldHandler() {
      return null;
   }

   default CompoundTag createCompoundTag(Map<String, Tag> value) {
      return null;
   }

   boolean doDamage(ActiveMob activeMob, AbstractEntity abstractEntity, float value);

   void CreateFireworksExplosion(Location location, boolean flag, boolean flag2, int value, int[] ints, int[] ints2, int value2);

   void setChickenHostile(Chicken chicken);

   Set<AbstractEntity> getEntitiesBySelector(SkillCaster skillCaster, String str);

   double getAbsorptionHearts(LivingEntity livingEntity);

   void saveSkinData(Player player, String str);

   float getItemRecharge(Player player);

   void doEffectArmSwing(AbstractEntity abstractEntity);

   default void lookAtLocation(AbstractEntity entity, AbstractLocation target, boolean headOnly, boolean immediate) {
   }

   default void lookAtEntity(AbstractEntity entity, AbstractEntity target, boolean headOnly, boolean immediate) {
   }

   default void lookAt(AbstractEntity entity, float yaw, float pitch) {
   }

   default void setHeadYaw(AbstractEntity entity, float yaw) {
   }

   default boolean getItemRecharging(Player player) {
      return false;
   }

   default void applyPhysics(Block target) {
   }

   default void sendResourcePack(AbstractPlayer player, String url, String hash) {
   }
}
