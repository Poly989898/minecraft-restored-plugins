package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

@MythicCondition(
   author = "Ash",
   name = "trim",
   aliases = {"armortrim"},
   description = "Tests if equipped armor has a specific trim pattern and/or material"
)
public class TrimCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "slot",
      aliases = {"s"},
      description = "The equipment slot to check",
      defValue = "HEAD"
   )
   private final EquipSlot slot;
   @MythicField(
      name = "pattern",
      aliases = {"p"},
      description = "The trim pattern to match (namespace:id). Use ANY or omit to match any pattern."
   )
   private final String pattern;
   @MythicField(
      name = "material",
      aliases = {"mat", "m"},
      description = "The trim material to match (namespace:id). Use ANY or omit to match any material."
   )
   private final String material;

   public TrimCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.slot = EquipSlot.of(mlc.getString(new String[]{"slot", "s"}, "HEAD").toUpperCase());
      String p = mlc.getString(new String[]{"pattern", "p"}, null);
      String mat = mlc.getString(new String[]{"material", "mat", "m"}, null);
      this.pattern = resolveParam(p);
      this.material = resolveParam(mat);
   }

   private static String resolveParam(String value) {
      if (value != null && !value.equalsIgnoreCase("ANY")) {
         return value.contains(":") ? value : "minecraft:" + value;
      } else {
         return null;
      }
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (ServerVersion.isBefore(MinecraftVersions.v1_20)) {
         return false;
      } else {
         MythicLogger.debug(
            MythicLogger.DebugLevel.CONDITION, "Checking TRIM Condition on slot={0}, pattern={1}, material={2}", this.slot, this.pattern, this.material
         );
         if (this.slot == EquipSlot.NONE) {
            MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Invalid slot supplied");
            return false;
         } else {
            AbstractItemStack abstractItem = this.slot.get(entity).orElse(null);
            if (abstractItem == null) {
               return false;
            } else {
               ItemStack bukkit = BukkitAdapter.adapt(abstractItem);
               if (bukkit.getItemMeta() instanceof ArmorMeta armorMeta) {
                  ArmorTrim trim = armorMeta.getTrim();
                  if (trim == null) {
                     return false;
                  } else {
                     if (this.pattern != null) {
                        String key = trim.getPattern().key().asString();
                        if (!key.equalsIgnoreCase(this.pattern)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "- Trim pattern mismatch: expected={0}, found={1}", this.pattern, key);
                           return false;
                        }
                     }

                     if (this.material != null) {
                        String key = trim.getMaterial().key().asString();
                        if (!key.equalsIgnoreCase(this.material)) {
                           MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "- Trim material mismatch: expected={0}, found={1}", this.material, key);
                           return false;
                        }
                     }

                     MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "+ Trim condition passed");
                     return true;
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }
}
