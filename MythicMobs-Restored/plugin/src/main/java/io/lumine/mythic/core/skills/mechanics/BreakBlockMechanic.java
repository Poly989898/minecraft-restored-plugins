package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@MythicMechanic(
   author = "Ashijin",
   name = "breakBlock",
   aliases = {"blockBreak"},
   description = "Breaks the block at the target location"
)
public class BreakBlockMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "doDrops",
      aliases = {"drops", "d"},
      description = "Drops the block broken",
      defValue = "true"
   )
   private final boolean doDrops;
   @MythicField(
      name = "doEffect",
      aliases = {"effect", "e"},
      description = "Plays the block break effect",
      defValue = "true"
   )
   private final boolean doEffect;
   @MythicField(
      name = "useTool",
      aliases = {"tool", "t"},
      description = "Whether to use the tool in the player's hand",
      defValue = "true"
   )
   private final boolean useTool;

   public BreakBlockMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.doDrops = mlc.getBoolean(new String[]{"dodrops", "drops", "d"}, true);
      this.doEffect = mlc.getBoolean(new String[]{"doeffect", "effect", "e"}, true);
      this.useTool = mlc.getBoolean(new String[]{"usetool", "tool", "t"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(data, target.getLocation());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(data, target);
      return SkillResult.SUCCESS;
   }

   public void playEffect(SkillMetadata data, AbstractLocation location) {
      Location l = BukkitAdapter.adapt(location);
      if (!data.getCaster().isUsingDamageSkill()) {
         data.getCaster().setUsingDamageSkill(true);

         try {
            if (data.getCaster().getEntity().isPlayer()) {
               BlockBreakEvent event = new BlockBreakEvent(l.getBlock(), (Player)data.getCaster().getEntity().getBukkitEntity());
               Events.call(event);
               if (event.isCancelled()) {
                  return;
               }
            }

            if (this.doDrops) {
               if (this.useTool) {
                  ItemStack tool;
                  if (data.getCaster().getEntity().isPlayer()) {
                     tool = ((Player)data.getCaster().getEntity().getBukkitEntity()).getInventory().getItemInMainHand();
                  } else {
                     tool = new ItemStack(Material.AIR);
                  }

                  l.getBlock().breakNaturally(tool);
               } else {
                  l.getBlock().breakNaturally();
               }
            } else {
               l.getBlock().setType(Material.AIR);
               return;
            }
         } finally {
            data.getCaster().setUsingDamageSkill(false);
         }
      }
   }
}
