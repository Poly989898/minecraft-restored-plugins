package io.lumine.mythic.bukkit.utils.network.messaging;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.Set;
import javax.annotation.Nonnull;

public interface ChannelAgent<T> extends Terminable {
   @Nonnull
   Channel<T> getChannel();

   @Nonnull
   Set<ChannelListener<T>> getListeners();

   boolean hasListeners();

   boolean addListener(@Nonnull ChannelListener<T> channelListener);

   boolean removeListener(@Nonnull ChannelListener<T> channelListener);

   @Override
   void close();
}
