package io.lumine.mythic.bukkit.utils.network.messaging;

@FunctionalInterface
public interface ChannelListener<T> {
   void onMessage(ChannelAgent channelAgent, String str, T t);
}
