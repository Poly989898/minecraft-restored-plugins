package net.Indyuce.mmoitems.comp.mmocore;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Optional;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.load.MMOLoader;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.comp.mmocore.crafting.AttributeCondition;
import net.Indyuce.mmoitems.comp.mmocore.crafting.ExperienceCraftingTrigger;
import net.Indyuce.mmoitems.comp.mmocore.crafting.ProfessionCondition;
import net.Indyuce.mmoitems.comp.mmocore.crafting.StelliumCondition;
import net.Indyuce.mmoitems.comp.mmocore.load.GetMMOItemObjective;
import net.Indyuce.mmoitems.comp.mmocore.load.ItemTemplateDropItem;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemTrigger;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemsBlockType;
import net.Indyuce.mmoitems.comp.mmocore.load.MineMIBlockExperienceSource;
import net.Indyuce.mmoitems.comp.mmocore.load.RandomItemDropItem;
import net.Indyuce.mmoitems.comp.mmocore.load.SmeltMMOItemExperienceSource;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MMOCoreMMOLoader extends MMOLoader {
   private static final ItemStat HEALTH_REGENERATION = new DoubleStat(
      "HEALTH_REGENERATION", Material.BREAD, "Health Regeneration", new String[]{"Amount of health pts regenerated every second."}
   );
   private static final ItemStat MAX_HEALTH_REGENERATION = new DoubleStat(
      "MAX_HEALTH_REGENERATION", Material.BREAD, "Max Health Regeneration", new String[]{"Percentage of max health regenerated every second."}
   );
   private static final ItemStat MANA_REGENERATION = new DoubleStat(
      "MANA_REGENERATION", Material.LAPIS_LAZULI, "Mana Regeneration", new String[]{"Amount of mana pts regenerated every second."}
   );
   private static final ItemStat MAX_MANA_REGENERATION = new DoubleStat(
      "MAX_MANA_REGENERATION", Material.LAPIS_LAZULI, "Max Mana Regeneration", new String[]{"Percentage of max mana regenerated every second."}
   );
   private static final ItemStat STAMINA_REGENERATION = new DoubleStat(
      "STAMINA_REGENERATION", Material.LIGHT_BLUE_DYE, "Stamina Regeneration", new String[]{"Amount of stamina pts regenerated every second."}
   );
   private static final ItemStat MAX_STAMINA_REGENERATION = new DoubleStat(
      "MAX_STAMINA_REGENERATION", Material.LIGHT_BLUE_DYE, "Max Stamina Regeneration", new String[]{"Percentage of max stamina regenerated every second."}
   );
   private static final ItemStat MAX_STAMINA = new DoubleStat(
      "MAX_STAMINA", Material.LIGHT_BLUE_DYE, "Max Stamina", new String[]{"Adds stamina to your max stamina bar."}
   );
   private static final ItemStat MAX_STELLIUM = new DoubleStat("MAX_STELLIUM", Material.ENDER_EYE, "Max Stellium", new String[]{"Additional maximum stellium."});
   private static final ItemStat ADDITIONAL_EXPERIENCE = new DoubleStat(
      "ADDITIONAL_EXPERIENCE", Material.EXPERIENCE_BOTTLE, "Additional Experience", new String[]{"Additional MMOCore main class experience in %."}
   );

   public MMOCoreMMOLoader() {
      MMOCore.plugin.loadManager.registerLoader(this);
      MMOCore.plugin.mineManager.registerBlockType(var0 -> {
         Optional<CustomBlock> var1 = MMOItems.plugin.getCustomBlocks().getFromBlock(var0.getBlockData());
         return var1.<BlockType>map(MMOItemsBlockType::new);
      });
      MMOItems.plugin.getStats().register(HEALTH_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_HEALTH_REGENERATION);
      MMOItems.plugin.getStats().register(MANA_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_MANA_REGENERATION);
      MMOItems.plugin.getStats().register(STAMINA_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_STAMINA_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_STAMINA);
      MMOItems.plugin.getStats().register(MAX_STELLIUM);
      MMOItems.plugin.getStats().register(ADDITIONAL_EXPERIENCE);
      MMOItems.plugin
         .getCrafting()
         .registerCondition(
            "profession", ProfessionCondition::new, new ConditionalDisplay("&a✔ Requires #level# in #profession#", "&c✖ Requires #level# in #profession#")
         );
      MMOItems.plugin
         .getCrafting()
         .registerCondition("stellium", StelliumCondition::new, new ConditionalDisplay("&a✔ Requires #stellium# Stellium", "&c✖ Requires #stellium# Stellium"));
      MMOItems.plugin
         .getCrafting()
         .registerCondition(
            "attribute", AttributeCondition::new, new ConditionalDisplay("&a✔ Requires #points# #attribute#", "&c✖ Requires #points# #attribute#")
         );
      MMOItems.plugin.getCrafting().registerTrigger("exp", ExperienceCraftingTrigger::new);
   }

   public Condition loadCondition(MMOLineConfig var1) {
      return null;
   }

   public Trigger loadTrigger(MMOLineConfig var1) {
      return var1.getKey().equals("mmoitem") ? new MMOItemTrigger(var1) : null;
   }

   public DropItem loadDropItem(MMOLineConfig var1) {
      if (var1.getKey().equals("mmoitem") || var1.getKey().equals("mmoitemtemplate")) {
         return new ItemTemplateDropItem(var1);
      } else {
         return var1.getKey().equals("miloot") ? new RandomItemDropItem(var1) : null;
      }
   }

   public Objective loadObjective(MMOLineConfig var1, ConfigurationSection var2) {
      return var1.getKey().equals("getmmoitem") ? new GetMMOItemObjective(var2, var1) : null;
   }

   public ExperienceSource<?> loadExperienceSource(MMOLineConfig var1, ExperienceDispenser var2) {
      if (var1.getKey().equals("minemiblock")) {
         return new MineMIBlockExperienceSource(var2, var1);
      } else {
         return var1.getKey().equalsIgnoreCase("smeltmmoitem") ? new SmeltMMOItemExperienceSource(var2, var1) : null;
      }
   }

   public BlockType loadBlockType(MMOLineConfig var1) {
      return !var1.getKey().equalsIgnoreCase("miblock")
            && !var1.getKey().equals("mmoitemsblock")
            && !var1.getKey().equals("mmoitem")
            && !var1.getKey().equals("mmoitems")
         ? null
         : new MMOItemsBlockType(var1);
   }
}
