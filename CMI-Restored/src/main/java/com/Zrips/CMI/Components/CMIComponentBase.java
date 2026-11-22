package com.Zrips.CMI.Components;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIComponentBase {
  private final Map<Integer, List<CMIComponentRoot>> componentMap = new HashMap<>();
  private boolean miniMessage = false;
  private static final Pattern VALID_MINIMESSAGE_TAGS =
      Pattern.compile("</?[a-zA-Z0-9_-]+(?:\\:[^<>]*)?/?>");
  private static final Pattern MINIMESSAGE_PATTERN =
      Pattern.compile("(?i)</?[a-z][a-z0-9:_-]{2,}(\\b[^>]*)?>|<#([0-9a-f]{6}|[0-9a-f]{8})>");
  private static final Map<UUID, CMIComponentBase.ActionBarTask> runningActionBarTasks =
      new ConcurrentHashMap<>();

  public static boolean isValidVersion() {
    return Version.isCurrentEqualOrHigher(Version.v1_21_R1) && Version.isPaperBranch();
  }

  public static String stripMiniMessageTags(String string) {
    return string == null ? null : VALID_MINIMESSAGE_TAGS.matcher(string).replaceAll("");
  }

  public static boolean containsMiniMessage(String string) {
    return MINIMESSAGE_PATTERN.matcher(string).find();
  }

  @Nonnull
  public static CMIComponentBase builder() {
    return builder(false);
  }

  @Nonnull
  public static CMIComponentBase builder(boolean flag) {
    return isValidVersion() ? new CMIComponentPaper(flag) : new CMIComponentSpigot(flag);
  }

  @Nonnull
  public static CMIComponentBase quickAdd(String string) {
    return isValidVersion()
        ? new CMIComponentPaper(false).add(quickCreate(string))
        : new CMIComponentSpigot(false).add(quickCreate(string));
  }

  @Nonnull
  public static CMIComponentBase quickAdd(String string, ItemStack itemStack) {
    return isValidVersion()
        ? new CMIComponentPaper(false).add(quickCreate(string, itemStack))
        : new CMIComponentSpigot(false).add(quickCreate(string, itemStack));
  }

  @Nonnull
  public static CMIComponentText quickCreate(String string) {
    return new CMIComponentText(string);
  }

  @Nullable
  public static CMIComponentItem quickCreate(String string, ItemStack itemStack) {
    return itemStack == null ? null : new CMIComponentItem(string, itemStack);
  }

  public CMIComponentBase() {}

  public CMIComponentBase(boolean flag) {
    this.miniMessage = flag;
  }

  public CMIComponentBase add(CMIComponentRoot cmiComponentRoot) {
    this.add(0, cmiComponentRoot);
    return this;
  }

  public CMIComponentBase add(int value, CMIComponentRoot cmiComponentRoot) {
    this.componentMap.computeIfAbsent(value, integer -> new ArrayList<>()).add(cmiComponentRoot);
    return this;
  }

  public Map<Integer, List<CMIComponentRoot>> getComponents() {
    return this.componentMap;
  }

  public boolean allowMiniMessage() {
    return this.miniMessage;
  }

  public CMIComponentBase setAllowMiniMessage(boolean flag) {
    this.miniMessage = flag;
    return this;
  }

  public void showTitle(CommandSender commandSender) {
    this.showTitle(commandSender, 500, 3500, 1000);
  }

  public void showTitle(CommandSender commandSender, int value, int stayTicks, int fadeOutTicks) {}

  public void sendPlayerListHeaderAndFooter(CommandSender commandSender) {}

  public void showBossBar(CommandSender commandSender) {}

  public void sendActionBar(CommandSender commandSender) {
    this.sendActionBar(commandSender, 0);
  }

  public void sendActionBar(CommandSender commandSender, int value) {
    if (commandSender instanceof Player) {
      Player player = (Player) commandSender;
      CMIComponentBase.ActionBarTask actionBarTask =
          runningActionBarTasks.remove(player.getUniqueId());
      if (actionBarTask != null) {
        actionBarTask.getTask().cancel();
      }

      CMIComponentBase.ActionBarTask currentActionBarTask =
          new CMIComponentBase.ActionBarTask(System.currentTimeMillis() + value);
      CMITask cmiTask =
          CMIScheduler.runTimerAsync(
              CMI.getInstance(),
              () -> {
                if (currentActionBarTask.getTime() < System.currentTimeMillis()) {
                  runningActionBarTasks.remove(player.getUniqueId());
                  currentActionBarTask.getTask().cancel();
                } else if (player.isOnline()) {
                  this.sendActionBar(commandSender);
                }
              },
              30L,
              30L);
      currentActionBarTask.setTask(cmiTask);
      runningActionBarTasks.put(player.getUniqueId(), currentActionBarTask);
    }
  }

  public String getTextOnly() {
    StringBuilder stringBuilder = new StringBuilder();

    for (var list : this.getComponents().values()) {
      for (CMIComponentRoot cmiComponentRoot : list) {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append("\n");
        }

        stringBuilder.append(cmiComponentRoot.getText());
      }
    }

    return stringBuilder.toString();
  }

  public void sendMessage(CommandSender commandSender) {}

  @Nonnull
  public ItemStack convertIntoBook(String string, String bookText) {
    return CMIMaterial.AIR.newItemStack();
  }

  public CompletableFuture<Boolean> applyOnSign(Block block) {
    return this.applyOnSign(block, null);
  }

  public CompletableFuture<Boolean> applyOnSign(Block block, @Nullable Player player) {
    return CompletableFuture.completedFuture(false);
  }

  class ActionBarTask {
    private CMITask task;
    private long time;

    public ActionBarTask(long longValue) {
      this.time = longValue;
    }

    public ActionBarTask(CMITask cmiTask, long longValue) {
      this.task = cmiTask;
      this.time = longValue;
    }

    public CMITask getTask() {
      return this.task;
    }

    public long getTime() {
      return this.time;
    }

    public void setTask(CMITask cmiTask) {
      this.task = cmiTask;
    }
  }
}
