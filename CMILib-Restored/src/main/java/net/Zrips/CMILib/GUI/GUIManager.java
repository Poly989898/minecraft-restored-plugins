package net.Zrips.CMILib.GUI;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIPlayerInventory;
import net.Zrips.CMILib.Permissions.CMILPerm;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CMICommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIManager {
  private HashMap<UUID, CMIGui> map = new HashMap<>();
  private CMILib plugin;
  private static int MAX_ENTRIES = 50;
  public static LinkedHashMap<UUID, Long> limit =
      new LinkedHashMap<UUID, Long>(MAX_ENTRIES + 1, 0.75F, false) {
        @Override
        protected boolean removeEldestEntry(Entry<UUID, Long> eldest) {
          return this.size() > GUIManager.MAX_ENTRIES;
        }
      };
  public static final String CMIGUIIcon = "CMIGUIIcon";
  public static final String LIProtection = "LIProtection";
  public static boolean usePackets = false;

  public GUIManager(CMILib plugin) {
    this.plugin = plugin;
  }

  public void closeAll() {
    for (Entry<UUID, CMIGui> one : this.map.entrySet()) {
      Player player = Bukkit.getPlayer(one.getKey());
      if (player != null) {
        player.closeInventory();
      }
    }
  }

  public GUIManager.GUIClickType getClickType(
      boolean left, boolean shift, InventoryAction action, ClickType clickType) {
    if ((action == null || !action.equals(InventoryAction.CLONE_STACK))
        && (clickType == null || !clickType.equals(ClickType.MIDDLE))) {
      if (action != null) {
        if (action.equals(InventoryAction.DROP_ONE_SLOT)) {
          return GUIManager.GUIClickType.Q;
        }

        if (action.equals(InventoryAction.DROP_ALL_SLOT)) {
          return GUIManager.GUIClickType.QControl;
        }
      }

      if (clickType != null) {
        if (clickType.equals(ClickType.LEFT)) {
          return GUIManager.GUIClickType.Left;
        }

        if (clickType.equals(ClickType.SHIFT_LEFT)) {
          return GUIManager.GUIClickType.LeftShift;
        }

        if (clickType.equals(ClickType.RIGHT)) {
          return GUIManager.GUIClickType.Right;
        }

        if (clickType.equals(ClickType.SHIFT_RIGHT)) {
          return GUIManager.GUIClickType.RightShift;
        }
      }

      if (left && !shift) {
        return GUIManager.GUIClickType.Left;
      } else if (left && shift) {
        return GUIManager.GUIClickType.LeftShift;
      } else {
        return !left && !shift ? GUIManager.GUIClickType.Right : GUIManager.GUIClickType.RightShift;
      }
    } else {
      return GUIManager.GUIClickType.MiddleMouse;
    }
  }

  public boolean processClick(
      Player player, List<Integer> buttons, GUIManager.GUIClickType clickType) {
    return this.processClick(player, null, buttons, clickType);
  }

  public boolean processClick(
      Player player,
      ItemStack currentItem,
      List<Integer> buttons,
      GUIManager.GUIClickType clickType) {
    Long time = limit.get(player.getUniqueId());
    if (time != null && time > System.currentTimeMillis()) {
      return false;
    }

    CMIGui gui = this.map.get(player.getUniqueId());
    if (gui == null) {
      return false;
    }

    if (!gui.isAllowMoveAll()) {
      limit.put(player.getUniqueId(), System.currentTimeMillis() + 150L);
    }

    int clicks = 0;

    try {
      if (!gui.getInv().equals(CMIPlayerInventory.getTopInventory(player))
          || gui.getInv().getHolder() != null) {
        player.closeInventory();
        this.map.remove(player.getUniqueId());
        return false;
      }
    } catch (Throwable var14) {
    }

    for (Integer one : buttons) {
      CMIGuiButton button = gui.getButtons().get(one);
      if (!gui.click(one, clickType, currentItem)) {
        return false;
      }

      if (button != null) {
        clicks++;
        boolean canClick = true;

        for (String oneC : button.getPermissions()) {
          if (!CMILPerm.hasPermission(player, oneC, true, false)) {
            canClick = false;
          }
        }

        if (canClick) {
          CMIScheduler.runTask(
              () -> {
                for (GUIButtonCommand oneCx : button.getCommands(clickType)) {
                  CMICommand.performCommand(player, oneCx.getCommand(), oneCx.getVis());
                }
              });
        }

        button.click();
        button.click(clickType);
        if (button.isCloseInv()) {
          player.closeInventory();
        }

        if (!button.getCommands(clickType).isEmpty()) {
          break;
        }
      }
    }

    if (clicks == 0) {
      gui.outsideClick(clickType);
    }

    return true;
  }

  public boolean isLockedPart(Player player, List<Integer> buttons) {
    return this.isLockedPart(player, buttons, null);
  }

  public boolean isLockedPart(Player player, List<Integer> buttons, ItemStack item) {
    CMIGui gui = this.map.get(player.getUniqueId());
    if (gui == null) {
      return false;
    }

    int size = gui.getInv().getSize();
    int mainInvMax = size + 36 - 9;
    int quickbar = size + 36;
    boolean itemInHand = item != null && !item.getType().equals(Material.AIR);

    for (Integer one : buttons) {
      if (one <= quickbar && quickbar >= 0) {
        if (one < size
            && gui.isLocked(GUIManager.InvType.Gui)
            && gui.isPermLocked(GUIManager.InvType.Gui)) {
          return true;
        }

        if (one < size && gui.isNoItemPlacement(GUIManager.InvType.Gui) && itemInHand) {
          return true;
        }

        if (one >= size
            && one < mainInvMax
            && gui.isLocked(GUIManager.InvType.Main)
            && gui.isPermLocked(GUIManager.InvType.Main)) {
          return true;
        }

        if (one >= mainInvMax
            && one < quickbar
            && (gui.isLocked(GUIManager.InvType.Quickbar)
                    && gui.isPermLocked(GUIManager.InvType.Quickbar)
                || gui.isLocked(GUIManager.InvType.Main)
                    && gui.isPermLocked(GUIManager.InvType.Main))) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean canClick(Player player, List<Integer> buttons) {
    return this.canClick(player, buttons, null);
  }

  public boolean canClick(Player player, List<Integer> buttons, ItemStack item) {
    try {
      CMIGui gui = this.map.get(player.getUniqueId());
      if (gui == null) {
        return true;
      }

      boolean itemInHand = item != null && !item.getType().equals(Material.AIR);

      for (Integer one : buttons) {
        CMIGuiButton button = gui.getButtons().get(one);
        if (button != null) {
          if (button.getFieldType() == GUIManager.GUIFieldType.Locked) {
            return false;
          }

          if (itemInHand && button.getFieldType() == GUIManager.GUIFieldType.noPlacing) {
            return false;
          }
        }
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public CMIGui getGui(Player player) {
    return this.map.get(player.getUniqueId());
  }

  public boolean isOpenedGui(Player player) {
    CMIGui gui = this.map.get(player.getUniqueId());
    if (gui == null) {
      return false;
    } else if (player.getOpenInventory() == null) {
      return false;
    } else if (!validInventory(gui)) {
      this.map.remove(player.getUniqueId());
      player.closeInventory();
      return false;
    } else {
      return true;
    }
  }

  public boolean removePlayer(Player player) {
    if (player == null) {
      return false;
    }

    CMIGui removed = this.map.remove(player.getUniqueId());
    if (removed == null) {
      return false;
    }

    if (player.getOpenInventory() != null
        && CMIPlayerInventory.getTopInventory(player).equals(removed.getInv())) {
      player.closeInventory();
    }

    removed.onClose();
    removed.playCloseSound();
    CMIGUICloseEvent event = new CMIGUICloseEvent(player, removed);
    Bukkit.getServer().getPluginManager().callEvent(event);
    return true;
  }

  public void generateInventory(CMIGui gui) {
    Inventory guiInv = null;
    if (gui.getInvSize() != null) {
      guiInv = Bukkit.createInventory(null, gui.getInvSize().getFields(), gui.getTitle());
    } else {
      guiInv = Bukkit.createInventory(null, gui.getInvType(), gui.getTitle());
    }

    if (guiInv != null) {
      for (Entry<Integer, CMIGuiButton> one : gui.getButtons().entrySet()) {
        if (one.getKey() <= guiInv.getSize()) {
          try {
            ItemStack item = one.getValue().getItem(gui.getPlayer());
            if (one.getKey() < guiInv.getSize()) {
              guiInv.setItem(one.getKey(), item);
            }
          } catch (Throwable e) {
            e.printStackTrace();
            break;
          }
        }
      }

      gui.setInv(guiInv);
    }
  }

  public void openGui(CMIGui gui) {
    Player player = gui.getPlayer();
    if (!player.isSleeping()) {
      CMIGui oldGui = null;
      if (this.isOpenedGui(player)) {
        oldGui = this.plugin.getGUIManager().getGui(player);
        if (!gui.isSimilar(oldGui)) {
          oldGui = null;
        }
      }

      if (oldGui == null) {
        this.generateInventory(gui);
        player.closeInventory();
        if (gui.getInv().getHolder() != null) {
          return;
        }

        player.openInventory(gui.getInv());
        if (gui.getInv().getViewers().isEmpty()) {
          return;
        }

        gui.updateButtons();
        gui.playOpenSound();
        gui.onOpen();
        this.map.put(player.getUniqueId(), gui);
      } else {
        this.updateContent(gui);
      }
    }
  }

  private static boolean validInventory(CMIGui gui) {
    Player player = gui.getPlayer();
    Inventory topInv = CMIPlayerInventory.getTopInventory(player);
    if (topInv == null) {
      return false;
    } else if (!topInv.getType().equals(gui.getInvType())) {
      return false;
    } else if (topInv.getSize() != gui.getInvSize().getFields()) {
      return false;
    } else {
      return Version.isCurrentEqualOrHigher(Version.v1_9_R1) && topInv.getLocation() != null
          ? false
          : topInv.getHolder() == null;
    }
  }

  public void updateContent(CMIGui gui) {
    Player player = gui.getPlayer();
    if (!validInventory(gui)) {
      player.closeInventory();
      this.map.remove(player.getUniqueId());
    } else {
      CMIPlayerInventory.getTopInventory(player).setContents(gui.getInv().getContents());
      player.updateInventory();
      gui.setInv(CMIPlayerInventory.getTopInventory(player));
      if (gui.getInv().getHolder() != null) {
        player.closeInventory();
      } else {
        gui.updateButtons();
        CMILib.getInstance().getReflectionManager().updateInventoryTitle(player, gui.getTitle());
        this.map.put(player.getUniqueId(), gui);
      }
    }
  }

  public void softUpdateContent(CMIGui gui) {
    Player player = gui.getPlayer();
    if (!validInventory(gui)) {
      player.closeInventory();
      this.map.remove(player.getUniqueId());
    } else {
      Inventory top = CMIPlayerInventory.getTopInventory(player);

      for (int i = 0; i < top.getSize(); i++) {
        CMIGuiButton button = gui.getButtons().get(i);
        if (button != null && button.isLocked()) {
          top.setItem(i, button.getItem(gui.getPlayer()));
        }
      }

      gui.setInv(top);
      this.map.put(player.getUniqueId(), gui);
      player.updateInventory();
    }
  }

  public enum CmiInventoryType {
    regular,
    SavedInv,
    EditableInv,
    RecipeCreator,
    ArmorStandEditor,
    ArmorStandCopy,
    EntityInventoryEditor,
    Recipes,
    SellHand;
  }

  public enum GUIButtonLocation {
    topLeft(0, 0),
    topRight(0, 1),
    bottomLeft(1, 0),
    bottomRight(1, 1);

    private Integer row;
    private Integer collumn;

    GUIButtonLocation(Integer row, Integer collumn) {
      this.collumn = collumn;
      this.row = row;
    }

    public Integer getRow() {
      return this.row;
    }

    public Integer getCollumn() {
      return this.collumn;
    }
  }

  public enum GUIClickType {
    Unknown,
    Left,
    LeftShift,
    Right,
    RightShift,
    MiddleMouse,
    QControl,
    Q;

    public boolean isShiftClick() {
      switch (this) {
        case LeftShift:
        case RightShift:
          return true;
        default:
          return false;
      }
    }

    public boolean isLeftClick() {
      switch (this) {
        case Left:
        case LeftShift:
          return true;
        default:
          return false;
      }
    }

    public boolean isRightClick() {
      switch (this) {
        case Right:
        case RightShift:
          return true;
        default:
          return false;
      }
    }

    public boolean isMiddleClick() {
      switch (this) {
        case MiddleMouse:
          return true;
        default:
          return false;
      }
    }

    public boolean isQClick() {
      switch (this) {
        case QControl:
        case Q:
          return true;
        default:
          return false;
      }
    }
  }

  public enum GUIFieldType {
    Free,
    Locked,
    noPlacing;
  }

  public enum GUIRows {
    r1(1),
    r2(2),
    r3(3),
    r4(4),
    r5(5),
    r6(6);

    private int rows;

    GUIRows(int rows) {
      this.rows = rows;
    }

    public Integer getFields() {
      return this.rows * 9;
    }

    public Integer getRows() {
      return this.rows;
    }

    public static GUIManager.GUIRows getByRows(Integer rows) {
      if (rows > 9) {
        rows = rows / 9;
      }

      for (GUIManager.GUIRows one : values()) {
        if (one.getRows().equals(rows)) {
          return one;
        }
      }

      return r6;
    }
  }

  public enum InvType {
    Gui,
    Main,
    Quickbar;
  }
}
