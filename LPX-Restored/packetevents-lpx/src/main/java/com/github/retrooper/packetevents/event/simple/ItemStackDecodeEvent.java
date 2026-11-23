/*
 * Reconstructed for LPX (fork-only class). Not part of upstream PacketEvents.
 *
 * Upstream PacketEvents is distributed under the GNU GPL v3 (see
 * packetevents-lpx/LICENSE-PacketEvents.txt); this file follows the same license.
 */
package com.github.retrooper.packetevents.event.simple;

import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.ProtocolPacketEvent;
import com.github.retrooper.packetevents.exception.PacketProcessException;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

/**
 * Fired after an {@link ItemStack} has been decoded from a packet.
 * The item's decoded size is recorded only if it has not been set yet.
 */
public class ItemStackDecodeEvent extends PacketEvent {

    private final PacketWrapper<?> wrapper;
    private final ProtocolPacketEvent event;
    private final int decodedSizeBytes;
    private ItemStack itemStack;

    public ItemStackDecodeEvent(PacketWrapper<?> wrapper, ProtocolPacketEvent event, ItemStack itemStack) throws PacketProcessException {
        this(wrapper, event, itemStack, itemStack.getDecodedSizeBytes());
    }

    public ItemStackDecodeEvent(PacketWrapper<?> wrapper, ProtocolPacketEvent event, ItemStack itemStack, int decodedSizeBytes) throws PacketProcessException {
        this.wrapper = wrapper;
        this.event = event;
        this.itemStack = itemStack;
        this.decodedSizeBytes = decodedSizeBytes;
        if (itemStack.getDecodedSizeBytes() < 0) {
            itemStack.setDecodedSizeBytes(decodedSizeBytes);
        }
    }

    public PacketWrapper<?> getWrapper() {
        return this.wrapper;
    }

    public ProtocolPacketEvent getEvent() {
        return this.event;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getDecodedSizeBytes() {
        return this.decodedSizeBytes;
    }

    public void setItemStack(ItemStack itemStack) {
        if (itemStack.getDecodedSizeBytes() < 0) {
            itemStack.setDecodedSizeBytes(this.decodedSizeBytes);
        }
        this.itemStack = itemStack;
    }
}
