package com.Zrips.CMI.Modules.InteractiveCommand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class CMIInteractiveCommand {
  private String name;
  private boolean publicIc = false;
  private boolean signRequiresPerm = true;
  private boolean cancelInteraction = false;
  private Set<CMILocation> loc = new HashSet<>();
  private HashMap<UUID, CMINPC> uuid = new HashMap<>();
  private Set<CMINPC> citizensCache = new HashSet<>();
  private List<String> commands = new ArrayList<>();
  private List<String> signLines = new ArrayList<>();

  public CMIInteractiveCommand(String string) {
    this.name = string;
    this.signLines.add(" ");
    this.signLines.add(" ");
    this.signLines.add(" ");
    this.signLines.add(" ");
  }

  public List<String> getCommands(Player player) {
    Snd snd = new Snd();
    snd.setSender(player);
    snd.setTarget(player);
    return CMI.getInstance().getLM().updateSnd(snd, new ArrayList<>(this.commands));
  }

  public List<String> getCommandsOriginal() {
    return this.commands;
  }

  public List<String> getCommands() {
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.commands) {
      arrayList.add(CMIChatColor.deColorize(string));
    }

    return arrayList;
  }

  public void setCommands(List<String> list) {
    this.commands = new ArrayList<>(list);
  }

  public void addCommand(String string) {
    this.commands.add(string);
  }

  public Set<CMILocation> getLocList() {
    return this.loc;
  }

  public List<String> getLocListAsString() {
    ArrayList<String> arrayList = new ArrayList();

    for (CMILocation cmiLocation : this.loc) {
      arrayList.add(CMI.getInstance().getPlayerManager().convertLocToStringShortBlock(cmiLocation));
    }

    return arrayList;
  }

  public void setLoc(List<CMILocation> list) {
    this.loc.clear();
    this.loc.addAll(list);
  }

  @Deprecated
  public void addLoc(CMILocation cmiLocation) {
    if (cmiLocation != null) {
      this.loc.add(cmiLocation);
    }
  }

  public HashMap<UUID, CMINPC> getUUIDMap() {
    return this.uuid;
  }

  public Set<UUID> getUUIDList() {
    HashSet<UUID> hashSet = new HashSet();

    for (var entry : this.uuid.entrySet()) {
      hashSet.add((UUID) entry.getKey());
    }

    return hashSet;
  }

  @Deprecated
  public List<Integer> getIDList() {
    ArrayList<Integer> arrayList = new ArrayList();

    for (var entry : this.uuid.entrySet()) {
      if (((CMINPC) entry.getValue()).getId() != null) {
        arrayList.add(((CMINPC) entry.getValue()).getId());
      }
    }

    return arrayList;
  }

  public List<CMINPC> getNPCList() {
    ArrayList<CMINPC> arrayList = new ArrayList();

    for (var entry : this.uuid.entrySet()) {
      if (((CMINPC) entry.getValue()).getId() != null && ((CMINPC) entry.getValue()).getId() >= 0) {
        arrayList.add((CMINPC) entry.getValue());
      }
    }

    for (CMINPC cminpc : this.citizensCache) {
      if (cminpc.getId() != null && cminpc.getId() >= 0) {
        arrayList.add(cminpc);
      }
    }

    return arrayList;
  }

  public List<String> getUUIDStringList() {
    ArrayList<String> arrayList = new ArrayList();

    for (UUID entryUuid : this.getUUIDList()) {
      arrayList.add(entryUuid.toString());
    }

    return arrayList;
  }

  public List<String> getUUIDStringListSave() {
    ArrayList<String> arrayList = new ArrayList();

    for (var entry : this.uuid.entrySet()) {
      if (((CMINPC) entry.getValue()).getId() == null || ((CMINPC) entry.getValue()).getId() < 0) {
        arrayList.add(((UUID) entry.getKey()).toString());
      }
    }

    return arrayList;
  }

  public void setUUIDList(HashMap<UUID, CMINPC> hashMap) {
    this.uuid.clear();
    this.uuid.putAll(hashMap);
  }

  public boolean hasUUID(UUID targetUuid) {
    return this.uuid.containsKey(targetUuid);
  }

  public void addUUID(UUID targetUuid, Integer integer) {
    this.addUUID(targetUuid, null, integer);
  }

  public void addUUID(UUID targetUuid, UUID secondaryUuid, Integer integer) {
    this.uuid.putIfAbsent(
        targetUuid, new CMINPC(targetUuid).setId(integer).setSecondaryUUID(secondaryUuid));
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public boolean isPublic() {
    return this.publicIc;
  }

  public void setPublic(boolean flag) {
    this.publicIc = flag;
  }

  public boolean containsSigntext() {
    for (String string : this.getSignLines()) {
      if (string != null && !string.isEmpty() && !string.equalsIgnoreCase(" ")) {
        return true;
      }
    }

    return false;
  }

  public List<String> getSignLines() {
    while (this.signLines.size() < 4) {
      this.signLines.add(" ");
    }

    while (this.signLines.size() > 4) {
      this.signLines.remove(this.signLines.size() - 1);
    }

    return this.signLines;
  }

  public boolean isSignLinesEmpty() {
    for (String string : this.signLines) {
      if (string != null && string.length() > 0 && !string.equalsIgnoreCase(" ")) {
        return false;
      }
    }

    return true;
  }

  public void updateSignText(Block block) {
    CMIScheduler.runTaskLater(
        CMI.getInstance(),
        () -> {
          if (!this.isSignLinesEmpty()) {
            if (block.getState() instanceof Sign) {
              Sign sign = (Sign) block.getState();

              for (int value = 0; value < 4; value++) {
                if (this.getSignLines().get(value) != null
                    && !this.getSignLines().get(value).isEmpty()
                    && !this.getSignLines().get(value).equals(" ")) {
                  sign.setLine(value, CMIChatColor.translate(this.getSignLines().get(value)));
                }
              }

              sign.update();
            }
          }
        },
        1L);
  }

  public void setSignLines(List<String> list) {
    this.signLines = list;
  }

  public Set<CMINPC> getCitizensCache() {
    return this.citizensCache;
  }

  public void setCitizensCache(Set<CMINPC> set) {
    this.citizensCache = set;
  }

  public void addCitizensCache(CMINPC cminpc) {
    this.citizensCache.add(cminpc);
  }

  public CMIInteractiveCommand removeEntity(UUID targetUuid) {
    if (targetUuid != null) {
      CMI.getInstance().getInteractiveCommandManager().removeEntity(targetUuid);
    }

    CMINPC cminpc = this.uuid.remove(targetUuid);
    if (cminpc != null) {
      this.citizensCache.remove(cminpc);
    }

    return this;
  }

  public CMIInteractiveCommand removeLoc(CMILocation cmiLocation) {
    if (cmiLocation != null) {
      CMI.getInstance().getInteractiveCommandManager().removeLoc(cmiLocation);
    }

    return this;
  }

  public boolean isSignRequiresPerm() {
    return this.signRequiresPerm;
  }

  public void setSignRequiresPerm(boolean flag) {
    this.signRequiresPerm = flag;
  }

  public boolean isCancelInteraction() {
    return this.cancelInteraction;
  }

  public void setCancelInteraction(boolean flag) {
    this.cancelInteraction = flag;
  }
}
