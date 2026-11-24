package io.lumine.mythic.lib.api.stat.handler;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.version.VersionUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class MovementSpeedStatHandler extends AttributeStatHandler {
   public MovementSpeedStatHandler(@NotNull ConfigurationSection config) {
      super(config, Attribute.MOVEMENT_SPEED, "MOVEMENT_SPEED", 0.1, Material.LEATHER_BOOTS, "Movement speed of an Entity.");
   }

   @Override
   public void runUpdate(@NotNull StatInstance instance) {
      AttributeInstance attrIns = instance.getMap().getData().getPlayer().getAttribute(Attribute.MOVEMENT_SPEED);
      this.removeModifiers(attrIns);
      double coef = 1.0 - instance.getMap().getStat("SPEED_MALUS_REDUCTION") / 100.0;
      double vanillaBase = instance.getMap().getData().getPlayer().getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue();
      double mmoFinal = this.clampValue(
         instance.getFilteredTotal(vanillaBase + this.baseValue, EquipmentSlot.MAIN_HAND::isCompatible, mod -> mod.getValue() < 0.0 ? mod.multiply(coef) : mod)
      );
      double difference = mmoFinal - vanillaBase;
      if (Math.abs(difference) > 1.0E-4) {
         attrIns.addModifier(VersionUtils.attrMod(ATTRIBUTE_KEY, difference, Operation.ADD_NUMBER));
      }
   }
}
