package com.Zrips.CMI.Components;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Modules.CustomText.CText;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

public class CMIComponentPaper extends CMIComponentBase {
  private Component provided = null;
  private static final String prefixColor = "<color:";
  private static final String prefixShadow = "<shadow:";
  private static final String prefixHover = "<hover:";
  private static final String prefixClick = "<click:";
  private static final String suffix = "'>";
  private static final String prefixTextHover = "<hover:show_text:'";
  private static final String prefixItemHover = "<hover:show_item:'";
  private static final String suffixHover = "</hover>";
  private static final String suffixClick = "</click>";

  public CMIComponentPaper(Component component) {
    super(false);
    this.provided = component;
  }

  public CMIComponentPaper(String string) {
    super(false);
    this.add(CMIComponentBase.quickCreate(string));
  }

  public CMIComponentPaper(boolean flag) {
    super(flag);
  }

  @Override
  public String getTextOnly() {
    Component component = this.getCombined();
    return CMIComponentDeserializer.serialize(component);
  }

  @Override
  public void sendMessage(CommandSender commandSender) {
    Component component = this.combine(this.getComponents());
    commandSender.sendMessage(component);
  }

  @Override
  public void sendActionBar(CommandSender commandSender, int value) {
    Component component = this.combine(this.getComponents());
    commandSender.sendActionBar(component);
    if (value > 0) {
      super.sendActionBar(commandSender, value);
    }
  }

  @Nonnull
  @Override
  public ItemStack convertIntoBook(String string, String author) {
    ItemStack itemStack = CMIMaterial.WRITTEN_BOOK.newItemStack();
    BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
    bookMeta.title(Component.text(string));
    bookMeta.author(Component.text(author));
    ArrayList<Component> arrayList = new ArrayList<>();
    int value = this.getComponents().keySet().stream().max(Integer::compareTo).orElse(-1);

    for (int pageIndex = 0; pageIndex <= value; pageIndex++) {
      var list = this.getComponents().get(pageIndex);
      if (list != null) {
        arrayList.add(this.combine(list));
      } else {
        arrayList.add(Component.empty());
      }
    }

    bookMeta.addPages(arrayList.toArray(new Component[0]));
    itemStack.setItemMeta(bookMeta);
    return itemStack;
  }

