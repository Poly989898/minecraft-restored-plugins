package io.netty.channel;

// Compile-only stub generated from the original CMI bytecode. Not packaged. Not verified against the real plugin.
public interface ChannelPipeline {
    public io.netty.channel.ChannelHandler get(java.lang.String p0);
    public io.netty.channel.ChannelPipeline addBefore(java.lang.String p0, java.lang.String p1, io.netty.channel.ChannelHandler p2);
    public io.netty.channel.ChannelPipeline replace(io.netty.channel.ChannelHandler p0, java.lang.String p1, io.netty.channel.ChannelHandler p2);
    public io.netty.channel.ChannelHandler remove(java.lang.String p0);
}
