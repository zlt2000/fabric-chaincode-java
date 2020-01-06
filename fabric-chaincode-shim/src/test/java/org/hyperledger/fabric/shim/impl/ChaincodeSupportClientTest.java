/*
 * Copyright 2020 IBM All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.fabric.shim.impl;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.hyperledger.fabric.metrics.Metrics;
import org.hyperledger.fabric.protos.peer.Chaincode;
import org.hyperledger.fabric.protos.peer.ChaincodeShim;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.chaincode.EmptyChaincode;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

class ChaincodeSupportClientTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    void startInnvocationTaskManager() throws IOException {
        environmentVariables.set("PORT_CHAINCODE_SERVER", "9999");
        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        final ChaincodeBase chaincodeBase = new EmptyChaincode();
        chaincodeBase.processEnvironmentOptions();
        chaincodeBase.validateOptions();

        Properties props = chaincodeBase.getChaincodeConfig();
        Metrics.initialize(props);

        final ManagedChannelBuilder<?> managedChannelBuilder = chaincodeBase.newChannelBuilder();
        ChaincodeSupportClient chaincodeSupportClient = new ChaincodeSupportClient(managedChannelBuilder);

        final Chaincode.ChaincodeID chaincodeId = Chaincode.ChaincodeID.newBuilder().setName("chaincodeIdNumber12345").build();
        final InnvocationTaskManager itm = InnvocationTaskManager.getManager(chaincodeBase, chaincodeId);

        chaincodeSupportClient.start(itm);
    }

    @Test
    void testStartInnvocationTaskManagerAndRequestObserverNull() throws IOException {
        environmentVariables.set("PORT_CHAINCODE_SERVER", "9999");
        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        final ChaincodeBase chaincodeBase = new EmptyChaincode();
        chaincodeBase.processEnvironmentOptions();
        chaincodeBase.validateOptions();

        Properties props = chaincodeBase.getChaincodeConfig();
        Metrics.initialize(props);

        final ManagedChannelBuilder<?> managedChannelBuilder = chaincodeBase.newChannelBuilder();
        ChaincodeSupportClient chaincodeSupportClient = new ChaincodeSupportClient(managedChannelBuilder);

        Assertions.assertThrows(
                IOException.class,
                () -> {
                    final Chaincode.ChaincodeID chaincodeId = Chaincode.ChaincodeID.newBuilder().setName("chaincodeIdNumber12345").build();
                    final InnvocationTaskManager itm = InnvocationTaskManager.getManager(chaincodeBase, chaincodeId);

                    final StreamObserver<ChaincodeShim.ChaincodeMessage> requestObserver = null;
                    chaincodeSupportClient.start(itm, requestObserver);
                },
                "StreamObserver 'requestObserver' for chat with peer can't be null"
        );
    }

    @Test
    void testStartInnvocationTaskManagerNullAndRequestObserver() throws IOException {
        environmentVariables.set("PORT_CHAINCODE_SERVER", "9999");
        environmentVariables.set("CORE_CHAINCODE_ID_NAME", "mycc");
        final ChaincodeBase chaincodeBase = new EmptyChaincode();
        chaincodeBase.processEnvironmentOptions();
        chaincodeBase.validateOptions();

        Properties props = chaincodeBase.getChaincodeConfig();
        Metrics.initialize(props);

        final ManagedChannelBuilder<?> managedChannelBuilder = chaincodeBase.newChannelBuilder();
        ChaincodeSupportClient chaincodeSupportClient = new ChaincodeSupportClient(managedChannelBuilder);

        Assertions.assertThrows(
                IOException.class,
                () -> {
                    chaincodeSupportClient.start(null, new StreamObserver<ChaincodeShim.ChaincodeMessage>() {
                        @Override
                        public void onNext(final ChaincodeShim.ChaincodeMessage value) {

                        }

                        @Override
                        public void onError(final Throwable t) {

                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
                },
                "InnvocationTaskManager 'itm' can't be null"
        );
    }
}
