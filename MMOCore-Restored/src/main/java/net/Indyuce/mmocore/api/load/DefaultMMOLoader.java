package net.Indyuce.mmocore.api.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.SkullBlockType;
import net.Indyuce.mmocore.api.block.VanillaBlockType;
import net.Indyuce.mmocore.api.quest.objective.ClickonObjective;
import net.Indyuce.mmocore.api.quest.objective.GoToObjective;
import net.Indyuce.mmocore.api.quest.objective.KillMobObjective;
import net.Indyuce.mmocore.api.quest.objective.MineBlockObjective;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import net.Indyuce.mmocore.api.quest.trigger.BindSkillTrigger;
import net.Indyuce.mmocore.api.quest.trigger.CommandTrigger;
import net.Indyuce.mmocore.api.quest.trigger.ExperienceTrigger;
import net.Indyuce.mmocore.api.quest.trigger.FromTrigger;
import net.Indyuce.mmocore.api.quest.trigger.ItemTrigger;
import net.Indyuce.mmocore.api.quest.trigger.LevelUpSkillTrigger;
import net.Indyuce.mmocore.api.quest.trigger.ManaTrigger;
import net.Indyuce.mmocore.api.quest.trigger.MessageTrigger;
import net.Indyuce.mmocore.api.quest.trigger.SkillModifierTrigger;
import net.Indyuce.mmocore.api.quest.trigger.SoundTrigger;
import net.Indyuce.mmocore.api.quest.trigger.StaminaTrigger;
import net.Indyuce.mmocore.api.quest.trigger.StatTrigger;
import net.Indyuce.mmocore.api.quest.trigger.StelliumTrigger;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.api.quest.trigger.UnlockSkillTrigger;
import net.Indyuce.mmocore.api.quest.trigger.UnlockSlotTrigger;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.BrewPotionExperienceSource;
import net.Indyuce.mmocore.experience.source.ClimbExperienceSource;
import net.Indyuce.mmocore.experience.source.CraftItemExperienceSource;
import net.Indyuce.mmocore.experience.source.DamageDealtExperienceSource;
import net.Indyuce.mmocore.experience.source.DamageTakenExperienceSource;
import net.Indyuce.mmocore.experience.source.EatExperienceSource;
import net.Indyuce.mmocore.experience.source.EnchantItemExperienceSource;
import net.Indyuce.mmocore.experience.source.FishItemExperienceSource;
import net.Indyuce.mmocore.experience.source.FromExperienceSource;
import net.Indyuce.mmocore.experience.source.KillMobExperienceSource;
import net.Indyuce.mmocore.experience.source.MineBlockExperienceSource;
import net.Indyuce.mmocore.experience.source.MoveExperienceSource;
import net.Indyuce.mmocore.experience.source.PlaceBlockExperienceSource;
import net.Indyuce.mmocore.experience.source.PlayExperienceSource;
import net.Indyuce.mmocore.experience.source.ProjectileExperienceSource;
import net.Indyuce.mmocore.experience.source.RepairItemExperienceSource;
import net.Indyuce.mmocore.experience.source.ResourceExperienceSource;
import net.Indyuce.mmocore.experience.source.RideExperienceSource;
import net.Indyuce.mmocore.experience.source.SmeltItemExperienceSource;
import net.Indyuce.mmocore.experience.source.TameExperienceSource;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.loot.chest.condition.BiomeCondition;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.chest.condition.DistanceCondition;
import net.Indyuce.mmocore.loot.chest.condition.FromCondition;
import net.Indyuce.mmocore.loot.chest.condition.LevelCondition;
import net.Indyuce.mmocore.loot.chest.condition.PermissionCondition;
import net.Indyuce.mmocore.loot.chest.condition.TimeCondition;
import net.Indyuce.mmocore.loot.chest.condition.WeatherCondition;
import net.Indyuce.mmocore.loot.chest.condition.WorldCondition;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropTableDropItem;
import net.Indyuce.mmocore.loot.droptable.dropitem.GoldDropItem;
import net.Indyuce.mmocore.loot.droptable.dropitem.NoteDropItem;
import net.Indyuce.mmocore.loot.droptable.dropitem.VanillaDropItem;
import org.bukkit.configuration.ConfigurationSection;

public class DefaultMMOLoader extends MMOLoader {
   @Override
   public Trigger loadTrigger(MMOLineConfig config) {
      if (config.getKey().equals("from")) {
         return new FromTrigger(config);
      } else if (config.getKey().equals("stat")) {
         return new StatTrigger(config);
      } else if (config.getKey().equals("unlock_slot")) {
         return new UnlockSlotTrigger(config);
      } else if (config.getKey().equals("unlock_skill")) {
         return new UnlockSkillTrigger(config);
      } else if (config.getKey().equals("bind_skill")) {
         return new BindSkillTrigger(config);
      } else if (config.getKey().equals("levelup_skill")) {
         return new LevelUpSkillTrigger(config);
      } else if (config.getKey().equals("skill_buff") || config.getKey().equals("skill_modifier")) {
         return new SkillModifierTrigger(config);
      } else if (config.getKey().equals("message")) {
         return new MessageTrigger(config);
      } else if (config.getKey().equals("sound") || config.getKey().equals("playsound")) {
         return new SoundTrigger(config);
      } else if (config.getKey().equals("mana")) {
         return new ManaTrigger(config);
      } else if (config.getKey().equals("stamina")) {
         return new StaminaTrigger(config);
      } else if (config.getKey().equals("stellium")) {
         return new StelliumTrigger(config);
      } else if (config.getKey().equals("command")) {
         return new CommandTrigger(config);
      } else if (config.getKey().equals("item") || config.getKey().equals("vanilla")) {
         return new ItemTrigger(config);
      } else {
         return !config.getKey().equals("exp") && !config.getKey().equals("experience") ? null : new ExperienceTrigger(config);
      }
   }

