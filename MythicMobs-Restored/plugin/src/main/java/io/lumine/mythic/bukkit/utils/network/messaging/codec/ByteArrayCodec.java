package io.lumine.mythic.bukkit.utils.network.messaging.codec;

public class ByteArrayCodec<M> implements Codec<M> {
   private final Codec<M> delegate;

   public ByteArrayCodec(Codec<M> delegate) {
      this.delegate = delegate;
   }

   @Override
   public byte[] encode(M message) throws EncodingException {
      return this.delegate.encode(message);
   }

   @Override
   public M decode(byte[] buf) throws EncodingException {
      return this.delegate.decode(buf);
   }
}
