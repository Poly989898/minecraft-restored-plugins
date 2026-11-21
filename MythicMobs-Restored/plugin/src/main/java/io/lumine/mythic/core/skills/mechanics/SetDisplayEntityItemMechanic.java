package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Seyarada",
   name = "setDisplayEntityItem",
   description = "Sets the item component of Item Display entity"
)
public class SetDisplayEntityItemMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "item",
      aliases = {"i", "type", "t", "material", "mat", "m"},
      description = "The item to use. If not set, the mechanic will remove the item from the entity instead."
   )
   private PlaceholderString item;

   public SetDisplayEntityItemMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.item = mlc.getPlaceholderString(new String[]{"item", "i", "type", "t", "material", "mat", "m"}, "AIR");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof ItemDisplay itemDisplay) {
         AbstractItemStack itemStack = MythicBukkit.inst().getBootstrap().createItem(this.item.get(data, target));
         ItemStack item = BukkitAdapter.adapt(itemStack);
         Runnable applyRunnable = () -> itemDisplay.setItemStack(item);
         Schedulers.runOnRegion(target.getBukkitEntity(), applyRunnable);
         return SkillResult.SUCCESS;
      } else if (target.getBukkitEntity() instanceof BlockDisplay blockDisplay) {
         AbstractItemStack itemStack = MythicBukkit.inst().getBootstrap().createItem(this.item.get(data, target));
         ItemStack item = BukkitAdapter.adapt(itemStack);
         if (!item.getType().isBlock()) {
            return SkillResult.INVALID_TARGET;
         } else {
            Runnable applyRunnable = () -> blockDisplay.setBlock(item.getType().asBlockType().createBlockData());
            Schedulers.runOnRegion(target.getBukkitEntity(), applyRunnable);
            return SkillResult.SUCCESS;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
