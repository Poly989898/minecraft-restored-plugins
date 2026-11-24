package net.Indyuce.mmoitems.api;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.player.cooldown.CooldownObject;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.interaction.GemStone;
import net.Indyuce.mmoitems.api.interaction.ItemSkin;
import net.Indyuce.mmoitems.api.interaction.Tool;
import net.Indyuce.mmoitems.api.interaction.UseItem;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Lute;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Musket;
import net.Indyuce.mmoitems.api.item.util.identify.UnidentifiedItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.manager.TypeManager;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Type implements CooldownObject, PreloadedObject {
   public static final Type SWORD = new Type("SWORD", ModifierSource.MELEE_WEAPON);
   public static final Type DAGGER = new Type("DAGGER", ModifierSource.MELEE_WEAPON);
   public static final Type SPEAR = new Type("SPEAR", ModifierSource.MELEE_WEAPON);
   public static final Type HAMMER = new Type("HAMMER", ModifierSource.MELEE_WEAPON);
   public static final Type GAUNTLET = new Type("GAUNTLET", ModifierSource.MELEE_WEAPON);
   public static final Type WHIP = new Type("WHIP", ModifierSource.RANGED_WEAPON);
   public static final Type STAFF = new Type("STAFF", ModifierSource.RANGED_WEAPON);
   public static final Type BOW = new Type("BOW", ModifierSource.RANGED_WEAPON);
   public static final Type CROSSBOW = new Type("CROSSBOW", ModifierSource.RANGED_WEAPON);
   public static final Type MUSKET = new Type("MUSKET", ModifierSource.RANGED_WEAPON, Musket::new);
   public static final Type LUTE = new Type("LUTE", ModifierSource.RANGED_WEAPON, Lute::new);
   public static final Type TOOL = new Type("TOOL", ModifierSource.MELEE_WEAPON, Tool::new);
   public static final Type CATALYST = new Type("CATALYST", ModifierSource.HAND_ITEM);
   public static final Type OFF_CATALYST = new Type("OFF_CATALYST", ModifierSource.OFFHAND_ITEM);
   public static final Type MAIN_CATALYST = new Type("MAIN_CATALYST", ModifierSource.MAINHAND_ITEM);
   public static final Type ORNAMENT = new Type("ORNAMENT", ModifierSource.VOID);
   public static final Type ARMOR = new Type("ARMOR", ModifierSource.ARMOR);
   public static final Type CONSUMABLE = new Type("CONSUMABLE", ModifierSource.MAINHAND_ITEM, Consumable::new);
   public static final Type MISCELLANEOUS = new Type("MISCELLANEOUS", ModifierSource.MAINHAND_ITEM);
   public static final Type GEM_STONE = new Type("GEM_STONE", ModifierSource.VOID, GemStone::new);
   public static final Type SKIN = new Type("SKIN", ModifierSource.VOID, ItemSkin::new);
   public static final Type ACCESSORY = new Type("ACCESSORY", ModifierSource.ACCESSORY);
   public static final Type BLOCK = new Type("BLOCK", ModifierSource.VOID);
   private final String id;
   private final ModifierSource modifierSource;
   private final BiFunction<PlayerData, NBTItem, UseItem> interactionProvider;
   private String name;
   @Nullable
   private String loreFormat;
   @NotNull
   private String attackCooldownKey;
   private ItemStack item;
   private final Type parent;
   private UnidentifiedItem unidentifiedTemplate;
   private SkillHandler<?> onLeftClick;
   private SkillHandler<?> onRightClick;
   private SkillHandler<?> onAttack;
   private SkillHandler<?> onEntityInteract;
   private boolean meleeAttacks;
   private boolean hideInGame;
   private final List<ItemStat> available = new ArrayList<>();
   private final PostLoadAction postLoadAction = new PostLoadAction(true, var1x -> {
      this.onLeftClick = var1x.contains("on-left-click") ? MythicLib.plugin.getSkills().loadSkillHandler(var1x.get("on-left-click")) : null;
      this.onRightClick = var1x.contains("on-right-click") ? MythicLib.plugin.getSkills().loadSkillHandler(var1x.get("on-right-click")) : null;
      this.onAttack = var1x.contains("on-attack") ? MythicLib.plugin.getSkills().loadSkillHandler(var1x.get("on-attack")) : null;
      this.onEntityInteract = var1x.contains("on-entity-interact") ? MythicLib.plugin.getSkills().loadSkillHandler(var1x.get("on-entity-interact")) : null;
   });

   public Type(@NotNull String text, @NotNull ModifierSource modifierSource) {
      this(text, modifierSource, modifierSource.isWeapon() ? Weapon::new : UseItem::new);
   }

   public Type(@NotNull String text, @NotNull ModifierSource modifierSource, @NotNull BiFunction<PlayerData, NBTItem, UseItem> biFunction) {
      this.id = UtilityMethods.enumName(text);
      this.modifierSource = modifierSource;
      this.interactionProvider = biFunction;
      this.parent = null;
   }

   public Type(@NotNull TypeManager typeManager, @NotNull ConfigurationSection section) {
      this.id = UtilityMethods.enumName(section.getName());
      Validate.isTrue(section.contains("parent"), "Custom types require a parent type");
      this.parent = typeManager.getOrThrow(UtilityMethods.enumName(section.getString("parent")));
      this.modifierSource = section.contains("modifier-source")
         ? ModifierSource.valueOf(UtilityMethods.enumName(section.getString("modifier-source")))
         : (this.parent != null ? this.parent.modifierSource : ModifierSource.OTHER);
      this.interactionProvider = this.parent.interactionProvider;
   }

   public void load(@NotNull ConfigurationSection section) {
      this.postLoadAction.cacheConfig(section);
      this.name = section.getString("name", this.name);
      this.item = MMOUtils.readIcon(section.getString("display", this.item == null ? Material.STONE.toString() : this.item.getType().toString()));
      (this.unidentifiedTemplate = new UnidentifiedItem(this)).update(section.getConfigurationSection("unident-item"));
      this.loreFormat = section.getString("LoreFormat", this.parent != null ? this.parent.loreFormat : null);
      this.attackCooldownKey = section.getString("attack-cooldown-key", "default");
      this.meleeAttacks = !section.getBoolean("disable-melee-attacks");
      this.hideInGame = section.getBoolean("hide-in-game");
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public boolean isDisplayed() {
      return !this.hideInGame;
   }

   @NotNull
   public UseItem toUseItem(@NotNull Player player, @NotNull NBTItem nbtItem) {
      return this.toUseItem(PlayerData.get(player), nbtItem);
   }

   @NotNull
   public UseItem toUseItem(@NotNull PlayerData playerData, @NotNull NBTItem nbtItem) {
      return this.interactionProvider.apply(playerData, nbtItem);
   }

   public String getId() {
      return this.id;
   }

   public boolean isWeapon() {
      return this.modifierSource.isWeapon();
   }

   public boolean hasMeleeAttacks() {
      return this.meleeAttacks;
   }

   public String getName() {
      return this.name;
   }

   @NotNull
   public ModifierSource getModifierSource() {
      return this.modifierSource;
   }

   public ItemStack getItem() {
      return this.item.clone();
   }

   @Nullable
   public SkillHandler<?> onLeftClick() {
      return this.onLeftClick;
   }

   public String getCooldownPath() {
      return "mmoitems_weapons:" + this.attackCooldownKey;
   }

   @Nullable
   public SkillHandler<?> onRightClick() {
      return this.onRightClick;
   }

   @Nullable
   public SkillHandler<?> onAttack() {
      return this.onAttack;
   }

   @Nullable
   public SkillHandler<?> onEntityInteract() {
      return this.onEntityInteract;
   }

   public boolean isFourGUIMode() {
      return this.getSupertype().modifierSource == ModifierSource.ARMOR;
   }

   @Nullable
   public String getLoreFormat() {
      return this.loreFormat;
   }

   @NotNull
   public Type getSupertype() {
      Type type = this;

      while (type.parent != null) {
         type = type.parent;
      }

      return type;
   }

   public boolean corresponds(Type type) {
      return this.getSupertype().equals(type);
   }

   public List<ItemStat> getAvailableStats() {
      return this.available;
   }

   public ConfigFile getConfigFile() {
      return new ConfigFile("/item", this.getId().toLowerCase());
   }

   public UnidentifiedItem getUnidentifiedTemplate() {
      return this.unidentifiedTemplate;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         Type type = (Type)obj;
         return this.id.equals(type.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   @Override
   public String toString() {
      return "Type{id='" + this.id + '\'' + '}';
   }

   @Nullable
   public static Type get(@Nullable ItemStack item) {
      return item == null ? null : get(NBTItem.get(item).getType());
   }

   @Nullable
   public static Type get(@Nullable String text) {
      return text == null ? null : MMOItems.plugin.getTypes().get(UtilityMethods.enumName(text));
   }

   @Nullable
   public static Type get(@NotNull NBTItem nbtItem) {
      return get(nbtItem.getType());
   }

   public static boolean isValid(@Nullable String text) {
      return text != null && MMOItems.plugin.getTypes().has(text.toUpperCase().replace("-", "_").replace(" ", "_"));
   }

   @Deprecated
   public boolean canHave(ItemStat itemStat) {
      return itemStat.isCompatible(this);
   }

   @Deprecated
   public boolean isSubtype() {
      return this.parent != null;
   }

   @Deprecated
   public Type getParent() {
      return this.parent;
   }

   @Deprecated
   public Type(@NotNull String text, boolean flag, @NotNull ModifierSource modifierSource) {
      this(text, modifierSource);
   }

   @Deprecated
   public void postload(ConfigurationSection section) {
      this.postLoadAction.cacheConfig(section);
      this.postLoadAction.performAction();
   }

   @Deprecated
   public String name() {
      return this.getId();
   }
}
