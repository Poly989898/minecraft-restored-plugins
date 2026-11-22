package com.Zrips.CMI.Modules.Dialogs;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentPaper;
import com.Zrips.CMI.Components.CMIComponentText;
import com.Zrips.CMI.Locale.CMILC;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.ActionButton.Builder;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction.CustomClickAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput.MultilineOptions;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Items.CMIItemSerializer;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIDialog {
  private DialogBase base = null;
  private List<DialogBody> body = new ArrayList<>();
  private List<ActionButton> buttons = new ArrayList<>();
  private List<DialogInput> inputs = new ArrayList<>();
  private ActionButton exitButton = null;
  private boolean canCloseWithESC = true;
  private Dialog dialog = null;
  private int collumns = 1;
  private boolean closeOnExit = true;
  private String title = "";
  private HashMap<String, Consumer<?>> callbacks = new HashMap<>();
  protected HashMap<String, Object> values = new HashMap<>();
  private CMICustomDialog customDialog = null;
  private Player player = null;

  public CMIDialog(CommandSender commandSender) {
    this(commandSender instanceof Player ? (Player) commandSender : null, "");
  }

  public CMIDialog(Player viewer) {
    this(viewer, "");
  }

  public CMIDialog(CommandSender commandSender, String string) {
    this(commandSender instanceof Player ? (Player) commandSender : null, string);
  }

  public CMIDialog(Player viewer, String string) {
    this.player = viewer;
    this.title = string;
  }

  public CMIDialog(Player viewer, CMICustomDialog cmiCustomDialog) {
    this.player = viewer;
    this.customDialog = cmiCustomDialog;
    CMIDialog cmiDialog = this.getCustomDialog().getCMIDialog(viewer);
    Object object = null;
    if (cmiDialog != null) {
      object = cmiDialog.values;
    } else {
      object = this.getCustomDialog().values.get(viewer.getUniqueId());
    }

    if (object != null) {
      this.values = (HashMap<String, Object>) object;
    }

    this.title = updateVariable(viewer, this.getCustomDialog().getLabel());
    if (!this.getCustomDialog().getVisualFields().isEmpty()) {
      this.addVisuals(this.getCustomDialog().getVisualFields());
    }

    if (this.getCustomDialog().getButtons() != null) {
      this.setCollumns(this.getCustomDialog().getColumns());
      this.addMultiButtons(this.getCustomDialog().getButtons());
    }

    if (!this.getCustomDialog().getInputFields().isEmpty()) {
      this.addInputs(this.getCustomDialog().getInputFields());
    }

    if (this.getCustomDialog().getCloseButton() != null) {
      this.addExitButton(this.getCustomDialog().getCloseButton());
      if (!this.getCustomDialog().getCloseButton().getType().equals(CMIDialogButtonType.URL)) {
        this.setCloseOnExit(this.getCustomDialog().isCloseOnExit());
      }
    }
  }

  public static List<String> updateVariable(Player viewer, List<String> list) {
    if (list == null) {
      return new ArrayList<>();
    }

    for (int value = 0; value < list.size(); value++) {
      list.set(value, updateVariable(viewer, (String) list.get(value)));
    }

    return list;
  }

  public static String updateVariable(Player viewer, String string) {
    if (string == null) {
      return string;
    }

    if (viewer != null) {
      string = string.replace("[playerName]", viewer.getName());
    }

    return CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(viewer, string);
  }

  public void addMultiButton(CMIDialogButton cmiDialogButton, Consumer<Player> consumer) {
    CustomClickAction customClickAction =
        DialogAction.customClick(
            (dialogResponseView, audience) -> {
              if (cmiDialogButton.isSave()) {
                this.save(dialogResponseView);
              }

              if (audience instanceof Player) {
                consumer.accept((Player) audience);
              }

              if (cmiDialogButton.isSave() && this.customDialog != null) {
                this.customDialog.show((Player) audience);
              }
            },
            (Options) Options.builder().build());
    this.getButtons()
        .add(this.makeButtonBuilder(cmiDialogButton).action(customClickAction).build());
  }

  public void addMultiButtons(List<CMIDialogButton> list) {
    for (CMIDialogButton cmiDialogButton : list) {
      this.addMultiButton(cmiDialogButton);
    }
  }

  public void addMultiButton(CMIDialogButton cmiDialogButton) {
    if (!cmiDialogButton.getCondition().isConditionValid(this.player, this.getCustomDialog())) {
      if (this.getCollumns() > 1) {
        CMIDialogButton actionButton = new CMIDialogButton("");
        actionButton.setWidth(1);
        this.addMultiButton(actionButton);
      }
    } else {
      if (cmiDialogButton.getType().equals(CMIDialogButtonType.URL)) {
        this.addMultiButtonUrl(cmiDialogButton);
      } else if (cmiDialogButton.getType().equals(CMIDialogButtonType.SUGGESTION)) {
        this.addMultiButtonCmdSuggestion(cmiDialogButton);
      } else {
        this.addMultiButton(
            cmiDialogButton,
            viewer -> {
              if (cmiDialogButton.getType().equals(CMIDialogButtonType.OPENDIALOG)) {
                String string = this.updateCommandVariables(cmiDialogButton.getCustomActionValue());
                CMICustomDialog cmiCustomDialog =
                    CMI.getInstance().getDialogManager().getDialog(string, false);
                if (cmiCustomDialog != null) {
                  cmiCustomDialog.show(viewer);
                }
              } else if (cmiDialogButton.getAction() != null
                  && cmiDialogButton.getType().equals(CMIDialogButtonType.COMMANDS)) {
                this.show(viewer);
                var list = this.updateCommandVariables(cmiDialogButton.getAction().getActions());
                CMI.getInstance()
                    .getSpecializedCommandManager()
                    .processCmds(updateVariable(viewer, list), viewer);
              }

              if (cmiDialogButton.getOnClick() != null) {
                cmiDialogButton.getOnClick().accept(viewer);
              }
            });
      }
    }
  }

  public void addMultiButtonUrl(CMIDialogButton cmiDialogButton) {
    String string = this.updateCommandVariables(cmiDialogButton.getCustomActionValue());
    this.getButtons()
        .add(
            this.makeButtonBuilder(cmiDialogButton)
                .action(DialogAction.staticAction(ClickEvent.openUrl(string)))
                .build());
  }

  public void addMultiButtonCmdSuggestion(CMIDialogButton cmiDialogButton) {
    String string = this.updateCommandVariables(cmiDialogButton.getCustomActionValue());
    this.getButtons()
        .add(
            this.makeButtonBuilder(cmiDialogButton)
                .action(DialogAction.staticAction(ClickEvent.suggestCommand(string)))
                .build());
  }

  private Builder makeButtonBuilder(CMIDialogButton cmiDialogButton) {
    String string = this.updateCommandVariables(cmiDialogButton.getLabel());
    Builder builder = ActionButton.builder(getComponent(updateVariable(this.player, string)));
    if (cmiDialogButton.getTooltip() != null) {
      String tooltip = this.updateCommandVariables(cmiDialogButton.getTooltip());
      builder.tooltip(getComponent(updateVariable(this.player, tooltip)));
    }

    return builder.width(cmiDialogButton.getWidth());
  }

  private void save(DialogResponseView dialogResponseView) {
    this.callbacks.forEach(
        (string, consumer) -> {
          if (string.startsWith("boolean_")) {
            ((Consumer<Boolean>) consumer).accept(dialogResponseView.getBoolean(string));
            this.values.put(string, dialogResponseView.getBoolean(string));
          } else if (string.startsWith("text_")) {
            ((Consumer<String>) consumer).accept(dialogResponseView.getText(string));
            this.values.put(string, dialogResponseView.getText(string));
          } else if (string.startsWith("float_")) {
            ((Consumer<Float>) consumer).accept(dialogResponseView.getFloat(string));
            this.values.put(string, dialogResponseView.getFloat(string));
          }
        });
  }

  public void addVisuals(List<CMIDialogVisualField> list) {
    for (CMIDialogVisualField cmiDialogVisualField : list) {
      this.addVisual(cmiDialogVisualField);
    }
  }

  public void addVisual(CMIDialogVisualField cmiDialogVisualField) {
    if (cmiDialogVisualField instanceof CMIDialogText) {
      this.addText((CMIDialogText) cmiDialogVisualField);
    } else if (cmiDialogVisualField instanceof CMIDialogItem) {
      this.addItem((CMIDialogItem) cmiDialogVisualField);
    }
  }

  public void addText(CMIDialogText cmiDialogText) {
    if (cmiDialogText.getCondition().isConditionValid(this.player, this.getCustomDialog())) {
      var list = this.updateCommandVariables(cmiDialogText.getLines());
      list = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(this.player, list);
      this.getBody()
          .add(
              DialogBody.plainMessage(
                  getComponent(this.linesToString(list)), cmiDialogText.getWidth()));
    }
  }

  private ItemStack getItem(String string) {
    if (string == null) {
      return CMIMaterial.STONE.newItemStack();
    } else {
      string = this.updateCommandVariables(string);
      if (!string.equalsIgnoreCase("[ItemInHand]") && !string.equalsIgnoreCase("%itemInHand%")) {
        String text =
            CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(this.player, string);
        CMIItemStack cmiItemStack = CMIItemSerializer.deserialize(text);
        return cmiItemStack == null
            ? CMIMaterial.STONE.newItemStack()
            : cmiItemStack.getItemStack();
      } else {
        return CMIItemStack.getItemInMainHand(this.player);
      }
    }
  }

  private List<String> updateCommandVariables(List<String> list) {
    if (this.getCustomDialog() == null) {
      return list;
    } else {
      return this.getCustomDialog().getVariables().isEmpty()
          ? list
          : updateCommandVariables(list, this.getCustomDialog().getVariables());
    }
  }

  public static List<String> updateCommandVariables(List<String> list, Map<String, String> map) {
    if (list == null) {
      return null;
    }

    list = new ArrayList(list);

    for (int value = 0; value < list.size(); value++) {
      list.set(value, updateCommandVariables((String) list.get(value), map));
    }

    return list;
  }

  private String updateCommandVariables(String string) {
    if (this.getCustomDialog() == null) {
      return string;
    } else {
      return this.getCustomDialog().getVariables().isEmpty()
          ? string
          : updateCommandVariables(string, this.getCustomDialog().getVariables());
    }
  }

  public static String updateCommandVariables(String string, Map<String, String> map) {
    if (string == null) {
      return null;
    }

    for (var entry : map.entrySet()) {
      string = string.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
    }

    return string;
  }

  private String linesToString(List<String> list) {
    ArrayList<String> arrayList = new ArrayList();
    boolean flag = false;

    for (String string : list) {
      if (CMIComponentBase.containsMiniMessage(string)) {
        flag = true;
        break;
      }
    }

    if (!flag) {
      for (String line : list) {
        if (line.startsWith("<T>")) {
          arrayList.add(line + "<T>\n</T>");
        } else {
          arrayList.add("<T>" + line + "\n</T>");
        }
      }
    } else {
      arrayList.addAll(list);
    }

    return CMIList.listToString(arrayList);
  }

  public void addItem(CMIDialogItem cmiDialogItem) {
    if (cmiDialogItem.getCondition().isConditionValid(this.player, this.getCustomDialog())) {
      List list =
          cmiDialogItem.getText() != null
              ? this.updateCommandVariables(cmiDialogItem.getText().getLines())
              : null;
      list = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(this.player, list);
      ItemStack itemStack =
          cmiDialogItem.getItemStack() != null
              ? cmiDialogItem.getItemStack()
              : this.getItem(cmiDialogItem.getItemString());
      String string = list == null ? null : this.linesToString(list);
      int value = cmiDialogItem.getText() != null ? cmiDialogItem.getText().getWidth() : 200;
      int height = cmiDialogItem.getHeight();
      int width = cmiDialogItem.getWidth();
      boolean flag = cmiDialogItem.isTooltip();
      boolean decorations = cmiDialogItem.isDecorations();
      if (itemStack.getType() == Material.AIR) {
        this.addText(new CMIDialogText(string == null ? "" : string).setWidth(width));
      } else {
        this.getBody()
            .add(
                DialogBody.item(itemStack)
                    .height(height)
                    .width(width)
                    .showDecorations(decorations)
                    .showTooltip(flag)
                    .description(
                        string == null
                            ? null
                            : DialogBody.plainMessage(getComponent(string), value))
                    .build());
      }
    }
  }

  public void addInputs(List<CMIDialogInputField> list) {
    for (CMIDialogInputField cmiDialogInputField : list) {
      if (cmiDialogInputField instanceof CMIDialogInputBoolean) {
        CMIDialogInputBoolean cmiDialogInputBoolean = (CMIDialogInputBoolean) cmiDialogInputField;
        this.addBoolean(
            cmiDialogInputBoolean,
            value -> {
              if (cmiDialogInputBoolean.getCommandsOnTrue() != null && value) {
                cmiDialogInputBoolean
                    .getCommandsOnTrue()
                    .perform(
                        this,
                        this.player,
                        cmiDialogInputBoolean.getValueOnTrue() != null
                            ? cmiDialogInputBoolean.getValueOnTrue()
                            : String.valueOf(value));
              } else if (cmiDialogInputBoolean.getCommandsOnFalse() != null && !value) {
                cmiDialogInputBoolean
                    .getCommandsOnFalse()
                    .perform(
                        this,
                        this.player,
                        cmiDialogInputBoolean.getValueOnFalse() != null
                            ? cmiDialogInputBoolean.getValueOnFalse()
                            : String.valueOf(value));
              }

              if (cmiDialogInputBoolean.getConsumer() != null) {
                cmiDialogInputBoolean.getConsumer().accept(this.player, value);
              }
            });
      } else if (cmiDialogInputField instanceof CMIDialogInputSingletons) {
        CMIDialogInputSingletons cmiDialogInputSingletons =
            (CMIDialogInputSingletons) cmiDialogInputField;
        this.addSingletonInput(
            cmiDialogInputSingletons,
            string -> {
              CMIDialogInputSingleton cmiDialogInputSingleton =
                  cmiDialogInputSingletons.getEntry(string);
              String text = string;
              if (cmiDialogInputSingletons.getAction() != null) {
                if (cmiDialogInputSingleton != null && cmiDialogInputSingleton.getValue() != null) {
                  text = cmiDialogInputSingleton.getValue();
                }

                cmiDialogInputSingletons.getAction().perform(this, this.player, text);
              }

              if (cmiDialogInputSingleton != null && cmiDialogInputSingleton.getAction() != null) {
                cmiDialogInputSingleton.getAction().perform(this, this.player, text);
              }

              if (cmiDialogInputSingletons.getConsumer() != null) {
                cmiDialogInputSingletons.getConsumer().accept(this.player, string);
              }
            });
      } else if (cmiDialogInputField instanceof CMIDialogInputText) {
        CMIDialogInputText cmiDialogInputText = (CMIDialogInputText) cmiDialogInputField;
        this.addTextInput(
            cmiDialogInputText,
            value -> {
              if (cmiDialogInputText.getAction() != null) {
                cmiDialogInputText.getAction().perform(this, this.player, value);
              }

              if (cmiDialogInputText.getConsumer() != null) {
                cmiDialogInputText.getConsumer().accept(this.player, value);
              }
            });
      } else if (cmiDialogInputField instanceof CMIDialogInputSlider) {
        CMIDialogInputSlider cmiDialogInputSlider = (CMIDialogInputSlider) cmiDialogInputField;
        this.addNumberRangeInput(
            cmiDialogInputSlider,
            value -> {
              if (cmiDialogInputSlider.getAction() != null) {
                cmiDialogInputSlider.getAction().perform(this, this.player, floatToString(value));
              }

              if (cmiDialogInputSlider.getConsumer() != null) {
                cmiDialogInputSlider.getConsumer().accept(this.player, value);
              }
            });
      }
    }
  }

  private static String floatToString(float floatValue) {
    return floatValue % 1.0F == 0.0F
        ? String.valueOf((int) floatValue)
        : String.valueOf(floatValue);
  }

  @Nullable
  public String addBoolean(
      CMIDialogInputBoolean cmiDialogInputBoolean, @Nullable Consumer<Boolean> consumer) {
    if (!cmiDialogInputBoolean
        .getCondition()
        .isConditionValid(this.player, this.getCustomDialog())) {
      return null;
    }

    String string =
        "boolean_"
            + (cmiDialogInputBoolean.getId() == null
                ? this.callbacks.size()
                : cmiDialogInputBoolean.getId());
    String labelText = cmiDialogInputBoolean.getLabel(this.player);
    String valueOnTrue = cmiDialogInputBoolean.getValueOnTrue();
    String valueOnFalse = cmiDialogInputBoolean.getValueOnFalse();
    boolean flag = cmiDialogInputBoolean.getInitialState().getBoolValue(this.player);
    labelText = this.updateCommandVariables(labelText);
    io.papermc.paper.registry.data.dialog.input.BooleanDialogInput.Builder builder =
        DialogInput.bool(string, labelText == null ? null : getComponent(labelText));
    builder.initial(flag);
    if (valueOnTrue != null) {
      builder.onTrue(valueOnTrue);
    }

    if (valueOnFalse != null) {
      builder.onFalse(valueOnFalse);
    }

    this.getInputs().add(builder.build());
    if (consumer != null) {
      this.callbacks.put(string, consumer);
    }

    return string;
  }

  @Nullable
  public String addTextInput(
      CMIDialogInputText cmiDialogInputText, @Nullable Consumer<String> consumer) {
    if (!cmiDialogInputText.getCondition().isConditionValid(this.player, this.getCustomDialog())) {
      return null;
    }

    String string =
        "text_"
            + (cmiDialogInputText.getId() == null
                ? this.callbacks.size()
                : cmiDialogInputText.getId());
    String labelText = cmiDialogInputText.getLabel(this.player);
    int value = cmiDialogInputText.getMaxLength().getIntValue(this.player);
    int height = cmiDialogInputText.getHeight().getIntValue(this.player);
    int maxLines = cmiDialogInputText.getMaxLines().getIntValue(this.player);
    int width = cmiDialogInputText.getWidth();
    String initialValue =
        CMIChatColor.deColorize(
            cmiDialogInputText
                .getInitialText()
                .getStringValue(this.player, this.values.get(string)));
    initialValue = this.updateCommandVariables(initialValue);
    if (value == 0) {
      value = initialValue != null ? initialValue.length() * 2 : 64;
    }

    labelText = this.updateCommandVariables(labelText);
    io.papermc.paper.registry.data.dialog.input.TextDialogInput.Builder builder =
        DialogInput.text(string, getComponent(labelText));
    builder.labelVisible(labelText != null && !labelText.isEmpty());
    if (value > 0) {
      builder.maxLength(value);
    }

    if (height > 0 || maxLines > 0) {
      builder.multiline(
          MultilineOptions.create(maxLines > 0 ? maxLines : null, height > 0 ? height : null));
    }

    if (value > 0 && initialValue != null && initialValue.length() > value) {
      initialValue = initialValue.substring(0, value);
    }

    if (initialValue != null) {
      builder.initial(initialValue);
    }

    if (width > 0) {
      builder.width(width);
    } else if (value > 0) {
      width = 15 + (int) Math.ceil((value - 1) * 7.5);
      width = CMINumber.clamp(width, 15, 512);
      builder.width(width);
    }

    this.getInputs().add(builder.build());
    if (consumer != null) {
      this.callbacks.put(string, consumer);
    }

    return string;
  }

  @Nullable
  public String addNumberRangeInput(
      CMIDialogInputSlider cmiDialogInputSlider, @Nullable Consumer<Float> consumer) {
    if (!cmiDialogInputSlider
        .getCondition()
        .isConditionValid(this.player, this.getCustomDialog())) {
      return null;
    }

    String string =
        "float_"
            + (cmiDialogInputSlider.getId() == null
                ? this.callbacks.size()
                : cmiDialogInputSlider.getId());
    String labelText = cmiDialogInputSlider.getLabel(this.player);
    float floatValue = cmiDialogInputSlider.getStart().getFloatValue(this.player);
    float end = cmiDialogInputSlider.getEnd().getFloatValue(this.player);
    float step = cmiDialogInputSlider.getStep().getFloatValue(this.player);
    Float startValue =
        cmiDialogInputSlider
            .getInitialValue()
            .getFloatValueNullable(this.player, this.values.get(string));
    int value = cmiDialogInputSlider.getWidth();
    if (floatValue > end) {
      float current = floatValue;
      floatValue = end;
      end = current;
    }

    labelText = this.updateCommandVariables(labelText);
    io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput.Builder builder =
        DialogInput.numberRange(string, getComponent(labelText), floatValue, end);
    builder.step(step);
    if (startValue != null && startValue >= floatValue && startValue <= end) {
      builder.initial(startValue);
    }

    if (value > 0) {
      builder.width(value);
    }

    this.getInputs().add(builder.build());
    if (consumer != null) {
      this.callbacks.put(string, consumer);
    }

    return string;
  }

  @Nullable
  public String addSingletonInput(
      CMIDialogInputSingletons cmiDialogInputSingletons, @Nullable Consumer<String> consumer) {
    if (!cmiDialogInputSingletons
        .getCondition()
        .isConditionValid(this.player, this.getCustomDialog())) {
      return null;
    }

    String string =
        "text_"
            + (cmiDialogInputSingletons.getId() != null
                ? cmiDialogInputSingletons.getId()
                : this.callbacks.size());
    Object object = this.values.get(string);
    String labelText = cmiDialogInputSingletons.getLabel(this.player);
    labelText = this.updateCommandVariables(labelText);
    SingleOptionDialogInput singleOptionDialogInput =
        DialogInput.singleOption(
            string,
            cmiDialogInputSingletons.getWidth(),
            cmiDialogInputSingletons.getEntries(
                this.player,
                object instanceof String ? (String) object : null,
                this.getCustomDialog()),
            getComponent(labelText),
            labelText != null && !labelText.isEmpty());
    this.getInputs().add(singleOptionDialogInput);
    if (consumer != null) {
      this.callbacks.put(string, consumer);
    }

    return string;
  }

  public DialogBase createBase() {
    io.papermc.paper.registry.data.dialog.DialogBase.Builder builder =
        DialogBase.builder(getComponent(this.updateCommandVariables(this.title)));
    builder.body(this.getBody());
    builder.inputs(this.getInputs());
    builder.afterAction(DialogAfterAction.NONE).pause(false);
    builder.canCloseWithEscape(true);
    this.setBase(builder.build());
    return this.getBase();
  }

  public static Component getComponent(String string) {
    return getComponent(null, string);
  }

  public static Component getComponent(Player viewer, String string) {
    if (viewer != null) {
      string = updateVariable(viewer, string);
    }

    if (string == null || string.isEmpty()) {
      return Component.empty();
    }

    if (CMIComponentBase.isValidVersion() && CMIComponentBase.containsMiniMessage(string)) {
      CMIComponentPaper cmiComponentPaper = new CMIComponentPaper(true);
      cmiComponentPaper.add(new CMIComponentText(string));
      return cmiComponentPaper.getCombined();
    }

    if (!string.startsWith("<T>")) {
      string = "<T>" + string + "</T>";
    }

    RawMessage rawMessage = RawMessage.translateRawMessage(viewer, string);
    return GsonComponentSerializer.gson().deserialize(rawMessage.getRaw());
  }

  public static void showDialog(@Nonnull Player viewer, @Nonnull String string) {
    show(viewer, getCustomDialog(viewer, string));
  }

  public static CMICustomDialog getCustomDialog(@Nullable Player viewer, @Nonnull String string) {
    return CMI.getInstance().getDialogManager().getDialog(string, viewer);
  }

  public static Set<String> getDialogNames(CommandSender commandSender) {
    HashSet hashSet = new HashSet();
    hashSet.addAll(CMI.getInstance().getDialogManager().getDialogNames(commandSender));
    return hashSet;
  }

  public static Set<String> getDialogNames(boolean flag) {
    HashSet hashSet = new HashSet();
    hashSet.addAll(CMI.getInstance().getDialogManager().getDialogNames(flag));
    return hashSet;
  }

  private static Set<String> getVanillaDialogNames() {
    HashSet<String> hashSet = new HashSet();
    var registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG);

    for (Dialog candidate : registry.stream().collect(Collectors.toList())) {
      hashSet.add(candidate.getKey().getKey());
    }

    return hashSet;
  }

  private Dialog getDialog() {
    if (this.dialog != null) {
      return this.dialog;
    }

    MultiActionType multiActionType =
        !this.getButtons().isEmpty()
            ? DialogType.multiAction(this.getButtons(), this.getExitButton(), this.getCollumns())
            : null;
    this.dialog =
        Dialog.create(
            registryBuilderFactory -> {
              io.papermc.paper.registry.data.dialog.DialogRegistryEntry.Builder builder =
                  (io.papermc.paper.registry.data.dialog.DialogRegistryEntry.Builder)
                      registryBuilderFactory.empty();
              builder.base(this.createBase());
              if (multiActionType != null) {
                builder.type(multiActionType);
              } else if (this.getExitButton() != null) {
                builder.type(DialogType.notice(this.getExitButton()));
              } else {
                this.addExitButton();
                builder.type(DialogType.notice(this.getExitButton()));
              }
            });
    return this.dialog;
  }

  public int getCollumns() {
    return this.collumns;
  }

  public void setCollumns(int value) {
    this.collumns = value;
  }

  public List<ActionButton> getButtons() {
    return this.buttons;
  }

  public void setButtons(List<ActionButton> list) {
    this.buttons = list;
  }

  public ActionButton getExitButton() {
    return this.exitButton;
  }

  public DialogBase getBase() {
    return this.base;
  }

  public CMIDialog setBase(DialogBase dialogBase) {
    this.base = dialogBase;
    return this;
  }

  public List<DialogInput> getInputs() {
    return this.inputs;
  }

  public CMIDialog setInputs(List<DialogInput> list) {
    this.inputs = list;
    return this;
  }

  public List<DialogBody> getBody() {
    return this.body;
  }

  public CMIDialog setBody(List<DialogBody> list) {
    this.body = list;
    return this;
  }

  public boolean isCanCloseWithESC() {
    return this.canCloseWithESC;
  }

  public CMIDialog setCanCloseWithESC(boolean flag) {
    this.canCloseWithESC = flag;
    return this;
  }

  public void show() {
    this.show(this.player);
  }

  public void show(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      this.show((Player) commandSender);
    }
  }

  public void show(Player viewer) {
    show(viewer, this.getDialog());
  }

  private static void show(Player viewer, CMICustomDialog cmiCustomDialog) {
    if (cmiCustomDialog != null && viewer != null) {
      cmiCustomDialog.build(viewer);
      if (cmiCustomDialog.getCMIDialog(viewer) != null) {
        show(viewer, cmiCustomDialog.getCMIDialog(viewer).getDialog());
      }
    }
  }

  private static void show(Player viewer, Dialog dialog) {
    if (dialog != null && viewer != null) {
      viewer.showDialog(dialog);
    } else {
      CMIMessages.consoleMessage("Can't show dialog. Missing entries");
    }
  }

  public void addSaveButton(@Nullable Consumer<Player> consumer) {
    this.addSaveButton(new CMIDialogButton(CMILC.dialog_save.getLocale()), consumer);
  }

  public void addSaveButton(CMIDialogButton cmiDialogButton, @Nullable Consumer<Player> consumer) {
    CustomClickAction customClickAction =
        DialogAction.customClick(
            (dialogResponseView, audience) -> {
              this.save(dialogResponseView);
              this.show((Player) audience);
              if (consumer != null) {
                consumer.accept((Player) audience);
              }
            },
            (Options) Options.builder().build());
    this.getButtons()
        .add(this.makeButtonBuilder(cmiDialogButton).action(customClickAction).build());
  }

  public void addExitButton() {
    this.addExitButton(new CMIDialogButton(CMILC.dialog_close.getLocale()), null);
  }

  public void addExitButton(CMIDialogButton cmiDialogButton) {
    this.addExitButton(cmiDialogButton, null);
  }

  public void addExitButton(CMIDialogButton cmiDialogButton, @Nullable Consumer<Player> consumer) {
    if (cmiDialogButton.getType().equals(CMIDialogButtonType.URL)) {
      this.addExitButtonUrl(cmiDialogButton);
    } else {
      CustomClickAction customClickAction =
          DialogAction.customClick(
              (dialogResponseView, audience) -> {
                if (audience instanceof Player) {
                  if (cmiDialogButton.isSave()) {
                    this.save(dialogResponseView);
                  }

                  if (consumer != null) {
                    consumer.accept((Player) audience);
                  }

                  try {
                    if (cmiDialogButton.getType().equals(CMIDialogButtonType.OPENDIALOG)) {
                      CMICustomDialog cmiCustomDialog =
                          CMI.getInstance()
                              .getDialogManager()
                              .getDialog(cmiDialogButton.getCustomActionValue(), false);
                      if (cmiCustomDialog != null) {
                        cmiCustomDialog.show((Player) audience);
                      }
                    } else if (cmiDialogButton.getAction() != null
                        && !cmiDialogButton.getAction().getActions().isEmpty()) {
                      CMI.getInstance()
                          .getSpecializedCommandManager()
                          .processCmds(
                              updateVariable(
                                  (Player) audience, cmiDialogButton.getAction().getActions()),
                              (Player) audience);
                    }
                  } catch (Exception exception) {
                    exception.printStackTrace();
                  }

                  if (consumer != null) {
                    consumer.accept((Player) audience);
                  }

                  if (this.isCloseOnExit()) {
                    ((Player) audience).closeInventory();
                  }
                }
              },
              (Options) Options.builder().build());
      this.exitButton = this.makeButtonBuilder(cmiDialogButton).action(customClickAction).build();
    }
  }

  public void addExitButtonUrl(CMIDialogButton cmiDialogButton) {
    this.exitButton =
        this.makeButtonBuilder(cmiDialogButton)
            .action(
                DialogAction.staticAction(
                    ClickEvent.openUrl(cmiDialogButton.getCustomActionValue())))
            .build();
  }

  public void close(Player viewer) {
    viewer.closeInventory();
  }

  public boolean isCloseOnExit() {
    return this.closeOnExit;
  }

  public void setCloseOnExit(boolean flag) {
    this.closeOnExit = flag;
  }

  @Nullable
  public CMICustomDialog getCustomDialog() {
    return this.customDialog;
  }
}
