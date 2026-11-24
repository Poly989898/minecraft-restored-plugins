package com.comphenix.protocol.wrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import java.util.UUID;
public class PlayerInfoData {
    public PlayerInfoData(UUID profileId, int latency, boolean listed, NativeGameMode gameMode, WrappedGameProfile profile, WrappedChatComponent displayName, WrappedRemoteChatSessionData chatSession) {}
    public UUID getProfileId() { return null; }
    public int getLatency() { return 0; }
    public boolean isListed() { return false; }
    public NativeGameMode getGameMode() { return null; }
    public WrappedGameProfile getProfile() { return null; }
    public WrappedChatComponent getDisplayName() { return null; }
    public WrappedRemoteChatSessionData getRemoteChatSessionData() { return null; }
}
