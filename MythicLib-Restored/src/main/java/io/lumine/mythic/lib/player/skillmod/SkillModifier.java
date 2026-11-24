package io.lumine.mythic.lib.player.skillmod;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.api.InstanceModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillModifier extends InstanceModifier {
   private final List<SkillHandler<?>> skills;
   private final String parameter;

   public SkillModifier(String key, String parameter, List<SkillHandler<?>> skills, double value) {
      this(key, parameter, skills, value, ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER);
   }

   public SkillModifier(String key, String parameter, List<SkillHandler<?>> skills, double value, ModifierType type) {
      this(key, parameter, skills, value, type, EquipmentSlot.OTHER, ModifierSource.OTHER);
   }

   public SkillModifier(String key, String parameter, List<SkillHandler<?>> skills, double value, ModifierType type, EquipmentSlot slot, ModifierSource source) {
      super(key, slot, source, value, type);
      this.skills = skills;
      this.parameter = parameter;
   }

   public SkillModifier(
      UUID uniqueId, String key, String parameter, List<SkillHandler<?>> skills, double value, ModifierType type, EquipmentSlot slot, ModifierSource source
   ) {
      super(uniqueId, key, slot, source, value, type);
      this.skills = skills;
      this.parameter = parameter;
   }

   public SkillModifier add(double offset) {
      return new SkillModifier(
         this.getUniqueId(), this.getKey(), this.parameter, new ArrayList<>(this.skills), this.value + offset, this.type, this.getSlot(), this.getSource()
      );
   }

   public List<SkillHandler<?>> getSkills() {
      return this.skills;
   }

   public String getParameter() {
      return this.parameter;
   }

   @Deprecated
   public void register(MMOPlayerData playerData, SkillHandler<?> handler) {
      this.register(playerData);
   }

   @Deprecated
   public void unregister(MMOPlayerData playerData, SkillHandler<?> handler) {
      this.unregister(playerData);
   }

   @Override
   public void register(MMOPlayerData playerData) {
      playerData.getSkillModifierMap().addModifier(this);
   }

   @Override
   public void unregister(MMOPlayerData playerData) {
      playerData.getSkillModifierMap().removeModifier(this.getUniqueId());
   }
}
