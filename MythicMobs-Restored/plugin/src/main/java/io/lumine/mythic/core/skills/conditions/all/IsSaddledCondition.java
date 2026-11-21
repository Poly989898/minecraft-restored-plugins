package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractNautilus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Steerable;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@MythicCondition(
   author = "Seyarada",
   name = "isSaddled",
   aliases = {"saddled"},
   description = "If the target has a saddle"
)
public class IsSaddledCondition extends SkillCondition implements IEntityCondition {
   public IsSaddledCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      Entity bukkitEntity = entity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)) {
         Boolean result = Schedulers.of(bukkitEntity).supply(() -> this.checkSaddled(bukkitEntity)).join();
         return result != null ? result : false;
      } else {
         return this.checkSaddled(bukkitEntity);
      }
   }

   private boolean checkSaddled(Entity bukkitEntity) {
      if (bukkitEntity instanceof Steerable steerable) {
         return steerable.hasSaddle();
      } else if (bukkitEntity instanceof Horse horse) {
         return this.hasItem(horse.getInventory().getSaddle());
      } else if (bukkitEntity instanceof AbstractNautilus nautilus) {
         return this.hasItem(nautilus.getInventory().getSaddle());
      } else if (bukkitEntity instanceof HappyGhast happyGhast) {
         EntityEquipment equipment = happyGhast.getEquipment();
         if (equipment == null) {
            return false;
         } else {
            ItemStack bodyItem = equipment.getItem(EquipmentSlot.BODY);
            return this.hasItem(bodyItem) && Tag.ITEMS_HARNESSES.isTagged(bodyItem.getType());
         }
      } else {
         return false;
      }
   }

   private boolean hasItem(ItemStack itemStack) {
      return itemStack != null && !itemStack.getType().isAir();
   }
}
