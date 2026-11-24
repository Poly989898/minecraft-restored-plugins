package net.Indyuce.mmoitems;

import io.lumine.mythic.lib.version.VMaterial;
import net.Indyuce.mmoitems.stat.Abilities;
import net.Indyuce.mmoitems.stat.ActionLeftClick;
import net.Indyuce.mmoitems.stat.ActionRightClick;
import net.Indyuce.mmoitems.stat.Amphibian;
import net.Indyuce.mmoitems.stat.Armor;
import net.Indyuce.mmoitems.stat.ArmorToughness;
import net.Indyuce.mmoitems.stat.ArrowParticles;
import net.Indyuce.mmoitems.stat.ArrowPotionEffects;
import net.Indyuce.mmoitems.stat.AttackDamage;
import net.Indyuce.mmoitems.stat.AttackSpeed;
import net.Indyuce.mmoitems.stat.BlockBreakSpeed;
import net.Indyuce.mmoitems.stat.BlockInteractionRange;
import net.Indyuce.mmoitems.stat.BrowserDisplayIDX;
import net.Indyuce.mmoitems.stat.BurningTime;
import net.Indyuce.mmoitems.stat.CanBreak;
import net.Indyuce.mmoitems.stat.CanDeconstruct;
import net.Indyuce.mmoitems.stat.CanDeskin;
import net.Indyuce.mmoitems.stat.CanIdentify;
import net.Indyuce.mmoitems.stat.Commands;
import net.Indyuce.mmoitems.stat.CompatibleIds;
import net.Indyuce.mmoitems.stat.CompatibleMaterials;
import net.Indyuce.mmoitems.stat.CompatibleTypes;
import net.Indyuce.mmoitems.stat.Crafting;
import net.Indyuce.mmoitems.stat.CraftingPermission;
import net.Indyuce.mmoitems.stat.CustomDurability;
import net.Indyuce.mmoitems.stat.CustomModelData;
import net.Indyuce.mmoitems.stat.CustomSounds;
import net.Indyuce.mmoitems.stat.DisableDeathDrop;
import net.Indyuce.mmoitems.stat.DisplayName;
import net.Indyuce.mmoitems.stat.DisplayedType;
import net.Indyuce.mmoitems.stat.DyeColor;
import net.Indyuce.mmoitems.stat.Effects;
import net.Indyuce.mmoitems.stat.Elements;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.EntityInteractionRange;
import net.Indyuce.mmoitems.stat.ExplosionKnockbackResistance;
import net.Indyuce.mmoitems.stat.FallDamageMultiplier;
import net.Indyuce.mmoitems.stat.GemColor;
import net.Indyuce.mmoitems.stat.GemSockets;
import net.Indyuce.mmoitems.stat.GemUpgradeScaling;
import net.Indyuce.mmoitems.stat.GrantedPermissions;
import net.Indyuce.mmoitems.stat.Gravity;
import net.Indyuce.mmoitems.stat.HideDurabilityBar;
import net.Indyuce.mmoitems.stat.HideDye;
import net.Indyuce.mmoitems.stat.HideEnchants;
import net.Indyuce.mmoitems.stat.HidePotionEffects;
import net.Indyuce.mmoitems.stat.HideTrim;
import net.Indyuce.mmoitems.stat.ItemDamage;
import net.Indyuce.mmoitems.stat.ItemLevel;
import net.Indyuce.mmoitems.stat.ItemParticles;
import net.Indyuce.mmoitems.stat.ItemSetStat;
import net.Indyuce.mmoitems.stat.ItemTierStat;
import net.Indyuce.mmoitems.stat.ItemTypeRestriction;
import net.Indyuce.mmoitems.stat.JumpStrength;
import net.Indyuce.mmoitems.stat.KnockbackResistance;
import net.Indyuce.mmoitems.stat.Lore;
import net.Indyuce.mmoitems.stat.LoreFormat;
import net.Indyuce.mmoitems.stat.LostWhenBroken;
import net.Indyuce.mmoitems.stat.LuteAttackEffectStat;
import net.Indyuce.mmoitems.stat.LuteAttackSoundStat;
import net.Indyuce.mmoitems.stat.ManaCost;
import net.Indyuce.mmoitems.stat.MaterialStat;
import net.Indyuce.mmoitems.stat.MaxAbsorption;
import net.Indyuce.mmoitems.stat.MaxConsume;
import net.Indyuce.mmoitems.stat.MaxHealth;
import net.Indyuce.mmoitems.stat.MaxItemDamage;
import net.Indyuce.mmoitems.stat.MaxStackSize;
import net.Indyuce.mmoitems.stat.MaximumDurability;
import net.Indyuce.mmoitems.stat.MiningEfficiency;
import net.Indyuce.mmoitems.stat.MovementEfficiency;
import net.Indyuce.mmoitems.stat.MovementSpeed;
import net.Indyuce.mmoitems.stat.NBTTags;
import net.Indyuce.mmoitems.stat.OxygenBonus;
import net.Indyuce.mmoitems.stat.PermanentEffects;
import net.Indyuce.mmoitems.stat.Permission;
import net.Indyuce.mmoitems.stat.PickaxePower;
import net.Indyuce.mmoitems.stat.PotionColor;
import net.Indyuce.mmoitems.stat.PotionEffects;
import net.Indyuce.mmoitems.stat.ProjectileParticles;
import net.Indyuce.mmoitems.stat.RandomUnsocket;
import net.Indyuce.mmoitems.stat.RepairPower;
import net.Indyuce.mmoitems.stat.RepairPowerPercent;
import net.Indyuce.mmoitems.stat.RepairReference;
import net.Indyuce.mmoitems.stat.RequiredBiomes;
import net.Indyuce.mmoitems.stat.RequiredClass;
import net.Indyuce.mmoitems.stat.RequiredLevel;
import net.Indyuce.mmoitems.stat.RestoreFood;
import net.Indyuce.mmoitems.stat.RestoreHealth;
import net.Indyuce.mmoitems.stat.RestoreMana;
import net.Indyuce.mmoitems.stat.RestoreSaturation;
import net.Indyuce.mmoitems.stat.RestoreStamina;
import net.Indyuce.mmoitems.stat.RevisionID;
import net.Indyuce.mmoitems.stat.SafeFallDistance;
import net.Indyuce.mmoitems.stat.Scale;
import net.Indyuce.mmoitems.stat.ShieldPatternStat;
import net.Indyuce.mmoitems.stat.SkullTextureStat;
import net.Indyuce.mmoitems.stat.SneakingSpeed;
import net.Indyuce.mmoitems.stat.SoulbindingBreakChance;
import net.Indyuce.mmoitems.stat.SoulbindingChance;
import net.Indyuce.mmoitems.stat.Soulbound;
import net.Indyuce.mmoitems.stat.SoulboundLevel;
import net.Indyuce.mmoitems.stat.StepHeight;
import net.Indyuce.mmoitems.stat.StoredTags;
import net.Indyuce.mmoitems.stat.SubmergedMiningSpeed;
import net.Indyuce.mmoitems.stat.SuccessRate;
import net.Indyuce.mmoitems.stat.SweepingDamageRatio;
import net.Indyuce.mmoitems.stat.TooltipStat;
import net.Indyuce.mmoitems.stat.TrimMaterialStat;
import net.Indyuce.mmoitems.stat.TrimPatternStat;
import net.Indyuce.mmoitems.stat.Unbreakable;
import net.Indyuce.mmoitems.stat.Unstackable;
import net.Indyuce.mmoitems.stat.UpgradeStat;
import net.Indyuce.mmoitems.stat.VanillaEatingAnimation;
import net.Indyuce.mmoitems.stat.WaterMovementEfficiency;
import net.Indyuce.mmoitems.stat.annotation.DeprecatedStat;
import net.Indyuce.mmoitems.stat.block.BlockID;
import net.Indyuce.mmoitems.stat.block.GenTemplate;
import net.Indyuce.mmoitems.stat.block.MaxXP;
import net.Indyuce.mmoitems.stat.block.MinXP;
import net.Indyuce.mmoitems.stat.block.RequirePowerToBreak;
import net.Indyuce.mmoitems.stat.block.RequiredPower;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.DisableStat;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.bukkit.Material;

