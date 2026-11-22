package net.Zrips.CMILib.Items;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Skin.CMISkin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.CMILibConfig;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemManager {
  private CMILib plugin;
  static HashMap<Material, CMIMaterial> byRealMaterial = new HashMap<>();
  static HashMap<String, CMIMaterial> byName = new HashMap<>();
  private static HashMap<String, Material> byNameMaterial = new HashMap<>();
  @Deprecated static HashMap<Integer, CMIMaterial> byId = new HashMap<>();
  ConcurrentHashMap<String, ItemStack> headCache = new ConcurrentHashMap<>();

  public ItemManager(CMILib plugin) {
    this.plugin = plugin;
  }

  @Deprecated
  public HashMap<Integer, CMIMaterial> idMap() {
    return new HashMap<>();
  }

  public HashMap<String, CMIMaterial> NameMap() {
    return byName;
  }

  public static CMIMaterial get(Material material) {
    return byRealMaterial.get(material);
  }

  public void load() {
    byRealMaterial.clear();

    for (CMIMaterial one : CMIMaterial.values()) {
      if (one != null && (!Version.isCurrentEqualOrHigher(Version.v1_13_R1) || !one.isLegacy())) {
        one.updateMaterial();
        Material mat = one.getMaterial();
        if (mat != null) {
          short data = one.getLegacyData();
          Integer legacyId = one.getLegacyId();
          String cmiName = one.getName().replace("_", "").replace(" ", "").toLowerCase();
          String materialName = one.toString().replace("_", "").replace(" ", "").toLowerCase();
          String mojangName = null;

          try {
            if (Version.isCurrentEqualOrLower(Version.v1_14_R1) || mat.isItem()) {
              mojangName =
                  this.plugin.getReflectionManager().getItemMinecraftName(new ItemStack(mat));
            }
          } catch (Exception | Error e) {
            e.printStackTrace();
          }

          if (mojangName == null) {
            mojangName = mat.toString();
          }

          mojangName =
              mojangName == null
                  ? mat.toString().replace("_", "").replace(" ", "").toLowerCase()
                  : mojangName.replace("_", "").replace(" ", "").toLowerCase();
          if (byName.containsKey(cmiName) && Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
            byName.put(cmiName + ":" + data, one);
          } else {
            byName.put(cmiName, one);
          }

          byName.put(materialName, one);
          if (Version.isCurrentEqualOrLower(Version.v1_13_R1)
              && !byName.containsKey(cmiName + ":" + data)) {
            byName.put(cmiName + ":" + data, one);
          }

          if (!one.getLegacyNames().isEmpty()) {
            for (String oneL : one.getLegacyNames()) {
              String legacyName = oneL.replace("_", "").replace(" ", "").toLowerCase();
              if (Version.isCurrentEqualOrLower(Version.v1_13_R1)
                  && (byName.containsKey(legacyName) || data > 0)) {
                byName.put(legacyName + ":" + data, one);
              }

              byName.put(legacyName, one);
            }
          }

          if (byName.containsKey(mojangName) && Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
            byName.put(mojangName + ":" + data, one);
          } else {
            byName.put(mojangName, one);
          }

          if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
            Integer id = one.getId();
            if (!byName.containsKey(String.valueOf(id)) && data <= 0) {
              byName.put(String.valueOf(id), one);
            } else {
              byName.put(id + ":" + data, one);
            }

            if (!byId.containsKey(id)) {
              byId.put(id, one);
            }

            if (!byId.containsKey(one.getLegacyId())) {
              byId.put(one.getLegacyId(), one);
            }

            if (one.getLegacyData() == 0) {
              byId.put(one.getLegacyId(), one);
            }

            if (!byName.containsKey(String.valueOf(legacyId)) && data <= 0) {
              byName.put(String.valueOf(legacyId), one);
            } else {
              byName.put(legacyId + ":" + data, one);
            }
          }

          byRealMaterial.put(mat, one);
        }
      }
    }

    CMIScheduler.runTask(
        () -> {
          for (Material onex : Material.values()) {
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
              byNameMaterial.put(
                  onex.getKey().getKey().replace("_", "").replace(" ", "").toLowerCase(), onex);
              byNameMaterial.put(
                  onex.getKey()
                      .toString()
                      .replace("_", "")
                      .replace(" ", "")
                      .toLowerCase()
                      .replace(":", ""),
                  onex);
            } else {
              byNameMaterial.put(
                  onex.toString().replace("_", "").replace(" ", "").toLowerCase(), onex);
            }
          }
        });
  }

  @Deprecated
  public CMIItemStack getItem(Material mat) {
    CMIMaterial cmat = CMIMaterial.get(mat);
    return cmat != null && !cmat.equals(CMIMaterial.NONE) ? new CMIItemStack(cmat) : null;
  }

  public CMIItemStack getItem(CMIMaterial mat) {
    return mat != null && !mat.equals(CMIMaterial.NONE) ? new CMIItemStack(mat) : null;
  }

  public CMIItemStack getItem(ItemStack item) {
    if (item == null) {
      item = new ItemStack(Material.AIR);
    }

    CMIItemStack cm = this.getItem(CMIMaterial.get(item));
    if (cm == null) {
      return new CMIItemStack(item.getType());
    }

    cm.setItemStack(item);
    return cm;
  }

  public void clearHeadCache() {
    this.headCache.clear();
  }

  public CMIItemStack getItem(String name) {
    return this.getItem(name, null);
  }

  public CMIItemStack getItem(String name, CMIAsyncHead ahead) {
    if (name == null) {
      return null;
    }

    CMIItemStack cm = null;
    String original = name.replace("minecraft:", "");
    name = name.replace("minecraft:", "");
    name = name.replace("_", "");
    Integer amount = null;
    CMIEntityType entityType = null;
    String tag = null;
    if (name.contains("{") && name.contains("}")) {
      Pattern ptr = Pattern.compile("(\\{).+(\\})");
      Matcher match = ptr.matcher(name);
      if (match.find()) {
        tag = match.group();
        name = name.replace(match.group(), "");
      }

      name = name.replace("  ", " ");
    }

    name = name.toLowerCase();
    if (name.contains("-")) {
      String[] split = name.split("-");
      if (split.length > 1) {
        String a = split[split.length - 1];

        try {
          amount = Integer.parseInt(a);
          name = name.substring(0, name.length() - (split[split.length - 1].length() + 1));
        } catch (Exception var29) {
        }
      }
    }

    String subdata = null;
    if (name.contains(":")) {
      CMIMaterial mat = byName.get(name);
      if (mat != null) {
        return new CMIItemStack(mat);
      }

      try {
        subdata = name.split(":")[1];
      } catch (Throwable var28) {
      }
    }

    if (name.contains(">")) {
      String[] split = name.split(">");
      if (split.length > 1) {
        String a = name.split(">")[1];

        try {
          amount = Integer.parseInt(a);
        } catch (Exception var27) {
        }
      }

      name = name.split(">")[0];
    }

    short data = -999;
    if (name.contains(":") || name.contains("-")) {
      try {
        data = (short) Integer.parseInt(name.split(":")[1]);
      } catch (Exception var26) {
      }

      try {
        if (name.contains(":")) {
          entityType = CMIEntityType.getByName(name.split(":")[1]);
        } else if (name.contains("-")) {
          entityType = CMIEntityType.getByName(name.split("-")[1]);
        }

        if (entityType != null) {
          data = (short) entityType.getId();
        } else if (name.split(":")[1].equalsIgnoreCase("random")
            && !CMILibConfig.mysterySpawners.isEmpty()) {
          Collections.shuffle(CMILibConfig.mysterySpawners);
          entityType = CMIEntityType.getByName(CMILibConfig.mysterySpawners.get(0));
          if (entityType != null) {
            data = (short) entityType.getId();
          }
        }
      } catch (Exception var25) {
      }

      name = name.split(":")[0];
      name = name.split("-")[0];
    }

    switch (name.toLowerCase()) {
      case "skull":
        cm = CMIMaterial.SKELETON_SKULL.newCMIItemStack();
        break;
      case "door":
        cm = CMIMaterial.SPRUCE_DOOR.newCMIItemStack();
        break;
      case "head":
      case "playerhead":
        cm = CMIMaterial.PLAYER_HEAD.newCMIItemStack();
        data = 3;
        if (original.contains(":")) {
          ItemStack old = this.headCache.get(original);
          if (old == null || ahead != null && ahead.isIgnoreCached()) {
            String[] split = original.split(":");
            if (split.length > 1) {
              String d = split[1];
              if (d.length() <= 36 && !d.startsWith("eyJ0ZXh0dXJlcy")) {
                ItemStack skull = CMIMaterial.PLAYER_HEAD.newItemStack();
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                if (d.length() == 36) {
                  try {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(UUID.fromString(d));
                    if (offPlayer != null && offPlayer.getName() != null) {
                      skullMeta.setOwningPlayer(offPlayer);
                      skull.setItemMeta(skullMeta);
                      cm.setItemStack(skull);
                      this.headCache.put(original, skull);
                    } else {
                      if (ahead != null) {
                        ahead.setAsyncHead(true);
                      }

                      CMIScheduler.get()
                          .runTaskAsynchronously(
                              () -> {
                                OfflinePlayer offlineP = Bukkit.getOfflinePlayer(d);
                                if (offlineP != null) {
                                  ItemStack lskull = skull;
                                  SkullMeta lskullMeta = (SkullMeta) lskull.getItemMeta();
                                  lskullMeta.setOwningPlayer(offlineP);
                                  lskull.setItemMeta(lskullMeta);
                                  if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)
                                      && CMILib.getInstance().isCmiPresent()) {
                                    CMISkin skin = CMI.getInstance().getSkinManager().getSkin(d);
                                    if (skin != null) {
                                      lskull =
                                          CMILib.getInstance()
                                              .getReflectionManager()
                                              .setSkullTexture(
                                                  lskull, offlineP.getName(), skin.getSkin());
                                      lskullMeta = (SkullMeta) lskull.getItemMeta();
                                    }
                                  }

                                  CMINBT nbt = new CMINBT(lskull);
                                  lskull =
                                      (ItemStack)
                                          nbt.setString("SkullOwner.Name", offlineP.getName());
                                  this.headCache.put(original, lskull);
                                  Bukkit.createInventory(null, InventoryType.CHEST, "")
                                      .addItem(new ItemStack[] {lskull});
                                  SkullMeta smeta = (SkullMeta) lskull.getItemMeta();
                                  lskull.setItemMeta(smeta);
                                  this.headCache.put(original, lskull);
                                  if (ahead != null) {
                                    ahead.afterAsyncUpdate(lskull);
                                  }
                                }
                              });
                    }
                  } catch (Exception var30) {
                  }
                } else {
                  if (!Version.isCurrentEqualOrHigher(Version.v1_16_R3)) {
                    skullMeta.setOwner(d);
                    skull.setItemMeta(skullMeta);
                    cm.setItemStack(skull);
                    this.headCache.put(original, skull);
                  } else if ((ahead != null && !ahead.isForce() || ahead == null)
                      && Bukkit.getPlayer(d) != null) {
                    Player player = Bukkit.getPlayer(d);
                    skullMeta.setOwningPlayer(player);
                    skull.setItemMeta(skullMeta);
                    this.headCache.put(original, skull);
                    if (ahead != null) {
                      ahead.afterAsyncUpdate(skull);
                    }
                  } else {
                    CMIEntityType type = CMIEntityType.getByName(d);
                    if (type != null) {
                      skullMeta = (SkullMeta) type.getHead().getItemMeta();
                      skull.setItemMeta(skullMeta);
                      cm.setItemStack(skull);
                      subdata = null;
                    } else {
                      if (ahead != null) {
                        ahead.setAsyncHead(true);
                      }

                      CMIScheduler.get()
                          .runTaskAsynchronously(
                              () -> {
                                OfflinePlayer offlineP = Bukkit.getOfflinePlayer(d);
                                if (offlineP != null) {
                                  ItemStack lskull = skull;
                                  SkullMeta lskullMeta = (SkullMeta) lskull.getItemMeta();
                                  lskullMeta.setOwningPlayer(offlineP);
                                  lskull.setItemMeta(lskullMeta);
                                  if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)
                                      && CMILib.getInstance().isCmiPresent()) {
                                    CMISkin skin =
                                        CMI.getInstance()
                                            .getSkinManager()
                                            .getSkin(offlineP.getName());
                                    if (skin != null) {
                                      lskull =
                                          CMILib.getInstance()
                                              .getReflectionManager()
                                              .setSkullTexture(
                                                  lskull, offlineP.getName(), skin.getSkin());
                                      lskullMeta = (SkullMeta) lskull.getItemMeta();
                                    }
                                  }

                                  CMINBT nbt = new CMINBT(lskull);
                                  lskull =
                                      (ItemStack)
                                          nbt.setString("SkullOwner.Name", offlineP.getName());
                                  this.headCache.put(original, lskull);
                                  Bukkit.createInventory(null, InventoryType.CHEST, "")
                                      .addItem(new ItemStack[] {lskull});
                                  SkullMeta smeta = (SkullMeta) lskull.getItemMeta();
                                  lskull.setItemMeta(smeta);
                                  this.headCache.put(original, lskull);
                                  if (ahead != null) {
                                    ahead.afterAsyncUpdate(lskull);
                                  }
                                }
                              });
                    }
                  }

                  if (ahead == null || !ahead.isAsyncHead()) {
                    skull.setItemMeta(skullMeta);
                    this.headCache.put(original, skull);
                  }
                }
              } else {
                String texture = d;
                if (texture.contains("-")) {
                  try {
                    String[] sp = texture.split("-");
                    Double.parseDouble(sp[sp.length - 1]);
                    texture =
                        texture.substring(0, texture.length() - 1 - sp[sp.length - 1].length());
                  } catch (Throwable var24) {
                  }
                }

                ItemStack skull = CMIItemStack.getHead(texture);
                this.headCache.put(original, skull);
                cm.setItemStack(skull);
              }
            }
          } else {
            cm.setItemStack(old);
          }
        }
    }

    CMIMaterial cmat = CMIMaterial.get(subdata == null ? name : name + ":" + subdata);
    if (cmat == null || cmat.equals(CMIMaterial.NONE)) {
      cmat = CMIMaterial.get(name);
    }

    if (cmat == null || cmat.equals(CMIMaterial.NONE) || cm != null && !cm.getCMIType().isNone()) {
      cmat = CMIMaterial.get(subdata == null ? original : original + ":" + subdata);
    } else {
      cm = cmat.newCMIItemStack();
    }

    if (cmat != null
        && !cmat.equals(CMIMaterial.NONE)
        && (cm == null || cm.getCMIType().isNone())) {
      cm = cmat.newCMIItemStack();
    }

    if (cm == null) {
      Material match = getMaterialByName(original);
      if (match != null && new CMIItemStack(match).getItemStack() != null) {
        cm = new CMIItemStack(match);
      }
    }

    if (cm != null && entityType != null) {
      cm.setEntityType(entityType);
    }

    CMIItemStack ncm = null;
    if (cm != null) {
      ncm = cm.clone();
    }

    if (ncm != null && data != -999) {
      if (ncm.getMaxDurability() > 15) {
        ncm.setData((short) 0);
      } else {
        ncm.setData(data);
      }
    }

    if (ncm != null && tag != null) {
      ncm.setTag(CMIChatColor.translate(tag));
    }

    if (ncm != null && amount != null) {
      ncm.setAmount(amount);
    }

    if (ncm != null && subdata != null) {
      if (ncm.getCMIType().isPotion()
          || ncm.getCMIType().equals(CMIMaterial.SPLASH_POTION)
          || ncm.getCMIType().equals(CMIMaterial.TIPPED_ARROW)) {
        PotionType potionType = null;
        Boolean upgraded = false;
        Boolean extended = false;
        String[] split = subdata.split("-");

        try {
          String n = (split.length > 0 ? split[0] : subdata).replace("_", "");
          if (split.length > 1) {
            try {
              upgraded = Boolean.parseBoolean(split[1]);
            } catch (Exception var22) {
            }
          }

          if (split.length > 2) {
            try {
              extended = Boolean.parseBoolean(split[2]);
            } catch (Exception var21) {
            }
          }

          ItemStack item = ncm.getItemStack();
          if (extended && upgraded) {
            extended = false;
          }

          PotionMeta meta = (PotionMeta) item.getItemMeta();
          potionType = CMIPotionType.get(n, upgraded, extended);
          if (potionType != null) {
            if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
              meta.setBasePotionType(potionType);
            } else {
              PotionData pdata = new PotionData(potionType, extended, upgraded);
              meta.setBasePotionData(pdata);
            }
          }

          if (CMIMaterial.TIPPED_ARROW.equals(CMIMaterial.get(item)) && potionType != null) {
            if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
              meta.setBasePotionType(potionType);
            } else {
              if (!potionType.isExtendable()) {
                extended = false;
              }

              if (!potionType.isUpgradeable()) {
                upgraded = false;
              }

              meta.setBasePotionData(new PotionData(potionType, extended, upgraded));
            }
          }

          item.setItemMeta(meta);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      if (ncm.getItemStack() != null
          && ncm.getItemStack().getItemMeta() instanceof EnchantmentStorageMeta) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) ncm.getItemStack().getItemMeta();
        List<String> split = new ArrayList<>();
        if (subdata.contains(";")) {
          split.addAll(Arrays.asList(subdata.split(";")));
        } else {
          split.add(subdata);
        }

        for (String one : split) {
          Enchantment type = null;
          Integer level = 1;
          if (one.contains("x")) {
            try {
              level = Integer.parseInt(one.split("x")[one.split("x").length - 1]);
              type = CMIEnchantment.get(one.substring(0, one.length() - ("x" + level).length()));
            } catch (Exception var20) {
            }
          } else {
            type = CMIEnchantment.get(one);
          }

          if (type != null) {
            meta.addStoredEnchant(type, level, true);
          }
        }

        ncm.getItemStack().setItemMeta(meta);
      }
    }

    return ncm;
  }

  public List<Recipe> getAllRecipes() {
    List<Recipe> results = new ArrayList<>();
    Iterator<Recipe> iter = Bukkit.recipeIterator();

    while (iter.hasNext()) {
      Recipe recipe = iter.next();
      results.add(recipe);
    }

    return results;
  }

  public List<Recipe> getRecipesFor(ItemStack result) {
    List<Recipe> results = new ArrayList<>();
    Iterator<Recipe> iter = Bukkit.recipeIterator();

    while (iter.hasNext()) {
      Recipe recipe = iter.next();
      ItemStack stack = recipe.getResult();
      if (stack.getType() == result.getType()
          && (result.getDurability() == -1 || result.getDurability() == stack.getDurability())) {
        results.add(recipe);
      }
    }

    return results;
  }

  public Material getMaterial(String name) {
    CMIItemStack cm = this.getItem(name);
    return cm == null ? Material.AIR : cm.getType();
  }

  public void loadLocale() {
    String path = "Translations" + File.separator + "Items";
    File cc = new File(CMILib.getInstance().getDataFolder(), path);
    if (!cc.isDirectory()) {
      cc.mkdir();
    }

    ConfigReader locale = null;

    try {
      locale =
          new ConfigReader(
              CMILib.getInstance(), path + File.separator + "items_" + CMILibConfig.lang + ".yml");
    } catch (Throwable e) {
      e.printStackTrace();
    }

    if (locale == null && !CMILibConfig.lang.equalsIgnoreCase("EN")) {
      CMIMessages.consoleMessage(
          "Failed to load item name ("
              + CMILibConfig.lang
              + ") locale file. Trying to default to EN version");

      try {
        locale = new ConfigReader(CMILib.getInstance(), path + File.separator + "items_EN.yml");
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    if (locale == null) {
      CMIMessages.consoleMessage("Failed to load item name locale file");
    } else {
      for (CMIMaterial one : CMIMaterial.values()) {
        if (!one.isLegacy() && !one.isNone()) {
          String name = locale.get(one.toString(), one.getName());
          if (!name.equals(one.getName())) {
            one.setTranslatedName(name);
          }
        }
      }

      locale.save();
    }
  }

  public static Material getMaterialByName(String name) {
    name = name.replace("_", "").replace(" ", "").toLowerCase();
    Material mat = byNameMaterial.get(name);
    if (mat != null) {
      return mat;
    }

    if (name.contains(":")) {
      String[] split = name.split(":", 2);
      mat = byNameMaterial.get(split[0]);
      if (mat != null) {
        return mat;
      }

      mat = byNameMaterial.get(split[1]);
      if (mat != null) {
        return mat;
      }

      try {
        mat = Material.matchMaterial(split[0]);
        if (mat != null) {
          return mat;
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }

      try {
        mat = Material.matchMaterial(split[1]);
        if (mat != null) {
          return mat;
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    try {
      mat = Material.matchMaterial(name);
      if (mat != null) {
        return mat;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    try {
      mat = Material.matchMaterial(name.replace("-", "_"));
      if (mat != null) {
        return mat;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return null;
  }
}
