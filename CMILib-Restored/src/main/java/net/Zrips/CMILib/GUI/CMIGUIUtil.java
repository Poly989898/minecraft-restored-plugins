package net.Zrips.CMILib.GUI;

import java.util.function.Consumer;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.entity.Player;

public class CMIGUIUtil {
  private static int modifyValue(GUIManager.GUIClickType type, int lv, int lsv, int rv, int rsv) {
    switch (type) {
      case Left:
        return lv;
      case LeftShift:
        return lsv;
      case Right:
        return rv;
      case RightShift:
        return rsv;
      default:
        return 0;
    }
  }

  private static double modifyValue(
      GUIManager.GUIClickType type, double lv, double lsv, double rv, double rsv) {
    switch (type) {
      case Left:
        return lv;
      case LeftShift:
        return lsv;
      case Right:
        return rv;
      case RightShift:
        return rsv;
      default:
        return 0.0;
    }
  }

  public static boolean openVectorEditor(
      Player player,
      final CMIVector2D vector,
      final double baseAmount,
      final double resetTo,
      final Consumer<CMIVector2D> consumer,
      final Consumer<CMIVector2D> onModified) {
    CMIGui gui =
        new CMIGui(player) {
          @Override
          public void onClose() {}
        };
    gui.setTitle("Vector modification");
    gui.setInvSize(6);
    CMIGuiButton button =
        new CMIGuiButton(
            20,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4YjcxYjAwOGFkMTQyOGE5NDQxMmZhNTUzMTg3ZWY0MmVmZWFhYzVhNzBkZGY3MTQ4ZGFjZDY4NjMyYTQyMCJ9fX0=")) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case Q:
                vector.setX(resetTo);
                break;
              default:
                vector.setX(
                    CMINumber.sum(
                        vector.getX(),
                        CMIGUIUtil.modifyValue(
                            type, baseAmount, baseAmount * 10.0, -baseAmount, -baseAmount * 10.0)));
            }

            onModified.accept(vector);
            this.updateLooks();
            this.update();
          }

          @Override
          public void updateLooks() {
            this.setName("&7X: &f" + String.format("%.2f", vector.getX()));
          }
        };
    button.addLore(LC.info_reset.getLocale());
    gui.addButton(button);
    button =
        new CMIGuiButton(
            24,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGMwZWI2MjZmYTQ0YTAyMTFjZTEyNzc0YzRjYzQ0ZTZkYTUyMzFhYWJhNGE1MzBkYzJlMjE0MDlmNzk1YTY3YiJ9fX0=")) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case Q:
                vector.setY(resetTo);
                break;
              default:
                vector.setY(
                    CMINumber.sum(
                        vector.getY(),
                        CMIGUIUtil.modifyValue(
                            type, baseAmount, baseAmount * 10.0, -baseAmount, -baseAmount * 10.0)));
            }

            onModified.accept(vector);
            this.updateLooks();
            this.update();
          }

          @Override
          public void updateLooks() {
            this.setName("&7Y: &f" + String.format("%.2f", vector.getY()));
          }
        };
    button.addLore(LC.info_reset.getLocale());
    gui.addButton(button);
    button =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            consumer.accept(vector);
          }
        };
    button.setName(LC.info_Back.getLocale());
    gui.addButton(button);
    gui.fillEmptyButtons();
    gui.open();
    return true;
  }

  public static boolean openVectorEditor(
      Player player,
      final CMIVector3D vector,
      boolean includeZ,
      final double baseAmount,
      final double resetTo,
      final Consumer<CMIVector3D> consumer,
      final Consumer<CMIVector3D> onModified) {
    CMIGui gui =
        new CMIGui(player) {
          @Override
          public void onClose() {}
        };
    gui.setTitle("Vector modification");
    gui.setInvSize(6);
    CMIGuiButton button =
        new CMIGuiButton(
            20,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4YjcxYjAwOGFkMTQyOGE5NDQxMmZhNTUzMTg3ZWY0MmVmZWFhYzVhNzBkZGY3MTQ4ZGFjZDY4NjMyYTQyMCJ9fX0=")) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case Q:
                vector.setX(resetTo);
                break;
              default:
                vector.setX(
                    CMINumber.sum(
                        vector.getX(),
                        CMIGUIUtil.modifyValue(
                            type, baseAmount, baseAmount * 10.0, -baseAmount, -baseAmount * 10.0)));
            }

            onModified.accept(vector);
            this.updateLooks();
            this.update();
          }

          @Override
          public void updateLooks() {
            this.setName("&7X: &f" + String.format("%.2f", vector.getX()));
          }
        };
    button.addLore(LC.info_reset.getLocale());
    gui.addButton(button);
    if (includeZ) {
      button =
          new CMIGuiButton(
              24,
              CMIItemStack.deserialize(
                  "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk4MDQ3NmNhYTczYzMzNTYzMGM0MDA0NTAxZGY3NjIwMzNkNWZkOGUzNjQzNDRkN2M4YzEzNWU5NzQwNGZjMiJ9fX0=")) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              switch (type) {
                case Q:
                  vector.setZ(resetTo);
                  break;
                default:
                  vector.setZ(
                      CMINumber.sum(
                          vector.getZ(),
                          CMIGUIUtil.modifyValue(
                              type,
                              baseAmount,
                              baseAmount * 10.0,
                              -baseAmount,
                              -baseAmount * 10.0)));
              }

              onModified.accept(vector);
              this.updateLooks();
              this.update();
            }

            @Override
            public void updateLooks() {
              this.setName("&7Z: &f" + String.format("%.2f", vector.getZ()));
            }
          };
      button.addLore(LC.info_reset.getLocale());
      gui.addButton(button);
    }

    button =
        new CMIGuiButton(
            22,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGMwZWI2MjZmYTQ0YTAyMTFjZTEyNzc0YzRjYzQ0ZTZkYTUyMzFhYWJhNGE1MzBkYzJlMjE0MDlmNzk1YTY3YiJ9fX0=")) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case Q:
                vector.setY(resetTo);
                break;
              default:
                vector.setY(
                    CMINumber.sum(
                        vector.getY(),
                        CMIGUIUtil.modifyValue(
                            type, baseAmount, baseAmount * 10.0, -baseAmount, -baseAmount * 10.0)));
            }

            onModified.accept(vector);
            this.updateLooks();
            this.update();
          }

          @Override
          public void updateLooks() {
            this.setName("&7Y: &f" + String.format("%.2f", vector.getY()));
          }
        };
    button.addLore(LC.info_reset.getLocale());
    gui.addButton(button);
    button =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            consumer.accept(vector);
          }
        };
    button.setName(LC.info_Back.getLocale());
    gui.addButton(button);
    gui.fillEmptyButtons();
    gui.open();
    return true;
  }
}
