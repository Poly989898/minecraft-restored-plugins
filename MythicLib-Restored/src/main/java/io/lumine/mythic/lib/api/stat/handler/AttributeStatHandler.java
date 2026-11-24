package io.lumine.mythic.lib.api.stat.handler;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.version.VersionUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class AttributeStatHandler extends StatHandler {
   protected final Attribute attribute;
   private final Material material;
   private final String description;
   private final double playerDefaultBase;
   protected static final NamespacedKey ATTRIBUTE_KEY = new NamespacedKey(MythicLib.plugin, "main");
   protected static final double EPSILON = 1.0E-4;

   public AttributeStatHandler(
      ConfigurationSection config,
      @NotNull Attribute attribute,
      @NotNull String stat,
      double playerDefaultBase,
      @NotNull Material material,
      @NotNull String description
   ) {
      super(config, stat);
      this.attribute = attribute;
      this.material = material;
      this.description = description;
      this.playerDefaultBase = playerDefaultBase;
   }

   @Override
   public void runUpdate(@NotNull StatInstance instance) {
      AttributeInstance attrIns = instance.getMap().getData().getPlayer().getAttribute(this.attribute);
      this.removeModifiers(attrIns);
      double vanillaBase = instance.getMap().getData().getPlayer().getAttribute(this.attribute).getBaseValue();
      double mmoFinal = this.clampValue(instance.getFilteredTotal(vanillaBase + this.baseValue, EquipmentSlot.MAIN_HAND::isCompatible));
      double difference = mmoFinal - vanillaBase;
      if (Math.abs(difference) > 1.0E-4) {
         attrIns.addModifier(VersionUtils.attrMod(ATTRIBUTE_KEY, difference, Operation.ADD_NUMBER));
      }
   }

   @Override
   public double getBaseValue(@NotNull StatInstance instance) {
      return this.baseValue + instance.getMap().getData().getPlayer().getAttribute(this.attribute).getBaseValue();
   }

   @Override
   public double getFinalValue(@NotNull StatInstance instance) {
      return instance.getMap().getData().getPlayer().getAttribute(this.attribute).getValue();
   }

   protected void removeModifiers(@NotNull AttributeInstance ins) {
      for (AttributeModifier mod : ins.getModifiers()) {
         if (VersionUtils.matches(mod, ATTRIBUTE_KEY)) {
            ins.removeModifier(mod);
         }
      }
   }

   @NotNull
   public Attribute getAttribute() {
      return this.attribute;
   }

   @NotNull
   public Material getMaterial() {
      return this.material;
   }

   @NotNull
   public String getDescription() {
      return this.description;
   }

   public double getPlayerDefaultBase() {
      return this.playerDefaultBase;
   }
}
