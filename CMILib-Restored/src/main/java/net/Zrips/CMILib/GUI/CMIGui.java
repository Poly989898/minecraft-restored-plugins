package net.Zrips.CMILib.GUI;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Sounds.CMISound;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CMIGui {
  private InventoryType invType;
  private GUIManager.GUIRows gUIRows;
  private Player player;
  private Inventory inv;
  private String title;
  private HashMap<Integer, CMIGuiButton> buttons = new HashMap<>();
  private LinkedHashSet<CMIGuiButton> noSlotButtons = new LinkedHashSet<>();
  private EnumMap<GUIManager.InvType, GUIManager.GUIFieldType> lock =
      new EnumMap<>(GUIManager.InvType.class);
  private EnumMap<GUIManager.InvType, String> permLock = new EnumMap<>(GUIManager.InvType.class);
  private Set<Integer> prevButtons = new HashSet<>();
  private Set<Integer> nextButtons = new HashSet<>();
  private Set<Integer> middleButtons = new HashSet<>();
  private GUIManager.CmiInventoryType type = GUIManager.CmiInventoryType.regular;
  private Object whatShows;
  private Object tempData;
  private boolean allowClone = false;
  private boolean allowShift = false;
  private boolean allowPickUpAll = false;
  private boolean allowItemPickup = true;
  private boolean allowMoveAll = false;
  private CMISound openSound = null;
  private CMISound closeSound = null;
  private CMIMaterial filler = null;
  private boolean logging = false;
  private LinkedHashSet<GUIClickActionLog> clickLog = new LinkedHashSet<>();

  public CMIGui(Player player) {
    this.player = player;
  }

  public CMIGui clone() {
    CMIGui g = new CMIGui(this.player);
    g.setInvSize(this.gUIRows);
    g.setButtons(this.buttons);
    g.setInv(this.inv);
    g.setInvType(this.invType);
    g.setTitle(this.title);
    g.setCmiInventoryType(this.type);
    g.setWhatShows(this.whatShows);
    return g;
  }

  public void playOpenSound() {
    if (this.openSound != null) {
      this.openSound.play(this.player);
    }
  }

  public void playCloseSound() {
    if (this.closeSound != null) {
      this.closeSound.play(this.player);
    }
  }

  public boolean isOpened() {
    return CMILib.getInstance().getGUIManager().isOpenedGui(this.getPlayer());
  }

  public boolean isSimilar(CMIGui gui) {
    return this.getInvSize() != gui.getInvSize() ? false : this.getInvType() == gui.getInvType();
  }

  public CMIGui open() {
    CMILib.getInstance().getGUIManager().openGui(this);
    return this;
  }

  public CMIGui update() {
    CMILib.getInstance().getGUIManager().softUpdateContent(this);
    return this;
  }

  public void outsideClick(GUIManager.GUIClickType type) {}

  public boolean click(int slot) {
    return this.click(slot, null, null);
  }

  public boolean click(int slot, GUIManager.GUIClickType type, ItemStack currentItem) {
    return true;
  }

  public InventoryType getInvType() {
    if (this.invType == null) {
      this.invType = InventoryType.CHEST;
    }

    return this.invType;
  }

  public void setInvType(InventoryType invType) {
    this.invType = invType;
  }

  public GUIManager.GUIRows getInvSize() {
    if (this.gUIRows == null) {
      this.autoResize();
    }

    return this.gUIRows;
  }

  public void setInvSize(GUIManager.GUIRows GUIRows) {
    this.gUIRows = GUIRows;
  }

  public void setInvSize(int rows) {
    this.gUIRows = GUIManager.GUIRows.getByRows(rows);
  }

  public void autoResize() {
    this.combineButtons();
    int max = 0;

    for (Entry<Integer, CMIGuiButton> one : this.buttons.entrySet()) {
      if (one.getKey() > max) {
        max = one.getKey();
      }
    }

    if (max < 9) {
      this.gUIRows = GUIManager.GUIRows.r1;
    } else if (max < 18) {
      this.gUIRows = GUIManager.GUIRows.r2;
    } else if (max < 27) {
      this.gUIRows = GUIManager.GUIRows.r3;
    } else if (max < 36) {
      this.gUIRows = GUIManager.GUIRows.r4;
    } else if (max < 45) {
      this.gUIRows = GUIManager.GUIRows.r5;
    } else {
      this.gUIRows = GUIManager.GUIRows.r6;
    }
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Inventory getInv() {
    if (this.inv == null) {
      CMILib.getInstance().getGUIManager().generateInventory(this);
    }

    return this.inv;
  }

  public void setInv(Inventory inv) {
    this.inv = inv;
  }

  public String getTitle() {
    if (this.title == null) {
      this.title = "";
    }

    return CMIChatColor.translate(this.title);
  }

  public void updateTitle(String title) {
    this.setTitle(title);
    CMILib.getInstance().getReflectionManager().updateInventoryTitle(this.player, this.title);
  }

  public void setTitle(String title) {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      if (CMIChatColor.stripColor(title).length() > 64) {
        title = title.substring(0, 63) + "~";
      }
    } else if (title.length() > 32) {
      title = title.substring(0, 31) + "~";
    }

    this.title = title;
  }

  public HashMap<Integer, CMIGuiButton> getButtons() {
    this.combineButtons();
    return this.buttons;
  }

  public CMIGuiButton getButton(int slot) {
    CMIGuiButton b = this.getButtons().get(slot);
    return b != null ? b : null;
  }

  public CMIGui replaceButton(CMIGuiButton button) {
    button.updateLooks();
    if (button.getSlot() != null) {
      this.buttons.remove(button.getSlot());
    }

    if (this.getInv() != null) {
      this.getInv().setItem(button.getSlot(), button.getItem(this.getPlayer()));
    }

    return this.addButton(button, 54);
  }

  public CMIGui addButton(CMIGuiButton button) {
    button.updateLooks();
    return this.addButton(button, 54);
  }

  public CMIGui addButton(CMIGuiButton button, int maxSlot) {
    button.setGui(this);
    if (button.getSlot() != null && this.buttons.get(button.getSlot()) != null) {
      for (int ii = button.getSlot(); ii < maxSlot; ii++) {
        CMIGuiButton b = this.buttons.get(ii);
        if (b == null) {
          this.buttons.put(ii, button);
          break;
        }
      }

      return this;
    } else if (button.getSlot() == null) {
      this.noSlotButtons.add(button);
      return this;
    } else {
      this.buttons.put(button.getSlot(), button);
      return this;
    }
  }

  public CMIGui removeButton(int slot) {
    this.buttons.remove(slot);
    return this;
  }

  private void combineButtons() {
    for (CMIGuiButton button : this.noSlotButtons) {
      for (int ii = 0; ii < 54; ii++) {
        CMIGuiButton b = this.buttons.get(ii);
        if (b == null) {
          button.setSlot(ii);
          this.buttons.put(ii, button);
          break;
        }
      }
    }

    this.noSlotButtons.clear();
  }

  public void fillEmptyButtons() {
    this.fillEmptyButtons(null);
  }

  public void fillEmptyButtons(ItemStack item) {
    this.combineButtons();

    for (int i = 0; i < this.getInvSize().getFields(); i++) {
      if (!this.buttons.containsKey(i)) {
        this.addEmptyButton(item, i);
      }
    }
  }

  public void updateButton(CMIGuiButton button) {
    if (this.inv != null && button.getSlot() != null && this.inv.getSize() > button.getSlot()) {
      if (GUIManager.usePackets) {
        CMILib.getInstance()
            .getReflectionManager()
            .updateItemWithPacket(
                this.getPlayer(), button.getItem(this.getPlayer()), button.getSlot());
      } else {
        this.inv.setItem(button.getSlot(), button.getItem(this.getPlayer()));
      }

      this.buttons.put(button.getSlot(), button);
    }
  }

  public void fakeUpdate(int slot, ItemStack item) {
    CMILib.getInstance().getReflectionManager().updateItemWithPacket(this.getPlayer(), item, slot);
  }

  public void addEmptyButton(int slot) {
    this.addEmptyButton(null, slot);
  }

  public void addEmptyButton(ItemStack item, int slot) {
    ItemStack miscInfo =
        item == null
            ? (this.filler == null
                ? CMILib.getInstance().getConfigManager().getGUIEmptyField().getItemStack().clone()
                : this.filler.newItemStack())
            : item.clone();
    ItemMeta miscInfoMeta = miscInfo.getItemMeta();
    if (miscInfoMeta != null) {
      if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
        miscInfoMeta.setHideTooltip(true);
        miscInfo.setItemMeta(miscInfoMeta);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
        miscInfoMeta.setDisplayName(" ");
        miscInfo.setItemMeta(miscInfoMeta);
      }
    }

    CMIGuiButton button = new CMIGuiButton(slot, GUIManager.GUIFieldType.Locked, miscInfo);
    this.addButton(button);
    this.updateButton(button);
  }

  public void setButtons(HashMap<Integer, CMIGuiButton> buttons) {
    this.buttons = buttons;
  }

  public boolean isLocked(GUIManager.InvType type) {
    return this.lock.containsKey(type)
        ? this.lock.get(type) == GUIManager.GUIFieldType.Locked
        : false;
  }

  public boolean isNoItemPlacement(GUIManager.InvType type) {
    return this.lock.containsKey(type)
        ? this.lock.get(type) == GUIManager.GUIFieldType.noPlacing
        : false;
  }

  public void addLock(GUIManager.InvType type) {
    this.addLock(type, GUIManager.GUIFieldType.Locked);
  }

  public void setNoItemPlacement(GUIManager.InvType type) {
    this.addLock(type, GUIManager.GUIFieldType.noPlacing);
  }

  public void addLock(GUIManager.InvType type, GUIManager.GUIFieldType lock) {
    this.lock.put(type, lock);
  }

  public boolean isPermLocked(GUIManager.InvType type) {
    return this.permLock.containsKey(type)
        ? !this.player.hasPermission(this.permLock.get(type))
        : true;
  }

  public void addPermLock(GUIManager.InvType type, String perm) {
    this.permLock.put(type, perm);
  }

  public GUIManager.CmiInventoryType getType() {
    return this.type;
  }

  public void setCmiInventoryType(GUIManager.CmiInventoryType type) {
    this.type = type;
  }

  public Object getWhatShows() {
    return this.whatShows;
  }

  public void setWhatShows(Object whatShows) {
    this.whatShows = whatShows;
  }

  public Integer getSlot(GUIManager.GUIButtonLocation place) {
    GUIManager.GUIRows size = this.getInvSize();
    int v = place.getCollumn() * 9;
    v = place.getCollumn() > 0 ? v - 1 : v;
    Integer value = place.getRow() * size.getRows() * 9 - 8 + v;
    value = place.getRow() > 0 ? value : value + 9;
    return value - 1;
  }

  public void onClose() {}

  public void onOpen() {}

  public void pageChange(int page) {}

  public void addPagination(PageInfo pi) {
    final int CurrentPage = pi.getCurrentPage();
    int pageCount = pi.getTotalPages();
    int totalEntries = pi.getTotalEntries();
    if (pageCount != 1) {
      if (this.getInvSize().getRows() < 6) {
        boolean emptyLastLine = true;

        for (int i = this.getInvSize().getFields() - 9; i < this.getInvSize().getFields(); i++) {
          if (this.getButtons().get(i - 1) != null) {
            emptyLastLine = false;
            break;
          }
        }

        if (!emptyLastLine) {
          this.setInvSize(this.getInvSize().getRows() + 1);
        }
      }

      int NextPage = CurrentPage + 1;
      int Prevpage = CurrentPage - 1;
      if (Prevpage == 0) {
        Prevpage = pageCount;
      }

      if (NextPage > pageCount) {
        NextPage = 1;
      }

      final int prevP = Prevpage;
      final int nextP = NextPage;
      if (pageCount > 0) {
        Integer mid = this.getSlot(GUIManager.GUIButtonLocation.bottomRight) - 4;
        Set<Integer> midButttons = this.getPageMiddleButtons();
        if (midButttons.isEmpty()) {
          midButttons.add(mid);
        }

        for (Integer midSlot : midButttons) {
          CMIGuiButton button =
              new CMIGuiButton(
                  midSlot, CMILib.getInstance().getConfigManager().getGUIMiddlePage()) {
                @Override
                public void click(GUIManager.GUIClickType type) {
                  if (CurrentPage != 1) {
                    CMIGui.this.pageChange(1);
                  }
                }
              };
          button.setName(LC.info_pageCountHover.getLocale("[totalEntries]", totalEntries));
          button.addLore(
              LC.info_pageCount.getLocale("[current]", CurrentPage, "[total]", pageCount));
          this.removeButton(midSlot);
          this.addButton(button);
        }

        Integer prev = this.getSlot(GUIManager.GUIButtonLocation.bottomLeft);
        Set<Integer> prevButttons = this.getPagePrevButtons();
        if (prevButttons.isEmpty()) {
          prevButttons.add(prev);
        }

        for (Integer prevSlot : prevButttons) {
          if (this.getButtons().get(prevSlot) == null) {
            CMIGuiButton button =
                new CMIGuiButton(
                    prevSlot, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
                  @Override
                  public void click(GUIManager.GUIClickType type) {
                    CMIGui.this.pageChange(prevP);
                  }
                };
            button.setName(LC.info_prevPageGui.getLocale());
            button.addLore(
                LC.info_pageCount.getLocale("[current]", CurrentPage, "[total]", pageCount)
                    + (prevP == pageCount ? " " + LC.info_lastPageHover.getLocale() : ""));
            this.removeButton(prevSlot);
            this.addButton(button);
          }
        }

        Integer next = this.getSlot(GUIManager.GUIButtonLocation.bottomRight);
        Set<Integer> nextButttons = this.getPageNextButtons();
        if (nextButttons.isEmpty()) {
          nextButttons.add(next);
        }

        for (Integer nextSlot : nextButttons) {
          if (this.getButtons().get(nextSlot) == null) {
            CMIGuiButton button =
                new CMIGuiButton(
                    nextSlot, CMILib.getInstance().getConfigManager().getGUINextPage()) {
                  @Override
                  public void click(GUIManager.GUIClickType type) {
                    CMIGui.this.pageChange(nextP);
                  }
                };
            button.setName(LC.info_nextPageGui.getLocale());
            button.addLore(
                LC.info_pageCount.getLocale("[current]", CurrentPage, "[total]", pageCount)
                    + (nextP == 1 ? " " + LC.info_firstPageHover.getLocale() : ""));
            this.removeButton(nextSlot);
            this.addButton(button);
          }
        }
      }
    }
  }

  public boolean isAllowShift() {
    return this.allowShift;
  }

  public void setAllowShift(boolean allowShift) {
    this.allowShift = allowShift;
  }

  public boolean isAllowClone() {
    return this.allowClone;
  }

  public void setAllowClone(boolean allowClone) {
    this.allowClone = allowClone;
  }

  public CMISound getOpenSound() {
    return this.openSound;
  }

  public void setOpenSound(CMISound openSound) {
    this.openSound = openSound;
  }

  public CMISound getCloseSound() {
    return this.closeSound;
  }

  public void setCloseSound(CMISound closeSound) {
    this.closeSound = closeSound;
  }

  public Object getTempData() {
    return this.tempData;
  }

  public void setTempData(Object tempData) {
    this.tempData = tempData;
  }

  public boolean isAllowPickUpAll() {
    return this.allowPickUpAll;
  }

  public void setAllowPickUpAll(boolean allowPickUpAll) {
    this.allowPickUpAll = allowPickUpAll;
  }

  public void updateButtons() {
    if (GUIManager.usePackets) {
      for (Entry<Integer, CMIGuiButton> one : this.getButtons().entrySet()) {
        if (one.getKey() <= this.getInv().getSize()) {
          try {
            ItemStack item = one.getValue().getItem(this.getPlayer());
            item = item == null ? null : item.clone();
            CMILib.getInstance()
                .getReflectionManager()
                .updateItemWithPacket(this.getPlayer(), item, one.getKey());
          } catch (ArrayIndexOutOfBoundsException var4) {
          }
        }
      }
    }
  }

  public boolean isAllowItemPickup() {
    return this.allowItemPickup;
  }

  public void setAllowItemPickup(boolean allowItemPickup) {
    this.allowItemPickup = allowItemPickup;
  }

  public boolean isAllowMoveAll() {
    return this.allowMoveAll;
  }

  public void setAllowMoveAll(boolean allowMoveAll) {
    this.allowMoveAll = allowMoveAll;
  }

  public CMIMaterial getFiller() {
    return this.filler;
  }

  public void setFiller(CMIMaterial filler) {
    this.filler = filler;
  }

  public LinkedHashSet<GUIClickActionLog> getLog() {
    return this.clickLog;
  }

  @Deprecated
  public void addClickLog(
      boolean canceled,
      ClickType clickType,
      InventoryAction action,
      ItemStack currentItem,
      ItemStack holdingItem,
      int slot) {
    this.addClickLog(null, canceled, clickType, action, currentItem, holdingItem, slot);
  }

  public void addClickLog(
      InventoryType inv,
      boolean canceled,
      ClickType clickType,
      InventoryAction action,
      ItemStack currentItem,
      ItemStack holdingItem,
      int slot) {
    if (this.isClickLogging()) {
      GUIClickActionLog log = new GUIClickActionLog(canceled);
      this.clickLog.add(log);
      log.setInv(inv);
      log.setBukkitClickType(clickType);
      log.setAction(action);
      log.setHoldingItem(holdingItem);
      log.addSlot(slot);
      log.setCurrentItem(currentItem);
    }
  }

  public void addClickLog(
      boolean canceled,
      ItemStack holdingItem,
      Map<Integer, ItemStack> map,
      Set<Integer> slots,
      DragType dragType) {
    this.addClickLog(null, canceled, holdingItem, map, slots, dragType);
  }

  public void addClickLog(
      InventoryType inv,
      boolean canceled,
      ItemStack holdingItem,
      Map<Integer, ItemStack> map,
      Set<Integer> slots,
      DragType dragType) {
    if (this.isClickLogging()) {
      GUIClickActionLog log = new GUIClickActionLog(canceled);
      this.clickLog.add(log);
      log.setHoldingItem(holdingItem);
      log.setDragtype(dragType);
      log.addSlots(slots);
      log.setNewItems(map);
      log.setInv(inv);
    }
  }

  public boolean isClickLogging() {
    return this.logging;
  }

  public void logClicks(boolean log) {
    this.logging = log;
  }

  public Set<Integer> getPagePrevButtons() {
    return this.prevButtons;
  }

  public void setPagePrevButtons(Set<Integer> prevButtons) {
    this.prevButtons = prevButtons;
  }

  public void setPagePrevButtons(Integer... prevButtons) {
    this.prevButtons.clear();
    this.prevButtons.addAll(Arrays.asList(prevButtons));
  }

  public Set<Integer> getPageNextButtons() {
    return this.nextButtons;
  }

  public void setPageNextButtons(Set<Integer> nextButtons) {
    this.nextButtons = nextButtons;
  }

  public void setPageNextButtons(Integer... nextButtons) {
    this.nextButtons.clear();
    this.nextButtons.addAll(Arrays.asList(nextButtons));
  }

  public Set<Integer> getPageMiddleButtons() {
    return this.middleButtons;
  }

  public void setPageMiddleButtons(Set<Integer> middleButtons) {
    this.middleButtons = middleButtons;
  }

  public void setPageMiddleButtons(Integer... middleButtons) {
    this.middleButtons.clear();
    this.middleButtons.addAll(Arrays.asList(middleButtons));
  }

  public void fillInEdges() {
    for (int i = 1; i <= this.getInvSize().getRows(); i++) {
      if (i != 1 && i != this.getInvSize().getRows()) {
        CMIGuiButton slotButton = this.getButtons().get(0 + (i - 1) * 9);
        if (slotButton == null) {
          this.addButton(
              new CMIGuiButton(
                      0 + (i - 1) * 9, CMILib.getInstance().getConfigManager().getGUIEmptyField())
                  .hideToltip());
        }

        slotButton = this.getButtons().get(8 + (i - 1) * 9);
        if (slotButton == null) {
          this.addButton(
              new CMIGuiButton(
                      8 + (i - 1) * 9, CMILib.getInstance().getConfigManager().getGUIEmptyField())
                  .hideToltip());
        }
      } else {
        for (int x = 0; x < 9; x++) {
          CMIGuiButton slotButton = this.getButtons().get(x + (i - 1) * 9);
          if (slotButton == null) {
            this.addButton(
                new CMIGuiButton(
                        x + (i - 1) * 9, CMILib.getInstance().getConfigManager().getGUIEmptyField())
                    .hideToltip());
          }
        }
      }
    }
  }
}
