package net.Zrips.CMILib.Effects;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Container.MaterialPicker;
import net.Zrips.CMILib.GUI.CMIGUIUtil;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

public class CMIEffectPicker {
  private CMIEffect effect = new CMIEffect(CMIEffectManager.CMIParticle.DUST);
  private Player player = null;
  private int tick = 0;
  private int tickRate = 1;
  private boolean showSerializeButton = false;
  private boolean showExampleParticle = false;
  private boolean showBackButton = false;
  private String id = null;
  private double exampleRadius = 2.0;
  private double exampleSpacing = 0.2;
  private double exampleForwardDistance = 3.5;
  private CMITask task = null;
  private static Map<UUID, CMIEffectPicker> openPickers = new HashMap<>();

  private void addOpenPicker(CMIEffectPicker picker) {
    if (picker.player != null) {
      openPickers.put(picker.player.getUniqueId(), picker);
    }
  }

  public static void removePicker(UUID uuid) {
    if (uuid != null) {
      openPickers.remove(uuid);
    }
  }

  private static void recheckCache() {
    openPickers
        .entrySet()
        .removeIf(
            entry -> {
              Player p = Bukkit.getPlayer(entry.getKey());
              return p == null || !p.isOnline();
            });
  }

  @Deprecated
  public CMIEffectPicker(Player player) {
    this(player, null, (CMIEffect) null);
  }

  @Deprecated
  public CMIEffectPicker(Player player, CMIEffectManager.CMIParticle particle) {
    this(player, null, new CMIEffect(particle));
  }

  @Deprecated
  public CMIEffectPicker(Player player, CMIEffect effect) {
    this(player, null, effect);
  }

  public CMIEffectPicker(Player player, String id) {
    this(player, null, (CMIEffect) null);
  }

  public CMIEffectPicker(Player player, String id, CMIEffectManager.CMIParticle particle) {
    this(player, null, new CMIEffect(particle));
  }

  public CMIEffectPicker(Player player, String id, CMIEffect effect) {
    this.player = player;
    this.id = id;
    if (effect != null && effect.getParticle() != null && effect.getParticle().isParticle()) {
      this.effect = effect;
    } else if (player != null && openPickers.containsKey(player.getUniqueId())) {
      CMIEffectPicker existingPicker = openPickers.get(player.getUniqueId());
      if (existingPicker != null && Objects.equals(this.id, existingPicker.getId())) {
        this.effect = existingPicker.getEffect();
        this.tickRate = existingPicker.tickRate;
      }
    }

    this.addOpenPicker(this);
  }

  public void onUIClose() {}

  public void onGoingBack() {}

  public void onUpdate() {}

  public void onSerializeClick() {}

  private Location getNextLocation() {
    Location eye = this.player.getEyeLocation();
    Vector forward = eye.getDirection().normalize();
    Vector up = new Vector(0, 1, 0);
    if (Math.abs(forward.dot(up)) > 0.99) {
      up = new Vector(1, 0, 0);
    }

    Vector right = forward.clone().crossProduct(up).normalize();
    Vector realUp = right.clone().crossProduct(forward).normalize();
    double angle = -(this.tick / this.tickRate) * this.getExampleSpacing();
    double x = this.getExampleRadius() * Math.cos(angle);
    double y = this.getExampleRadius() * Math.sin(angle);
    Vector circleOffset = right.multiply(x).add(realUp.multiply(y));
    return eye.add(forward.clone().multiply(this.getExampleForwardDistance())).add(circleOffset);
  }

  private void showParticle() {
    if (this.task != null) {
      this.task.cancel();
    }

    this.task =
        CMIScheduler.runTimerAsync(
            CMILib.getInstance(),
            () -> {
              this.tick++;
              if (this.tick % this.tickRate == 0) {
                this.getEffect().show(this.player, this.getNextLocation());
              }
            },
            1L,
            1L);
  }

  private void stopParticle() {
    if (this.task != null) {
      this.task.cancel();
      this.task = null;
    }
  }

