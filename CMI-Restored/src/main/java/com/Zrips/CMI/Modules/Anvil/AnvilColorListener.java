package com.Zrips.CMI.Modules.Anvil;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilColorListener implements Listener {
  private CMI plugin;

  public AnvilColorListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PrepareAnvilEvent(PrepareAnvilEvent prepareAnvilEvent) {
    var list = prepareAnvilEvent.getViewers();
    if (!list.isEmpty()
        && PermissionsManager.CMIPerm.anvil_colors.hasPermission(
            (CommandSender) prepareAnvilEvent.getViewers().get(0))) {
      ItemStack itemStack = prepareAnvilEvent.getResult();
      if (itemStack == null) {
        itemStack = prepareAnvilEvent.getInventory().getContents()[0];
      }

      if (itemStack != null && itemStack.getType() != Material.AIR) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.hasDisplayName()) {
          if (!Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
            if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
              try {
                var anvilClass =
                    Class.forName(
                        "org.bukkit.craftbukkit."
                            + Version.getCurrent()
                            + ".inventory.CraftInventoryAnvil");
                var anvilContainerClass =
                    Class.forName("net.minecraft.world.inventory.ContainerAnvil");
                Object object = anvilClass.cast(prepareAnvilEvent.getInventory());
                Field field = object.getClass().getDeclaredField("container");
                field.setAccessible(true);
                Object anvilContainer = anvilContainerClass.cast(field.get(object));
                Field renameField = anvilContainer.getClass().getField("v");
                renameField.set(anvilContainer, CMIChatColor.deColorize(itemMeta.getDisplayName()));
              } catch (Throwable throwable) {
                throwable.printStackTrace();
              }
            } else {
              try {
                var craftAnvilClass =
                    Class.forName(
                        "org.bukkit.craftbukkit."
                            + Version.getCurrent()
                            + ".inventory.CraftInventoryAnvil");
                var craftContainerClass =
                    Class.forName(
                        "net.minecraft.server." + Version.getCurrent() + ".ContainerAnvil");
                Object craftAnvil = craftAnvilClass.cast(prepareAnvilEvent.getInventory());
                if (Version.isCurrentEqualOrLower(Version.v1_9_R2)) {
                  Method method =
                      craftAnvil.getClass().getMethod("setItem", int.class, ItemStack.class);
                  itemMeta.setDisplayName(CMIChatColor.translate(itemMeta.getDisplayName()));
                  itemStack.setItemMeta(itemMeta);
                  method.invoke(craftAnvil, 2, itemStack);
                } else {
                  Field containerField = craftAnvil.getClass().getDeclaredField("container");
                  containerField.setAccessible(true);
                  Object craftContainer = craftContainerClass.cast(containerField.get(craftAnvil));
                  Field renameTextField = craftContainer.getClass().getField("renameText");
                  renameTextField.set(
                      craftContainer, CMIChatColor.deColorize(itemMeta.getDisplayName()));
                }
              } catch (Throwable throwable2) {
              }
            }
          }

          if (Version.isCurrentEqualOrHigher(Version.v1_12_R1)) {
            if (prepareAnvilEvent
                .getInventory()
                .getRenameText()
                .equals(
                    CMIChatColor.stripColor(prepareAnvilEvent.getInventory().getRenameText()))) {
              return;
            }

            String string =
                CMITextCleaner.cleanFromColorCodes(
                    prepareAnvilEvent.getViewers().get(0),
                    prepareAnvilEvent.getInventory().getRenameText(),
                    CMIColorTypes.itemname,
                    true);
            string = CMIChatColor.translate(string);
            if (Config.AddItalicByDefault
                && !PermissionsManager.CMIPerm.anvil_bypassitalic.hasPermission(
                    (CommandSender) prepareAnvilEvent.getViewers().get(0))) {
              try {
                string = string.replaceAll("(§[0-9a-fk-or]|§x(§[0-9a-f]){6})", "$0§o");
              } catch (Throwable throwable3) {
                throwable3.printStackTrace();
              }
            }

            itemMeta.setDisplayName(string);
            if (prepareAnvilEvent.getResult() != null) {
              prepareAnvilEvent.getResult().setItemMeta(itemMeta);
            }
          }
        }
      }
    }
  }
}
