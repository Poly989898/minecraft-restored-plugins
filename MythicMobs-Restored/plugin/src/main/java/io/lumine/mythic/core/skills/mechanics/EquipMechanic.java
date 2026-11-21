package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.variables.MetaVariableMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "equip",
   description = "Causes the caster to equip an item or droptable"
)
public class EquipMechanic extends MetaVariableMechanic implements INoTargetSkill {
   private final PlaceholderString equipString;
   @MythicField(
      name = "items",
      aliases = {"item", "i", "equipment", "equip", "e"},
      description = "The items/droptable to equip"
   )
   private DropTable equipmentTable;

   public EquipMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.equipString = mlc.getPlaceholderString(new String[]{"items", "item", "i", "equipment", "equip", "e"}, null);
      if (this.equipString.isStatic()) {
         this.getPlugin().getDropManager().queueSecondPass(() -> {
            String equipString = this.equipString.get();
            Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(equipString);
            if (maybeTable.isPresent()) {
               this.equipmentTable = maybeTable.get();
            } else {
               if (equipString.startsWith("\"")) {
                  try {
                     equipString = equipString.substring(1, equipString.length() - 1);
                  } catch (Exception e) {
                     MythicLogger.errorMechanicConfig(this, mlc, "The 'items' attribute is required.");
                  }

                  equipString = SkillString.parseMessageSpecialChars(equipString);
               }

               List<String> items = new ArrayList<>(Arrays.asList(equipString.split(",")));
               this.equipmentTable = new DropTable("EquipMechanic", "EquipMechanic", items);
            }
         });
      } else {
         this.equipmentTable = null;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      this.getLoot(data).equip(data.getCaster().getEntity());
      if (data.getCaster() instanceof ActiveMob am) {
         am.checkEquipment();
         am.getType().reapplyAIGoalSelectors(am);
      }

      return SkillResult.SUCCESS;
   }

   private LootBag getLoot(SkillMetadata data) {
      if (this.equipmentTable != null) {
         return this.equipmentTable.generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
      } else {
         String in = this.equipString.get(data);
         Optional<DropTable> maybeTable = this.getPlugin().getDropManager().getDropTable(in);
         if (maybeTable.isPresent()) {
            return maybeTable.get().generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
         } else {
            List<String> items = Lists.newArrayList(in.split(","));
            return new DropTable("EquipMechanic", "EquipMechanic", items)
               .generate(new DropMetadataImpl(data.getCaster(), data.getCaster().getEntity(), data, null));
         }
      }
   }
}
