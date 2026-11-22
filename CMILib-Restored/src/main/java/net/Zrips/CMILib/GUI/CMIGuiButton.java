package net.Zrips.CMILib.GUI;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CommandType;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CMIGuiButton {
  private Integer slot = null;
  private GUIManager.GUIFieldType fieldType = GUIManager.GUIFieldType.Locked;
  private boolean closeInv = false;
  private EnumMap<GUIManager.GUIClickType, List<GUIButtonCommand>> commandMap =
      new EnumMap<>(GUIManager.GUIClickType.class);
  private List<String> permissions = new ArrayList<>();
  private ItemStack item = null;
  private CMITask sched = null;
  private int updateInterval = 20;
  private int ticks = 0;
  private CMIGui sgui;

  public CMIGuiButton clone() {
    CMIGuiButton b = new CMIGuiButton(this.slot, this.fieldType, this.item);
    b.setPermissions(new ArrayList<>(this.permissions));
    b.setCommandMap(new HashMap<>(this.commandMap));
    return b;
  }

  public CMIGuiButton(Integer slot, GUIManager.GUIFieldType fieldType, ItemStack item) {
    this.slot = slot;
    this.fieldType = fieldType;
    this.item = item == null ? null : item.clone();
  }

  public CMIGuiButton(Integer slot) {
    this.slot = slot;
  }

  public void hideItemFlags() {
    if (this.item != null) {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(
            new ItemFlag[] {
              ItemFlag.HIDE_ARMOR_TRIM,
              ItemFlag.HIDE_ATTRIBUTES,
              ItemFlag.HIDE_DESTROYS,
              ItemFlag.HIDE_DYE,
              ItemFlag.HIDE_ENCHANTS,
              ItemFlag.HIDE_UNBREAKABLE,
              ItemFlag.HIDE_PLACED_ON
            });

        try {
          meta.addItemFlags(new ItemFlag[] {ItemFlag.HIDE_ADDITIONAL_TOOLTIP});
        } catch (Throwable var4) {
        }

        try {
          meta.addItemFlags(new ItemFlag[] {ItemFlag.HIDE_STORED_ENCHANTS});
        } catch (Throwable var3) {
        }

        this.item.setItemMeta(meta);
      } else {
        this.item = CMINBT.HideFlag(this.item, 63);
      }
    }
  }

  public CMIGuiButton hideToltip() {
    if (!Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
      return this;
    }

    ItemMeta meta = this.item.getItemMeta();
    if (meta != null) {
      meta.setHideTooltip(true);
      this.item.setItemMeta(meta);
    }

    return this;
  }

  public CMIGuiButton(ItemStack item) {
    this.item = item == null ? null : item.clone();
  }

  public CMIGuiButton(CMIMaterial mat) {
    this.item = mat == null ? null : mat.newItemStack();
  }

  public CMIGuiButton(Integer slot, CMIItemStack item) {
    this(slot, item == null ? null : item.getItemStack());
  }

  public CMIGuiButton(Integer slot, Material material) {
    this(slot, CMIMaterial.get(material), null);
  }

  public CMIGuiButton(Integer slot, CMIMaterial material) {
    this(slot, material, null);
  }

  @Deprecated
  public CMIGuiButton(Integer slot, Material material, int data) {
    this(slot, material, data, null);
  }

  public CMIGuiButton(Integer slot, Material material, String name) {
    this(slot, CMIMaterial.get(material), name);
  }

  public CMIGuiButton(Integer slot, CMIMaterial material, String name) {
    this.slot = slot;
    this.item = material == null ? null : material.newItemStack();
    if (this.item != null && name != null) {
      ItemMeta meta = this.item.getItemMeta();
      if (meta != null) {
        meta.setDisplayName(CMIChatColor.translate(name));
        this.item.setItemMeta(meta);
      }
    }
  }

  @Deprecated
  public CMIGuiButton(Integer slot, Material material, int data, String name) {
    this.slot = slot;
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      this.item = new ItemStack(material, 1);
    } else {
      this.item = new ItemStack(material, 1, (short) data);
    }

    if (name != null) {
      ItemMeta meta = this.item.getItemMeta();
      meta.setDisplayName(CMIChatColor.translate(name));
      this.item.setItemMeta(meta);
    }
  }

  public CMIGuiButton(Integer slot, ItemStack item) {
    this(slot, item, false);
  }

  public CMIGuiButton(Integer slot, ItemStack item, boolean hideFlags) {
    this.slot = slot;
    this.item = item == null ? null : item.clone();
    if (this.item != null && this.item.getDurability() == 32767) {
      CMIMaterial d = CMIMaterial.getRandom(CMIMaterial.get(this.item));
      if (d != null && d.getLegacyData() != -1) {
        this.item.setDurability(d.getLegacyData());
      }
    }

    if (hideFlags) {
      this.hideItemFlags();
    }
  }

  public void setGui(CMIGui sgui) {
    this.sgui = sgui;
  }

  public void startAutoUpdate(int intervalTicks) {
    this.updateInterval = intervalTicks;
    this.tasker();
  }

  @Deprecated
  public void startAutoUpdate(CMIGui sgui, int intervalTicks) {
    this.updateInterval = intervalTicks;
    this.sgui = sgui;
    this.tasker();
  }

  private void tasker() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }

    final CMIGuiButton b = this;
    this.sched =
        CMIScheduler.get()
            .scheduleSyncRepeatingTask(
                new Runnable() {
                  @Override
                  public void run() {
                    CMIGuiButton.this.ticks++;
                    if (CMIGuiButton.this.sgui != null
                        && CMILib.getInstance()
                                .getGUIManager()
                                .getGui(CMIGuiButton.this.sgui.getPlayer())
                            != CMIGuiButton.this.sgui
                        && CMIGuiButton.this.sched != null) {
                      CMIGuiButton.this.sched.cancel();
                      CMIGuiButton.this.sched = null;
                    } else {
                      CMIGuiButton.this.updateLooks();
                      CMIGuiButton.this.update(CMIGuiButton.this.sgui);
                      if (CMIGuiButton.this.sgui != null) {
                        CMIGuiButton.this.sgui.updateButton(b);
                      }
                    }
                  }
                },
                20L,
                this.updateInterval);
  }

  public void updateLooks() {}

  public void update() {
    if (this.sgui != null) {
      this.sgui.updateButton(this);
    }
  }

  public void update(CMIGui gui) {
    if (gui != null) {
      gui.updateButton(this);
    }
  }

  public Integer getSlot() {
    return this.slot;
  }

  public CMIGuiButton setSlot(Integer slot) {
    this.slot = slot;
    return this;
  }

  public GUIManager.GUIFieldType getFieldType() {
    return this.fieldType;
  }

  public CMIGuiButton setFieldType(GUIManager.GUIFieldType fieldType) {
    this.fieldType = fieldType;
    return this;
  }

  public CMIGuiButton lockField() {
    this.fieldType = GUIManager.GUIFieldType.Locked;
    return this;
  }

  public CMIGuiButton unlockField() {
    this.fieldType = GUIManager.GUIFieldType.Free;
    return this;
  }

  public boolean isLocked() {
    return this.fieldType.equals(GUIManager.GUIFieldType.Locked);
  }

  public List<String> getPermissions() {
    return this.permissions;
  }

  public CMIGuiButton addPermission(String perm) {
    this.permissions.add(perm);
    return this;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }

  public List<GUIButtonCommand> getCommands(GUIManager.GUIClickType type) {
    List<GUIButtonCommand> list = this.commandMap.get(type);
    if (list == null) {
      list = new ArrayList<>();
    }

    return list;
  }

  public CMIGuiButton setName(String name) {
    if (this.item == null) {
      return this;
    }

    ItemMeta meta = this.item.getItemMeta();
    if (meta == null) {
      return this;
    }

    if (name.contains("\n")) {
      String[] split = name.split("\\n");
      meta.setDisplayName(CMIChatColor.translate(split[0]));
      this.item.setItemMeta(meta);

      for (int i = 1; i < split.length; i++) {
        this.addLore(split[i]);
      }
    } else {
      meta.setDisplayName(CMIChatColor.translate(name));
      this.item.setItemMeta(meta);
    }

    return this;
  }

  public CMIGuiButton addLore(List<String> l) {
    for (String one : CMIList.spreadList(l)) {
      this.addLore(one);
    }

    return this;
  }

  public CMIGuiButton addLore(String l) {
    if (this.item == null) {
      return this;
    }

    ItemMeta meta = this.item.getItemMeta();
    if (meta == null) {
      return this;
    }

    List<String> lore = meta.getLore();
    if (lore == null) {
      lore = new ArrayList<>();
    }

    if (l.contains("\\n")) {
      String[] split = l.split("\\\\n");

      for (String one : split) {
        lore.add(CMIChatColor.translate(one));
      }
    } else if (l.contains("\n")) {
      String[] split = l.split("\\n");

      for (String one : split) {
        lore.add(CMIChatColor.translate(one));
      }
    } else {
      lore.add(CMIChatColor.translate(l));
    }

    meta.setLore(lore);
    this.item.setItemMeta(meta);
    return this;
  }

  public CMIGuiButton clearLore() {
    if (this.item == null) {
      return this;
    }

    ItemMeta meta = this.item.getItemMeta();
    if (meta != null) {
      meta.setLore(new ArrayList());
      this.item.setItemMeta(meta);
    }

    return this;
  }

  public CMIGuiButton addItemName(String name) {
    if (this.item == null) {
      return this;
    }

    ItemMeta meta = this.item.getItemMeta();
    meta.setDisplayName(CMIChatColor.translate(name));
    this.item.setItemMeta(meta);
    return this;
  }

  public CMIGuiButton addCommand(String command) {
    return this.addCommand(null, command);
  }

  public CMIGuiButton addCommand(String command, CommandType vis) {
    return this.addCommand(null, command, vis);
  }

  public CMIGuiButton addCommand(GUIManager.GUIClickType type, String command) {
    return this.addCommand(type, command, CommandType.gui);
  }

  public CMIGuiButton addCommand(GUIManager.GUIClickType type, String command, CommandType vis) {
    if (type == null) {
      for (GUIManager.GUIClickType one : GUIManager.GUIClickType.values()) {
        List<GUIButtonCommand> list = this.commandMap.get(one);
        if (list == null) {
          list = new ArrayList<>();
        }

        list.add(new GUIButtonCommand(command, vis));
        this.commandMap.put(one, list);
      }
    } else {
      List<GUIButtonCommand> list = this.commandMap.get(type);
      if (list == null) {
        list = new ArrayList<>();
      }

      list.add(new GUIButtonCommand(command, vis));
      this.commandMap.put(type, list);
    }

    return this;
  }

  public void click() {}

  public void click(GUIManager.GUIClickType type) {}

  public CMIGuiButton addCommand(Location loc) {
    if (loc == null) {
      return this;
    }

    this.addCommand(
        "cmi tppos "
            + loc.getWorld().getName()
            + " "
            + loc.getX()
            + " "
            + loc.getY()
            + " "
            + loc.getBlockZ()
            + " "
            + loc.getPitch()
            + " "
            + loc.getYaw());
    return this;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public ItemStack getItem(Player player) {
    if (this.item == null) {
      return null;
    }

    ItemStack i = this.item.clone();
    if (this.isLocked() && !CMIMaterial.isAir(i.getType())) {
      i = (ItemStack) new CMINBT(i).setString("CMIGUIIcon", "LIProtection");
    }

    ItemMeta meta = i.hasItemMeta() ? i.getItemMeta() : null;
    boolean modified = false;
    if (meta == null) {
      try {
        meta = i.getItemMeta();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    if (meta != null && meta.hasDisplayName()) {
      String oldName = meta.getDisplayName();
      String newName =
          CMILib.getInstance()
              .getPlaceholderAPIManager()
              .updatePlaceHolders(player, meta.getDisplayName());
      if (!oldName.equals(newName)) {
        meta.setDisplayName(newName);
        modified = true;
      }
    }

    if (meta != null && meta.hasLore()) {
      List<String> oldLore = meta.getLore();
      List<String> newLore =
          CMILib.getInstance()
              .getPlaceholderAPIManager()
              .updatePlaceHolders(player, meta.getLore());
      if (!oldLore.equals(newLore)) {
        meta.setLore(newLore);
        modified = true;
      }
    }

    if (meta != null && modified) {
      i.setItemMeta(meta);
    }

    return i;
  }

  public CMIGuiButton setItem(CMIMaterial material) {
    return this.setItem(material == null ? null : material.newItemStack().clone());
  }

  public CMIGuiButton setItem(ItemStack item) {
    this.item = item == null ? null : item.clone();
    return this;
  }

  public void setCommandMap(HashMap<GUIManager.GUIClickType, List<GUIButtonCommand>> commandMap) {
    this.commandMap.clear();

    for (Entry<GUIManager.GUIClickType, List<GUIButtonCommand>> one : commandMap.entrySet()) {
      this.commandMap.put(one.getKey(), one.getValue());
    }
  }

  public boolean isCloseInv() {
    return this.closeInv;
  }

  public void setCloseInv(boolean closeInv) {
    this.closeInv = closeInv;
  }

  public CMIGui getGui() {
    return this.sgui;
  }

  public int getTicks() {
    return this.ticks;
  }

  public void setGlowing() {
    this.item.addUnsafeEnchantment(CMIEnchantment.get("LUCK"), 1);
    this.hideItemFlags();
  }

  public void removeGlowing() {
    this.item.removeEnchantment(CMIEnchantment.get("LUCK"));
    this.hideItemFlags();
  }
}