   @Override
   public DropItem loadDropItem(MMOLineConfig config) {
      if (config.getKey().equals("droptable")) {
         return new DropTableDropItem(config);
      } else if (config.getKey().equals("vanilla")) {
         return new VanillaDropItem(config);
      } else if (config.getKey().equals("note")) {
         return new NoteDropItem(config);
      } else {
         return !config.getKey().equals("gold") && !config.getKey().equals("coin") ? null : new GoldDropItem(config);
      }
   }

   @Override
   public Objective loadObjective(MMOLineConfig config, ConfigurationSection section) {
      if (config.getKey().equals("goto")) {
         return new GoToObjective(section, config);
      } else if (config.getKey().equals("mineblock")) {
         return new MineBlockObjective(section, config);
      } else if (config.getKey().equals("killmob")) {
         return new KillMobObjective(section, config);
      } else {
         return config.getKey().equals("clickon") ? new ClickonObjective(section, config) : null;
      }
   }

   @Override
   public Condition loadCondition(MMOLineConfig config) {
      if (config.getKey().equals("from")) {
         return new FromCondition(config);
      } else if (config.getKey().equals("distance")) {
         return new DistanceCondition(config);
      } else if (config.getKey().equals("world")) {
         return new WorldCondition(config);
      } else if (config.getKey().equals("biome")) {
         return new BiomeCondition(config);
      } else if (config.getKey().equals("level")) {
         return new LevelCondition(config);
      } else if (config.getKey().equals("permission")) {
         return new PermissionCondition(config);
      } else if (config.getKey().equals("weather")) {
         return new WeatherCondition(config);
      } else {
         return config.getKey().equals("time") ? new TimeCondition(config) : null;
      }
   }

   @Override
   public ExperienceSource<?> loadExperienceSource(MMOLineConfig config, ExperienceDispenser dispenser) {
      if (config.getKey().equals("from")) {
         return new FromExperienceSource(dispenser, config);
      } else if (config.getKey().equals("resource")) {
         return new ResourceExperienceSource(dispenser, config);
      } else if (config.getKey().equals("climb")) {
         return new ClimbExperienceSource(dispenser, config);
      } else if (config.getKey().equals("eat")) {
         return new EatExperienceSource(dispenser, config);
      } else if (config.getKey().equals("damagedealt")) {
         return new DamageDealtExperienceSource(dispenser, config);
      } else if (config.getKey().equals("damagetaken")) {
         return new DamageTakenExperienceSource(dispenser, config);
      } else if (config.getKey().equals("move")) {
         return new MoveExperienceSource(dispenser, config);
      } else if (config.getKey().equals("play")) {
         return new PlayExperienceSource(dispenser, config);
      } else if (config.getKey().equals("projectile")) {
         return new ProjectileExperienceSource(dispenser, config);
      } else if (config.getKey().equals("ride")) {
         return new RideExperienceSource(dispenser, config);
      } else if (config.getKey().equals("tame")) {
         return new TameExperienceSource(dispenser, config);
      } else if (config.getKey().equals("killmob")) {
         return new KillMobExperienceSource(dispenser, config);
      } else if (config.getKey().equals("mineblock")) {
         return new MineBlockExperienceSource(dispenser, config);
      } else if (config.getKey().equals("placeblock")) {
         return new PlaceBlockExperienceSource(dispenser, config);
      } else if (config.getKey().equals("brewpotion")) {
         return new BrewPotionExperienceSource(dispenser, config);
      } else if (config.getKey().equals("smeltitem")) {
         return new SmeltItemExperienceSource(dispenser, config);
      } else if (config.getKey().equals("enchantitem")) {
         return new EnchantItemExperienceSource(dispenser, config);
      } else if (config.getKey().equals("repairitem")) {
         return new RepairItemExperienceSource(dispenser, config);
      } else if (config.getKey().equals("craftitem")) {
         return new CraftItemExperienceSource(dispenser, config);
      } else {
         return config.getKey().equals("fishitem") ? new FishItemExperienceSource(dispenser, config) : null;
      }
   }

   @Override
   public BlockType loadBlockType(MMOLineConfig config) {
      if (config.getKey().equalsIgnoreCase("vanilla")) {
         return new VanillaBlockType(config);
      } else {
         return !config.getKey().equalsIgnoreCase("skull") && !config.getKey().equals("head") && !config.getKey().equals("playerhead")
            ? null
            : new SkullBlockType(config);
      }
   }
}
