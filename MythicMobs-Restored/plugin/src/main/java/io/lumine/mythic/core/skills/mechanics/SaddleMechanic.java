package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractNautilus;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Steerable;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Seyarada",
   name = "saddle",
   aliases = {"giveSaddle", "setSaddle"},
   description = "Gives a saddle to the target"
)
public class SaddleMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "take",
      aliases = {"t", "remove", "r"},
      description = "If it should take the saddle away",
      defValue = "false"
   )
   protected boolean take;

   public SaddleMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.take = mlc.getBoolean(new String[]{"take", "t", "remove", "r"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Runnable applyRunnable = () -> {
         if (target.getBukkitEntity() instanceof Steerable steerable) {
            steerable.setSaddle(!this.take);
         } else if (target.getBukkitEntity() instanceof Horse horse) {
            if (this.take) {
               horse.getInventory().setSaddle(new ItemStack(Material.AIR));
            } else {
               horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            }
         } else if (target.getBukkitEntity() instanceof AbstractNautilus nautilus) {
            if (this.take) {
               nautilus.getInventory().setSaddle(new ItemStack(Material.AIR));
            } else {
               nautilus.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            }
         } else if (target.getBukkitEntity() instanceof HappyGhast happyGhast) {
            EntityEquipment equipment = happyGhast.getEquipment();
            if (equipment == null) {
               return;
            }

            if (this.take) {
               equipment.setItem(EquipmentSlot.BODY, new ItemStack(Material.AIR));
            } else {
               ItemStack bodyItem = equipment.getItem(EquipmentSlot.BODY);
               if (bodyItem == null || bodyItem.getType().isAir() || !Tag.ITEMS_HARNESSES.isTagged(bodyItem.getType())) {
                  equipment.setItem(EquipmentSlot.BODY, new ItemStack(Material.WHITE_HARNESS));
               }
            }
         }
      };
      Schedulers.runOnRegion(target.getBukkitEntity(), applyRunnable);
      return SkillResult.SUCCESS;
   }
}
