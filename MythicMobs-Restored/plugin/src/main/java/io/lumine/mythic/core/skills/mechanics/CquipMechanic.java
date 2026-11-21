package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "jaylawl",
   name = "cquip",
   description = "Causes the caster to cquip an item or droptable"
)
public class CquipMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "item",
      aliases = {"i"},
      description = "The item or droptable to cquip."
   )
   private final String item;
   @MythicField(
      name = "slot",
      aliases = {"s"},
      defValue = "-1",
      description = "The slot to cquip the item in. Must be an integer between 0 and 5."
   )
   private final int slot;

   public CquipMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.item = mlc.getString(new String[]{"item", "i"}, null);
      this.slot = mlc.getInteger(new String[]{"slot", "s"}, -1);
      if (this.item == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'item' attribute is required.");
      } else if (this.slot < 0 || this.slot > 5) {
         MythicLogger.errorMechanicConfig(this, mlc, "The 'slot' attribute must be an integer between 0 and 5.");
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      String fuck = this.item;
      fuck = fuck.replace("<internal-id>", ((ActiveMob)data.getCaster()).getType().getInternalName());
      Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(fuck);
      if (!maybeItem.isPresent()) {
         MythicLogger.error("Couldn't find MythicItem with name \"" + fuck + "\"");
         return SkillResult.INVALID_CONFIG;
      } else {
         LivingEntity e = (LivingEntity)data.getCaster().getEntity().getBukkitEntity();
         EntityEquipment ee = e.getEquipment();
         if (ee == null) {
            MythicLogger.error("This entity cannot have any equipment");
            return SkillResult.INVALID_TARGET;
         } else {
            ItemStack i = ((BukkitItemStack)maybeItem.get().generateItemStack(1)).getItemStack();
            switch (this.slot) {
               case 0:
                  ee.setItemInMainHand(i);
                  break;
               case 1:
                  ee.setBoots(i);
                  break;
               case 2:
                  ee.setLeggings(i);
                  break;
               case 3:
                  ee.setChestplate(i);
                  break;
               case 4:
                  ee.setHelmet(i);
                  break;
               case 5:
                  ee.setItemInOffHand(i);
            }

            return SkillResult.SUCCESS;
         }
      }
   }
}
