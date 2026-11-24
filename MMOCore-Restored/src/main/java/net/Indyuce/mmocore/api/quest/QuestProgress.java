package net.Indyuce.mmocore.api.quest;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.objective.Objective;

public class QuestProgress {
   private final Quest quest;
   private final PlayerData player;
   private int objective;
   private ObjectiveProgress objectiveProgress;

   public QuestProgress(Quest quest, PlayerData player) {
      this(quest, player, 0);
   }

   public QuestProgress(Quest quest, PlayerData player, int objective) {
      this.quest = quest;
      this.player = player;
      this.objective = objective;
      this.objectiveProgress = this.nextObjective().newProgress(this);
   }

   public Quest getQuest() {
      return this.quest;
   }

   public PlayerData getPlayer() {
      return this.player;
   }

   public int getObjectiveNumber() {
      return this.objective;
   }

   public ObjectiveProgress getProgress() {
      return this.objectiveProgress;
   }

   private Objective nextObjective() {
      return this.quest.getObjectives().get(this.objective);
   }

   public void completeObjective() {
      this.objective++;
      this.objectiveProgress.close();
      ObjectiveProgress finishedObjectiveProgress = this.objectiveProgress;
      if (this.objective >= this.quest.getObjectives().size()) {
         this.player.getQuestData().finishCurrent();
      } else {
         this.objectiveProgress = this.nextObjective().newProgress(this);
      }

      this.player.getQuestData().updateBossBar();
      finishedObjectiveProgress.getObjective().getTriggers().forEach(trigger -> trigger.schedule(this.getPlayer()));
   }

   public String getFormattedLore() {
      return MythicLib.plugin.parseColors(this.objectiveProgress.formatLore(this.objectiveProgress.getObjective().getDefaultLore()));
   }
}
