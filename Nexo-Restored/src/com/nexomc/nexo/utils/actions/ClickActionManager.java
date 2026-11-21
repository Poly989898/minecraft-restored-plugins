package com.nexomc.nexo.utils.actions;

import com.nexomc.libs.actions.actions.Action;
import com.nexomc.libs.actions.actions.ActionMeta;
import com.nexomc.libs.actions.placeholders.PlaceholderProvider;
import com.nexomc.libs.actions.spigot.actions.SpigotActionManager;
import com.nexomc.libs.actions.spigot.placeholders.PlaceholderAPIProvider;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.actions.command.ConsoleAction;
import com.nexomc.nexo.utils.actions.command.PlayerAction;
import com.nexomc.nexo.utils.actions.message.ActionBarAction;
import com.nexomc.nexo.utils.actions.message.MessageAction;
import com.nexomc.nexo.utils.actions.other.SoundAction;
import kotlin.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001\u0004B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0005"},
   d2 = {"Lcom/nexomc/nexo/utils/actions/ClickActionManager;", "Lcom/nexomc/libs/actions/spigot/actions/SpigotActionManager;", "<init>", "()V", "PlayerNamePlaceholderProvider", "com.nexomc:core"}
)
public final class ClickActionManager extends SpigotActionManager {
   public ClickActionManager() {
      super(NexoPlugin.Companion.instance());
      this.registerDefaults(Player.class);
      this.getComponentParser().registerDefaults(Player.class);
      if (PluginUtils.INSTANCE.isEnabled("PlaceholderAPI")) {
         this.getPlaceholderManager().register(new PlaceholderAPIProvider());
      }

      this.getPlaceholderManager().register(new PlayerNamePlaceholderProvider());
      this.register(Player.class, "console", ClickActionManager::_init_$lambda$0);
      this.register(Player.class, "player", ClickActionManager::_init_$lambda$1);
      this.register(Player.class, "actionbar", ClickActionManager::_init_$lambda$2);
      this.register(Player.class, "message", ClickActionManager::_init_$lambda$3);
      this.register(Player.class, "sound", ClickActionManager::_init_$lambda$4);
   }

   private static final Action _init_$lambda$0(ActionMeta meta) {
      return new ConsoleAction(meta);
   }

   private static final Action _init_$lambda$1(ActionMeta meta) {
      return new PlayerAction(meta);
   }

   private static final Action _init_$lambda$2(ActionMeta meta) {
      return new ActionBarAction(meta);
   }

   private static final Action _init_$lambda$3(ActionMeta meta) {
      return new MessageAction(meta);
   }

   private static final Action _init_$lambda$4(ActionMeta meta) {
      return new SoundAction(meta);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u000e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u0006H\u0016J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\bH\u0016¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/utils/actions/ClickActionManager$PlayerNamePlaceholderProvider;", "Lcom/nexomc/libs/actions/placeholders/PlaceholderProvider;", "Lorg/bukkit/entity/Player;", "<init>", "()V", "getType", "Ljava/lang/Class;", "replace", "", "player", "input", "com.nexomc:core"}
   )
   private static final class PlayerNamePlaceholderProvider implements PlaceholderProvider<Player> {
      public PlayerNamePlaceholderProvider() {
      }

      @NotNull
      public Class<Player> getType() {
         return Player.class;
      }

      @NotNull
      public String replace(@NotNull Player player, @NotNull String input) {
         String str = StringUtils.replace(input, "<player>", player.getName());
         return str;
      }
   }
}
