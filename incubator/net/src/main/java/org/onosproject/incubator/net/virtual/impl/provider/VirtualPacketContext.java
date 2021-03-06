/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.incubator.net.virtual.impl.provider;

import org.onosproject.incubator.net.virtual.NetworkId;
import org.onosproject.net.packet.DefaultPacketContext;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.OutboundPacket;

/**
 * Represents context for processing an inbound packet for a virtual network
 * and (optionally) emitting a corresponding outbound packet.
 * The translation of context is handled by VirtualPacketProvider.
 */
public class VirtualPacketContext extends DefaultPacketContext {
    private DefaultVirtualPacketProvider dvpp;
    private NetworkId networkId;

    /**
     * Creates a new packet context.
     *
     * @param time   creation time
     * @param inPkt  inbound packet
     * @param outPkt outbound packet
     * @param block  whether the context is blocked or not
     * @param networkId virtual network ID where this context is handled
     * @param dvpp The pointer for DefaultVirtualPacketProvider
     */

    protected VirtualPacketContext(long time, InboundPacket inPkt,
                                   OutboundPacket outPkt, boolean block,
                                   NetworkId networkId,
                                   DefaultVirtualPacketProvider dvpp) {
        super(time, inPkt, outPkt, block);

        this.networkId = networkId;
        this.dvpp = dvpp;
    }

    @Override
    public void send() {
        if (!this.block()) {
            dvpp.devirtualizeContext(this);
        }
    }

    public NetworkId getNetworkId() {
        return networkId;
    }
}
