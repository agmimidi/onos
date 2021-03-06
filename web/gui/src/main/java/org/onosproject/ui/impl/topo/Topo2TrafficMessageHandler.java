/*
 * Copyright 2017-present Open Networking Laboratory
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
 *
 */

package org.onosproject.ui.impl.topo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.onlab.osgi.ServiceDirectory;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiConnection;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.impl.TrafficMonitorBase.Mode;
import org.onosproject.ui.impl.topo.util.ServicesBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Server-side component to handle messages pertaining to topo-2 traffic.
 */
public class Topo2TrafficMessageHandler extends UiMessageHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // === Inbound event identifiers
    private static final String REQUEST_ALL_TRAFFIC = "topo2RequestAllTraffic";
    private static final String CANCEL_TRAFFIC = "topo2CancelTraffic";

    // === Outbound event identifiers
    private static final String HIGHLIGHTS = "topo2Highlights";

    // field values
    private static final String TRAFFIC_TYPE = "trafficType";
    private static final String FLOW_STATS_BYTES = "flowStatsBytes";
    private static final String PORT_STATS_BIT_SEC = "portStatsBitSec";
    private static final String PORT_STATS_PKT_SEC = "portStatsPktSec";

    // configuration parameters
    private static final long TRAFFIC_PERIOD = 5000;

//    private UiTopoSession topoSession;
//    private Topo2Jsonifier t2json;

    protected ServicesBundle services;
    private String version;


    private Traffic2Monitor traffic;


    @Override
    public void init(UiConnection connection, ServiceDirectory directory) {
        super.init(connection, directory);

        services = new ServicesBundle(directory);

        traffic = new Traffic2Monitor(TRAFFIC_PERIOD, services, this);

        // get the topo session from the UiWebSocket
//        topoSession = ((UiWebSocket) connection).topoSession();
//        t2json = new Topo2Jsonifier(directory, connection.userName());

    }

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new Topo2AllTraffic(),
                new Topo2CancelTraffic()
        );
    }

    // ==================================================================

    private final class Topo2AllTraffic extends RequestHandler {

        private Topo2AllTraffic() {
            super(REQUEST_ALL_TRAFFIC);
        }

        @Override
        public void process(ObjectNode payload) {
            String mode = string(payload, TRAFFIC_TYPE);
            log.debug("SHOW TRAFFIC: {}", mode);

            switch (mode) {
                case FLOW_STATS_BYTES:
                    traffic.monitor(Mode.ALL_FLOW_TRAFFIC_BYTES);
                    break;

                case PORT_STATS_BIT_SEC:
                    traffic.monitor(Mode.ALL_PORT_TRAFFIC_BIT_PS);
                    break;

                case PORT_STATS_PKT_SEC:
                    traffic.monitor(Mode.ALL_PORT_TRAFFIC_PKT_PS);
                    break;

                default:
                    log.warn("Unknown traffic monitor type: " + mode);
                    break;
            }
        }
    }

    private final class Topo2CancelTraffic extends RequestHandler {
        private Topo2CancelTraffic() {
            super(CANCEL_TRAFFIC);
        }

        @Override
        public void process(ObjectNode payload) {
            log.debug("CANCEL TRAFFIC");
            traffic.stopMonitoring();
        }
    }
}


