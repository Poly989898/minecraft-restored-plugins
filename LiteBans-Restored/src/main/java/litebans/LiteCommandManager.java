package litebans;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public abstract class LiteCommandManager extends PluginModule {
   private final Map map3 = new HashMap();
   private Collection collection4;

   public LiteCommandManager(Platform platform) {
      super(platform);
   }

   @Override
   public void load() {
      this.collection4 = this.obtainCollection2();
      this.perform();
   }

   public Collection obtainCollection2() {
      return this.platform.getClassUtil().computeList(LiteCommand.class, new Class[]{Platform.class}, new Object[]{this.platform});
   }

   public void handleLiteCommand(LiteCommand liteCommand, LiteSender liteSender, String[] texts) {
      liteCommand.execute(liteSender, texts);
   }

   public void handleLiteSender(LiteSender liteSender, String text, String[] texts) {
      LiteCommand liteCommand = this.computeLiteCommand(text);
      if (liteCommand != null) {
         this.handleLiteCommand(liteCommand, liteSender, texts);
      }
   }

   public @Nullable LiteCommand computeLiteCommand(String text) {
      return (LiteCommand)this.map3.get(text);
   }

   public Collection getCollection4() {
      return this.collection4;
   }

   public List computeList(@Nullable LiteCommand liteCommand, String text, LiteSender liteSender, String[] texts) {
      this.platform.getLogger().warning("No suggest implementation for /" + text);
      return Collections.emptyList();
   }

   private final void handleLiteCommand2(LiteCommand liteCommand) {
      this.handleText(liteCommand.getName(), liteCommand);
   }

   public void handleText(String text, LiteCommand liteCommand) {
      this.map3.put(text, liteCommand);
   }

   void perform() {
      Collection collection = this.collection4;
      if (collection == null) {
         throw new IllegalStateException();
      }

      for (LiteCommand liteCommand : (Iterable<LiteCommand>) collection) {
         this.handleLiteCommand2(liteCommand);
      }
   }
}
