package io.lumine.mythic.bukkit.utils.network.messaging;

import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.interfaces.TypeAware;
import io.lumine.mythic.bukkit.utils.network.messaging.codec.Codec;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import javax.annotation.Nonnull;

public interface Channel<T> extends TypeAware<T> {
   @Nonnull
   String getName();

   @Nonnull
   @Override
   TypeToken<T> getType();

   @Nonnull
   Codec<T> getCodec();

   @Nonnull
   ChannelAgent<T> newAgent();

   @Nonnull
   default ChannelAgent<T> newAgent(ChannelListener<T> listener) {
      ChannelAgent<T> agent = this.newAgent();
      agent.addListener(listener);
      return agent;
   }

   @Nonnull
   Promise<Void> sendMessage(@Nonnull T t);

   @Nonnull
   Promise<Void> sendMessage(@Nonnull String str, @Nonnull T t);
}
