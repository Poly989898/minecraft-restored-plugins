package net.Zrips.CMILib.Container;

import java.util.Map;
import java.util.TreeMap;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MaterialPicker {
  Player player;

  public MaterialPicker(Player player) {
    this.player = player;
  }

  public boolean validMaterial(CMIMaterial one) {
    return one.isValidAsItemStack();
  }

  public void pickedMaterial(GUIManager.GUIClickType type, CMIMaterial material) {}

  public void onUIClose() {}

  public boolean openPicker(int page, final String title) {
    CMIGui gui =
        new CMIGui(this.player) {
          @Override
          public void pageChange(int page) {
            MaterialPicker.this.openPicker(page, title);
          }

          @Override
          public void onClose() {
            MaterialPicker.this.onUIClose();
          }

          @Override
          public boolean click(int slot, GUIManager.GUIClickType type, ItemStack currentItem) {
            if (this.getInvSize().getFields() >= slot) {
              return true;
            }

            CMIMaterial mat = CMIMaterial.get(currentItem);
            if (!MaterialPicker.this.validMaterial(mat)) {
              return true;
            }

            MaterialPicker.this.pickedMaterial(type, mat);
            return false;
          }
        };
    gui.setTitle(title);
    Map<String, CMIMaterial> map = new TreeMap<>();

    for (CMIMaterial one : CMIMaterial.values()) {
      if (this.validMaterial(one)) {
        map.put(one.toString(), one);
      }
    }

    PageInfo pi = new PageInfo(45, map.size(), page);

    for (final CMIMaterial one : map.values()) {
      if (this.validMaterial(one) && !pi.isContinue()) {
        if (pi.isBreak()) {
          break;
        }

        CMIGuiButton button =
            new CMIGuiButton(one) {
              @Override
              public void click(GUIManager.GUIClickType type) {
                MaterialPicker.this.pickedMaterial(type, one);
              }
            };
        button.setName("&f" + one.getTranslatedName());
        gui.addButton(button);
      }
    }

    gui.addPagination(pi);
    gui.fillEmptyButtons();
    gui.addLock(GUIManager.InvType.Main);
    gui.open();
    return true;
  }
}
