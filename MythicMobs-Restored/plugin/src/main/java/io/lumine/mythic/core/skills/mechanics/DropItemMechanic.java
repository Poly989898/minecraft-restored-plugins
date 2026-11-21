package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Item;

@MythicMechanic(
   author = "Ashijin",
   name = "dropitem",
   aliases = {"drop", "dropitems", "itemdrop"},
   description = "Drops an item or droptable"
)
public class DropItemMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "items",
      aliases = {"item", "i"},
      description = "The items/droptable to drop"
   )
   private final PlaceholderString drops;
   private DropTable dropTable;
   private boolean naturally;
   @MythicField(
      name = "owner",
      version = "5.14",
      description = "UUID set as the owner of the dropped items, only that player can pick them up"
   )
   private final PlaceholderString owner;
   @MythicField(
      name = "onDrop",
      aliases = {"then"},
      version = "5.7",
      description = "Skill to execute when the item is dropped"
   )
   protected Optional<Skill> onDropSkill = Optional.empty();
   protected String onDropSkillName;

   public DropItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.drops = mlc.getPlaceholderString(new String[]{"items", "item", "i"}, "");
      this.naturally = mlc.getBoolean(new String[]{"naturally", "natural", "n"}, true);
      this.owner = mlc.getPlaceholderString(new String[]{"owner"}, null);
      boolean forceDynamic = mlc.getBoolean(new String[]{"dynamic", "fd"}, false);
      if (!forceDynamic && this.drops.isStatic()) {
         this.getPlugin().getDropManager().queueSecondPass(() -> {
            String itemString = this.drops.get();
            Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(itemString);
            if (maybeTable.isPresent()) {
               this.dropTable = maybeTable.get();
            } else {
               List<String> items = Lists.newArrayList(itemString.split(","));
               this.dropTable = new DropTable("DropItemMechanic", "DropItemMechanic", items);
            }
         });
      } else {
         this.dropTable = null;
      }

      this.onDropSkillName = mlc.getString(new String[]{"ondropskill", "ondrop", "then"});
      this.getManager().queueSecondPass(() -> {
         if (this.onDropSkillName != null) {
            this.onDropSkill = this.getManager().getSkill(file, this, this.onDropSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      LootBag loot = this.getLoot(data, target);
      this.dropLoot(data, loot, target.getLocation(), this.getOwner(data, target));
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      LootBag loot = this.getLoot(data, null);
      this.dropLoot(data, loot, target, this.getOwner(data, null));
      return SkillResult.SUCCESS;
   }

   private void dropLoot(SkillMetadata data, LootBag loot, AbstractLocation location, UUID ownerUUID) {
      Collection<AbstractEntity> drops = loot.drop(location, this.naturally);
      if (ownerUUID != null) {
         for (AbstractEntity drop : drops) {
            ((Item)drop.getBukkitEntity()).setOwner(ownerUUID);
         }
      }

      if (this.onDropSkill.isPresent()) {
         SkillMetadata summonData = data.deepClone();
         summonData.setEntityTargets(drops);
         this.onDropSkill.get().execute(summonData);
      }
   }

   private UUID getOwner(SkillMetadata data, AbstractEntity target) {
      return this.owner == null ? null : UUID.fromString(target == null ? this.owner.get(data) : this.owner.get(data, target));
   }

   private LootBag getLoot(SkillMetadata data, AbstractEntity target) {
      if (this.dropTable != null) {
         return this.dropTable.generate(new DropMetadataImpl(data.getCaster(), data.getTrigger(), data, null));
      } else {
         String in = target == null ? this.drops.get(data) : this.drops.get(data, target);
         Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(in);
         if (maybeTable.isPresent()) {
            return maybeTable.get().generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
         } else {
            List<String> items = Lists.newArrayList(in.split(","));
            return new DropTable("DropItemMechanic", "DropItemMechanic", items)
               .generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
         }
      }
   }
}
