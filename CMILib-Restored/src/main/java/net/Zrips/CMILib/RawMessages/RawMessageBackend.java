package net.Zrips.CMILib.RawMessages;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import net.Zrips.CMILib.Container.CMICommandSender;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface RawMessageBackend {
  RawMessageBackend RawMessage();

  RawMessageBackend RawMessage(String text);

  RawMessageBackend append(RawMessage other);

  RawMessageBackend append(RawMessage other, boolean sameLine);

  RawMessageBackend addText(LC locale, boolean colorize);

  RawMessageBackend addText(LC locale);

  RawMessageBackend addText(String text);

  RawMessageBackend addText(String text, boolean colorize);

  RawMessageBackend addHover(List<String> lines);

  RawMessageBackend addHover(LC locale);

  RawMessageBackend addHover(String text);

  RawMessageBackend addCommand(RawMessageCommand rmc);

  RawMessageBackend addCommand(String command);

  RawMessageBackend addSuggestion(String suggestion);

  RawMessageBackend addInsertion(String insertion);

  RawMessageBackend addItem(ItemStack item);

  RawMessageBackend addUrl(String url);

  RawMessageBackend build();

  RawMessageBackend show(Set<Player> players);

  RawMessageBackend show(Player player);

  RawMessageBackend show(CMICommandSender sender);

  RawMessageBackend show(CommandSender sender);

  RawMessageBackend showActionBar(Set<Player> players, int keepFor);

  RawMessageBackend showActionBar(Player player, int keepFor);

  RawMessageBackend showTitle(Set<Player> players, int fadeIn, int keep, int fadeOut);

  RawMessageBackend showTitle(Player player, int fadeIn, int keep, int fadeOut);

  void clear();

  @Deprecated
  RawMessageBackend add(
      String text, String hoverText, String command, String suggestion, String insertion);

  @Deprecated
  RawMessageBackend add(
      String text,
      String url,
      String hoverText,
      String command,
      String suggestion,
      String insertion);

  @Deprecated
  RawMessageBackend addUrl(String text, String url);

  @Deprecated
  RawMessageBackend addUrl(String text, String url, String hoverText);

  @Deprecated
  RawMessageBackend add(String text);

  @Deprecated
  RawMessageBackend add(String text, String hoverText);

  @Deprecated
  RawMessageBackend add(String text, List<String> hoverText);

  @Deprecated
  RawMessageBackend add(String text, String hoverText, RawMessageCommand rmc);

  @Deprecated
  RawMessageBackend add(String text, String hoverText, String command);

  @Deprecated
  RawMessageBackend add(String text, String hoverText, String command, String suggestion);

  @Deprecated
  RawMessageBackend addHoverText(List<String> hoverText);

  @Deprecated
  RawMessageBackend addHoverText(String hover);

  @Deprecated
  RawMessageBackend addItem(
      String text, ItemStack item, String hoverText, String command, String suggestion);

  @Deprecated
  List<String> softCombine();

  @Deprecated
  RawMessageBackend combineClean();

  @Deprecated
  RawMessageBackend show(Set<Player> players, boolean softCombined);

  @Deprecated
  RawMessageBackend show(Player player, boolean softCombined);

  @Deprecated
  int getFinalLength();

  int getFinalLenght();

  String getTextOnly();

  String getRaw();

  void setCombined(String combined);

  String getShortRaw();

  boolean isDontBreakLine();

  void setDontBreakLine(boolean dontBreakLine);

  boolean isFormatFrozen();

  void freezeFormat();

  void unFreezeFormat();

  LinkedHashMap<RawMessagePartType, String> getTemporyCache();
}
