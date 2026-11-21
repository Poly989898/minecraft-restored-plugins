package io.lumine.mythic.core.skills.mechanics;

import com.sk89q.worldedit.EditSession;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.worldedit.Regions;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.HashMap;

@MythicMechanic(
   author = "Seyarada",
   name = "undoPaste",
   aliases = {"undoSchematic", "undoSchem"},
   description = "Undo a paste"
)
public class UndoPasteMechanic extends Aura implements INoTargetSkill {
   static HashMap<String, EditSession> SESSIONS = new HashMap<>();
   @MythicField(
      name = "id",
      aliases = {"pasteID", "pid"},
      description = "The ID of the paste to undo"
   )
   final String pasteID;

   public UndoPasteMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.pasteID = mlc.getString(new String[]{"id", "pasteid", "pid"}, null);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (SESSIONS.containsKey(this.pasteID)) {
         Regions.undo(SESSIONS.remove(this.pasteID));
      }

      return SkillResult.SUCCESS;
   }

   public static void addUndoableSchematic(String pasteID, EditSession session) {
      SESSIONS.put(pasteID, session);
   }
}
