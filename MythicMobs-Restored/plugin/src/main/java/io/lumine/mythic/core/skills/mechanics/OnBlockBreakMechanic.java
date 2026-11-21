package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitTriggerMetadata;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.bukkit.event.block.BlockBreakEvent;

@MythicMechanic(
   author = "Ashijin",
   name = "onblockbreak",
   aliases = {"onbreakblock"},
   description = "Applies an aura to the target that triggers a skill when they break a block",
   deprecated = true
)
public class OnBlockBreakMechanic extends Aura implements ITargetedEntitySkill {
   @MythicField(
      name = "onBreak",
      aliases = {"ob"},
      version = "4.13",
      description = "Skill to execute if the target hits something"
   )
   protected Optional<Skill> onBreakSkill = Optional.empty();
   protected String onBreakSkillName;
   @MythicField(
      name = "blocktype",
      aliases = {"blocktypes", "bt", "t", "material", "materials", "mat", "m", "blocks", "block", "b"},
      description = "Blocks to add to the Vein",
      defValue = "STONE"
   )
   protected PlaceholderString blockType;
   @MythicField(
      name = "cancelEvent",
      aliases = {"ce"},
      defValue = "false",
      version = "4.13",
      description = "Whether or not to cancel the event that triggered the aura"
   )
   protected boolean cancelEvent;
   @MythicField(
      name = "dropItem",
      aliases = {"drop", "allowDrop"},
      defValue = "true",
      description = "Whether or not to drop the item"
   )
   protected boolean dropItems;

   public OnBlockBreakMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.onBreakSkillName = mlc.getString(new String[]{"onbreakskill", "onbreak", "ob"});
      this.blockType = mlc.getPlaceholderString(new String[]{"blocktypes", "blocktypes", "bt", "t", "material", "materials", "m", "blocks", "block", "b"}, null);
      this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce", "cancel"}, false);
      this.dropItems = mlc.getBoolean(new String[]{"dropItem", "drop", "allowDrop"}, true);
      this.getManager().queueSecondPass(() -> {
         if (this.onBreakSkillName != null) {
            this.onBreakSkill = this.getManager().getSkill(file, this, this.onBreakSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnBlockBreakMechanic.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      @Override
      public void auraStart() {
         this.registerAuraComponent(
            Events.subscribe(BlockBreakEvent.class).filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId())).filter(event -> {
               if (OnBlockBreakMechanic.this.blockType != null) {
                  List<String> wantedBlocks = List.of(OnBlockBreakMechanic.this.blockType.get(this.skillMetadata.deepClone()).split(","));
                  return ILocationSelector.blockMatches(event.getBlock().getType(), wantedBlocks);
               } else {
                  return true;
               }
            }).handler(event -> {
               SkillMetadata meta = this.skillMetadata.deepClone();
               AbstractLocation target = BukkitAdapter.adapt(event.getBlock().getLocation()).add(0.5, 0.5, 0.5);
               meta.setLocationTarget(target);
               BukkitTriggerMetadata.apply(meta, event);
               if (OnBlockBreakMechanic.this.blockType != null) {
                  List<String> wantedBlocks = List.of(OnBlockBreakMechanic.this.blockType.get(meta).split(","));
                  if (!ILocationSelector.blockMatches(event.getBlock().getType(), wantedBlocks)) {
                     return;
                  }
               }

               if (this.executeAuraSkill(OnBlockBreakMechanic.this.onBreakSkill, meta)) {
                  this.consumeCharge();
                  if (OnBlockBreakMechanic.this.cancelEvent) {
                     event.setCancelled(true);
                  } else if (!OnBlockBreakMechanic.this.dropItems) {
                     event.setDropItems(false);
                  }
               }
            })
         );
         this.executeAuraSkill(OnBlockBreakMechanic.this.onStartSkill, this.skillMetadata);
      }
   }
}
