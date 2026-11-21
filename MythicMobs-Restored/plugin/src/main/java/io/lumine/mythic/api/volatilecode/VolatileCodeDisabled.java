package io.lumine.mythic.api.volatilecode;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileBlockHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.volatilecode.disabled.VolatileAIHandlerDisabled;
import io.lumine.mythic.core.volatilecode.disabled.VolatileBlockHandlerDisabled;
import io.lumine.mythic.core.volatilecode.disabled.VolatileCompatibilityHandlerDisabled;
import io.lumine.mythic.core.volatilecode.disabled.VolatileParticleHandlerDisabled;
import io.lumine.mythic.core.volatilecode.disabled.VolatileWorldHandlerDisabled;
import java.util.Collections;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@Getter
public class VolatileCodeDisabled implements VolatileCodeHandler {
   private final VolatileAIHandler AIHandler = new VolatileAIHandlerDisabled();
   private final VolatileCompatibilityHandler compatibilityHandler = new VolatileCompatibilityHandlerDisabled();
   private final VolatileBlockHandler blockHandler = new VolatileBlockHandlerDisabled();
   private final VolatileEntityHandler entityHandler = null;
   private final VolatileItemHandler itemHandler = null;
   private final VolatileWorldHandler worldHandler = new VolatileWorldHandlerDisabled();
   private final VolatileParticleHandler particleHandler = new VolatileParticleHandlerDisabled();

   @Override
   public void CreateFireworksExplosion(Location location, boolean flicker, boolean trail, int type, int[] colors, int[] fadeColors, int flightDuration) {
   }

   @Override
   public void setChickenHostile(Chicken c) {
   }

   @Override
   public Set<AbstractEntity> getEntitiesBySelector(SkillCaster am, String targetSelector) {
      return Collections.emptySet();
   }

   @Override
   public boolean doDamage(ActiveMob mob, AbstractEntity target, float amount) {
      return target.damage(amount);
   }

   @Override
   public double getAbsorptionHearts(LivingEntity entity) {
      return 0.0;
   }

   @Override
   public void saveSkinData(Player player, String name) {
   }

   @Override
   public float getItemRecharge(Player player) {
      return 0.0F;
   }

   @Override
   public void doEffectArmSwing(AbstractEntity entity) {
   }
}
