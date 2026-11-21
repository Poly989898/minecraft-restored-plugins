package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "equipCopy",
   description = "Causes the caster to equip a copy of the target's equipment"
)
public class EquipCopyMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private List<EquipSlot> slots = Lists.newArrayList();

   public EquipCopyMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String slots = mlc.getString(new String[]{"slots", "s"}, "ALL");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      AbstractEntity caster = data.getCaster().getEntity();
      if (this.slots.isEmpty()) {
         for (EquipSlot slot : EquipSlot.values()) {
            Optional<AbstractItemStack> maybeItem = slot.get(target);
            if (maybeItem.isPresent()) {
               slot.equip(caster, maybeItem.get());
               slot.preventDrop(caster);
            }
         }
      }

      if (data.getCaster() instanceof ActiveMob am) {
         am.checkEquipment();
         am.getType().reapplyAIGoalSelectors(am);
      }

      return SkillResult.SUCCESS;
   }
}