  private CMIGuiButton createbutton(int slot, final CMIEffectManager.CMIParticle part) {
    CMIGuiButton button =
        new CMIGuiButton(slot, part.getSafeIcon()) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            CMIEffectPicker.this.getEffect().setParticle(part);
            CMIEffectPicker.this.onUpdate();
            CMIEffectPicker.this.open();
          }
        };
    button.setName("&7" + part.getName());
    button.hideItemFlags();
    return button;
  }

  private void updateTitle(CMIGui gui) {
    if (gui != null) {
      gui.updateTitle(this.getEffect().getParticle().getName());
    }
  }

  public boolean open() {
    final CMIGui gui =
        new CMIGui(this.player) {
          @Override
          public void onClose() {
            CMIEffectPicker.this.stopParticle();
            CMIEffectPicker.recheckCache();
            CMIScheduler.runTask(CMILib.getInstance(), () -> CMIEffectPicker.this.onUIClose());
          }
        };
    final CMIEffectManager.CMIParticle currentParticle = this.getEffect().getParticle();
    this.updateTitle(gui);
    gui.setInvSize(GUIManager.GUIRows.r6);
    CMIEffectManager.CMIParticle prev = currentParticle.getPrevParticleEffect();
    gui.addButton(this.createbutton(3, prev));
    prev = prev.getPrevParticleEffect();
    gui.addButton(this.createbutton(2, prev));
    prev = prev.getPrevParticleEffect();
    gui.addButton(this.createbutton(1, prev));
    prev = prev.getPrevParticleEffect();
    gui.addButton(this.createbutton(0, prev));
    CMIEffectManager.CMIParticle next = currentParticle.getNextParticleEffect();
    gui.addButton(this.createbutton(5, next));
    next = next.getNextParticleEffect();
    gui.addButton(this.createbutton(6, next));
    next = next.getNextParticleEffect();
    gui.addButton(this.createbutton(7, next));
    next = next.getNextParticleEffect();
    gui.addButton(this.createbutton(8, next));
    CMIGuiButton button =
        new CMIGuiButton(13, currentParticle.getSafeIcon()) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case Left:
              case LeftShift:
                CMIEffectPicker.this
                    .getEffect()
                    .setParticle(
                        CMIEffectPicker.this.getEffect().getParticle().getNextParticleEffect());
              case MiddleMouse:
              case Q:
              default:
                break;
              case Right:
              case RightShift:
                CMIEffectPicker.this
                    .getEffect()
                    .setParticle(
                        CMIEffectPicker.this.getEffect().getParticle().getPrevParticleEffect());
            }

            CMIEffectPicker.this.onUpdate();
            CMIEffectPicker.this.open();
          }

          @Override
          public void updateLooks() {
            this.setName("&7" + currentParticle.getName());
          }
        };
    button.hideItemFlags();
    gui.addButton(button);
    if (this.isShowExampleParticle()) {
      button =
          new CMIGuiButton(47, CMIMaterial.STICK) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              if (!type.equals(GUIManager.GUIClickType.Q)
                  && !type.equals(GUIManager.GUIClickType.MiddleMouse)) {
                CMIEffectPicker.this.tickRate =
                    CMINumber.clamp(
                        CMIEffectPicker.this.tickRate + CMIEffectPicker.this.returnChange(type), 1);
              } else {
                CMIEffectPicker.this.tickRate = 1;
              }

              this.updateLooks();
              this.update(gui);
              CMIEffectPicker.this.updateTitle(gui);
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_updateSpeed.getLocale("[value]", CMIEffectPicker.this.tickRate));
            }
          };
      gui.addButton(button);
    }

    if (this.isShowSerializeButton()) {
      button =
          new CMIGuiButton(49, CMIMaterial.GREEN_WOOL) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              RawMessage rm = new RawMessage();
              rm.addText(CMIEffectPicker.this.getEffect().serialize());
              rm.addSuggestion(CMIEffectPicker.this.getEffect().serialize());
              rm.show(CMIEffectPicker.this.player);
              CMIEffectPicker.this.onSerializeClick();
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_serializeAndShow.getLocale());
            }
          };
      gui.addButton(button);
    }

    button =
        new CMIGuiButton(21, CMIMaterial.CHEST) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            if (!type.equals(GUIManager.GUIClickType.Q)
                && !type.equals(GUIManager.GUIClickType.MiddleMouse)) {
              CMIEffectPicker.this
                  .getEffect()
                  .setAmount(
                      CMINumber.clamp(
                          CMIEffectPicker.this.getEffect().getAmount()
                              + CMIEffectPicker.this.returnChange(type),
                          1));
            } else {
              CMIEffectPicker.this.getEffect().setAmount(1);
            }

            CMIEffectPicker.this.getEffect().setParticleParameters(null);
            this.updateLooks();
            this.update(gui);
            CMIEffectPicker.this.updateTitle(gui);
          }

          @Override
          public void updateLooks() {
            this.setName(
                LC.info_amount.getLocale("[value]", CMIEffectPicker.this.getEffect().getAmount()));
          }
        };
    gui.addButton(button);
    button =
        new CMIGuiButton(22, CMIMaterial.ACACIA_BOAT) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case MiddleMouse:
              case Q:
                CMIEffectPicker.this.getEffect().setSpeed(0.0F);
                break;
              default:
                CMIEffectPicker.this
                    .getEffect()
                    .setSpeed(
                        CMINumber.clamp(
                            Math.round(
                                    (CMIEffectPicker.this.getEffect().getSpeed()
                                            + CMIEffectPicker.this.returnChange(type) / 10.0F)
                                        * 1000.0F)
                                / 1000.0F,
                            -100.0F));
            }

            CMIEffectPicker.this.getEffect().setParticleParameters(null);
            this.updateLooks();
            this.update(gui);
            CMIEffectPicker.this.updateTitle(gui);
          }

          @Override
          public void updateLooks() {
            this.setName(
                LC.info_speed.getLocale("[value]", CMIEffectPicker.this.getEffect().getSpeed()));
          }
        };
    gui.addButton(button);
    button =
        new CMIGuiButton(23, CMIMaterial.STONE_BRICK_WALL) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            switch (type) {
              case Q:
                CMIEffectPicker.this.getEffect().setOffset(new Vector());
                this.updateLooks();
                this.update(gui);
                CMIEffectPicker.this.getEffect().setParticleParameters(null);
                CMIEffectPicker.this.updateTitle(gui);
                break;
              default:
                CMIGUIUtil.openVectorEditor(
                    CMIEffectPicker.this.player,
                    new CMIVector3D(CMIEffectPicker.this.getEffect().getOffset()),
                    true,
                    0.01,
                    0.0,
                    b -> CMIEffectPicker.this.open(),
                    c -> {
                      c.setX(CMINumber.clamp(c.getX(), -999.0, 999));
                      c.setY(CMINumber.clamp(c.getY(), -999.0, 999));
                      c.setZ(CMINumber.clamp(c.getZ(), -999.0, 999));
                      CMIEffectPicker.this
                          .getEffect()
                          .setOffset(new Vector(c.getX(), c.getY(), c.getZ()));
                      CMIEffectPicker.this.getEffect().setParticleParameters(null);
                      CMIEffectPicker.this.updateTitle(gui);
                    });
            }
          }

          @Override
          public void updateLooks() {
            this.setName(
                LC.info_fullVector.getLocale(CMIEffectPicker.this.getEffect().getOffset()));
          }
        };
    button.addLore(LC.info_reset.getLocale());
    gui.addButton(button);
    final CMIParticleOptions options = this.getEffect().getOptions();
    if (options instanceof CMIParticleColor) {
      CMIParticleColor option = (CMIParticleColor) options;
      gui.addButton(
          this.addColorButton(
              28,
              LC.info_color.getLocale("[color]", this.toHex(option.getColor())),
              this.player,
              option.getColor(),
              newColor -> option.setColor(newColor.getRGBColor())));
    } else if (options instanceof CMIParticleTrail) {
      CMIParticleTrail option = (CMIParticleTrail) options;
      gui.addButton(
          this.addColorButton(
              28,
              LC.info_color.getLocale("[color]", this.toHex(option.getColor())),
              this.player,
              option.getColor(),
              newColor -> option.setColor(newColor.getRGBColor())));
    }

    if (options instanceof CMIParticleDustTransition) {
      CMIParticleDustTransition option = (CMIParticleDustTransition) options;
      gui.addButton(
          this.addColorButton(
              29,
              LC.info_color.getLocale("[color]", this.toHex(option.getColorTo())),
              this.player,
              option.getColorTo(),
              newColor -> option.setColorTo(newColor.getRGBColor())));
    }

    if (options instanceof CMIParticleLocation) {
      final CMIParticleLocation option = (CMIParticleLocation) options;
      button =
          new CMIGuiButton(30, CMIMaterial.STONE_BRICK_WALL) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              switch (type) {
                case Q:
                  option.setOffset(new CMIVector3D(0.0, 0.0, 0.0));
                  this.updateLooks();
                  this.update(gui);
                  CMIEffectPicker.this
                      .getEffect()
                      .resetParticleParameters(CMIEffectPicker.this.player.getLocation());
                  CMIEffectPicker.this.updateTitle(gui);
                  break;
                default:
                  CMIVector3D offset = option.getOffset();
                  CMIGUIUtil.openVectorEditor(
                      CMIEffectPicker.this.player,
                      offset,
                      true,
                      0.01,
                      0.0,
                      b -> CMIEffectPicker.this.open(),
                      c -> {
                        c.setX(CMINumber.clamp(c.getX(), -999.0, 999));
                        c.setY(CMINumber.clamp(c.getY(), -999.0, 999));
                        c.setZ(CMINumber.clamp(c.getZ(), -999.0, 999));
                        option.setOffset(c);
                        CMIEffectPicker.this.getEffect().setParticleParameters(null);
                        CMIEffectPicker.this.updateTitle(gui);
                      });
              }
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_fullVector.getLocale(option.getOffset()));
            }
          };
      button.addLore(LC.info_reset.getLocale());
      gui.addButton(button);
    }

    if (options instanceof CMIParticleMaterial) {
      CMIParticleMaterial option = (CMIParticleMaterial) options;
      CMIMaterial mat = option.getMaterial() == null ? CMIMaterial.STONE : option.getMaterial();
      button =
          new CMIGuiButton(31, mat) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              MaterialPicker picker =
                  new MaterialPicker(CMIEffectPicker.this.player) {
                    @Override
                    public void pickedMaterial(GUIManager.GUIClickType type, CMIMaterial material) {
                      if (material != null) {
                        if (options instanceof CMIParticleBlockData) {
                          ((CMIParticleBlockData) options).setMaterial(material);
                        } else if (options instanceof CMIParticleItemStack) {
                          ((CMIParticleItemStack) options).setMaterial(material);
                        }

                        CMIEffectPicker.this.getEffect().setParticleParameters(null);
                        CMIScheduler.runTask(
                            CMILib.getInstance(), () -> CMIEffectPicker.this.open());
                        CMIEffectPicker.this.updateTitle(gui);
                      }
                    }

                    @Override
                    public boolean validMaterial(CMIMaterial one) {
                      if (one.equals(CMIMaterial.STONE)
                          && Material.STONE.equals(one.getMaterial())) {
                        return true;
                      } else if (!one.equals(CMIMaterial.STONE)
                          && Material.STONE.equals(one.getMaterial())) {
                        return false;
                      } else {
                        return options instanceof CMIParticleBlockData
                            ? one.getMaterial() != null && one.getMaterial().isBlock()
                            : one.getMaterial() != null && !one.getMaterial().isBlock();
                      }
                    }

                    @Override
                    public void onUIClose() {
                      CMIEffectPicker.this.getEffect().setParticleParameters(null);
                      CMIScheduler.runTask(CMILib.getInstance(), () -> CMIEffectPicker.this.open());
                    }
                  };
              picker.openPicker(1, LC.info_pickIcon.getLocale());
            }

            @Override
            public void updateLooks() {}
          };
      button.setName(mat.toString());
      gui.addButton(button);
    }

    if (options instanceof CMIParticleTrail) {
      final CMIParticleTrail option = (CMIParticleTrail) options;
      button =
          new CMIGuiButton(32, CMIMaterial.GREEN_WOOL) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              if (!type.equals(GUIManager.GUIClickType.Q)
                  && !type.equals(GUIManager.GUIClickType.MiddleMouse)) {
                option.setDuration(
                    CMINumber.clamp(
                        option.getDuration() + CMIEffectPicker.this.returnChange(type), 1));
              } else {
                option.setDuration(1);
              }

              CMIEffectPicker.this.getEffect().setParticleParameters(null);
              this.updateLooks();
              this.update(gui);
              CMIEffectPicker.this.updateTitle(gui);
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_duration.getLocale("[value]", option.getDuration()));
            }
          };
      gui.addButton(button);
    }

    if (options instanceof CMIParticleDustOptions) {
      final CMIParticleDustOptions option = (CMIParticleDustOptions) options;
      button =
          new CMIGuiButton(33, CMIMaterial.BLUE_WOOL) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              if (!type.equals(GUIManager.GUIClickType.Q)
                  && !type.equals(GUIManager.GUIClickType.MiddleMouse)) {
                option.setSize(
                    (option.getSize() * 10.0F + CMIEffectPicker.this.returnChange(type)) / 10.0F);
              } else {
                option.setSize(1.0F);
              }

              CMIEffectPicker.this.getEffect().setParticleParameters(null);
              this.updateLooks();
              this.update(gui);
              CMIEffectPicker.this.updateTitle(gui);
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_size.getLocale("[value]", option.getSize()));
            }
          };
      gui.addButton(button);
    }

    if (options instanceof CMIParticleFloat) {
      final CMIParticleFloat option = (CMIParticleFloat) options;
      button =
          new CMIGuiButton(34, CMIMaterial.ACACIA_DOOR) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              if (!type.equals(GUIManager.GUIClickType.Q)
                  && !type.equals(GUIManager.GUIClickType.MiddleMouse)) {
                option.setValue(
                    (option.getValue() * 10.0F + CMIEffectPicker.this.returnChange(type)) / 10.0F);
              } else {
                option.setValue(0.0F);
              }

              CMIEffectPicker.this.getEffect().setParticleParameters(null);
              this.updateLooks();
              this.update(gui);
              CMIEffectPicker.this.updateTitle(gui);
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_value.getLocale("[value]", option.getValue()));
            }
          };
      gui.addButton(button);
    }

    if (options instanceof CMIParticleInteger) {
      final CMIParticleInteger option = (CMIParticleInteger) options;
      button =
          new CMIGuiButton(35, CMIMaterial.TRAPPED_CHEST) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              if (!type.equals(GUIManager.GUIClickType.Q)
                  && !type.equals(GUIManager.GUIClickType.MiddleMouse)) {
                option.setValue(option.getValue() + CMIEffectPicker.this.returnChange(type));
              } else {
                option.setValue(0);
              }

              CMIEffectPicker.this.getEffect().setParticleParameters(null);
              this.updateLooks();
              this.update(gui);
              CMIEffectPicker.this.updateTitle(gui);
            }

            @Override
            public void updateLooks() {
              this.setName(LC.info_value.getLocale("[value]", option.getValue()));
            }
          };
      gui.addButton(button);
    }

    if (this.isShowBackButton()) {
      button =
          new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
            @Override
            public void click(GUIManager.GUIClickType type) {
              CMIEffectPicker.this.onGoingBack();
            }
          };
      button.setName(LC.info_Back.getLocale());
      gui.addButton(button);
    }

    gui.fillEmptyButtons();
    gui.open();
    if (this.isShowExampleParticle()) {
      this.showParticle();
    }

    return true;
  }

  private int returnChange(GUIManager.GUIClickType type) {
    switch (type) {
      case Left:
        return 1;
      case LeftShift:
        return 10;
      case MiddleMouse:
      case Q:
        return 1;
      case Right:
        return -1;
      case RightShift:
        return -10;
      default:
        return 0;
    }
  }

  private CMIGuiButton addColorButton(
      int slot,
      String name,
      final Player player,
      final Color color,
      final Consumer<CMIChatColor> consumer) {
    ItemStack item = CMIMaterial.LEATHER_CHESTPLATE.newItemStack();
    LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
    meta.setColor(color);
    item.setItemMeta(meta);
    CMIGuiButton button =
        new CMIGuiButton(slot, item) {
          @Override
          public void click(GUIManager.GUIClickType type) {
            ChatMessageEdit chatEdit =
                new ChatMessageEdit(player, CMIEffectPicker.this.toHex(color)) {
                  @Override
                  public void run(String message) {
                    if (CMIText.isValidString(message)) {
                      if (!message.contains("#")) {
                        message = "#" + message;
                      }

                      if (!message.startsWith("{")) {
                        message = "{" + message + "}";
                      }

                      CMIChatColor colorx = CMIChatColor.getColor(message);
                      if (colorx != null && colorx.isColor()) {
                        consumer.accept(colorx);
                        CMIEffectPicker.this.getEffect().setParticleParameters(null);
                        CMIEffectPicker.this.updateTitle(getGui());
                      } else {
                        LC.info_incorrectColor.sendMessage(player);
                      }
                    }
                  }

                  @Override
                  public void onDisable() {
                    CMIEffectPicker.this.open();
                  }
                };
            chatEdit.setCheckForCancel(true);
            chatEdit.printMessage();
            this.getGui().getPlayer().closeInventory();
          }
        };
    button.setName(name.replace("[color]", this.toHex(color)));
    button.hideItemFlags();
    return button;
  }

  public CMIEffect getEffect() {
    return this.effect;
  }

  private String toHex(Color color) {
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();

    try {
      int a = color.getAlpha();
      if (a < 255) {
        return String.format("{#%02X%02X%02X%02X}", r, g, b, a);
      }
    } catch (NoSuchMethodError var6) {
    }

    return String.format("{#%02X%02X%02X}", r, g, b);
  }

  public boolean isShowSerializeButton() {
    return this.showSerializeButton;
  }

  public void setShowSerializeButton(boolean showSerializeButton) {
    this.showSerializeButton = showSerializeButton;
  }

  public boolean isShowExampleParticle() {
    return this.showExampleParticle;
  }

  public void setShowExampleParticle(boolean showExampleParticle) {
    this.showExampleParticle = showExampleParticle;
  }

  public double getExampleRadius() {
    return this.exampleRadius;
  }

  public void setExampleRadius(double exampleRadius) {
    this.exampleRadius = exampleRadius;
  }

  public double getExampleSpacing() {
    return this.exampleSpacing;
  }

  public void setExampleSpacing(double exampleSpacing) {
    this.exampleSpacing = exampleSpacing;
  }

  public double getExampleForwardDistance() {
    return this.exampleForwardDistance;
  }

  public void setExampleForwardDistance(double exampleForwardDistance) {
    this.exampleForwardDistance = exampleForwardDistance;
  }

  public boolean isShowBackButton() {
    return this.showBackButton;
  }

  public void setShowBackButton(boolean showBackButton) {
    this.showBackButton = showBackButton;
  }

  public String getId() {
    return this.id;
  }

  public CMIEffectPicker setId(String id) {
    this.id = id;
    return this;
  }
}
