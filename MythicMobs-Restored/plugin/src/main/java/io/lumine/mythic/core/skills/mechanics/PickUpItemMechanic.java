package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.constants.ItemKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@MythicMechanic(
   author = "Ashijin",
   name = "pickupitem",
   aliases = {},
   description = "Picks up the target item entity"
)
public class PickUpItemMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private boolean doFakeLooting;
   protected Optional<Skill> onPickupSkill = Optional.empty();

   public PickUpItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.doFakeLooting = mlc.getBoolean(new String[]{"fakelooting", "fl"}, true);
      String strPickupSkill = mlc.getString(new String[]{"onpickup", "pickup", "then"}, null);
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         if (strPickupSkill != null) {
            this.onPickupSkill = this.getPlugin().getSkillManager().getSkill(file, this, strPickupSkill);
            if (this.onPickupSkill.isEmpty()) {
               MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + strPickupSkill);
            }
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(target.getBukkitEntity() instanceof Item item)) {
         return SkillResult.INVALID_TARGET;
      } else {
         AbstractEntity entity = data.getCaster().getEntity();
         ItemStack stack = item.getItemStack();
         if (stack.getType() == Material.AIR) {
            return SkillResult.INVALID_TARGET;
         } else {
            ItemMeta stackMeta = stack.getItemMeta();
            if (stackMeta != null && stackMeta.getPersistentDataContainer().has(ItemKeys.FANCY_DROP)) {
               stackMeta.getPersistentDataContainer().remove(ItemKeys.FANCY_DROP);
               stack.setItemMeta(stackMeta);
            }

            if (entity.isPlayer()) {
               Player player = (Player)entity.getBukkitEntity();
               if (this.doFakeLooting) {
                  int id = this.getPlugin().getVolatileCodeHandler().getItemHandler().spawnFakeItem(player, stack, target.getLocation());
                  Schedulers.sync().runLater(() -> this.getPlugin().getVolatileCodeHandler().getItemHandler().collectFakeItem(player, id), 5L);
               }

               HashMap<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{stack});

               for (ItemStack entry : left.values()) {
                  item.getLocation().getWorld().dropItem(item.getLocation(), entry);
               }
            } else {
               if (!entity.isLiving()) {
                  return SkillResult.INVALID_TARGET;
               }

               LivingEntity livingEntity = (LivingEntity)entity.getBukkitEntity();
               livingEntity.playPickupItemAnimation(item);
            }

            this.onPickupSkill.ifPresent(skill -> {
               SkillMetadata newData = data.deepCloneNoTargets();
               newData.setOrigin(target.getLocation());
               newData.setLocationTarget(target.getLocation());
               newData.setMetadata(PlaceholderMetadataKey.ITEM_STACK, stack);
               if (skill.isUsable(newData)) {
                  skill.execute(newData);
               }
            });
            item.remove();
            return SkillResult.SUCCESS;
         }
      }
   }
}
