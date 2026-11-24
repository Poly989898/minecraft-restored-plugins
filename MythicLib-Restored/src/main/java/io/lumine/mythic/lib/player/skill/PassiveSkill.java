package io.lumine.mythic.lib.player.skill;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.PlayerModifier;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class PassiveSkill extends PlayerModifier {
   private final Skill triggered;

   @Deprecated
   public PassiveSkill(String key, TriggerType type, Skill triggered, EquipmentSlot equipmentSlot, ModifierSource modifierSource) {
      this(key, triggered, equipmentSlot, modifierSource);
   }

   public PassiveSkill(String key, Skill triggered, EquipmentSlot equipmentSlot, ModifierSource modifierSource) {
      super(key, equipmentSlot, modifierSource);
      Validate.isTrue(triggered.getTrigger().isPassive(), "Skill is active");
      this.triggered = Objects.requireNonNull(triggered, "Skill cannot be null");
   }

   @Deprecated
   public PassiveSkill(String key, TriggerType type, Skill triggered) {
      this(key, triggered, EquipmentSlot.OTHER, ModifierSource.OTHER);
   }

   public PassiveSkill(ConfigObject obj) {
      super(obj.getString("key"), EquipmentSlot.OTHER, ModifierSource.OTHER);
      this.triggered = new SimpleSkill(TriggerType.API, MythicLib.plugin.getSkills().getHandlerOrThrow(obj.getString("skill")));
   }

   @NotNull
   public Skill getTriggeredSkill() {
      return this.triggered;
   }

   public long getTimerPeriod() {
      return Math.max(1L, (long)this.triggered.getParameter("timer")) * 50L;
   }

   @NotNull
   public TriggerType getType() {
      return this.triggered.getTrigger();
   }

   @Override
   public void register(MMOPlayerData playerData) {
      playerData.getPassiveSkillMap().addModifier(this);
   }

   @Override
   public void unregister(MMOPlayerData playerData) {
      playerData.getPassiveSkillMap().removeModifier(this.getUniqueId());
   }
}
