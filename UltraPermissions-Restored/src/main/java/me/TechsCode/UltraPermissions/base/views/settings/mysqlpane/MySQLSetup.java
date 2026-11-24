package me.TechsCode.UltraPermissions.base.views.settings.mysqlpane;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class MySQLSetup {
   private static final Phrase TITLE = Phrase.create("mysqlSetup.title", "MySQL Setup", Colors.AQUA);
   private static final Phrase SUB_TITLE = Phrase.create("mysqlSetup.subtitle", "Type in **%** into Chat", Colors.GRAY, Colors.YELLOW);
   private static final Phrase ACTION_BAR = Phrase.create("mysqlSetup.actionBar", "Default: **%**", Colors.GRAY, Colors.GREEN);
   private final Player p;
   private final SpigotTechPlugin plugin;
   private int stageNum;

   public MySQLSetup(Player player, SpigotTechPlugin spigotTechPlugin) {
      this.p = player;
      this.plugin = spigotTechPlugin;
      this.stageNum = -1;
      this.nextStage();
   }

   public void nextStage() {
      this.stageNum++;
      SetupStage[] stages = this.getStages();
      if (this.stageNum >= stages.length) {
         this.onCompletion();
      } else {
         final SetupStage setupStage = stages[this.stageNum];
         new UserInput(
            this.p, this.plugin, TITLE.get(), SUB_TITLE.get().replace("%", setupStage.getFieldName()), ACTION_BAR.get().replace("%", setupStage.getDefaultValue())
         ) {
            @Override
            public void onClose(Player closingPlayer) {
               MySQLSetup.this.onClose();
            }

            @Override
            public boolean onResult(String text) {
               setupStage.getSetFunction().set(text);
               MySQLSetup.this.nextStage();
               return true;
            }
         };
      }
   }

   public abstract void onClose();

   public abstract void onCompletion();

   public abstract SetupStage[] getStages();
}
