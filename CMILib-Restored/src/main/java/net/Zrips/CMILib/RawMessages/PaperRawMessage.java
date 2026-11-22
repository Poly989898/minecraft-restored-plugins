package net.Zrips.CMILib.RawMessages;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMICommandSender;
import net.Zrips.CMILib.Container.CMIKyori;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.Style.Merge.Strategy;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PaperRawMessage implements RawMessageBackend {
  private Component message = Component.empty();
  private Component pending = Component.empty();
  private HoverEvent<?> hover = null;
  private ClickEvent<?> clickEvent = null;
  private String insertion = null;
  private final List<String> plainText = new ArrayList<>();
  private boolean dontBreakLine = false;
  private boolean freezeFormat = false;
  private Component frozenFormat = Component.empty();
  private Style lastFormat = Style.empty();
  private final Map<UUID, CMITask> actionBarTasks = new ConcurrentHashMap<>();

  @Override
  public RawMessageBackend RawMessage() {
    return this;
  }

  @Override
  public RawMessageBackend RawMessage(String text) {
    this.addText(text);
    return this;
  }

  private Component parse(String text) {
    return (Component)
        (text != null && !text.isEmpty()
            ? CMIKyori.deserializeLegacy(CMIChatColor.translate(text))
            : Component.empty());
  }

  private Component applyEvents(Component component) {
    if (this.hover != null) {
      component = component.hoverEvent(this.hover);
    }

    if (this.clickEvent != null) {
      component = component.clickEvent(this.clickEvent);
    }

    if (this.insertion != null) {
      component = component.insertion(this.insertion);
    }

    return component;
  }

  private static boolean hasColor(Component component) {
    if (component.style().color() != null) {
      return true;
    }

    for (Component child : component.children()) {
      if (hasColor(child)) {
        return true;
      }
    }

    return false;
  }

  private PaperRawMessage commit() {
    if (!this.pending.equals(Component.empty())) {
      Component component = this.pending;
      if (this.freezeFormat && this.frozenFormat != null) {
        component =
            component.style(
                component.style().merge(this.frozenFormat.style(), Strategy.IF_ABSENT_ON_TARGET));
      }

      if (!this.freezeFormat && !this.message.equals(Component.empty()) && !hasColor(component)) {
        component =
            component.style(component.style().merge(this.lastFormat, Strategy.IF_ABSENT_ON_TARGET));
      }

      this.message = this.message.append(this.applyEvents(component));
      this.lastFormat = getLastEffectiveStyle(this.message, Style.empty());
      this.pending = Component.empty();
    }

    this.hover = null;
    this.clickEvent = null;
    this.insertion = null;
    return this;
  }

  private static Style getLastEffectiveStyle(Component component, Style inherited) {
    Style effective = component.style().merge(inherited, Strategy.IF_ABSENT_ON_TARGET);
    List<Component> children = component.children();
    return children.isEmpty()
        ? effective
        : getLastEffectiveStyle(children.get(children.size() - 1), effective);
  }

  @Override
  public void clear() {
    this.message = Component.empty();
    this.pending = Component.empty();
    this.hover = null;
    this.clickEvent = null;
    this.insertion = null;
    this.plainText.clear();
    this.lastFormat = Style.empty();
  }

  public PaperRawMessage addText(String text) {
    return this.addText(text, true);
  }

  @Deprecated
  @Override
  public RawMessageBackend add(String text, String hoverText, RawMessageCommand rmc) {
    this.addText(text);
    this.addHover(hoverText);
    this.addCommand(rmc.getCommand());
    return this;
  }

  private static boolean endsWithReset(String text) {
    if (text == null) {
      return false;
    }

    int lastReset = Math.max(text.lastIndexOf("§r"), text.lastIndexOf("&r"));
    int lastColor = Math.max(text.lastIndexOf(167), text.lastIndexOf(38));
    return lastReset >= 0 && lastReset == lastColor;
  }

  public PaperRawMessage addText(String text, boolean colorize) {
    if (text != null && !text.isEmpty()) {
      if (!this.pending.equals(Component.empty())) {
        this.commit();
      }

      if (endsWithReset(text)) {
        this.lastFormat = Style.empty();
      }

      this.pending = colorize ? this.parse(text) : CMIKyori.deserializeLegacy(text);
      this.plainText.add(colorize ? CMIChatColor.translate(text) : text);
      return this;
    } else {
      return this;
    }
  }

  @Deprecated
  public PaperRawMessage add(
      String text,
      String hoverText,
      String command,
      String suggestion,
      String url,
      String insertion) {
    this.addText(text);
    this.addHover(hoverText);
    this.addCommand(command);
    this.addSuggestion(suggestion);
    this.addUrl(url);
    this.addInsertion(insertion);
    return null;
  }

  @Deprecated
  public PaperRawMessage addItem(
      String text, ItemStack item, String command, String suggestion, String insertion) {
    this.addText(text);
    this.addItem(item);
    this.addCommand(command);
    this.addSuggestion(suggestion);
    this.addInsertion(insertion);
    return this;
  }

  public PaperRawMessage addText(LC locale) {
    return this.addText(locale.getLocale(), true);
  }

  public PaperRawMessage addText(LC locale, boolean colorize) {
    return this.addText(locale.getLocale(), colorize);
  }

  @Deprecated
  public PaperRawMessage add(String text, List<String> hoverText) {
    this.addText(text);
    this.addHoverText(hoverText);
    return this;
  }

  public PaperRawMessage addHoverText(List<String> hoverText) {
    String hover = hoverText != null ? String.join("\n", hoverText) : null;
    this.addHover(hover);
    return this;
  }

  public PaperRawMessage addHoverText(String hover) {
    this.addHover(hover);
    return this;
  }

  @Override
  public RawMessageBackend addHover(LC lc) {
    this.addHover(lc.getLocale());
    return this;
  }

  public PaperRawMessage addHover(String text) {
    this.hover = this.parse(text).asHoverEvent();
    return this;
  }

  public PaperRawMessage addHover(List<String> lines) {
    TextComponent temp = Component.empty();
    if (lines == null) {
      return this;
    }

    for (int i = 0; i < lines.size(); i++) {
      if (i > 0) {
        temp = (TextComponent) temp.append(Component.newline());
      }

      temp = (TextComponent) temp.append(this.parse(lines.get(i)));
    }

    this.hover = temp.asHoverEvent();
    return this;
  }

  @Override
  public RawMessageBackend addCommand(RawMessageCommand rmc) {
    this.addCommand(rmc.getCommand());
    return this;
  }

  public PaperRawMessage addCommand(String command) {
    if (command != null && !command.isEmpty()) {
      if (!command.startsWith("/")) {
        command = "/" + command;
      }

      this.clickEvent = ClickEvent.runCommand(command);
      return this;
    } else {
      return this;
    }
  }

  public PaperRawMessage addSuggestion(String suggestion) {
    if (suggestion != null && !suggestion.isEmpty()) {
      this.clickEvent = ClickEvent.suggestCommand(suggestion);
      return this;
    } else {
      return this;
    }
  }

  public PaperRawMessage addInsertion(String insertion) {
    this.insertion = insertion;
    return this;
  }

  public PaperRawMessage addUrl(String text, String url, String hoverText) {
    this.addText(text);
    this.addUrl(url);
    this.addHover(hoverText);
    return this;
  }

  public PaperRawMessage addUrl(String text, String url) {
    this.addText(text);
    this.addUrl(url);
    return this;
  }

  public PaperRawMessage addUrl(String url) {
    if (url != null && !url.isEmpty()) {
      if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "https://" + url;
      }

      this.clickEvent = ClickEvent.openUrl(toUrl(url));
      return this;
    } else {
      return this;
    }
  }

  public PaperRawMessage addItem(ItemStack item) {
    if (item != null && !item.getType().isAir()) {
      this.hover = item.asHoverEvent();
      return this;
    } else {
      return this;
    }
  }

  public PaperRawMessage append(RawMessage other, boolean sameLine) {
    this.commit();
    if (!sameLine) {
      this.message = this.message.append(Component.newline());
    }

    PaperRawMessage paper = (PaperRawMessage) other.backend;
    paper.commit();
    this.message = this.message.append(paper.message);
    return this;
  }

  public PaperRawMessage build() {
    return this.commit();
  }

  public PaperRawMessage combineClean() {
    this.commit();
    return this;
  }

  @Override
  public List<String> softCombine() {
    this.commit();
    List<String> result = new ArrayList<>();
    result.add(this.getCombinedClean());
    return result;
  }

  @Override
  public int getFinalLength() {
    this.commit();
    return this.getCombinedClean().length();
  }

  public PaperRawMessage show(Player player) {
    return this.show(player, true);
  }

  public PaperRawMessage show(Player player, boolean softCombined) {
    if (player != null) {
      this.commit();
      player.sendMessage(this.message);
    }

    return this;
  }

  public PaperRawMessage show(Set<Player> players) {
    return this.show(players, true);
  }

  public PaperRawMessage show(Set<Player> players, boolean softCombined) {
    if (players != null && !players.isEmpty()) {
      this.commit();

      for (Player player : players) {
        if (player != null) {
          player.sendMessage(this.message);
        }
      }

      return this;
    } else {
      return this;
    }
  }

  public PaperRawMessage show(CommandSender sender) {
    if (sender == null) {
      return this;
    }

    this.commit();
    if (sender instanceof Player) {
      sender.sendMessage(this.message);
    } else {
      sender.sendMessage(this.getCombinedClean());
    }

    return this;
  }

  public PaperRawMessage show(CMICommandSender sender) {
    return sender == null ? this : this.show(sender.getSender());
  }

  public PaperRawMessage addComponent(Component component) {
    if (component != null) {
      this.message = this.message.append(component);
    }

    return this;
  }

  private static String toUrl(String url) {
    if (url != null && !url.trim().isEmpty()) {
      String normalized = url.trim();
      if (!normalized.matches("(?i)^[a-z][a-z0-9+.-]*://.*")) {
        normalized = "https://" + normalized;
      }

      try {
        URI uri = new URI(normalized);
        if (uri.getScheme() != null && uri.getHost() != null) {
          String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
          if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new IllegalArgumentException("Unsupported URL scheme: " + scheme);
          } else {
            return uri.toASCIIString();
          }
        } else {
          throw new IllegalArgumentException("Invalid URL: " + url);
        }
      } catch (URISyntaxException exception) {
        throw new IllegalArgumentException("Invalid URL: " + url, exception);
      }
    } else {
      return "";
    }
  }

  @Override
  public String getRaw() {
    this.commit();
    return (String) GsonComponentSerializer.gson().serialize(this.message);
  }

  @Override
  public boolean isDontBreakLine() {
    return this.dontBreakLine;
  }

  @Override
  public void setDontBreakLine(boolean dontBreakLine) {
    this.dontBreakLine = dontBreakLine;
  }

  @Override
  public boolean isFormatFrozen() {
    return this.freezeFormat;
  }

  @Override
  public void freezeFormat() {
    this.commit();
    if (this.message.equals(Component.empty())) {
      this.frozenFormat = Component.empty();
    } else {
      this.frozenFormat =
          Component.empty().style(getLastEffectiveStyle(this.message, Style.empty()));
    }

    this.freezeFormat = true;
  }

  @Override
  public void unFreezeFormat() {
    this.freezeFormat = false;
    this.frozenFormat = Component.empty();
  }

  public PaperRawMessage add(String text) {
    return this.addText(text);
  }

  public PaperRawMessage add(String text, String hoverText) {
    return this.add(text, hoverText, null, null, null);
  }

  public PaperRawMessage add(
      String text, String hoverText, String command, String suggestion, String url) {
    this.addText(text);
    if (hoverText != null && !hoverText.isEmpty()) {
      this.addHover(hoverText);
    }

    if (command != null && !command.isEmpty()) {
      this.addCommand(command);
    } else if (suggestion != null && !suggestion.isEmpty()) {
      this.addSuggestion(suggestion);
    } else if (url != null && !url.isEmpty()) {
      this.addUrl(url);
    }

    return this;
  }

  public PaperRawMessage add(String text, String hoverText, String command) {
    return this.add(text, hoverText, command, null, null);
  }

  public PaperRawMessage add(String text, String hoverText, String command, String suggestion) {
    return this.add(text, hoverText, command, suggestion, null);
  }

  public PaperRawMessage add(
      String text,
      String hoverText,
      String command,
      String suggestion,
      String url,
      ItemStack item) {
    this.add(text, hoverText, command, suggestion, url);
    if (item != null) {
      this.addItem(item);
    }

    return this;
  }

  public PaperRawMessage addText(String text, String hoverText) {
    this.addText(text);
    if (hoverText != null) {
      this.addHover(hoverText);
    }

    return this;
  }

  public PaperRawMessage addText(String text, String hoverText, String command) {
    this.addText(text);
    if (hoverText != null) {
      this.addHover(hoverText);
    }

    if (command != null) {
      this.addCommand(command);
    }

    return this;
  }

  public PaperRawMessage addText(
      String text, String hoverText, String command, String suggestion, String url) {
    return this.add(text, hoverText, command, suggestion, url);
  }

  public PaperRawMessage addItem(CMIItemStack item) {
    return item == null ? this : this.addItem(item.getItemStack());
  }

  public PaperRawMessage append(RawMessage other) {
    return this.append(other, true);
  }

  public PaperRawMessage append(PaperRawMessage other) {
    return this.append(other, true);
  }

  public PaperRawMessage append(PaperRawMessage other, boolean sameLine) {
    if (other == null) {
      return this;
    }

    this.commit();
    other.commit();
    if (!sameLine) {
      this.message = this.message.append(Component.newline());
    }

    this.message = this.message.append(other.message);
    this.plainText.add(other.getCombinedClean());
    return this;
  }

  public String getCombinedClean() {
    this.commit();
    StringBuilder result = new StringBuilder();

    for (String text : this.plainText) {
      result.append(text);
    }

    return result.toString();
  }

  public PaperRawMessage combine() {
    return this.commit();
  }

  public String getCombined() {
    return this.getRaw();
  }

  @Override
  public int getFinalLenght() {
    return this.getFinalLength();
  }

  public PaperRawMessage show(CMICommandSender sender, boolean softCombined) {
    return sender == null ? this : this.show(sender.getSender());
  }

  @Override
  public String getTextOnly() {
    this.commit();
    return PlainTextComponentSerializer.plainText().serialize(this.message);
  }

  @Override
  public void setCombined(String combined) {
    this.message = Component.empty();
    this.pending = Component.empty();
    this.plainText.clear();
    this.lastFormat = Style.empty();
    if (combined != null && !combined.isEmpty()) {
      this.message = this.parse(combined);
      this.plainText.add(CMIChatColor.translate(combined));
      this.hover = Component.empty().asHoverEvent();
      this.clickEvent = null;
      this.insertion = null;
    }
  }

  @Override
  public String getShortRaw() {
    this.commit();
    return LegacyComponentSerializer.legacyAmpersand().serialize(this.message);
  }

  @Override
  public LinkedHashMap<RawMessagePartType, String> getTemporyCache() {
    LinkedHashMap<RawMessagePartType, String> cache = new LinkedHashMap<>();
    this.commit();
    String text = LegacyComponentSerializer.legacyAmpersand().serialize(this.message);
    if (!text.isEmpty()) {
      cache.put(RawMessagePartType.Text, text);
    }

    return cache;
  }

  @Override
  public RawMessageBackend showActionBar(Set<Player> players, int keepFor) {
    this.commit();
    if (players != null && !players.isEmpty()) {
      for (Player player : players) {
        if (player != null && player.isOnline()) {
          UUID uuid = player.getUniqueId();
          CMITask previous = this.actionBarTasks.remove(uuid);
          if (previous != null) {
            previous.cancel();
          }

          player.sendActionBar(this.message);
          if (keepFor > 0) {
            long endTime = System.currentTimeMillis() + keepFor * 1000L;
            long interval = 40L;
            CMITask task =
                CMIScheduler.runTimerAsync(
                    CMILib.getInstance(),
                    () -> {
                      if (player.isOnline() && System.currentTimeMillis() < endTime) {
                        player.sendActionBar(this.message);
                      } else {
                        CMITask current = this.actionBarTasks.get(uuid);
                        if (current != null) {
                          this.actionBarTasks.remove(uuid);
                          current.cancel();
                        }
                      }
                    },
                    interval,
                    interval);
            this.actionBarTasks.put(uuid, task);
          }
        }
      }

      return this;
    } else {
      return this;
    }
  }

  @Override
  public RawMessageBackend showActionBar(Player player, int keepFor) {
    return this.showActionBar(Collections.singleton(player), keepFor);
  }

  @Override
  public RawMessageBackend showTitle(Player player, int fadeIn, int keep, int fadeOut) {
    return player == null
        ? this
        : this.showTitle(Collections.singleton(player), fadeIn, keep, fadeOut);
  }

  @Override
  public RawMessageBackend showTitle(Set<Player> players, int fadeIn, int keep, int fadeOut) {
    if (players != null && !players.isEmpty()) {
      this.commit();
      String plainText = PlainTextComponentSerializer.plainText().serialize(this.message);
      int newline = plainText.indexOf(10);
      Component title = Component.empty();
      Component subtitle = Component.empty();
      if (newline >= 0) {
        String titleText = plainText.substring(0, newline);
        String subtitleText = plainText.substring(newline + 1);
        if (!titleText.isEmpty()) {
          title = LegacyComponentSerializer.legacyAmpersand().deserialize(titleText);
        }

        if (!subtitleText.isEmpty()) {
          subtitle = LegacyComponentSerializer.legacyAmpersand().deserialize(subtitleText);
        }
      } else if (!plainText.isEmpty()) {
        title = this.message;
      }

      Times times =
          Times.times(
              Duration.ofMillis(fadeIn * 50L),
              Duration.ofMillis(keep * 50L),
              Duration.ofMillis(fadeOut * 50L));
      Title adventureTitle = Title.title(title, subtitle, times);

      for (Player player : players) {
        if (player != null && player.isOnline()) {
          player.showTitle(adventureTitle);
        }
      }

      return this;
    } else {
      return this;
    }
  }
}
