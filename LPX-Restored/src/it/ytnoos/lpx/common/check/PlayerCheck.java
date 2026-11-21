package it.ytnoos.lpx.common.check;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemBees.BeeEntry;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.check.enums.CheckType;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.check.options.PersonalCheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class PlayerCheck<T> implements Check<T> {
   protected final ProtocolPlayer player;
   protected final PersonalCheckOptions options;
   private static final long z = it.ytnoos.lpx.ClassKeyDerivation.a(-3174215763872042634L, 8482857004403749137L, java.lang.invoke.MethodHandles.lookup().lookupClass()).a(213391283956661L);
   private static final String PATH_SEPARATOR = "options.";

   protected PlayerCheck(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      this.player = protocolPlayer;
      this.options = new PersonalCheckOptions(checkOptions);
      this.loadOptions();
   }

   private void flag(Object object, FlagInfo[] flagInfos) {
      this.player.flag(this, object, flagInfos);
   }

   protected boolean raiseViolation(Object object, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      this.options.increaseVl();
      this.options.setBuffer(this.options.getBuffer() * this.options.getCheckOptions().getBufferMultiplier());
      this.flag(object, flagInfos2);
      if (this.options.getVl() >= this.options.getCheckOptions().getMaxVL()) {
         if (this.options.getCheckOptions().isPunish()) {
            this.player.punish(this);
         }

         this.options.setVl(0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void reset() {
      this.options.reset();
   }

   protected boolean incrementBuffer(Object object, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      return this.addBuffer(object, 1.0, flagInfos2);
   }

   protected boolean addBuffer(Object object, double number, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      this.options.setBuffer(Math.min(this.options.getCheckOptions().getMaxBuffer(), this.options.getBuffer() + number));
      if (this.options.getBuffer() >= this.options.getCheckOptions().getMaxBuffer()) {
         this.raiseViolation(object, flagInfos2);
         return true;
      } else {
         return false;
      }
   }

   protected void decayBuffer() {
      if (!(this.options.getBuffer() <= 0.0)) {
         this.options.setBuffer(Math.max(0.0, this.options.getBuffer() - this.options.getCheckOptions().getBufferDecay()));
      }
   }

   protected abstract CheckResult check(ProtocolPlayer protocolPlayer, T value);

   @Override
   public void loadOptions() {
   }

   @Override
   public CheckResult handle(T value) {
      return this.options.getCheckOptions().isEnabled() && !this.player.ignoreChecks()
         ? this.check(this.player, value)
         : CheckResult.ALLOW;
   }

   @Override
   public PersonalCheckOptions getOptions() {
      return this.options;
   }

   @Override
   public CheckType getType() {
      return this.options.getCheckOptions().getType();
   }

   protected String getPath(String string) {
      return this.options.getCheckOptions().getPath() + PATH_SEPARATOR + string;
   }

   protected FileConfiguration getConfig() {
      return this.options.getCheckOptions().getConfig();
   }

   protected boolean getBoolean(String string, boolean flag2) {
      return this.getConfig().getBoolean(this.getPath(string), flag2);
   }

   protected int getInt(String string, int value) {
      return this.getConfig().getInt(this.getPath(string), value);
   }

   protected long getLong(String string, long longValue) {
      return this.getConfig().getLong(this.getPath(string), longValue);
   }

   protected double getDouble(String string, double number) {
      return this.getConfig().getDouble(this.getPath(string), number);
   }

   protected List getStringList(String string, String[] strings) {
      String string2 = this.getPath(string);
      return this.getConfig().isSet(string2) ? this.getConfig().getStringList(string2) : new ArrayList<>(Arrays.asList(strings));
   }

   protected void handleNBT(long longValue, ItemStack itemStack, Consumer consumer) {
      long longValue2 = longValue;
      longValue2 = z ^ longValue2;
      if (itemStack != null) {
         NBTCompound nbtCompound = itemStack.getNBT();
         if (longValue2 >= 1L) {
         }

         if (nbtCompound != null) {
            consumer.accept(nbtCompound);
         }

         itemStack.getComponent(ComponentTypes.CUSTOM_DATA).ifPresent(consumer);
         itemStack.getComponent(ComponentTypes.ENTITY_DATA).ifPresent(consumer);
         itemStack.getComponent(ComponentTypes.BUCKET_ENTITY_DATA).ifPresent(consumer);
         itemStack.getComponent(ComponentTypes.BLOCK_ENTITY_DATA).ifPresent(consumer);
         itemStack.getComponent(ComponentTypes.BEES).ifPresent(itemBees -> {
            for (BeeEntry beeEntry : itemBees.getBees()) {
               consumer.accept(beeEntry.getEntityData());
            }
         });
         itemStack.getComponent(ComponentTypes.DEBUG_STICK_STATE).ifPresent(debugStickState -> {});
      }
   }
}