  @Nonnull
  @Override
  public CompletableFuture<Boolean> applyOnSign(Block block, @Nullable Player player) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_0)
        && !(block.getBlockData() instanceof Sign)) {
      return CompletableFuture.completedFuture(false);
    }

    CompletableFuture completableFuture = new CompletableFuture();
    CMIScheduler.runAtLocation(
        CMI.getInstance(),
        block.getLocation(),
        () -> {
          if (!CMIMaterial.isSign(block.getType())) {
            completableFuture.complete(false);
          } else {
            try {
              org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
              if (player != null) {
                BlockPlaceEvent blockPlaceEvent = CMIEvent.placeSignEvent(block, sign, player);
                Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
                if (blockPlaceEvent.isCancelled()) {
                  completableFuture.complete(false);
                  return;
                }
              }

              ArrayList<List<CMIComponentRoot>> arrayList = new ArrayList();
              ArrayList<List<CMIComponentRoot>> pages = new ArrayList();

              for (int value = 0; value <= 3; value++) {
                arrayList.add(this.getComponents().getOrDefault(value, null));
              }

              for (int lineIndex = 0; lineIndex <= 3; lineIndex++) {
                pages.add(this.getComponents().getOrDefault(lineIndex + 4, null));
              }

              ArrayList<Component> pageComponents = new ArrayList();
              ArrayList rawLines = new ArrayList();
              ArrayList<Component> componentLines = new ArrayList();
              ArrayList<Component> textLines = new ArrayList<>();

              for (List list : arrayList) {
                if (list != null) {
                  pageComponents.add(this.combine(list));
                  rawLines.add(this.combine(list));
                } else {
                  pageComponents.add(null);
                  rawLines.add(Component.empty());
                }
              }

              for (List pageLines : pages) {
                if (pageLines != null) {
                  componentLines.add(this.combine(pageLines));
                  textLines.add(Component.empty());
                } else {
                  componentLines.add(null);
                  textLines.add(Component.empty());
                }
              }

              if (player != null) {
                SignChangeEvent signChangeEvent =
                    new SignChangeEvent(block, player, rawLines, Side.FRONT);
                Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
                if (signChangeEvent.isCancelled()) {
                  completableFuture.complete(false);
                  return;
                }

                signChangeEvent = new SignChangeEvent(block, player, textLines, Side.BACK);
                Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
                if (signChangeEvent.isCancelled()) {
                  completableFuture.complete(false);
                  return;
                }
              }

              for (int row = 0; row < 4; row++) {
                if (pageComponents.get(row) != null) {
                  sign.getSide(Side.FRONT).line(row, (Component) pageComponents.get(row));
                }
              }

              for (int col = 0; col < 4; col++) {
                if (componentLines.get(col) != null) {
                  sign.getSide(Side.BACK).line(col, (Component) componentLines.get(col));
                }
              }

              sign.update();
              completableFuture.complete(true);
            } catch (Exception exception) {
              completableFuture.complete(false);
              completableFuture.completeExceptionally(exception);
            }
          }
        });
    return completableFuture;
  }

  @Override
  public void showTitle(CommandSender commandSender, int fadeIn, int stay, int fadeOut) {
    Component component = this.combine(this.getComponents().getOrDefault(0, new ArrayList<>()));
    Component subtitle = this.combine(this.getComponents().getOrDefault(1, new ArrayList<>()));
    Times times =
        Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
    Title title = Title.title(component, subtitle, times);
    commandSender.showTitle(title);
  }

  @Override
  public void sendPlayerListHeaderAndFooter(CommandSender commandSender) {
    Component component = this.combine(this.getComponents().getOrDefault(0, new ArrayList<>()));
    Component footer = this.combine(this.getComponents().getOrDefault(1, new ArrayList<>()));
    commandSender.sendPlayerListHeaderAndFooter(component, footer);
  }

  private static TextComponent processBasic(CMIComponentRoot cmiComponentRoot) {
    ClickEvent clickEvent = null;
    if (!cmiComponentRoot.getAction().getClickActionType().equals(CMIComponentActionType.none)
        && cmiComponentRoot.getAction().getClickAction() != null) {
      switch (cmiComponentRoot.getAction().getClickActionType()) {
        case none:
        default:
          break;
        case run_command:
          clickEvent = ClickEvent.runCommand(cmiComponentRoot.getAction().getClickAction());
          break;
        case open_url:
          clickEvent = ClickEvent.openUrl(cmiComponentRoot.getAction().getClickAction());
          break;
        case suggest_command:
          clickEvent = ClickEvent.suggestCommand(cmiComponentRoot.getAction().getClickAction());
          break;
        case copy_to_clipboard:
          clickEvent = ClickEvent.copyToClipboard(cmiComponentRoot.getAction().getClickAction());
      }
    }

    HoverEvent hoverEvent = null;
    if (cmiComponentRoot instanceof CMIComponentText) {
      CMIComponentText cmiComponentText = (CMIComponentText) cmiComponentRoot;
      if (cmiComponentText.getHoverText() != null) {
        hoverEvent =
            HoverEvent.showText(
                legacyColorsToTextComponent(
                    CMIChatColor.translate(cmiComponentText.getHoverText())));
      }
    } else if (cmiComponentRoot instanceof CMIComponentItem) {
      CMIComponentItem cmiComponentItem = (CMIComponentItem) cmiComponentRoot;
      hoverEvent = cmiComponentItem.getItem().asHoverEvent();
    }

    TextComponent textComponent =
        (TextComponent)
            ((TextComponent)
                    legacyColorsToTextComponent(cmiComponentRoot.getText()).hoverEvent(hoverEvent))
                .clickEvent(clickEvent);
    if (cmiComponentRoot.getShadowColor() != null
        && Version.isCurrentEqualOrHigher(Version.v1_21_R3)) {
      textComponent =
          (TextComponent)
              CMIComponentShadow.applyShadow(textComponent, cmiComponentRoot.getShadowColor());
    }

    return textComponent;
  }

  public Component getCombined() {
    return this.provided != null ? this.provided : this.combine(this.getComponents());
  }

  public Component getCombinedByPage() {
    if (this.provided != null) {
      return this.provided;
    }

    Component object = Component.empty();
    int value = 0;

    for (var list : this.getComponents().values()) {
      ArrayList arrayList = new ArrayList(list);
      if (++value > 1) {
        object = object.appendNewline();
      }

      object = object.append(this.combine(arrayList));
    }

    return (Component) object;
  }

  private Component combine(Map<Integer, List<CMIComponentRoot>> map) {
    ArrayList arrayList = new ArrayList();

    for (var list : map.values()) {
      arrayList.addAll(list);
    }

    return this.combine(arrayList);
  }

  @Nonnull
  private Component combine(List<CMIComponentRoot> list) {
    Component object = Component.empty();

    for (Component component : this.combineIntoList(list)) {
      object = object.append(component);
    }

    return (Component) object;
  }

  private List<Component> combineIntoList(List<CMIComponentRoot> list) {
    if (list.isEmpty()) {
      return Collections.emptyList();
    }

    ArrayList arrayList = new ArrayList();

    for (CMIComponentRoot cmiComponentRoot : list) {
      if (!this.allowMiniMessage()) {
        arrayList.add(processBasic(cmiComponentRoot));
      } else if (!cmiComponentRoot.getText().contains("<T>")) {
        String string = legacyColorsToMiniMessage(cmiComponentRoot.getText());
        if (cmiComponentRoot instanceof CMIComponentText) {
          CMIComponentText cmiComponentText = (CMIComponentText) cmiComponentRoot;
          if (cmiComponentText.getHoverText() != null && !string.contains("<hover:show_text:'")) {
            string =
                "<hover:show_text:'" + cmiComponentText.getHoverText() + "'>" + string + "</hover>";
          }
        } else if (cmiComponentRoot instanceof CMIComponentItem) {
          CMIComponentItem cmiComponentItem = (CMIComponentItem) cmiComponentRoot;
          Component component = Component.text(string).hoverEvent(cmiComponentItem.getItem());
          string = (String) MiniMessage.miniMessage().serialize(component);
        }

        if (cmiComponentRoot.getAction().getClickActionType() != null
            && cmiComponentRoot.getAction().getClickAction() != null
            && !string.contains("<click:" + cmiComponentRoot.getAction().getClickActionType())) {
          string =
              "<click:"
                  + cmiComponentRoot.getAction().getClickActionType()
                  + ":'"
                  + cmiComponentRoot.getAction().getClickAction()
                  + "'>"
                  + string
                  + "</click>";
        }

        Component miniComponent = MiniMessage.miniMessage().deserialize(string);
        if (cmiComponentRoot.getShadowColor() != null
            && Version.isCurrentEqualOrHigher(Version.v1_21_R3)) {
          miniComponent =
              CMIComponentShadow.applyShadow(miniComponent, cmiComponentRoot.getShadowColor());
        }

        arrayList.add(miniComponent);
      } else {
        CMIComponentBase cmiComponentBase =
            CText.translateToComponent(null, cmiComponentRoot.getText(), false);

        for (var componentList : cmiComponentBase.getComponents().values()) {
          for (CMIComponentRoot childRoot : componentList) {
            arrayList.add(processBasic(childRoot));
          }
        }
      }
    }

    return arrayList;
  }

  @NotNull
  private static TextComponent legacyColorsToTextComponent(String string) {
    return LegacyComponentSerializer.legacySection().deserialize(CMIChatColor.translate(string));
  }

  private static String legacyColorsToMiniMessage(String string) {
    if (!string.contains("#") && !string.contains("§") && !string.contains("&")) {
      return string;
    }

    String result = string;
    if (string.contains("&") || string.contains("{#")) {
      string = CMIChatColor.translate(string, false);
    }

    if (!string.contains("§") && !CMIComponentBase.containsMiniMessage(string)) {
      string = CMIChatColor.translate(string, false);
      if (string.contains("§")) {
        result =
            (String)
                MiniMessage.miniMessage()
                    .serialize(LegacyComponentSerializer.legacySection().deserialize(string));
      }
    } else {
      result =
          (String)
              MiniMessage.miniMessage()
                  .serialize(LegacyComponentSerializer.legacySection().deserialize(string));
    }

    return result.replace("\\<", "<");
  }
}
