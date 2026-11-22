package net.Zrips.CMILib.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Attributes.Attribute;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Colors.CMIColors;
import net.Zrips.CMILib.Container.CMIKyori;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import net.Zrips.CMILib.Recipes.CMIRecipeIngredient;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CMIItemStack {
  @Deprecated private int id = 0;
  @Deprecated private short data = 0;
  private short durability = 0;
  private int amount = 0;
  private String bukkitName = null;
  private String mojangName = null;
  private CMIMaterial cmiMaterial = null;
  private Material material = null;
  private CMIEntityType entityType = null;
  private ItemStack item;

  public CMIItemStack(Material material) {
    this.material = material;
    this.cmiMaterial = CMIMaterial.get(material);
  }

  public CMIItemStack(CMIMaterial cmiMaterial) {
    this.cmiMaterial = cmiMaterial;
    if (cmiMaterial != null) {
      this.material = cmiMaterial.getMaterial();
    }
  }

  public CMIItemStack(ItemStack item) {
    if (item == null) {
      item = new ItemStack(Material.AIR);
    }

    this.cmiMaterial = CMIMaterial.get(item);
    this.setItemStack(item);
  }

  public CMIItemStack clone() {
    CMIItemStack cm = new CMIItemStack(this.material);
    cm.entityType = this.entityType;
    cm.setId(this.id);
    cm.setData(this.data);
    cm.setAmount(this.amount);
    cm.setDurability(this.durability);
    cm.setBukkitName(this.bukkitName);
    cm.setMojangName(this.mojangName);
    cm.setCMIMaterial(this.cmiMaterial);
    cm.setMaterial(this.material);
    cm.setItemStack(this.item != null ? this.item.clone() : null);
    return cm;
  }

  @Deprecated
  public int getId() {
    return this.id;
  }

  @Deprecated
  public void setId(Integer id) {
    this.id = id;
  }

  @Deprecated
  public short getData() {
    return this.data;
  }

  public boolean isTool() {
    return this.getMaxDurability() > 0;
  }

  public boolean isArmor() {
    return this.getCMIType() != null && this.getCMIType().isArmor()
        ? true
        : CMIMaterial.isArmor(this.getType());
  }

  public int getDurability() {
    return getDurability(this.getItemStack());
  }

  public int getMaxDurability() {
    return getMaxDurability(this.getItemStack());
  }

  public ItemStack setMaxDurability(int durability) {
    return setMaxDurability(this.getItemStack(), durability);
  }

  public boolean hasDurability() {
    return hasDurability(this.getItemStack());
  }

  public static int getDurability(ItemStack item) {
    if (item == null) {
      return 0;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        && item.getItemMeta() instanceof Damageable) {
      Damageable meta = (Damageable) item.getItemMeta();
      return meta.getDamage();
    } else {
      return item.getDurability();
    }
  }

  public static boolean hasDurability(ItemStack item) {
    if (item == null) {
      return false;
    } else {
      return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
              && item.getItemMeta() instanceof Damageable
          ? true
          : item.getDurability() > 15;
    }
  }

  public static int getMaxDurability(ItemStack item) {
    if (item == null) {
      return 0;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)
        && item.getItemMeta() instanceof Damageable) {
      Damageable meta = (Damageable) item.getItemMeta();
      return !meta.hasMaxDamage() ? item.getType().getMaxDurability() : meta.getMaxDamage();
    } else {
      return item.getType().getMaxDurability();
    }
  }

  public static ItemStack setMaxDurability(ItemStack item, int durability) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)
        && item.getItemMeta() instanceof Damageable) {
      durability = CMINumber.clamp(durability, 0, durability);
      Damageable meta = (Damageable) item.getItemMeta();
      meta.setMaxDamage(durability);
      item.setItemMeta(meta);
      return item;
    } else {
      return item;
    }
  }

  public boolean hasItemMeta() {
    return this.item != null && this.item.hasItemMeta();
  }

  public boolean hasDisplayName() {
    if (!this.hasItemMeta()) {
      return false;
    }

    ItemMeta meta = this.item.getItemMeta();
    return meta != null && meta.hasDisplayName();
  }

  public boolean hasItemName() {
    if (!this.hasItemMeta()) {
      return false;
    } else if (Version.isPaperBranch() && Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
      ItemMeta meta = this.item.getItemMeta();
      return meta != null && meta.hasItemName();
    } else {
      return false;
    }
  }

  public boolean hasLore() {
    if (!this.hasItemMeta()) {
      return false;
    }

    ItemMeta meta = this.item.getItemMeta();
    return meta != null && meta.hasLore();
  }

  public void setData(short data) {
    this.data = data;
    if (this.getCMIType() != null) {
      ItemMeta meta = null;
      if (this.item != null && this.item.hasItemMeta()) {
        meta = this.item.getItemMeta();
      }

      this.item = null;
      if (meta != null && this.getItemStack() != null) {
        this.getItemStack().setItemMeta(meta);
      }
    }
  }

  public CMIItemStack setUnbreakable(Boolean state) {
    if (state == null) {
      return this;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta meta = this.getItemStack().getItemMeta();
      meta.setUnbreakable(state);
      this.getItemStack().setItemMeta(meta);
      return this;
    } else {
      this.item =
          (ItemStack)
              new CMINBT(this.getItemStack())
                  .setByte("Unbreakable", Byte.valueOf((byte) (state ? 1 : 0)));
      return this;
    }
  }

  public CMIItemStack addAttributes(List<Attribute> attList) {
    if (attList != null && !attList.isEmpty()) {
      try {
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          ItemMeta meta = this.getItemStack().getItemMeta();
          attList.forEach(
              att -> {
                AttributeModifier attackDamage =
                    new AttributeModifier(
                        UUID.randomUUID(),
                        att.getType().getFullName(),
                        att.getMod(),
                        att.getOperation() == 0
                            ? Operation.ADD_NUMBER
                            : (att.getOperation() == 1
                                ? Operation.ADD_SCALAR
                                : Operation.MULTIPLY_SCALAR_1));
                meta.addAttributeModifier(att.getType().getAttribute(), attackDamage);
              });
          this.getItemStack().setItemMeta(meta);
          return this;
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }

      this.item =
          CMILib.getInstance().getReflectionManager().addAttributes(attList, this.getItemStack());
      return this;
    } else {
      return this;
    }
  }

  public CMIItemStack setTag(String tag) {
    if (tag != null && !tag.isEmpty()) {
      this.item = CMINBT.modifyItemStack(this.getItemStack(), tag);
      return this;
    } else {
      return this;
    }
  }

  public CMIItemStack setDisplayName(String name) {
    ItemMeta meta = this.getItemStack().getItemMeta();
    if (meta != null) {
      if (name == null) {
        meta.setDisplayName(null);
      } else {
        meta.setDisplayName(CMIChatColor.translate(name));
      }
    }

    this.getItemStack().setItemMeta(meta);
    return this;
  }

  public String getDisplayName() {
    if (this.getItemStack() == null) {
      return this.getRealName();
    }

    ItemMeta meta = this.getItemStack().getItemMeta();
    if (meta != null
        && Version.isPaperBranch()
        && Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
      if (meta.hasDisplayName()) {
        return CMIKyori.serializeLegacy(meta.displayName());
      } else {
        return meta.hasItemName() ? CMIKyori.serializeLegacy(meta.itemName()) : this.getRealName();
      }
    } else {
      return meta == null
          ? this.getRealName()
          : (meta.getDisplayName() != null && !meta.getDisplayName().isEmpty()
              ? meta.getDisplayName()
              : this.getRealName());
    }
  }

  public CMIItemStack setItemName(@Nullable String name) {
    if (!Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
      return this;
    }

    ItemMeta meta = this.getItemStack().getItemMeta();
    if (Version.isPaperBranch()) {
      if (name == null) {
        meta.itemName(null);
      } else {
        meta.itemName(CMIKyori.deserialize(name));
      }
    } else if (name == null) {
      meta.setItemName(null);
    } else {
      meta.setItemName(name);
    }

    this.getItemStack().setItemMeta(meta);
    return this;
  }

  @Nullable
  public String getItemName() {
    if (!Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
      return null;
    } else {
      return !Version.isPaperBranch()
          ? this.getItemStack().getItemMeta().getItemName()
          : CMIKyori.serialize(this.getItemStack().getItemMeta().itemName());
    }
  }

  public CMIItemStack addLore(String string) {
    if (string == null) {
      return this;
    }

    ItemMeta meta = this.getItemStack().getItemMeta();
    List<String> lore = meta.getLore();
    if (lore == null) {
      lore = new ArrayList<>();
    }

    lore.add(CMIChatColor.translate(string));
    meta.setLore(lore);
    this.getItemStack().setItemMeta(meta);
    return this;
  }

  public CMIItemStack clearLore() {
    ItemMeta meta = this.getItemStack().getItemMeta();
    if (meta != null) {
      List<String> t = new ArrayList<>();
      meta.setLore(t);
      this.getItemStack().setItemMeta(meta);
    }

    return this;
  }

  public CMIItemStack setLore(List<String> lore) {
    if (lore != null && !lore.isEmpty()) {
      ItemMeta meta = this.getItemStack().getItemMeta();
      List<String> t = new ArrayList<>();

      for (String one : lore) {
        t.add(CMIChatColor.translate(one));
      }

      meta.setLore(t);
      this.getItemStack().setItemMeta(meta);
      return this;
    } else {
      return this;
    }
  }

  public CMIItemStack addEnchant(Enchantment enchant, Integer level) {
    if (enchant == null) {
      return this;
    }

    if (this.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) {
      EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.getItemStack().getItemMeta();
      meta.addStoredEnchant(enchant, level, true);
      this.getItemStack().setItemMeta(meta);
    } else {
      ItemMeta meta = this.getItemStack().getItemMeta();
      meta.addEnchant(enchant, level, true);
      this.getItemStack().setItemMeta(meta);
    }

    return this;
  }

  public CMIItemStack addEnchant(HashMap<Enchantment, Integer> enchants) {
    if (enchants != null && !enchants.isEmpty()) {
      for (Entry<Enchantment, Integer> oneEnch : enchants.entrySet()) {
        this.addEnchant(oneEnch.getKey(), oneEnch.getValue());
      }

      return this;
    } else {
      return this;
    }
  }

  public CMIItemStack clearEnchants() {
    ItemMeta meta = this.getItemStack().getItemMeta();
    meta.getEnchants().clear();
    this.getItemStack().setItemMeta(meta);
    return this;
  }

  public List<String> getLore() {
    ItemMeta meta = this.getItemStack().getItemMeta();
    if (meta != null) {
      List<String> lore = meta.getLore();
      if (lore == null) {
        lore = new ArrayList<>();
        meta.setLore(lore);
      }

      return meta.getLore() == null ? new ArrayList<>() : meta.getLore();
    } else {
      return new ArrayList<>();
    }
  }

  public String getRealName() {
    return this.getCMIType() != null && this.getCMIType() != CMIMaterial.NONE
        ? (this.getCMIType().getTranslatedName() != null
            ? this.getCMIType().getTranslatedName()
            : this.getCMIType().getName())
        : this.getType().name();
  }

  public String getBukkitName() {
    return this.bukkitName != null && !this.bukkitName.isEmpty() ? this.bukkitName : null;
  }

  public void setBukkitName(String bukkitName) {
    this.bukkitName = bukkitName;
  }

  public String getMojangName() {
    return this.mojangName != null && !this.mojangName.isEmpty()
        ? this.mojangName
        : this.getCMIType().getMaterial().name();
  }

  public void setMojangName(String mojangName) {
    if (mojangName != null) {
      this.mojangName = mojangName.replace("minecraft:", "");
    }
  }

  public Material getType() {
    return this.material == null && this.cmiMaterial != null
        ? this.cmiMaterial.getMaterial()
        : this.material;
  }

  public CMIMaterial getCMIType() {
    return this.cmiMaterial == null ? CMIMaterial.get(this.material) : this.cmiMaterial;
  }

  @Deprecated
  public Material getMaterial() {
    return this.getType();
  }

  public void setMaterial(Material material) {
    this.cmiMaterial = CMIMaterial.get(material);
    this.material = material;
  }

  public void setCMIMaterial(CMIMaterial material) {
    this.cmiMaterial = material;
    this.material = material == null ? null : material.getMaterial();
  }

  private void setEnt() {
    if (this.cmiMaterial.isSpawnEgg()) {
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        this.item = this.item == null ? new ItemStack(this.getType()) : this.item;
        this.item.setAmount(this.getAmount());
        return;
      }

      this.item =
          this.item == null
              ? new ItemStack(
                  this.getType(),
                  this.amount == 0 ? 1 : this.amount,
                  this.data == 0 ? 90 : this.data)
              : this.item;
      if (this.getEntityType() != null) {
        this.item =
            CMILib.getInstance().getReflectionManager().setEggType(this.item, this.getEntityType());
      } else {
        CMIEntityType type = CMIEntityType.getById(this.data);
        if (type != null && !Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
          this.item =
              CMILib.getInstance().getReflectionManager().setEggType(this.item, type.getType());
        }
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      this.item = this.item == null ? new ItemStack(this.getType()) : this.item;
      this.item.setAmount(this.getAmount());
      if (this.getType().equals(CMIMaterial.SPAWNER.getMaterial())) {
        if (this.getEntityType() != null) {
          this.item = CMIEntity.setEntityType(this.item, this.getEntityType());
        } else if (Version.isCurrentEqualOrLower(Version.v1_19_R2)) {
          this.item =
              CMIEntity.setEntityType(
                  this.item, CMIEntityType.getById(this.data == 0 ? 90 : this.data).getType());
        }
      }
    } else {
      this.item = new ItemStack(this.getType(), this.amount == 0 ? 1 : this.amount, this.data);
    }
  }

  public ItemStack getItemStack() {
    if (this.item != null) {
      return this.item;
    }

    try {
      if (!this.getType().isItem()) {
        return null;
      }
    } catch (Throwable var3) {
    }

    this.setEnt();
    if (this.item == null) {
      return null;
    }

    if (this.getCMIType().isPotion()
        || this.item.getType().name().contains("SPLASH_POTION")
        || this.item.getType().name().contains("TIPPED_ARROW")) {
      PotionMeta potion = (PotionMeta) this.item.getItemMeta();
      PotionEffectType effect = PotionEffectType.getById(this.data);
      if (effect != null) {
        potion.addCustomEffect(new PotionEffect(effect, 60, 0), true);
      }

      this.item.setItemMeta(potion);
      this.item.setDurability((short) 0);
      potion = (PotionMeta) this.item.getItemMeta();
      if (effect != null) {
        potion.setDisplayName(this.getRealName());
      }

      this.item.setItemMeta(potion);
    }

    this.applyEntityType();
    if (this.durability > 0) {
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        Damageable damage = (Damageable) this.item.getItemMeta();
        damage.setDamage(this.item.getType().getMaxDurability() - this.durability);
      } else {
        this.item.setDurability(this.durability);
      }
    }

    return this.item;
  }

  private void applyEntityType() {
    if (CMIMaterial.SPAWNER.equals(this.getCMIType())) {
      if (this.data == 0) {
        this.data = 90;
      }

      CMIEntityType type = this.entityType;
      if (type == null && Version.isCurrentEqualOrLower(Version.v1_19_R2)) {
        type = CMIEntityType.getById(this.data);
      }

      if (type != null) {
        this.item = CMIEntity.setEntityType(this.getItemStack(), type.getType());
      }
    }
  }

  public CMIItemStack setItemStack(ItemStack item) {
    this.item = item;
    if (item == null) {
      return this;
    }

    this.amount = item.getAmount();
    this.material = item.getType();
    this.cmiMaterial = CMIMaterial.get(item);
    if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
      this.id = item.getType().getId();
      if (this.getType().isBlock() || this.getType().isSolid()) {
        this.data = item.getData().getData();
      }

      if (item.getType().getMaxDurability() - item.getDurability() < 0) {
        this.data = item.getData().getData();
      }
    } else if (this.cmiMaterial != null) {
      this.id = this.cmiMaterial.getId();
    }

    if (item.getType().getMaxDurability() > 15) {
      this.data = 0;
    }

    if (CMIMaterial.isMonsterEgg(item.getType())) {
      this.entityType =
          CMIEntityType.getByType(CMILib.getInstance().getReflectionManager().getEggType(item));
    }

    if (item.getType() == Material.POTION
        || item.getType().name().contains("SPLASH_POTION")
        || item.getType().name().contains("TIPPED_ARROW")) {
      PotionMeta potion = (PotionMeta) item.getItemMeta();

      try {
        if (potion != null
            && potion.getBasePotionData() != null
            && potion.getBasePotionData().getType() != null
            && potion.getBasePotionData().getType().getEffectType() != null) {
          this.data = (short) potion.getBasePotionData().getType().getEffectType().getId();
        }
      } catch (NoSuchMethodError var4) {
      }
    }

    return this;
  }

  public int getAmount() {
    return this.amount <= 0 ? 1 : this.amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
    if (this.item != null) {
      this.item.setAmount(this.amount == 0 ? this.item.getAmount() : this.amount);
    }
  }

  public boolean isSimilar(ItemStack item) {
    return this.isSimilar(CMILib.getInstance().getItemManager().getItem(item));
  }

  public boolean isSimilar(CMIItemStack item) {
    if (item == null) {
      return false;
    }

    try {
      if ((item.getCMIType().isPotion() || item.getCMIType().equals(CMIMaterial.TIPPED_ARROW))
          && (this.getCMIType().isPotion() || this.getCMIType().equals(CMIMaterial.TIPPED_ARROW))
          && this.getType().equals(item.getType())) {
        PotionMeta potion = (PotionMeta) item.getItemStack().getItemMeta();
        PotionMeta otherPotion = (PotionMeta) this.getItemStack().getItemMeta();

        try {
          if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
            if (potion.getBasePotionType() != null
                && otherPotion.getBasePotionType() != null
                && potion.getBasePotionType().equals(otherPotion.getBasePotionType())) {
              return true;
            }
          } else if (potion != null && potion.getBasePotionData() != null) {
            PotionData thisPotionData = potion.getBasePotionData();
            if (thisPotionData.getType() != null
                && otherPotion != null
                && otherPotion.getBasePotionData() != null) {
              PotionData otherPotionData = otherPotion.getBasePotionData();
              if (otherPotionData.getType() != null
                  && thisPotionData.getType().equals(otherPotionData.getType())
                  && thisPotionData.isExtended() == otherPotionData.isExtended()
                  && thisPotionData.isUpgraded() == otherPotionData.isUpgraded()) {
                return true;
              }
            }
          }

          return false;
        } catch (NoSuchMethodError var10) {
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      label207:
      if (this.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta
          && item.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) {
        EnchantmentStorageMeta thisMeta =
            (EnchantmentStorageMeta) this.getItemStack().getItemMeta();
        EnchantmentStorageMeta otherMeta =
            (EnchantmentStorageMeta) item.getItemStack().getItemMeta();

        for (Entry<Enchantment, Integer> one : thisMeta.getEnchants().entrySet()) {
          if (!otherMeta.getEnchants().containsKey(one.getKey())
              || otherMeta.getEnchants().get(one.getKey()) != one.getValue()) {
            return false;
          }
        }

        Iterator storedIterator = thisMeta.getStoredEnchants().entrySet().iterator();

        Entry<Enchantment, Integer> one;
        do {
          if (!storedIterator.hasNext()) {
            break label207;
          }

          one = (Entry<Enchantment, Integer>) storedIterator.next();
        } while (otherMeta.getStoredEnchants().containsKey(one.getKey())
            && otherMeta.getStoredEnchants().get(one.getKey()) == one.getValue());

        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (item.getCMIType() != CMIMaterial.SPAWNER && !item.getCMIType().isMonsterEgg()
        || this.getCMIType() != CMIMaterial.SPAWNER && !this.getCMIType().isMonsterEgg()) {
      if (item.getCMIType() == CMIMaterial.PLAYER_HEAD
          && this.getCMIType() == CMIMaterial.PLAYER_HEAD) {
        try {
          String thisTexture = null;

          try {
            CMINBT nbt = new CMINBT(this.getItemStack());
            List<String> ls = nbt.getList("SkullOwner.Properties.textures");
            if (ls != null) {
              thisTexture = ls.get(0);
            }
          } catch (Throwable var7) {
          }

          String otherTexture = null;

          try {
            CMINBT nbt = new CMINBT(item.getItemStack());
            List<String> ls = nbt.getList("SkullOwner.Properties.textures");
            if (ls != null) {
              otherTexture = ls.get(0);
            }
          } catch (Throwable var6) {
          }

          if (thisTexture != null && otherTexture == null
              || thisTexture == null && otherTexture != null
              || thisTexture != null && otherTexture != null && !thisTexture.equals(otherTexture)) {
            SkullMeta skullMeta = (SkullMeta) item.getItemStack().getItemMeta();
            SkullMeta otherSkullMeta = (SkullMeta) this.getItemStack().getItemMeta();
            if ((skullMeta.getOwner() == null || otherSkullMeta.getOwner() != null)
                && (skullMeta.getOwner() != null || otherSkullMeta.getOwner() == null)) {
              if (skullMeta.getOwner() != null
                  && otherSkullMeta.getOwner() != null
                  && !skullMeta.getOwner().equals(otherSkullMeta.getOwner())) {
                return false;
              }

              return false;
            }

            return false;
          }
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }

      return Version.isCurrentEqualOrHigher(Version.v1_13_R1)
          ? this.cmiMaterial == item.cmiMaterial
          : this.cmiMaterial == item.cmiMaterial && this.getData() == item.getData();
    } else {
      return this.cmiMaterial != item.cmiMaterial
          ? false
          : this.getEntityType() == item.getEntityType();
    }
  }

  public EntityType getEntityType() {
    if (this.getItemStack() == null) {
      return null;
    }

    if (this.entityType != null) {
      return this.entityType.getType();
    }

    ItemStack is = this.getItemStack().clone();
    if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)
        && is.getItemMeta() instanceof BlockStateMeta) {
      if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)
          && Version.isCurrentEqualOrLower(Version.v1_20_R3)) {
        CMINBT nbt = new CMINBT(is);
        if (!nbt.hasNBT("BlockEntityTag")) {
          return null;
        }
      }

      BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
      if (bsm.getBlockState() instanceof CreatureSpawner) {
        CreatureSpawner bs = (CreatureSpawner) bsm.getBlockState();

        try {
          return bs.getSpawnedType();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }

    if (CMIMaterial.isMonsterEgg(is.getType())) {
      return CMILib.getInstance().getReflectionManager().getEggType(is);
    } else if (CMIMaterial.get(is) != null && CMIMaterial.get(is).isMonsterEgg()) {
      return CMILib.getInstance().getReflectionManager().getEggType(is);
    } else {
      return Version.isCurrentEqualOrLower(Version.v1_12_R1)
          ? EntityType.fromId(is.getData().getData())
          : null;
    }
  }

  public boolean hasNbtTag() {
    return new CMINBT(this.getItemStack()).hasNBT();
  }

  public CMIItemStack setNbt(String path, String value) {
    ItemStack it = this.getItemStack();
    it = (ItemStack) new CMINBT(it).setString(path, value);
    this.item = it;
    return this;
  }

  public CMIItemStack setNbt(String path, int value) {
    ItemStack it = this.getItemStack();
    it = (ItemStack) new CMINBT(it).setInt(path, value);
    this.item = it;
    return this;
  }

  public CMIItemStack setNbt(String path, boolean value) {
    ItemStack it = this.getItemStack();
    it = (ItemStack) new CMINBT(it).setBoolean(path, value);
    ItemMeta meta = it.getItemMeta();
    if (meta != null) {
      this.getItemStack().setItemMeta(meta);
    }

    return this;
  }

  public String getNbtString(String path) {
    return new CMINBT(this.getItemStack()).getString(path);
  }

  public Integer getNbtInt(String path) {
    return new CMINBT(this.getItemStack()).getInt(path);
  }

  public Boolean getNbtBoolean(String path) {
    return new CMINBT(this.getItemStack()).getBoolean(path);
  }

  public List<Recipe> getRecipesFor() {
    if (this.getItemStack() == null) {
      return new ArrayList<>();
    }

    ItemStack i = this.getItemStack().clone();
    if (Version.isCurrentLower(Version.v1_13_R1)) {
      i.getData().setData((byte) this.data);
    }

    if (i.getType().getMaxDurability() > 15) {
      i.setDurability((short) 0);
    }

    List<Recipe> recipes = new ArrayList<>();

    try {
      recipes = Bukkit.getRecipesFor(i);
    } catch (Throwable var11) {
    }

    if (this.getCMIType().isShulkerBox()) {
      CMIMaterial colorMat = CMIColors.getColorMaterial(this.getCMIType());
      if (colorMat != null) {
        HashMap<Integer, CMIRecipeIngredient> Recipe = new HashMap<>();
        Recipe.put(1, new CMIRecipeIngredient(CMIMaterial.SHULKER_BOX.newItemStack()));
        Recipe.put(2, new CMIRecipeIngredient(colorMat.newItemStack()));
        Recipe rec = CMIRecipe.makeShaplessRecipe(this.getItemStack(), Recipe);
        if (rec != null) {
          recipes.add(rec);
        }

        for (CMIMaterial one : CMIMaterial.values()) {
          if (one.isShulkerBox()) {
            CMIMaterial c = CMIColors.getColorMaterial(one);
            if (c != null && !c.equals(colorMat)) {
              Recipe.put(1, new CMIRecipeIngredient(one.newItemStack()));
              Recipe.put(2, new CMIRecipeIngredient(colorMat.newItemStack()));
              rec = CMIRecipe.makeShaplessRecipe(this.getItemStack(), Recipe);
              if (rec != null) {
                recipes.add(rec);
              }
            }
          }
        }
      }
    }

    if (this.getCMIType().equals(CMIMaterial.FIREWORK_ROCKET)) {
      HashMap<Integer, CMIRecipeIngredient> Recipe = new HashMap<>();
      Recipe.put(1, new CMIRecipeIngredient(CMIMaterial.PAPER.newItemStack()));
      Recipe.put(2, new CMIRecipeIngredient(CMIMaterial.GUNPOWDER.newItemStack()));
      Recipe rec = CMIRecipe.makeShaplessRecipe(this.getItemStack(), Recipe);
      if (rec != null) {
        recipes.add(rec);
      }

      Recipe.put(3, new CMIRecipeIngredient(CMIMaterial.GUNPOWDER.newItemStack()));
      rec = CMIRecipe.makeShaplessRecipe(this.getItemStack(), Recipe);
      if (rec != null) {
        recipes.add(rec);
      }

      Recipe.put(4, new CMIRecipeIngredient(CMIMaterial.GUNPOWDER.newItemStack()));
      rec = CMIRecipe.makeShaplessRecipe(this.getItemStack(), Recipe);
      if (rec != null) {
        recipes.add(rec);
      }

      Recipe.put(5, new CMIRecipeIngredient(CMIMaterial.FIREWORK_STAR.newItemStack()));
      rec = CMIRecipe.makeShaplessRecipe(this.getItemStack(), Recipe);
      if (rec != null) {
        recipes.add(rec);
      }
    }

    return recipes;
  }

  public List<Recipe> getRecipesFrom() {
    List<Recipe> recipes = new ArrayList<>();
    ItemStack i = this.getItemStack();
    if (i == null) {
      return recipes;
    }

    i = i.clone();
    if (Version.isCurrentLower(Version.v1_13_R1)) {
      i.getData().setData((byte) this.data);
    }

    if (i.getType().getMaxDurability() > 15) {
      i.setDurability((short) 0);
    }

    Iterator<Recipe> it = Bukkit.recipeIterator();

    while (it.hasNext()) {
      Recipe rec = it.next();
      if (rec != null) {
        for (ItemStack one : CMIRecipe.getIngredientsList(rec)) {
          if (one.getType() == i.getType()
              && (one.getDurability() == -1
                  || one.getDurability() == 32767
                  || one.getDurability() == i.getDurability())) {
            recipes.add(rec);
            break;
          }
        }

        if (rec instanceof ShapelessRecipe) {
          ShapelessRecipe srec = (ShapelessRecipe) rec;
          if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
            for (ItemStack one : srec.getIngredientList()) {
              if (one != null && one.isSimilar(i)) {
                recipes.add(rec);
                break;
              }
            }
          } else {
            for (RecipeChoice one : srec.getChoiceList()) {
              if (one != null && one.test(i)) {
                recipes.add(rec);
                break;
              }
            }
          }
        } else if (rec instanceof ShapedRecipe) {
          ShapedRecipe srec = (ShapedRecipe) rec;
          if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
            for (ItemStack one : srec.getIngredientMap().values()) {
              if (one != null && one.isSimilar(i)) {
                recipes.add(rec);
                break;
              }
            }
          } else {
            for (RecipeChoice one : srec.getChoiceMap().values()) {
              if (one != null && one.test(i)) {
                recipes.add(rec);
                break;
              }
            }
          }
        }
      }
    }

    return recipes;
  }

  public void setDurability(short durability) {
    this.durability = durability;
    if (this.item != null) {
      if (Version.isCurrentEqualOrHigher(Version.v1_13_0)) {
        if (!this.item.hasItemMeta()) {
          return;
        }

        ItemMeta meta = this.item.getItemMeta();
        if (!(meta instanceof Damageable)) {
          return;
        }

        Damageable rmeta = (Damageable) meta;
        rmeta.setDamage(durability);
        this.item.setItemMeta(rmeta);
        return;
      }

      this.item.setDurability(durability);
    }
  }

  public Set<Enchantment> getValidEnchants() {
    Set<Enchantment> enchants = new HashSet<>();

    for (Enchantment one : CMIEnchantment.values()) {
      if (CMIEnchantment.isEnabled(one) && one.canEnchantItem(this.getItemStack())) {
        enchants.add(one);
      }
    }

    return enchants;
  }

  public String toOneLiner() {
    return CMIItemSerializer.toOneLiner(this);
  }

  public static ItemStack getHead(String texture) {
    if (texture != null && !texture.isEmpty()) {
      if (texture.length() < 120) {
        texture =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                + texture;
      }

      ItemStack cached = CMIEntityType.cache.get(texture);
      if (cached != null) {
        return cached.clone();
      }

      ItemStack item = CMIMaterial.PLAYER_HEAD.newItemStack();
      item = CMILib.getInstance().getReflectionManager().setSkullTexture(item, null, texture);
      CMIEntityType.cache.put(texture, item);
      return item.clone();
    } else {
      return null;
    }
  }

  public void setEntityType(CMIEntityType entityType) {
    this.entityType = entityType;
    this.applyEntityType();
  }

  public void setEntityType(EntityType entityType) {
    this.setEntityType(CMIEntityType.getByType(entityType));
  }

  public static ItemStack getItemInMainHand(Player player) {
    if (player == null) {
      return null;
    } else {
      return Version.isCurrentHigher(Version.v1_8_R3)
          ? player.getInventory().getItemInMainHand()
          : player.getItemInHand();
    }
  }

  public static ItemStack getItemInOffHand(Player player) {
    if (player == null) {
      return null;
    } else {
      return Version.isCurrentLower(Version.v1_9_R1)
          ? null
          : player.getInventory().getItemInOffHand();
    }
  }

  public static void setItemInMainHand(Player player, ItemStack item) {
    if (player != null) {
      if (Version.isCurrentHigher(Version.v1_8_R3)) {
        player.getInventory().setItemInMainHand(item);
      } else {
        player.setItemInHand(item);
      }
    }
  }

  public static void setItemInOffHand(Player player, ItemStack item) {
    if (player != null) {
      if (Version.isCurrentHigher(Version.v1_8_R3)) {
        player.getInventory().setItemInOffHand(item);
      }
    }
  }

  public static CMIItemStack deserialize(String input) {
    return CMIItemSerializer.deserialize(null, input, null);
  }

  public static CMIItemStack deserialize(String input, CMIAsyncHead ahead) {
    return CMIItemSerializer.deserialize(null, input, ahead);
  }

  public static CMIItemStack deserialize(CommandSender sender, String input) {
    return CMIItemSerializer.deserialize(sender, input, null);
  }

  public static CMIItemStack deserialize(CommandSender sender, String input, CMIAsyncHead ahead) {
    return CMIItemSerializer.deserialize(sender, input, ahead);
  }

  public static String serialize(ItemStack item) {
    return CMIItemSerializer.serialize(item);
  }

  public static boolean valid(ItemStack item) {
    return item != null && !CMIMaterial.isAir(item.getType());
  }

  @Deprecated
  public Integer getCustomModelData() {
    return getCustomModelData(this.getItemStack());
  }

  @Deprecated
  public static Integer getCustomModelData(ItemStack item) {
    if (item == null) {
      return null;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
        return null;
      } else {
        return meta.hasCustomModelData() ? meta.getCustomModelData() : null;
      }
    } else {
      Integer old = new CMINBT(item).getInt("CustomModelData");
      return old == null ? null : old;
    }
  }

  @NotNull
  public CMICustomModelDataComponent getCustomModelDataComponent() {
    return getCustomModelDataComponent(this.getItemStack());
  }

  @NotNull
  public static CMICustomModelDataComponent getCustomModelDataComponent(ItemStack item) {
    return CMICustomModelDataComponent.fromItem(item);
  }

  @Deprecated
  public CMIItemStack setCustomModelData(int data) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      setCustomModelData(this.getItemStack(), data);
      return this;
    } else {
      this.setItemStack(setCustomModelData(this.getItemStack(), data));
      return this;
    }
  }

  @Deprecated
  public static ItemStack setCustomModelData(ItemStack item, int data) {
    if (item == null) {
      return item;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      ItemMeta meta = item.getItemMeta();
      CustomModelDataComponent old = meta.getCustomModelDataComponent();
      List<Float> floats = new ArrayList<>(old.getFloats());
      floats.add((float) data);
      old.setFloats(floats);
      meta.setCustomModelDataComponent(old);
      item.setItemMeta(meta);
      return item;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta meta = item.getItemMeta();
      meta.setCustomModelData(data);
      item.setItemMeta(meta);
      return item;
    } else {
      return (ItemStack) new CMINBT(item).setInt("CustomModelData", data);
    }
  }

  @Deprecated
  public CMIItemStack setCustomModelData(String data) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      setCustomModelData(this.getItemStack(), data);
      return this;
    } else {
      this.setItemStack(setCustomModelData(this.getItemStack(), data));
      return this;
    }
  }

  public CMIItemStack setCustomModelDataComponent(CMICustomModelDataComponent data) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      setCustomModelDataComponent(this.getItemStack(), data);
      return this;
    } else {
      this.setItemStack(setCustomModelDataComponent(this.getItemStack(), data));
      return this;
    }
  }

  public static ItemStack setCustomModelDataComponent(
      ItemStack item, CMICustomModelDataComponent data) {
    return data.apply(item);
  }

  @Deprecated
  public static ItemStack setCustomModelData(ItemStack item, String data) {
    if (item == null) {
      return item;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      ItemMeta meta = item.getItemMeta();
      CustomModelDataComponent old = meta.getCustomModelDataComponent();
      List<String> strings = new ArrayList<>(old.getStrings());
      strings.add(data);
      old.setStrings(strings);
      item.setItemMeta(meta);
      return item;
    } else {
      return item;
    }
  }

  public static boolean isUnbreakable(ItemStack item) {
    if (item == null) {
      return false;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta meta = item.getItemMeta();
      return meta.isUnbreakable();
    } else {
      CMINBT nbt = new CMINBT(item);
      Byte b = nbt.getByte("Unbreakable");
      return b != null && b == 1;
    }
  }

  public static ItemStack setUnbreakable(ItemStack item, boolean state) {
    if (item == null) {
      return item;
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta meta = item.getItemMeta();
      meta.setUnbreakable(state);
      item.setItemMeta(meta);
      return item;
    } else {
      return (ItemStack) new CMINBT(item).setByte("Unbreakable", (byte) (state ? 1 : 0));
    }
  }

  public static ItemStack getItemFromObject(Object itemObject) {
    return CMIItemSerializer.getItemFromObject(itemObject);
  }
}
