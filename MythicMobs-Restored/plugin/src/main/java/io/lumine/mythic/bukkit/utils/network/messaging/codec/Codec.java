package io.lumine.mythic.bukkit.utils.network.messaging.codec;

public interface Codec<M> {
   byte[] encode(M m) throws EncodingException;

   M decode(byte[] bs) throws EncodingException;
}
