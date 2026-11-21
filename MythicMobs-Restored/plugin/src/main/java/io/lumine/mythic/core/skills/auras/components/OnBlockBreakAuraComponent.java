package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitTriggerMetadata;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.mechanics.AuraMechanic;
import io.lumine.mythic.core.skills.targeters.ILocationSelector;
import io.lumine.mythic.core.utils.annotations.MythicAuraComponent;
import java.util.List;
import java.util.Optional;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBreakAuraComponent implements AuraComponent {
   private final Aura.AuraTracker tracker;
   private final Optional<Skill> onBreakSkill;
   private final PlaceholderString blockType;
   private final boolean cancelEvent;
   private final boolean dropItems;

   public OnBlockBreakAuraComponent(Aura.AuraTracker tracker, Optional<Skill> onBreakSkill, PlaceholderString blockType, boolean cancelEvent, boolean dropItems) {
      this.tracker = tracker;
      this.onBreakSkill = onBreakSkill;
      this.blockType = blockType;
      this.cancelEvent = cancelEvent;
      this.dropItems = dropItems;
   }

   @Override
   public void auraStart() {
      Optional<AbstractEntity> maybeEntity = this.tracker.getEntity();
      if (!maybeEntity.isEmpty()) {
         this.tracker
            .registerAuraComponent(
               Events.subscribe(BlockBreakEvent.class)
                  .filter(event -> event.getPlayer().getUniqueId().equals(maybeEntity.get().getUniqueId()))
                  .filter(event -> {
                     if (this.blockType != null) {
                        List<String> wantedBlocks = List.of(this.blockType.get(this.tracker.getSkillMetadata().deepClone()).split(","));
                        return ILocationSelector.blockMatches(event.getBlock().getType(), wantedBlocks);
                     } else {
                        return true;
                     }
                  })
                  .handler(event -> {
                     SkillMetadata meta = this.tracker.getSkillMetadata().deepClone();
                     AbstractLocation target = BukkitAdapter.adapt(event.getBlock().getLocation()).add(0.5, 0.5, 0.5);
                     meta.setLocationTarget(target);
                     BukkitTriggerMetadata.apply(meta, event);
                     if (this.blockType != null) {
                        List<String> wantedBlocks = List.of(this.blockType.get(meta).split(","));
                        if (!ILocationSelector.blockMatches(event.getBlock().getType(), wantedBlocks)) {
                           return;
                        }
                     }

                     if (this.tracker.executeAuraSkill(this.onBreakSkill, meta)) {
                        this.tracker.consumeCharge();
                        if (this.cancelEvent) {
                           event.setCancelled(true);
                        } else if (!this.dropItems) {
                           event.setDropItems(false);
                        }
                     }
                  })
            );
      }
   }

   @MythicAuraComponent(
      name = "onblockbreak",
      aliases = {"onbreakblock"},
      description = "Triggers a skill when the aura holder breaks a block"
   )
   public static class Factory implements AuraComponentFactory {
      private final AuraMechanic mechanic;
      private final String onBreakSkillName;
      private Optional<Skill> onBreakSkill = Optional.empty();
      private final PlaceholderString blockType;
      private final boolean cancelEvent;
      private final boolean dropItems;

      public Factory(AuraMechanic mechanic, MythicLineConfig mlc) {
         this.mechanic = mechanic;
         this.onBreakSkillName = mlc.getString(new String[]{"onbreakskill", "onbreak", "ob"});
         this.blockType = mlc.getPlaceholderString(
            new String[]{"blocktypes", "blocktypes", "bt", "t", "material", "materials", "m", "blocks", "block", "b"}, null
         );
         this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce", "cancel"}, false);
         this.dropItems = mlc.getBoolean(new String[]{"dropItem", "drop", "allowDrop"}, true);
         mechanic.getManager().queueSecondPass(() -> {
            if (this.onBreakSkillName != null) {
               this.onBreakSkill = mechanic.getManager().getSkill(mechanic.getFile(), mechanic, this.onBreakSkillName);
            }
         });
      }

      @Override
      public AuraComponent create(Aura.AuraTracker tracker) {
         return new OnBlockBreakAuraComponent(tracker, this.onBreakSkill, this.blockType, this.cancelEvent, this.dropItems);
      }
   }
}
