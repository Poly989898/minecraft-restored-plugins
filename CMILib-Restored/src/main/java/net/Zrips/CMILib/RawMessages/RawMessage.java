package net.Zrips.CMILib.RawMessages;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILibConfig;
import net.Zrips.CMILib.Container.CMICommandSender;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RawMessage {
  final RawMessageBackend backend = createBackend();
  public static final String newLine = "¥n";
  private static final Pattern QUOTED_NUMBER_WITH_D =
      Pattern.compile("(?<!text:)\"(-?\\d+(?:\\.\\d+)?)(?:[dD])?\"(?=[,\\}])");
  static Pattern patern =
      Pattern.compile(
          "(?i)(?<![a-z0-9_&])(?:(?:https?|ftps?):\\/\\/[a-z0-9.-]+\\.[a-z]{2,}|[a-z0-9.-]+\\.[a-z]{2,3})(?::\\d+)?(?:\\/[^\\s]*)?(?=$|[\\s<>()])");

  public static void updateLinkRegex(String regex) {
    if (regex != null && !regex.isEmpty()) {
      try {
        patern = Pattern.compile(regex);
      } catch (Exception e) {
        CMIMessages.consoleMessage("Failed to compile new link regex: " + regex);
      }
    }
  }

  public static String fixQuotedAmountNumbers(String input) {
    return !input.contains("d\",") && !input.contains("d\"}")
        ? input
        : QUOTED_NUMBER_WITH_D.matcher(input).replaceAll("$1");
  }

  static String extractTag(String message, String tag) {
    int start = message.indexOf("<" + tag + ">");
    if (start == -1) {
      return "";
    }

    start += tag.length() + 2;
    int end = message.indexOf("</" + tag + ">", start);
    return end == -1 ? "" : message.substring(start, end);
  }

  public static RawMessageBackend createBackend() {
    return supportsAdventureComponents() ? new PaperRawMessage() : new LegacyRawMessage();
  }

  static boolean supportsAdventureComponents() {
    return !CMILibConfig.legacyRawMessage
        && Version.isPaperBranch()
        && Version.isCurrentEqualOrHigher(Version.v1_16_0);
  }

  public static RawMessage translateRawMessage(CommandSender sender, String textLine) {
    return translateRawMessage(sender, textLine, false);
  }

  public static RawMessage translateRawMessage(
      CommandSender sender, String textLine, boolean book) {
    return RawMessageParser.translateRawMessage(sender, textLine, book);
  }

  public static RawMessage translateTextOnlyRawMessage(String textLine) {
    return RawMessageParser.translateTextOnlyRawMessage(textLine);
  }

  public RawMessage() {}

  public RawMessage(String text) {
    this.addText(text);
  }

  public void clear() {
    this.backend.clear();
  }

  @Deprecated
  public RawMessage addItem(
      String text, ItemStack item, String command, String suggestion, String insertion) {
    this.addText(text);
    this.addCommand(command);
    this.addSuggestion(suggestion);
    this.addInsertion(insertion);
    this.addItem(item);
    return this;
  }

  @Deprecated
  public RawMessage add(
      String text, String hoverText, String command, String suggestion, String url) {
    this.add(text, hoverText, command, suggestion, url, null);
    return this;
  }

  @Deprecated
  public RawMessage add(
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
    return this;
  }

  @Deprecated
  public RawMessage addUrl(String text, String url) {
    return this.addUrl(text, url, null);
  }

  @Deprecated
  public RawMessage addUrl(String text, String url, String hoverText) {
    this.addText(text);
    this.addHover(hoverText);
    this.addUrl(url);
    return this;
  }

  @Deprecated
  public RawMessage add(String text) {
    return this.add(text, null, null, null, null);
  }

  @Deprecated
  public RawMessage add(String text, String hoverText) {
    return this.add(text, hoverText, null, null, null);
  }

  @Deprecated
  public RawMessage add(String text, List<String> hoverText) {
    String hover = "";
    if (hoverText != null) {
      hover = String.join("\n", hoverText);
    }

    return this.add(text, hover.isEmpty() ? null : hover, null, null, null);
  }

  @Deprecated
  public RawMessage add(String text, String hoverText, RawMessageCommand command) {
    return this.add(text, hoverText, command.getCommand(), null, null);
  }

  @Deprecated
  public RawMessage add(String text, String hoverText, String command) {
    return this.add(text, hoverText, command, null, null);
  }

  @Deprecated
  public RawMessage add(String text, String hoverText, String command, String suggestion) {
    return this.add(text, hoverText, command, suggestion, null);
  }

  @Deprecated
  public RawMessage addHoverText(List<String> hoverText) {
    return this.addHover(hoverText);
  }

  @Deprecated
  public RawMessage addHoverText(String hover) {
    return this.addHover(hover);
  }

  @Deprecated
  public RawMessage combineClean() {
    this.backend.combineClean();
    return this;
  }

  @Deprecated
  public List<String> softCombine() {
    return this.backend.softCombine();
  }

  @Deprecated
  public int getFinalLength() {
    return this.backend.getFinalLength();
  }

  @Deprecated
  public int getFinalLenght() {
    return this.getFinalLength();
  }

  @Deprecated
  public RawMessage show(Player player, boolean softCombined) {
    this.backend.show(player, softCombined);
    return this;
  }

  @Deprecated
  public RawMessage show(Set<Player> players, boolean softCombined) {
    this.backend.show(players, softCombined);
    return this;
  }

  @Deprecated
  public RawMessage addRM(RawMessage other) {
    return this.addRM(other, false);
  }

  @Deprecated
  public RawMessage addRM(RawMessage other, boolean sameLine) {
    this.backend.append(other, sameLine);
    return this;
  }

  public RawMessage addText(String text) {
    this.backend.addText(text);
    return this;
  }

  public RawMessage addText(String text, boolean colorize) {
    this.backend.addText(text, colorize);
    return this;
  }

  public RawMessage addText(LC locale) {
    this.backend.addText(locale);
    return this;
  }

  public RawMessage addText(LC locale, boolean colorize) {
    this.backend.addText(locale, colorize);
    return this;
  }

  public RawMessage addHover(String hover) {
    this.backend.addHover(hover);
    return this;
  }

  public RawMessage addHover(List<String> hover) {
    this.backend.addHover(hover);
    return this;
  }

  public RawMessage addHover(LC locale) {
    return this.addHover(locale.getLocale());
  }

  public RawMessage addCommand(String command) {
    this.backend.addCommand(command);
    return this;
  }

  public RawMessage addCommand(RawMessageCommand command) {
    if (command != null) {
      this.addCommand(command.getCommand());
    }

    return this;
  }

  public RawMessage addSuggestion(String suggestion) {
    this.backend.addSuggestion(suggestion);
    return this;
  }

  public RawMessage addInsertion(String insertion) {
    this.backend.addInsertion(insertion);
    return this;
  }

  public RawMessage addUrl(String url) {
    this.backend.addUrl(url);
    return this;
  }

  public RawMessage addItem(ItemStack item) {
    this.backend.addItem(item);
    return this;
  }

  public RawMessage append(RawMessage other) {
    return this.append(other, false);
  }

  public RawMessage append(RawMessage other, boolean sameLine) {
    this.backend.append(other, sameLine);
    return this;
  }

  public RawMessage build() {
    this.backend.build();
    return this;
  }

  public RawMessage show(Player player) {
    this.backend.show(player);
    return this;
  }

  public RawMessage show(Set<Player> players) {
    this.backend.show(players);
    return this;
  }

  public RawMessage show(CommandSender sender) {
    this.backend.show(sender);
    return this;
  }

  public RawMessage show(CMICommandSender sender) {
    this.backend.show(sender);
    return this;
  }

  public String getTextOnly() {
    return this.backend.getTextOnly();
  }

  public String getRaw() {
    return this.backend.getRaw();
  }

  public void setCombined(String combined) {
    this.backend.setCombined(combined);
  }

  public String getShortRaw() {
    return this.backend.getShortRaw();
  }

  public boolean isDontBreakLine() {
    return this.backend.isDontBreakLine();
  }

  public void setDontBreakLine(boolean dontBreakLine) {
    this.backend.setDontBreakLine(dontBreakLine);
  }

  public boolean isFormatFrozen() {
    return this.backend.isFormatFrozen();
  }

  public void freezeFormat() {
    this.backend.freezeFormat();
  }

  public void unFreezeFormat() {
    this.backend.unFreezeFormat();
  }

  public LinkedHashMap<RawMessagePartType, String> getTemporyCache() {
    return this.backend.getTemporyCache();
  }

  public RawMessage showActionBar(Set<Player> players, int keepForSec) {
    this.backend.showActionBar(players, keepForSec);
    return this;
  }

  public RawMessage showActionBar(Player player, int keepForSec) {
    this.backend.showActionBar(player, keepForSec);
    return this;
  }

  public RawMessage showTitle(Player player, int fadeIn, int keep, int fadeOut) {
    this.backend.showTitle(player, fadeIn, keep, fadeOut);
    return this;
  }

  public RawMessage showTitle(Set<Player> players, int fadeIn, int keep, int fadeOut) {
    this.backend.showTitle(players, fadeIn, keep, fadeOut);
    return this;
  }
}
