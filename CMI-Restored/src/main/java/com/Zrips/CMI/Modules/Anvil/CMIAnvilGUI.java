package com.Zrips.CMI.Modules.Anvil;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Reflections;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.MinecraftPlatform;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CMIAnvilGUI {
  private boolean colorrename = false;
  private Player player;
  private String title = "Repair & Name";
  private String defaulttext = "";
  private Inventory inventory;
  private HashMap<CMIAnvilGUI.AnvilSlot, ItemStack> items = new HashMap<>();
  private Listener listener;
  private CMIAnvilGUI.AnvilClickEventHandler handler;
  private static Class<?> BlockPosition;
  private static Class<?> PacketPlayOutOpenWindow;
  private static Class<?> ContainerAnvil;
  private static Class<?> ChatMessage;
  private static Class<?> EntityHuman;
  private static Class<?> ContainerAccess;
  private static Class<?> Containers;
  private static Class<?> Container;
  private static boolean useNewVersion = true;

  static {
    loadClasses();
  }

  private static void loadClasses() {
    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      try {
        Container = Class.forName("net.minecraft.world.inventory.Container");
      } catch (Throwable throwable) {
        useNewVersion = false;
      }
    } else {
      try {
        Class.forName(
            "net.minecraft.server." + Version.getCurrent().toString() + ".ContainerAccess");
        useNewVersion = true;
      } catch (Exception | Error value) {
        useNewVersion = false;
      }

      Reflections reflections = CMI.getInstance().getReflectionManager();
      BlockPosition = reflections.getMinecraftClass("BlockPosition");
      PacketPlayOutOpenWindow = reflections.getMinecraftClass("PacketPlayOutOpenWindow");
      ContainerAnvil = reflections.getMinecraftClass("ContainerAnvil");
      ChatMessage = reflections.getMinecraftClass("ChatMessage");
      EntityHuman = reflections.getMinecraftClass("EntityHuman");
      if (useNewVersion) {
        ContainerAccess = reflections.getMinecraftClass("ContainerAccess");
        Containers = reflections.getMinecraftClass("Containers");
      }
    }
  }

  public boolean getColorRename() {
    return this.colorrename;
  }

  public void setColorRename(boolean flag) {
    this.colorrename = flag;
  }

  public Player getPlayer() {
    return this.player;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String string) {
    this.title = string;
  }

  public String getDefaultText() {
    return this.defaulttext;
  }

  public void setDefaultText(String string) {
    this.defaulttext = string;
  }

  public ItemStack getSlot(CMIAnvilGUI.AnvilSlot anvilSlot) {
    return this.items.get(anvilSlot);
  }

  public void setSlot(CMIAnvilGUI.AnvilSlot anvilSlot, ItemStack itemStack) {
    this.items.put(anvilSlot, itemStack);
  }

  public String getSlotName(CMIAnvilGUI.AnvilSlot anvilSlot) {
    ItemStack itemStack = this.getSlot(anvilSlot);
    if (itemStack != null && itemStack.hasItemMeta()) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      return itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "";
    } else {
      return "";
    }
  }

  public void setSlotName(CMIAnvilGUI.AnvilSlot anvilSlot, String string) {
    ItemStack itemStack = this.getSlot(anvilSlot);
    if (itemStack != null) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      itemMeta.setDisplayName(string != null ? CMIChatColor.translate(string) : null);
      itemStack.setItemMeta(itemMeta);
      this.setSlot(anvilSlot, itemStack);
    }
  }

  public CMIAnvilGUI(Player viewer, CMIAnvilGUI.AnvilClickEventHandler anvilClickEventHandler) {
    this.player = viewer;
    this.handler = anvilClickEventHandler;
    this.listener = null;
    if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      this.listener =
          new Listener() {
            @EventHandler
            public void ICE(InventoryClickEvent inventoryClickEvent) {
              if (inventoryClickEvent.getInventory().equals(CMIAnvilGUI.this.inventory)
                  && (inventoryClickEvent.getClick() == ClickType.LEFT
                      || inventoryClickEvent.getClick() == ClickType.RIGHT)) {
                ItemStack itemStack = inventoryClickEvent.getCurrentItem();
                int value = inventoryClickEvent.getRawSlot();
                String string = null;
                if (itemStack != null && itemStack.hasItemMeta()) {
                  ItemMeta itemMeta = itemStack.getItemMeta();
                  if (itemMeta.hasDisplayName()) {
                    string = itemMeta.getDisplayName();
                  }
                }

                CMIAnvilGUI.AnvilClickEvent anvilClickEvent =
                    CMIAnvilGUI.this
                    .new AnvilClickEvent(CMIAnvilGUI.AnvilSlot.bySlot(value), itemStack, string);
                CMIAnvilGUI.this.handler.onAnvilClick(anvilClickEvent);
              }
            }

            @EventHandler
            public void ICE(InventoryCloseEvent inventoryCloseEvent) {
              if (inventoryCloseEvent.getInventory().equals(CMIAnvilGUI.this.inventory)) {
                CMIAnvilGUI.this.handler.onAnvilClose(inventoryCloseEvent);
                if (CMIAnvilGUI.this.inventory.getItem(0) != null) {
                  CMIAnvilGUI.this
                      .player
                      .getInventory()
                      .addItem(new ItemStack[] {CMIAnvilGUI.this.inventory.getItem(0)});
                }

                if (CMIAnvilGUI.this.inventory.getItem(1) != null) {
                  CMIAnvilGUI.this
                      .player
                      .getInventory()
                      .addItem(new ItemStack[] {CMIAnvilGUI.this.inventory.getItem(1)});
                }

                CMIAnvilGUI.this.inventory.clear();
                HandlerList.unregisterAll(CMIAnvilGUI.this.listener);
              }
            }

            @EventHandler
            public void PQE(PlayerQuitEvent playerQuitEvent) {
              if (playerQuitEvent.getPlayer().equals(CMIAnvilGUI.this.player)) {
                HandlerList.unregisterAll(CMIAnvilGUI.this.listener);
              }
            }
          };
    } else {
      this.listener =
          new Listener() {
            @EventHandler
            public void ICE(InventoryClickEvent clickEvent) {
              if (clickEvent.getInventory().equals(CMIAnvilGUI.this.inventory)
                  && (clickEvent.getClick() == ClickType.LEFT
                      || clickEvent.getClick() == ClickType.RIGHT)) {
                ItemStack resultItem = clickEvent.getCurrentItem();
                int rawSlot = clickEvent.getRawSlot();
                String inputText = null;
                if (resultItem != null && resultItem.hasItemMeta()) {
                  ItemMeta resultMeta = resultItem.getItemMeta();
                  if (resultMeta.hasDisplayName()) {
                    inputText = resultMeta.getDisplayName();
                  }
                }

                CMIAnvilGUI.AnvilClickEvent anvilEvent =
                    CMIAnvilGUI.this
                    .new AnvilClickEvent(
                        CMIAnvilGUI.AnvilSlot.bySlot(rawSlot), resultItem, inputText);
                CMIAnvilGUI.this.handler.onAnvilClick(anvilEvent);
              }
            }

            @EventHandler
            public void PAE(PrepareAnvilEvent prepareAnvilEvent) {
              if (prepareAnvilEvent.getInventory().equals(CMIAnvilGUI.this.inventory)) {
                ItemStack prepared = prepareAnvilEvent.getResult();
                if (CMIAnvilGUI.this.colorrename && prepared != null && prepared.hasItemMeta()) {
                  ItemMeta preparedMeta = prepared.getItemMeta();
                  if (preparedMeta.hasDisplayName()) {
                    preparedMeta.setDisplayName(
                        CMIChatColor.translate(preparedMeta.getDisplayName()));
                  }

                  prepared.setItemMeta(preparedMeta);
                  prepareAnvilEvent.setResult(prepared);
                }
              }
            }

            @EventHandler
            public void ICE(InventoryCloseEvent closeEvent) {
              if (closeEvent.getInventory().equals(CMIAnvilGUI.this.inventory)) {
                CMIAnvilGUI.this.handler.onAnvilClose(closeEvent);
                if (CMIAnvilGUI.this.inventory.getItem(0) != null) {
                  CMIAnvilGUI.this
                      .player
                      .getInventory()
                      .addItem(new ItemStack[] {CMIAnvilGUI.this.inventory.getItem(0)});
                }

                if (CMIAnvilGUI.this.inventory.getItem(1) != null) {
                  CMIAnvilGUI.this
                      .player
                      .getInventory()
                      .addItem(new ItemStack[] {CMIAnvilGUI.this.inventory.getItem(1)});
                }

                CMIAnvilGUI.this.inventory.clear();
                HandlerList.unregisterAll(CMIAnvilGUI.this.listener);
              }
            }

            @EventHandler
            public void PQE(PlayerQuitEvent quitEvent) {
              if (quitEvent.getPlayer().equals(CMIAnvilGUI.this.player)) {
                HandlerList.unregisterAll(CMIAnvilGUI.this.listener);
              }
            }
          };
    }

    Bukkit.getPluginManager().registerEvents(this.listener, CMI.getInstance());
  }

  public void open() {
    this.open(this.title);
  }

  public Field getField(Class<?> type, String string) throws NoSuchFieldException {
    Field field = type.getDeclaredField(string);
    field.setAccessible(true);
    return field;
  }

  public void open(String string) {
    net.Zrips.CMILib.Reflections reflections = CMILib.getInstance().getReflectionManager();

    try {
      Object object = reflections.getPlayerHandle(this.player);
      if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
        if (Version.isPaperBranch()) {
          CMI.getInstance().getReflectionManager().openAnvil(this.player);
          return;
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        if (Version.isPaperBranch()) {
          CMI.getInstance().getReflectionManager().openAnvil(this.player);
          return;
        }

        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(this.title);
        Map<Integer, ItemStack> slotItems = new HashMap<>();
        for (CMIAnvilGUI.AnvilSlot anvilSlot : this.items.keySet()) {
          slotItems.put(anvilSlot.getSlot(), this.items.get(anvilSlot));
        }

        this.inventory =
            CMI.getInstance().getNMS().openAnvilWindow(this.player, rawMessage.getRaw(), slotItems);
      } else {
        var constructor = ChatMessage.getConstructor(String.class, Object[].class);
        if (useNewVersion) {
          Method method =
              reflections.getMethod(
                  "at",
                  ContainerAccess,
                  new Class[] {reflections.getMinecraftClass("World"), BlockPosition});
          Object openPacketObject =
              ContainerAnvil.getConstructor(
                      int.class, reflections.getMinecraftClass("PlayerInventory"), ContainerAccess)
                  .newInstance(
                      9,
                      reflections.getPlayerField(this.player, "inventory"),
                      method.invoke(
                          ContainerAccess,
                          reflections.getPlayerField(this.player, "world"),
                          BlockPosition.getConstructor(int.class, int.class, int.class)
                              .newInstance(0, 0, 0)));
          this.getField(reflections.getMinecraftClass("Container"), "checkReachable")
              .set(openPacketObject, false);
          this.inventory =
              (Inventory)
                  reflections.invokeMethod(
                      "getTopInventory",
                      reflections.invokeMethod("getBukkitView", openPacketObject));

          for (CMIAnvilGUI.AnvilSlot slot : this.items.keySet()) {
            this.inventory.setItem(slot.getSlot(), this.items.get(slot));
          }

          int windowId = (Integer) reflections.invokeMethod("nextContainerCounter", object);
          Object playerConnection = reflections.getPlayerField(this.player, "playerConnection");
          Object packetObject =
              PacketPlayOutOpenWindow.getConstructor(
                      int.class, Containers, reflections.getMinecraftClass("IChatBaseComponent"))
                  .newInstance(
                      windowId,
                      this.getField(Containers, "ANVIL").get(Containers),
                      constructor.newInstance(CMIChatColor.translate(string), new Object[0]));
          Method sendMethod =
              reflections.getMethod(
                  "sendPacket", playerConnection.getClass(), new Class[] {PacketPlayOutOpenWindow});
          sendMethod.invoke(playerConnection, packetObject);
          Field activeContainerField = this.getField(EntityHuman, "activeContainer");
          if (activeContainerField != null) {
            activeContainerField.set(object, openPacketObject);
            this.getField(reflections.getMinecraftClass("Container"), "windowId")
                .set(activeContainerField.get(object), windowId);
            reflections
                .getMethod(
                    "addSlotListener",
                    activeContainerField.get(object).getClass(),
                    new Class[] {object.getClass()})
                .invoke(activeContainerField.get(object), object);
            Object container =
                reflections.getMinecraftClass("Container").cast(activeContainerField.get(object));
            container
                .getClass()
                .getMethod("setTitle", reflections.getMinecraftClass("IChatBaseComponent"))
                .invoke(
                    container,
                    CMILib.getInstance()
                        .getReflectionManager()
                        .textToIChatBaseComponent(
                            "{\"text\": \"" + CMIChatColor.translate(this.title) + "\"}"));
          }
        } else {
          Object anvilContainer =
              ContainerAnvil.getConstructor(
                      reflections.getMinecraftClass("PlayerInventory"),
                      reflections.getMinecraftClass("World"),
                      BlockPosition,
                      EntityHuman)
                  .newInstance(
                      reflections.getPlayerField(this.player, "inventory"),
                      reflections.getPlayerField(this.player, "world"),
                      BlockPosition.getConstructor(int.class, int.class, int.class)
                          .newInstance(0, 0, 0),
                      object);
          this.getField(reflections.getMinecraftClass("Container"), "checkReachable")
              .set(anvilContainer, false);
          this.inventory =
              (Inventory)
                  reflections.invokeMethod(
                      "getTopInventory", reflections.invokeMethod("getBukkitView", anvilContainer));

          for (CMIAnvilGUI.AnvilSlot anvilSlot : this.items.keySet()) {
            this.inventory.setItem(anvilSlot.getSlot(), this.items.get(anvilSlot));
          }

          Bukkit.getConsoleSender().sendMessage("" + reflections.getActiveContainerId(this.player));
          int containerId = 0;
          if (Version.getPlatform().equals(MinecraftPlatform.mohist)) {
            containerId = reflections.getActiveContainerId(this.player) + 1;
          } else {
            containerId = (Integer) reflections.invokeMethod("nextContainerCounter", object);
          }

          Object connection = reflections.getPlayerField(this.player, "playerConnection");
          Object windowPacket =
              PacketPlayOutOpenWindow.getConstructor(
                      int.class,
                      String.class,
                      reflections.getMinecraftClass("IChatBaseComponent"),
                      int.class)
                  .newInstance(
                      containerId,
                      "minecraft:anvil",
                      constructor.newInstance(CMIChatColor.translate(string), new Object[0]),
                      0);
          Method sendPacketMethod =
              reflections.getMethod(
                  "sendPacket", connection.getClass(), new Class[] {PacketPlayOutOpenWindow});
          sendPacketMethod.invoke(connection, windowPacket);
          Field containerField = null;
          if (Version.getPlatform().equals(MinecraftPlatform.mohist)) {
            containerField = this.getField(EntityHuman, "field_71070_bA");
          } else {
            containerField = this.getField(EntityHuman, "activeContainer");
          }

          if (containerField != null) {
            containerField.set(object, anvilContainer);
            this.getField(reflections.getMinecraftClass("Container"), "windowId")
                .set(containerField.get(object), containerId);

            try {
              reflections
                  .getMethod(
                      "addSlotListener",
                      containerField.get(object).getClass(),
                      new Class[] {object.getClass()})
                  .invoke(containerField.get(object), object);
            } catch (Throwable throwable) {
            }
          }
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public class AnvilClickEvent {
    private CMIAnvilGUI.AnvilSlot slot;
    private ItemStack item;
    private String text;

    public AnvilClickEvent(CMIAnvilGUI.AnvilSlot anvilSlot, ItemStack itemStack, String string) {
      this.slot = anvilSlot;
      this.item = itemStack;
      this.text = string;
    }

    public CMIAnvilGUI.AnvilSlot getSlot() {
      return this.slot;
    }

    public ItemStack getItemStack() {
      return this.item;
    }

    public void setItemStack(ItemStack itemStack) {
      this.item = itemStack;
      CMIAnvilGUI.this.inventory.setItem(this.slot.getSlot(), this.item);
    }

    public boolean hasText() {
      return this.text != null;
    }

    public String getText() {
      return this.text != null ? this.text : CMIAnvilGUI.this.defaulttext;
    }
  }

  public interface AnvilClickEventHandler {
    void onAnvilClick(CMIAnvilGUI.AnvilClickEvent anvilClickEvent);

    void onAnvilClose(InventoryCloseEvent inventoryCloseEvent);
  }

  public enum AnvilSlot {
    INPUT_LEFT(0),
    INPUT_RIGHT(1),
    OUTPUT(2);

    private int slot;

    AnvilSlot(int value) {
      this.slot = value;
    }

    public int getSlot() {
      return this.slot;
    }

    public static CMIAnvilGUI.AnvilSlot bySlot(int value) {
      CMIAnvilGUI.AnvilSlot[] anvilSlots;
      int slotCount = (anvilSlots = values()).length;

      for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
        CMIAnvilGUI.AnvilSlot anvilSlot = anvilSlots[slotIndex];
        if (anvilSlot.getSlot() == value) {
          return anvilSlot;
        }
      }

      return null;
    }
  }
}

