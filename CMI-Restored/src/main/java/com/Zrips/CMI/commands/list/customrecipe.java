package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Recipes.recipeInformation;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.HashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINamespacedKey;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import net.Zrips.CMILib.Recipes.CMIRecipeChoice;
import net.Zrips.CMILib.Recipes.CMIRecipeCraftData;
import net.Zrips.CMILib.Recipes.CMIRecipeIngredient;
import net.Zrips.CMILib.Recipes.CMIRecipeType;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class customrecipe implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "removed", "{gcp}Recipe removed: [id]. For FULL effect you will need server restart.");
    configReader.get("noRecipe", "{gcp}There is no custom recipe by this id");
    configReader.get("edit", "{gcp}Edit recipe ({gcs}[type]{gcp})");
    configReader.get("addNew", "{gcp}Add new recipe ({gcs}[type]{gcp})");
    configReader.get("shapelessRecipe", "{gcp}Make new shapeless recipe");
    configReader.get("shapedRecipe", "{gcp}Make new shaped recipe");
    configReader.get("furnaceRecipe", "{gcp}Make new furnace recipe");
    configReader.get("campfireRecipe", "{gcp}Make new campfire recipe");
    configReader.get("smithingRecipe", "{gcp}Make new smithing recipe");
    configReader.get("stonecuttingRecipe", "{gcp}Make new stonecutting recipe");
    configReader.get("blastingRecipe", "{gcp}Make new blasting recipe");
    configReader.get("smokingRecipe", "{gcp}Make new smoking recipe");
    configReader.get("exact", "{gcp}Require exact ingredients: {gcs}[state]");
    configReader.get("exists", "{gcw}Looks like recipe like this already exists");
    configReader.get("mainTitle", "{gcp}Custom recipes ({gcs}[current]{gcp}/{gcs}[total]{gcp})");
  }

  @CAnnotation(
      priority = 125,
      info = "Manages item custom recipes",
      args = "",
      regVar = 0,
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    this.openMainMenu(player, 1);
    return true;
  }

  public void openMainMenu(final Player player, final int value) {
    PageInfo pageInfo =
        new PageInfo(36, CMI.getInstance().getRecipeManager().getCustomRecipes().size(), value);
    CMIGui cmiGui = new CMIGui(player);
    cmiGui.setTitle(
        CMILC.getIM(
            this,
            "mainTitle",
            "[current]",
            pageInfo.getCurrentPage(),
            "[total]",
            pageInfo.getTotalPages()));
    cmiGui.setInvSize(GUIRows.r6);

    for (final CMIRecipe cmiRecipe :
        CMI.getInstance().getRecipeManager().getCustomRecipes().values()) {
      if (pageInfo.isEntryOk()) {
        ItemStack itemStack = cmiRecipe.getResult();
        itemStack = itemStack.clone();
        CMIItemStack cmiItemStack = new CMIItemStack(itemStack);
        cmiItemStack.addLore("&r&f" + cmiRecipe.getType().name());
        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(cmiItemStack.getItemStack()) {
              public void click(GUIClickType guiClickType) {
                customrecipe.this.openSubMenu(player, cmiRecipe);
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    }

    CMIGuiButton typeButton =
        new CMIGuiButton(45, CMIMaterial.YELLOW_WOOL) {
          public void click(GUIClickType clickType) {
            customrecipe.this.openSubMenu(player, CMIRecipeType.Shaped);
          }
        };
    typeButton.setName(CMILC.getIM(this, "shapedRecipe"));
    cmiGui.addButton(typeButton);
    typeButton =
        new CMIGuiButton(46, CMIMaterial.GREEN_WOOL) {
          public void click(GUIClickType clickType) {
            customrecipe.this.openSubMenu(player, CMIRecipeType.Shapeless);
          }
        };
    typeButton.setName(CMILC.getIM(this, "shapelessRecipe"));
    cmiGui.addButton(typeButton);
    typeButton =
        new CMIGuiButton(48, CMIMaterial.FURNACE) {
          public void click(GUIClickType clickType) {
            customrecipe.this.openSubMenu(player, CMIRecipeType.Furnace);
          }
        };
    typeButton.setName(CMILC.getIM(this, "furnaceRecipe"));
    cmiGui.addButton(typeButton);
    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      typeButton =
          new CMIGuiButton(49, CMIMaterial.CAMPFIRE) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openSubMenu(player, CMIRecipeType.Campfire);
            }
          };
      typeButton.setName(CMILC.getIM(this, "campfireRecipe"));
      cmiGui.addButton(typeButton);
      typeButton =
          new CMIGuiButton(50, CMIMaterial.STONECUTTER) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openSubMenu(player, CMIRecipeType.Stonecutting);
            }
          };
      typeButton.setName(CMILC.getIM(this, "stonecuttingRecipe"));
      cmiGui.addButton(typeButton);
      typeButton =
          new CMIGuiButton(51, CMIMaterial.SMITHING_TABLE) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openSubMenu(player, CMIRecipeType.Smithing);
            }
          };
      typeButton.setName(CMILC.getIM(this, "smithingRecipe"));
      cmiGui.addButton(typeButton);
      typeButton =
          new CMIGuiButton(52, CMIMaterial.BLAST_FURNACE) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openSubMenu(player, CMIRecipeType.Blasting);
            }
          };
      typeButton.setName(CMILC.getIM(this, "blastingRecipe"));
      cmiGui.addButton(typeButton);
      typeButton =
          new CMIGuiButton(53, CMIMaterial.SMOKER) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openSubMenu(player, CMIRecipeType.Smoking);
            }
          };
      typeButton.setName(CMILC.getIM(this, "smokingRecipe"));
      cmiGui.addButton(typeButton);
    }

    if (value > 1) {
      typeButton =
          new CMIGuiButton(36, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openMainMenu(player, value - 1);
            }
          };
      typeButton.setName(LC.info_Back.getLocale(new Object[0]));
      cmiGui.addButton(typeButton);
    }

    if (value < pageInfo.getTotalPages()) {
      typeButton =
          new CMIGuiButton(44, CMILib.getInstance().getConfigManager().getGUINextPage()) {
            public void click(GUIClickType clickType) {
              customrecipe.this.openMainMenu(player, value + 1);
            }
          };
      typeButton.setName(LC.info_Forward.getLocale(new Object[0]));
      cmiGui.addButton(typeButton);
    }

    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  public void openSubMenu(Player player, CMIRecipeType cmiRecipeType) {
    this.openSubMenu(player, cmiRecipeType, null);
  }

  public void openSubMenu(Player player, CMIRecipe cmiRecipe) {
    this.openSubMenu(player, null, cmiRecipe);
  }

  public void openSubMenu(
      final Player player, CMIRecipeType cmiRecipeType, final CMIRecipe cmiRecipe) {
    boolean flag = true;
    if (cmiRecipe != null) {
      cmiRecipeType = cmiRecipe.getType();
      flag = false;

      for (CMIRecipeIngredient cmiRecipeIngredient : cmiRecipe.getIngredients().values()) {
        if (cmiRecipeIngredient.getChoice().equals(CMIRecipeChoice.byItemStack)) {
          flag = true;
        }
      }
    }

    final CMIRecipeType recipeType = cmiRecipeType;
    final CMIGui cmiGui = new CMIGui(player);
    cmiGui.setTitle(CMILC.getIM(this, "addNew", "[type]", recipeType.name()));
    cmiGui.setInvSize(GUIRows.r6);
    recipeInformation recipeInformation = null;
    if (cmiGui.getTempData() == null) {
      recipeInformation = new recipeInformation();
    } else {
      recipeInformation = (recipeInformation) cmiGui.getTempData();
    }

    recipeInformation.setExact(flag);
    cmiGui.setTempData(recipeInformation);
    CMIGuiButton cmiGuiButton = new CMIGuiButton(10);
    cmiGuiButton.unlockField();
    if (cmiRecipe != null) {
      for (var entry : cmiRecipe.getIngredients().entrySet()) {
        CMIGuiButton slotButton =
            new CMIGuiButton(
                (Integer) recipeType.getIngredientSlots().get((Integer) entry.getKey()),
                ((CMIRecipeIngredient) entry.getValue()).getItem().clone());
        slotButton.unlockField();
        cmiGui.addButton(slotButton);
      }

      CMIGuiButton extraButton =
          new CMIGuiButton(
              CMI.getInstance().getRecipeManager().getGUIRecipeResultSlot(),
              cmiRecipe.getResult().clone());
      extraButton.unlockField();
      cmiGui.addButton(extraButton);
    }

    for (int value : recipeType.getIngredientSlots()) {
      if (cmiGui.getButtons().get(value) == null) {
        CMIGuiButton ingredientButton =
            new CMIGuiButton(value) {
              public void click(GUIClickType guiClickType) {}
            };
        ingredientButton.unlockField();
        cmiGui.addButton(ingredientButton);
      }
    }

    if (cmiGui.getButtons().get(CMI.getInstance().getRecipeManager().getGUIRecipeResultSlot())
        == null) {
      cmiGui.addButton(
          cmiGuiButton
              .clone()
              .setSlot(CMI.getInstance().getRecipeManager().getGUIRecipeResultSlot()));
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1) && recipeType.isTimeAndExp()) {
      CMIRecipeCraftData cmiRecipeCraftData = new CMIRecipeCraftData(0.0F, 60);
      if (cmiRecipe != null && cmiRecipe.getData() != null) {
        cmiRecipeCraftData = cmiRecipe.getData();
      }

      final CMIRecipeCraftData craftData = cmiRecipeCraftData;
      recipeInformation recipeInfo = null;
      if (cmiGui.getTempData() == null) {
        recipeInfo = new recipeInformation();
      } else {
        recipeInfo = (recipeInformation) cmiGui.getTempData();
      }

      recipeInfo.setCraftData(craftData);
      cmiGui.setTempData(recipeInfo);
      CMIGuiButton timeButton =
          new CMIGuiButton(4, CMIMaterial.CLOCK) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Left:
                  craftData.setCookingTime(craftData.getCookingTime() + 1);
                  break;
                case LeftShift:
                  craftData.setCookingTime(craftData.getCookingTime() + 10);
                  break;
                case Right:
                  craftData.setCookingTime(craftData.getCookingTime() - 1);
                  if (craftData.getCookingTime() < 1) {
                    craftData.setCookingTime(1);
                  }
                  break;
                case RightShift:
                  craftData.setCookingTime(craftData.getCookingTime() - 10);
                  if (craftData.getCookingTime() < 1) {
                    craftData.setCookingTime(1);
                  }
                  break;
                case MiddleMouse:
                case Q:
                  craftData.setCookingTime(20);
                case QControl:
              }

              this.updateLooks();
              this.update(cmiGui);
            }

            public void updateLooks() {
              this.setName(
                  "&7Cooking time: &f"
                      + (int) (craftData.getCookingTime() / 20.0 * 100.0) / 100.0
                      + "s");
            }
          };
      cmiGui.addButton(timeButton);
      timeButton =
          new CMIGuiButton(13, CMIMaterial.EXPERIENCE_BOTTLE) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Left:
                  craftData.setExp((craftData.getExp() * 100.0F + 1.0F) / 100.0F);
                  if (craftData.getExp() > 1.0F) {
                    craftData.setExp(1.0F);
                  }
                  break;
                case LeftShift:
                  craftData.setExp((craftData.getExp() * 100.0F + 10.0F) / 100.0F);
                  if (craftData.getExp() > 1.0F) {
                    craftData.setExp(1.0F);
                  }
                  break;
                case Right:
                  craftData.setExp(craftData.getExp() - 0.01F);
                  if (craftData.getExp() < 0.0F) {
                    craftData.setExp(0.0F);
                  }
                  break;
                case RightShift:
                  craftData.setExp(craftData.getExp() - 0.1F);
                  if (craftData.getExp() < 0.0F) {
                    craftData.setExp(0.0F);
                  }
                  break;
                case MiddleMouse:
                case Q:
                  craftData.setExp(0.0F);
                case QControl:
              }

              this.updateLooks();
              this.update(cmiGui);
            }

            public void updateLooks() {
              this.setName("&7Experience gain: &f" + craftData.getExp());
            }
          };
      cmiGui.addButton(timeButton);
    }

    CMIGuiButton saveButton =
        new CMIGuiButton(45, CMIMaterial.BLUE_WOOL) {
          public void click(GUIClickType clickType) {
            HashMap<Integer, CMIRecipeIngredient> hashMap = new HashMap();
            ArrayList<ItemStack> arrayList = new ArrayList();
            recipeInformation saveInfo = null;
            if (cmiGui.getTempData() == null) {
              saveInfo = new recipeInformation();
            } else {
              saveInfo = (recipeInformation) cmiGui.getTempData();
            }

            int slotIndex = -1;

            for (Integer integer : recipeType.getIngredientSlots()) {
              slotIndex++;
              ItemStack itemStack = cmiGui.getInv().getItem(integer);
              if (itemStack != null) {
                ItemStack ingredientStack = itemStack.clone();
                CMIRecipeIngredient ingredient = new CMIRecipeIngredient(ingredientStack);
                if (saveInfo.isExact()) {
                  ingredient.setChoice(CMIRecipeChoice.byItemStack);
                }

                hashMap.put(slotIndex, ingredient);
                arrayList.add(itemStack.clone());
              }
            }

            ItemStack resultStack =
                cmiGui
                    .getInv()
                    .getItem(CMI.getInstance().getRecipeManager().getGUIRecipeResultSlot());
            if (hashMap.size() >= recipeType.getMinSlots() && resultStack != null) {
              CMIRecipeCraftData saveCraftData = null;
              if (cmiGui.getTempData() instanceof recipeInformation) {
                saveCraftData = ((recipeInformation) cmiGui.getTempData()).getCraftData();
              }

              Recipe recipe =
                  CMIRecipe.createRecipe(recipeType, resultStack, hashMap, saveCraftData);
              if (CMIRecipe.getRecipe(CMINamespacedKey.getKey(recipe)) != null
                  || cmiRecipe != null
                      && CMIRecipe.getRecipe(CMINamespacedKey.getKey(cmiRecipe.getRecipe()))
                          != null) {
                if (cmiRecipe == null) {
                  CMILC.info(customrecipe.class, player, "exists");
                  return;
                }

                CMI.getInstance().getRecipeManager().removeRecipe(cmiRecipe);
                if (Version.isCurrentEqualOrHigher(Version.v1_15_R1)) {
                  CMINamespacedKey cmiNamespacedKey =
                      CMINamespacedKey.getKey(cmiRecipe.getRecipe());
                  Bukkit.getServer()
                      .removeRecipe(
                          new NamespacedKey(
                              cmiNamespacedKey.getNamespace(), cmiNamespacedKey.getKey()));
                } else {
                  CMILC.info(
                      customrecipe.class, player, "removed", "[id]", cmiRecipe.getIdentificator());
                }
              }

              CMI.getInstance()
                  .getRecipeManager()
                  .addRecipe(
                      recipeType, resultStack, hashMap, saveCraftData, saveInfo.getCustomId());
              player.getInventory().addItem(new ItemStack[] {resultStack});

              for (ItemStack extraStack : arrayList) {
                if (extraStack != null) {
                  player.getInventory().addItem(new ItemStack[] {extraStack});
                }
              }

              player.updateInventory();
              if (recipeType.equals(CMIRecipeType.Stonecutting)) {
                CMILC.info_Relog.sendMessage(player);
              }

              customrecipe.this.openMainMenu(player, 1);
            }
          }
        };
    saveButton.setName(LC.info_Save.getLocale(new Object[0]));
    cmiGui.addButton(saveButton);
    CMIGuiButton backButton =
        new CMIGuiButton(49, CMIMaterial.CYAN_WOOL) {
          public void click(GUIClickType clickType) {
            customrecipe.this.openMainMenu(player, 1);
          }
        };
    backButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(backButton);
    if (cmiRecipe != null) {
      CMIGuiButton deleteButton =
          new CMIGuiButton(53, CMIMaterial.RED_WOOL) {
            public void click(GUIClickType clickType) {
              if (CMI.getInstance().getRecipeManager().removeRecipe(cmiRecipe)) {
                CMILC.info(
                    customrecipe.class, player, "removed", "[id]", cmiRecipe.getIdentificator());
              }

              customrecipe.this.openMainMenu(player, 1);
            }
          };
      deleteButton.setName(LC.info_Delete.getLocale(new Object[0]));
      cmiGui.addButton(deleteButton);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        && !recipeType.equals(CMIRecipeType.Stonecutting)) {
      CMIGuiButton exactButton =
          new CMIGuiButton(18, CMIMaterial.YELLOW_STAINED_GLASS_PANE) {
            public void click(GUIClickType clickType) {
              recipeInformation toggleInfo = null;
              if (cmiGui.getTempData() == null) {
                toggleInfo = new recipeInformation();
              } else {
                toggleInfo = (recipeInformation) cmiGui.getTempData();
              }

              toggleInfo.setExact(!toggleInfo.isExact());
              cmiGui.setTempData(toggleInfo);
              this.updateLooks();
              this.update(cmiGui);
            }

            public void updateLooks() {
              recipeInformation labelInfo = null;
              if (cmiGui.getTempData() == null) {
                labelInfo = new recipeInformation();
              } else {
                labelInfo = (recipeInformation) cmiGui.getTempData();
              }

              this.setItem(
                  labelInfo.isExact()
                      ? CMIMaterial.BLUE_STAINED_GLASS_PANE
                      : CMIMaterial.YELLOW_STAINED_GLASS_PANE);
              this.setName(
                  CMILC.getIM(
                      customrecipe.class,
                      "exact",
                      "[state]",
                      labelInfo.isExact()
                          ? LC.modify_enabled.getLocale(new Object[0])
                          : LC.modify_disabled.getLocale(new Object[0])));
            }
          };
      cmiGui.addButton(exactButton);
    }

    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static boolean startChatKeyRecord(Player player, final CMIGui cmiGui) {
    CMIUser.closeInventory(player);
    ChatMessageEdit chatMessageEdit =
        new ChatMessageEdit(player) {
          public void run(String string) {
            string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
            if (CMIText.isValidString(string)) {
              recipeInformation recipeInformation = null;
              if (cmiGui.getTempData() == null) {
                recipeInformation = new recipeInformation();
              } else {
                recipeInformation = (recipeInformation) cmiGui.getTempData();
              }

              recipeInformation.setCustomId(CMIChatColor.stripColor(string));
              cmiGui.setTempData(recipeInformation);
              ((CMIGuiButton) cmiGui.getButtons().get(8)).updateLooks();
            }
          }

          public void onDisable() {
            cmiGui.open();
          }
        };
    chatMessageEdit.setCheckForCancel(true);
    return true;
  }
}
