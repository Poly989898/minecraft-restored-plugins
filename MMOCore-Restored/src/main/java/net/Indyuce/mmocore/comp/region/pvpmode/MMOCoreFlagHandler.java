package net.Indyuce.mmocore.comp.region.pvpmode;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MMOCoreFlagHandler extends FlagValueChangeHandler<State> {
   @NotNull
   protected PlayerData playerData;
   protected long lastMessage;
   protected static final long MESSAGE_TIMEOUT = 3000L;

   public MMOCoreFlagHandler(Session session, Flag<State> flag) {
      super(session, flag);
   }

   protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, State value) {
      try {
         this.playerData = PlayerData.get(player.getUniqueId());
         this.onSetValue(player, player.getLocation(), player.getLocation(), set, value, State.DENY, MoveType.TELEPORT);
      } catch (Exception exception) {
      }
   }

   public abstract State getDefaultState();

   protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, State lastValue, MoveType moveType) {
      return this.onSetValue(player, from, to, toSet, this.getDefaultState(), lastValue, moveType);
   }

   protected boolean isInvalid() {
      return this.playerData == null;
   }

   protected boolean toBoolean(@Nullable State state) {
      return state == State.ALLOW;
   }

   protected boolean canSendMessage() {
      return System.currentTimeMillis() > this.lastMessage + 3000L;
   }
}
