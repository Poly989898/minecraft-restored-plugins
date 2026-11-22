package net.Zrips.CMILib.Chat;

import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatMessageEdit {
  private UUID uuid = null;
  Long time = 0L;
  private boolean keep = false;
  private String cancelVariable = "cancel";
  private boolean checkForCancel = false;
  private String pasteInString = null;

  public ChatMessageEdit(CommandSender sender) {
    this(
        sender instanceof Player
            ? ((Player) sender).getUniqueId()
            : CMILib.getInstance().getServerUUID());
  }

  public ChatMessageEdit(CommandSender sender, String pasteInText) {
    this(
        sender instanceof Player
            ? ((Player) sender).getUniqueId()
            : CMILib.getInstance().getServerUUID(),
        pasteInText);
  }

  public ChatMessageEdit(UUID uuid) {
    this(uuid, null);
  }

  public ChatMessageEdit(UUID uuid, String pasteInText) {
    this.uuid = uuid;
    ChatEditorManager.add(this);
    this.time = System.currentTimeMillis();
    this.pasteInString = pasteInText;
  }

  public void printMessage() {
    this.printMessage(null);
  }

  public void printMessage(String customText) {
    if (this.pasteInString != null) {
      RawMessage rm = new RawMessage();
      rm.addText(
          customText == null
              ? LC.modify_commandEditInfo.getLocale()
              : CMIChatColor.translate(customText));
      rm.addHover(LC.info_Click.getLocale());
      rm.addSuggestion(this.pasteInString);
      rm.show(Bukkit.getPlayer(this.uuid));
    }
  }

  public void run(String message) {}

  public void onCancel() {}

  public void onDisable() {}

  public void delete() {
    ChatEditorManager.delete(this);
  }

  public UUID getUuid() {
    return this.uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public boolean isKeep() {
    return this.keep;
  }

  public void setKeep(boolean keep) {
    this.keep = keep;
  }

  public String getCancelVariable() {
    return this.cancelVariable;
  }

  public void setCancelVariable(String cancelVariable) {
    this.cancelVariable = cancelVariable;
  }

  public boolean isCheckForCancel() {
    return this.checkForCancel;
  }

  public void setCheckForCancel(boolean checkForCancel) {
    this.checkForCancel = checkForCancel;
  }

  public String getPasteInString() {
    return this.pasteInString;
  }
}