public class ItemStats {
   public static final ItemStat REVISION_ID = new RevisionID();
   public static final ItemStat MATERIAL = new MaterialStat();
   public static final ItemStat SKULL_TEXTURE = new SkullTextureStat();
   public static final ItemStat ITEM_DAMAGE = new ItemDamage();
   public static final ItemStat CUSTOM_MODEL_DATA = new CustomModelData();
   public static final ItemStat MAX_DURABILITY = new MaximumDurability();
   public static final ItemStat MAX_ITEM_DAMAGE = new MaxItemDamage();
   public static final ItemStat WILL_BREAK = new LostWhenBroken();
   public static final ItemStat NAME = new DisplayName();
   public static final ItemStat LORE = new Lore();
   public static final ItemStat NBT_TAGS = new NBTTags();
   public static final ItemStat MAX_STACK_SIZE = new MaxStackSize();
   public static final ItemStat CAN_BREAK = new CanBreak();
   public static final ItemStat LORE_FORMAT = new LoreFormat();
   public static final ItemStat TOOLTIP = new TooltipStat();
   public static final ItemStat BLOCK_ID = new BlockID();
   public static final ItemStat REQUIRED_POWER = new RequiredPower();
   public static final ItemStat REQUIRE_POWER_TO_BREAK = new RequirePowerToBreak();
   public static final ItemStat MIN_XP = new MinXP();
   public static final ItemStat MAX_XP = new MaxXP();
   public static final ItemStat GEN_TEMPLATE = new GenTemplate();
   public static final ItemStat DISPLAYED_TYPE = new DisplayedType();
   public static final ItemStat ENCHANTS = new Enchants();
   public static final ItemStat HIDE_ENCHANTS = new HideEnchants();
   public static final ItemStat PERMISSION = new Permission();
   public static final ItemStat ITEM_PARTICLES = new ItemParticles();
   public static final ItemStat ARROW_PARTICLES = new ArrowParticles();
   public static final ItemStat PROJECTILE_PARTICLES = new ProjectileParticles();
   public static final ItemStat DISABLE_INTERACTION = new DisableStat(
      "INTERACTION",
      VMaterial.GRASS_BLOCK.get(),
      "Disable Interaction",
      new String[]{"!block", "all"},
      "Disable any unwanted interaction:",
      "block placement, item use..."
   );
   public static final ItemStat DISABLE_CRAFTING = new DisableStat(
      "CRAFTING", Material.CRAFTING_TABLE, "Disable Crafting", "Players can't use this item while crafting."
   );
   public static final ItemStat DISABLE_SMELTING = new DisableStat("SMELTING", Material.FURNACE, "Disable Smelting", "Players can't use this item in furnaces.");
   public static final ItemStat DISABLE_SMITHING = new DisableStat(
      "SMITHING", Material.DAMAGED_ANVIL, "Disable Smithing", "Players can't smith this item in smithing tables."
   );
   public static final ItemStat DISABLE_ENCHANTING = new DisableStat(
      "ENCHANTING", Material.ENCHANTING_TABLE, "Disable Enchanting", new String[]{"!block", "all"}, "Players can't enchant this item."
   );
   public static final ItemStat DISABLE_REPAIRING = new DisableStat(
      "REPAIRING", Material.ANVIL, "Disable Repairing", new String[]{"!block", "all"}, "Players can't use this item in anvils."
   );
   public static final ItemStat DISABLE_ARROW_SHOOTING = new DisableStat(
      "ARROW_SHOOTING", Material.ARROW, "Disable Arrow Shooting", new Material[]{Material.ARROW}, "Players can't shoot this", "item using a bow."
   );
   public static final ItemStat DISABLE_DROP = new DisableStat(
      "DROPING", Material.LAVA_BUCKET, "Disable Item Dropping", new String[0], "Disables the dropping of this item!"
   );
   public static final ItemStat DISABLE_ARROW_CONSUMPTION = new DisableStat(
      "ARROW_CONSUMPTION", Material.ARROW, "Disable Arrow Consumption", new String[]{"crossbow"}, "Disable arrow requirement and consumption."
   );
   public static final ItemStat LEFT_CLICK_SCRIPT = new ActionLeftClick();
   public static final ItemStat RIGHT_CLICK_SCRIPT = new ActionRightClick();
   public static final ItemStat DISABLE_ATTACK_PASSIVE = new DisableStat(
      "ATTACK_PASSIVE", Material.BARRIER, "Disable On-Hit Effect", new String[]{"weapon"}, "Disables on-hit effects granted by the item type."
   );
   public static final ItemStat REQUIRED_LEVEL = new RequiredLevel();
   public static final ItemStat REQUIRED_CLASS = new RequiredClass();
   public static final ItemStat ATTACK_DAMAGE = new AttackDamage();
   public static final ItemStat ATTACK_SPEED = new AttackSpeed();
   public static final ItemStat CRITICAL_STRIKE_CHANCE = new DoubleStat(
      "CRITICAL_STRIKE_CHANCE",
      Material.NETHER_STAR,
      "Critical Strike Chance",
      new String[]{"Critical Strikes deal more damage.", "In % chance."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat CRITICAL_STRIKE_POWER = new DoubleStat(
      "CRITICAL_STRIKE_POWER",
      Material.NETHER_STAR,
      "Critical Strike Power",
      new String[]{"The extra damage weapon crits deals.", "(Stacks with default value)", "In %."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat SKILL_CRITICAL_STRIKE_CHANCE = new DoubleStat(
      "SKILL_CRITICAL_STRIKE_CHANCE",
      Material.NETHER_STAR,
      "Skill Critical Strike Chance",
      new String[]{"Increases the chance of dealing skill crits (in %)."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat SKILL_CRITICAL_STRIKE_POWER = new DoubleStat(
      "SKILL_CRITICAL_STRIKE_POWER",
      Material.NETHER_STAR,
      "Skill Critical Strike Power",
      new String[]{"Extra damage dealt (in %) by skill crits.", "(Stacks with default value)", "In %."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat BLOCK_POWER = new DoubleStat(
      "BLOCK_POWER",
      Material.IRON_HELMET,
      "Block Power",
      new String[]{"The % of the damage your", "armor/shield can block.", "Default: 25%"},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat BLOCK_RATING = new DoubleStat(
      "BLOCK_RATING",
      Material.IRON_HELMET,
      "Block Rating",
      new String[]{"The chance your piece of armor", "has to block any entity attack."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat BLOCK_COOLDOWN_REDUCTION = new DoubleStat(
      "BLOCK_COOLDOWN_REDUCTION",
      Material.IRON_HELMET,
      "Block Cooldown Reduction",
      new String[]{"Reduces the blocking cooldown (%)."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat DODGE_RATING = new DoubleStat(
      "DODGE_RATING",
      Material.FEATHER,
      "Dodge Rating",
      new String[]{"The chance to dodge an attack.", "Dodging completely negates", "the attack damage."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat DODGE_COOLDOWN_REDUCTION = new DoubleStat(
      "DODGE_COOLDOWN_REDUCTION",
      Material.FEATHER,
      "Dodge Cooldown Reduction",
      new String[]{"Reduces the dodging cooldown (%)."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat PARRY_RATING = new DoubleStat(
      "PARRY_RATING",
      Material.BUCKET,
      "Parry Rating",
      new String[]{"The chance to parry an attack.", "Parrying negates the damage", "and knocks the attacker back."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat PARRY_COOLDOWN_REDUCTION = new DoubleStat(
      "PARRY_COOLDOWN_REDUCTION",
      Material.BUCKET,
      "Parry Cooldown Reduction",
      new String[]{"Reduces the parrying cooldown (%)."},
      new String[]{"!miscellaneous", "!block", "all"}
   );
   public static final ItemStat COOLDOWN_REDUCTION = new DoubleStat(
      "COOLDOWN_REDUCTION", Material.BOOK, "Cooldown Reduction", new String[]{"Reduces cooldowns of item and player skills (%)."}
   );
   public static final ItemStat RANGE = new DoubleStat(
      "RANGE", Material.STICK, "Range", new String[]{"The range of your item attacks."}, new String[]{"staff", "whip", "wand", "musket", "gem_stone"}
   );
   public static final ItemStat MANA_COST = new ManaCost();
   public static final ItemStat STAMINA_COST = new DoubleStat(
      "STAMINA_COST", Material.LIGHT_GRAY_DYE, "Stamina Cost", new String[]{"Stamina spent by your weapon to be used."}, new String[]{"weapon"}
   );
   public static final ItemStat ARROW_VELOCITY = new DoubleStat(
      "ARROW_VELOCITY",
      Material.ARROW,
      "Arrow Velocity",
      new String[]{"Determines how far your", "weapon can shoot.", "Default: 1.0"},
      new String[]{"gem_stone", "bow", "crossbow"}
   );
   public static final ItemStat ARROW_POTION_EFFECTS = new ArrowPotionEffects();
   public static final ItemStat PVE_DAMAGE = new DoubleStat(
      "PVE_DAMAGE",
      Material.PORKCHOP,
      "PvE Damage",
      new String[]{"Additional damage against", "non human entities in %."},
      new String[]{"equipment", "gem_stone"}
   );
   public static final ItemStat PVP_DAMAGE = new DoubleStat(
      "PVP_DAMAGE", Material.SKELETON_SKULL, "PvP Damage", new String[]{"Additional damage", "against players in %."}, new String[]{"equipment", "gem_stone"}
   );
   public static final ItemStat BLUNT_POWER = new DoubleStat(
      "BLUNT_POWER",
      Material.IRON_AXE,
      "Blunt Power",
      new String[]{
         "The radius of the AoE attack.",
         "If set to 2.0, enemies within 2 blocks",
         "around your target will take damage.",
         "&9This stat only applies to Blunt weapons."
      },
      new String[]{"weapon", "gem_stone"}
   );
   public static final ItemStat BLUNT_RATING = new DoubleStat(
      "BLUNT_RATING",
      Material.BRICK,
      "Blunt Rating",
      new String[]{
         "The force of the blunt attack.",
         "If set to 50%, enemies hit by the attack",
         "will take 50% of the initial damage.",
         "&9This stat only applies to Blunt weapons."
      },
      new String[]{"weapon", "gem_stone"}
   );
   public static final ItemStat WEAPON_DAMAGE = new DoubleStat(
      "WEAPON_DAMAGE", Material.IRON_SWORD, "Weapon Damage", new String[]{"Additional on-hit weapon damage in %."}
   );
   public static final ItemStat SKILL_DAMAGE = new DoubleStat("SKILL_DAMAGE", Material.BOOK, "Skill Damage", new String[]{"Additional ability damage in %."});
   public static final ItemStat PROJECTILE_DAMAGE = new DoubleStat(
      "PROJECTILE_DAMAGE", Material.ARROW, "Projectile Damage", new String[]{"Additional skill/weapon projectile damage."}
   );
   public static final ItemStat MAGIC_DAMAGE = new DoubleStat(
      "MAGIC_DAMAGE", Material.MAGMA_CREAM, "Magic Damage", new String[]{"Additional magic skill damage in %."}
   );
   public static final ItemStat PHYSICAL_DAMAGE = new DoubleStat(
      "PHYSICAL_DAMAGE", Material.IRON_AXE, "Physical Damage", new String[]{"Additional skill/weapon physical damage."}
   );
   public static final ItemStat DEFENSE = new DoubleStat(
      "DEFENSE", Material.SHIELD, "Defense", new String[]{"Reduces damage from any source.", "Formula can be set in MMOLib Config."}
   );
   public static final ItemStat DAMAGE_REDUCTION = new DoubleStat(
      "DAMAGE_REDUCTION", Material.IRON_CHESTPLATE, "Damage Reduction", new String[]{"Reduces damage from any source.", "In %."}
   );
   public static final ItemStat FALL_DAMAGE_REDUCTION = new DoubleStat(
      "FALL_DAMAGE_REDUCTION", Material.FEATHER, "Fall Damage Reduction", new String[]{"Reduces fall damage.", "In %."}
   );
   public static final ItemStat PROJECTILE_DAMAGE_REDUCTION = new DoubleStat(
      "PROJECTILE_DAMAGE_REDUCTION", Material.SNOWBALL, "Projectile Damage Reduction", new String[]{"Reduces projectile damage.", "In %."}
   );
   public static final ItemStat PHYSICAL_DAMAGE_REDUCTION = new DoubleStat(
      "PHYSICAL_DAMAGE_REDUCTION", Material.LEATHER_CHESTPLATE, "Physical Damage Reduction", new String[]{"Reduces physical damage.", "In %."}
   );
   public static final ItemStat FIRE_DAMAGE_REDUCTION = new DoubleStat(
      "FIRE_DAMAGE_REDUCTION", Material.BLAZE_POWDER, "Fire Damage Reduction", new String[]{"Reduces fire damage.", "In %."}
   );
   public static final ItemStat MAGIC_DAMAGE_REDUCTION = new DoubleStat(
      "MAGIC_DAMAGE_REDUCTION", Material.POTION, "Magic Damage Reduction", new String[]{"Reduce magic damage dealt by potions.", "In %."}
   );
   public static final ItemStat PVE_DAMAGE_REDUCTION = new DoubleStat(
      "PVE_DAMAGE_REDUCTION", Material.PORKCHOP, "PvE Damage Reduction", new String[]{"Reduces damage dealt by mobs.", "In %."}
   );
   public static final ItemStat PVP_DAMAGE_REDUCTION = new DoubleStat(
      "PVP_DAMAGE_REDUCTION", Material.SKELETON_SKULL, "PvP Damage Reduction", new String[]{"Reduces damage dealt by players", "In %."}
   );
   public static final ItemStat UNDEAD_DAMAGE = new DoubleStat(
      "UNDEAD_DAMAGE", Material.SKELETON_SKULL, "Undead Damage", new String[]{"Deals additional damage to undead.", "In %."}
   );
   public static final ItemStat LIFESTEAL = new DoubleStat(
      "LIFESTEAL", Material.REDSTONE, "Lifesteal", new String[]{"Percentage of damage you gain back as", "health when inflicting weapon damage."}
   );
   public static final ItemStat SPELL_VAMPIRISM = new DoubleStat(
      "SPELL_VAMPIRISM", Material.REDSTONE, "Spell Vampirism", new String[]{"Percentage of damage you gain back as", "health when inflicting skill damage."}
   );
   public static final ItemStat UNBREAKABLE = new Unbreakable();
   public static final ItemStat TIER = new ItemTierStat();
   public static final ItemStat SET = new ItemSetStat();
   public static final ItemStat ARMOR = new Armor();
   public static final ItemStat ARMOR_TOUGHNESS = new ArmorToughness();
   public static final ItemStat MAX_HEALTH = new MaxHealth();
   public static final ItemStat UNSTACKABLE = new Unstackable();
   public static final ItemStat MAX_MANA = new DoubleStat("MAX_MANA", Material.LAPIS_LAZULI, "Max Mana", new String[]{"Adds mana to your max mana bar."});
   public static final ItemStat KNOCKBACK_RESISTANCE = new KnockbackResistance();
   public static final ItemStat MOVEMENT_SPEED = new MovementSpeed();
   public static final ItemStat TWO_HANDED = new BooleanStat(
      "TWO_HANDED",
      Material.IRON_INGOT,
      "Two Handed",
      new String[]{"If set to true, a player will be", "significantly slower if holding two", "items, one being Two Handed."},
      new String[]{"handheld"}
   );
   public static final ItemStat REQUIRED_BIOMES = new RequiredBiomes();
   public static final ItemStat DROP_ON_DEATH = new DisableDeathDrop();
   public static final ItemStat HIDE_DURABILITY_BAR = new HideDurabilityBar();
   public static final ItemStat MAX_ABSORPTION = new MaxAbsorption();
   public static final ItemStat BLOCK_BREAK_SPEED = new BlockBreakSpeed();
   public static final ItemStat BLOCK_INTERACTION_RANGE = new BlockInteractionRange();
   public static final ItemStat ENTITY_INTERACTION_RANGE = new EntityInteractionRange();
   public static final ItemStat FALL_DAMAGE_MULTIPLIER = new FallDamageMultiplier();
   public static final ItemStat GRAVITY = new Gravity();
   public static final ItemStat JUMP_STRENGTH = new JumpStrength();
   public static final ItemStat SAFE_FALL_DISTANCE = new SafeFallDistance();
   public static final ItemStat SCALE = new Scale();
   public static final ItemStat STEP_HEIGHT = new StepHeight();
   public static final ItemStat BURNING_TIME = new BurningTime();
   public static final ItemStat EXPLOSION_KNOCKBACK_RESISTANCE = new ExplosionKnockbackResistance();
   public static final ItemStat MINING_EFFICIENCY = new MiningEfficiency();
   public static final ItemStat MOVEMENT_EFFICIENCY = new MovementEfficiency();
   public static final ItemStat OXYGEN_BONUS = new OxygenBonus();
   public static final ItemStat SNEAKING_SPEED = new SneakingSpeed();
   public static final ItemStat SUBMERGED_MINING_SPEED = new SubmergedMiningSpeed();
   public static final ItemStat SWEEPING_DAMAGE_RATIO = new SweepingDamageRatio();
   public static final ItemStat WATER_MOVEMENT_EFFICIENCY = new WaterMovementEfficiency();
   public static final ItemStat PERM_EFFECTS = new PermanentEffects();
   public static final ItemStat GRANTED_PERMISSIONS = new GrantedPermissions();
   public static final ItemStat RESTORE_HEALTH = new RestoreHealth();
   public static final ItemStat RESTORE_FOOD = new RestoreFood();
   public static final ItemStat RESTORE_SATURATION = new RestoreSaturation();
   public static final ItemStat RESTORE_MANA = new RestoreMana();
   public static final ItemStat RESTORE_STAMINA = new RestoreStamina();
   public static final ItemStat CAN_IDENTIFY = new CanIdentify();
   public static final ItemStat CAN_DECONSTRUCT = new CanDeconstruct();
   public static final ItemStat CAN_DESKIN = new CanDeskin();
   public static final ItemStat EFFECTS = new Effects();
   public static final ItemStat SOULBINDING_CHANCE = new SoulbindingChance();
   public static final ItemStat SOULBOUND_BREAK_CHANCE = new SoulbindingBreakChance();
   public static final ItemStat SOULBOUND_LEVEL = new SoulboundLevel();
   public static final ItemStat ITEM_COOLDOWN = new DoubleStat(
      "ITEM_COOLDOWN",
      Material.COOKED_CHICKEN,
      "Item Cooldown",
      new String[]{"This cooldown applies for consumables", "as well as for item commands."},
      new String[]{"!armor", "!gem_stone", "!block", "all"}
   );
   public static final ItemStat COOLDOWN_REFERENCE = new StringStat(
      "COOLDOWN_REFERENCE",
      Material.CHICKEN,
      "Cooldown Reference",
      new String[]{"Two items with the same cooldown reference", "will share their cooldowns. This is useful", "for health or mana pots for example."},
      new String[]{"!armor", "!gem_stone", "!block", "all"}
   );
   public static final ItemStat VANILLA_EATING_ANIMATION = new VanillaEatingAnimation();
   public static final ItemStat GEM_COLOR = new GemColor();
   public static final ItemStat GEM_UPGRADE_SCALING = new GemUpgradeScaling();
   public static final ItemStat ITEM_TYPE_RESTRICTION = new ItemTypeRestriction();
   public static final ItemStat MAX_CONSUME = new MaxConsume();
   public static final ItemStat SUCCESS_RATE = new SuccessRate();
   public static final ItemStat CRAFTING = new Crafting();
   public static final ItemStat CRAFT_PERMISSION = new CraftingPermission();
   public static final ItemStat AUTOSMELT = new BooleanStat(
      "AUTOSMELT", Material.COAL, "Autosmelt", new String[]{"If set to true, your tool will", "automaticaly smelt mined ores."}, new String[]{"tool"}
   );
   public static final ItemStat BOUNCING_CRACK = new BooleanStat(
      "BOUNCING_CRACK",
      Material.COBBLESTONE_WALL,
      "Bouncing Crack",
      new String[]{"If set to true, your tool will", "also break nearby blocks."},
      new String[]{"tool"}
   );
   public static final ItemStat PICKAXE_POWER = new PickaxePower();
   public static final ItemStat CUSTOM_SOUNDS = new CustomSounds();
   public static final ItemStat ELEMENTS = new Elements();
   public static final ItemStat COMMANDS = new Commands();
   public static final ItemStat LUTE_ATTACK_SOUND = new LuteAttackSoundStat();
   public static final ItemStat LUTE_ATTACK_EFFECT = new LuteAttackEffectStat();
   public static final ItemStat NOTE_WEIGHT = new DoubleStat(
      "NOTE_WEIGHT",
      Material.MUSIC_DISC_MALL,
      "Note Weight",
      new String[]{"Defines how the projectile cast", "by your lute tilts downwards."},
      new String[]{"lute"}
   );
   public static final ItemStat REMOVE_ON_CRAFT = new BooleanStat(
      "REMOVE_ON_CRAFT",
      Material.GLASS_BOTTLE,
      "Remove on Craft",
      new String[]{"If the item should be completely", "removed when used in a recipe,", "or if it should become an", "empty bottle or bucket."},
      new String[0],
      Material.POTION,
      Material.SPLASH_POTION,
      Material.LINGERING_POTION,
      Material.MILK_BUCKET,
      Material.LAVA_BUCKET,
      Material.WATER_BUCKET
   );
   public static final ItemStat COMPATIBLE_TYPES = new CompatibleTypes();
   public static final ItemStat COMPATIBLE_IDS = new CompatibleIds();
   public static final ItemStat COMPATIBLE_MATERIALS = new CompatibleMaterials();
   public static final ItemStat GEM_SOCKETS = new GemSockets();
   public static final ItemStat RANDOM_UNSOCKET = new RandomUnsocket();
   public static final ItemStat REPAIR = new RepairPower();
   public static final ItemStat REPAIR_PERCENT = new RepairPowerPercent();
   public static final ItemStat REPAIR_TYPE = new RepairReference();
   public static final ItemStat INEDIBLE = new BooleanStat(
      "INEDIBLE",
      Material.POISONOUS_POTATO,
      "Inedible",
      new String[]{"Players won't be able to right-click this consumable.", "", "No effects of it will take place."},
      new String[]{"consumable"}
   );
   public static final ItemStat DISABLE_RIGHT_CLICK_CONSUME = new DisableStat(
      "RIGHT_CLICK_CONSUME",
      Material.BAKED_POTATO,
      "Infinite Consume",
      new String[]{"consumable"},
      "Players will be able to right-click this consumable",
      "and benefit from its effects, but it won't be consumed."
   );
   public static final ItemStat KNOCKBACK = new DoubleStat(
      "KNOCKBACK",
      Material.IRON_HORSE_ARMOR,
      "Knockback",
      new String[]{"Using this musket will knock", "the user back if positive."},
      new String[]{"musket", "gem_stone"}
   );
   public static final ItemStat RECOIL = new DoubleStat(
      "RECOIL", Material.IRON_HORSE_ARMOR, "Recoil", new String[]{"Corresponds to the shooting innacuracy."}, new String[]{"musket", "gem_stone"}
   );
   public static final ItemStat HANDWORN = new BooleanStat(
      "HANDWORN",
      Material.STRING,
      "Handworn",
      new String[]{"This item can be held along with a", "two-handed weapon in the other hand."},
      new String[]{"catalyst", "off_catalyst", "main_catalyst"}
   );
   public static final ItemStat AMPHIBIAN = new Amphibian();
   public static final ItemStat ABILITIES = new Abilities();
   public static final ItemStat UPGRADE = new UpgradeStat();
   public static final ItemStat DOWNGRADE_ON_BREAK = new BooleanStat(
      "BREAK_DOWNGRADE",
      Material.DAMAGED_ANVIL,
      "Downgrade when Broken",
      new String[]{
         "If this item's durability reaches 0,",
         "it will be fully repaired but also",
         "downgraded by one level.",
         "",
         "&cIt will only break if it cannot be",
         "&cdowngraded further",
         "",
         "Requires to define an &6Upgrade Template",
         "Required to define &6Custom Durability"
      },
      new String[]{"equipment"}
   );
   public static final ItemStat DOWNGRADE_ON_DEATH = new BooleanStat(
      "DEATH_DOWNGRADE",
      Material.DAMAGED_ANVIL,
      "Downgrade on Death",
      new String[]{
         "If the wearer of this item dies, it may",
         "downgrade (based on &6Death Downgrade",
         "&6Chance &7stat)",
         "",
         "Required to define an &6Upgrade Template",
         "Requires keep-inventory gamerule. "
      },
      new String[]{"equipment"}
   );
   public static final ItemStat DOWNGRADE_ON_DEATH_CHANCE = new DoubleStat(
      "DEATH_DOWNGRADE_CHANCE",
      Material.SKELETON_SKULL,
      "Death Downgrade Chance",
      new String[]{
         "Probability that an item with &cDowngrade ",
         "&con Death&7 will be downgraded when the",
         "player dies. ",
         "",
         "Exceeding 100% will for sure downgrade",
         "one item, and roll again to downgrade",
         "another (with the excess probability).",
         "&6The same item wont be downgraded twice."
      },
      new String[]{"equipment"},
      false
   );
   public static final ItemStat DYE_COLOR = new DyeColor();
   public static final ItemStat HIDE_DYE = new HideDye();
   public static final ItemStat TRIM_MATERIAL = new TrimMaterialStat();
   public static final ItemStat TRIM_PATTERN = new TrimPatternStat();
   public static final ItemStat HIDE_ARMOR_TRIM = new HideTrim();
   public static final ItemStat POTION_EFFECTS = new PotionEffects();
   public static final ItemStat POTION_COLOR = new PotionColor();
   public static final ItemStat SHIELD_PATTERN = new ShieldPatternStat();
   public static final ItemStat HIDE_POTION_EFFECTS = new HidePotionEffects();
   public static final ItemStat SOULBOUND = new Soulbound();
   public static final ItemStat CUSTOM_DURABILITY = new CustomDurability();
   public static final ItemStat STORED_TAGS = new StoredTags();
   public static final ItemStat ITEM_LEVEL = new ItemLevel();
   public static final ItemStat BROWSER_DISPLAY_IDX = new BrowserDisplayIDX();
   @Deprecated
   @DeprecatedStat
   public static final ItemStat DURABILITY = ITEM_DAMAGE;
}
