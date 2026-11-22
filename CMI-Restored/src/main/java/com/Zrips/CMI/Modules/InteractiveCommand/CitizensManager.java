package com.Zrips.CMI.Modules.InteractiveCommand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import java.util.UUID;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.entity.Entity;

public class CitizensManager {
  private CMI plugin;
  NPCRegistry registry;

  public CitizensManager(CMI cmi) {
    this.plugin = cmi;
    if (CMIHookType.Citizens.isEnabled()) {
      this.registry = CitizensAPI.getNPCRegistry();
    }
  }

  public boolean isCitizen(Entity entity) {
    return this.registry == null ? false : this.registry.isNPC(entity);
  }

  public UUID getCitizenPrivateUUID(UUID uuid) {
    if (this.registry == null) {
      return null;
    }

    NPC value = this.registry.getByUniqueId(uuid);
    return value == null ? null : value.getUniqueId();
  }

  public Integer getEntityId(Entity entity) {
    NPC value = CitizensAPI.getNPCRegistry().getNPC(entity);
    return value != null ? value.getId() : null;
  }

  public Integer getEntityId(UUID uuid) {
    NPC value = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
    if (value != null) {
      return value.getId();
    }

    value = CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(uuid);
    if (value != null) {
      return value.getId();
    }

    try {
      String string = uuid.toString();
      String[] strings = string.split("-", 3);
      string =
          strings[0]
              + "-"
              + strings[1]
              + "-"
              + (strings[2].startsWith("2") ? "4" : "2")
              + strings[2].substring(1);
      UUID npcUuid = UUID.fromString(string);
      value = CitizensAPI.getNPCRegistry().getByUniqueId(npcUuid);
      if (value != null) {
        return value.getId();
      }
    } catch (Throwable throwable) {
    }

    return null;
  }

  public UUID getEntityUUID(Entity entity) {
    NPC value = CitizensAPI.getNPCRegistry().getNPC(entity);
    return value != null ? value.getUniqueId() : null;
  }

  public UUID getEntityUUID(int value) {
    NPC npc = CitizensAPI.getNPCRegistry().getById(value);
    return npc != null && npc.getEntity() != null ? npc.getUniqueId() : null;
  }

  public UUID getEntityRealUUID(int value) {
    NPC npc = CitizensAPI.getNPCRegistry().getById(value);
    return npc != null && npc.getEntity() != null ? npc.getEntity().getUniqueId() : null;
  }
}
