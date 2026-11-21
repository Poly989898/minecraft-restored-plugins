package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@MythicMechanic(
   author = "Ashijin",
   name = "decapitate",
   aliases = {"dropHead"},
   description = "Drops the target entity's head"
)
public class DecapitateMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public DecapitateMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.SUCCESS;
      } else {
         ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
         SkullMeta meta = (SkullMeta)skull.getItemMeta();
         meta.setOwner(target.asPlayer().getName());
         skull.setItemMeta(meta);
         Location location = BukkitAdapter.adapt(target.getLocation());
         location.getWorld().dropItem(location, skull);
         return SkillResult.SUCCESS;
      }
   }
}
