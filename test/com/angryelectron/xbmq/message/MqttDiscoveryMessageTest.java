/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.angryelectron.xbmq.message;

import com.angryelectron.xbmq.message.MqttDiscoveryMessage;
import com.angryelectron.xbmq.Xbmq;
import com.angryelectron.xbmq.XbmqTopic;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.models.XBee64BitAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author abythell
 */
public class MqttDiscoveryMessageTest {

    private Xbmq xbmq;
    private final XbmqTopic topics = new XbmqTopic("rootTopic", "ABCDABCDABCDABCD");
    private final XBee64BitAddress device = new XBee64BitAddress("1234567812345678");

    public MqttDiscoveryMessageTest() {
    }

    @Before
    public void setUp() {
        xbmq = mock(Xbmq.class);
        when(xbmq.getTopics()).thenReturn(topics);
    }

    @Test
    public void testSend() throws Exception {        
        RemoteXBeeDevice rxb = mock(RemoteXBeeDevice.class);
        when(rxb.get64BitAddress()).thenReturn(device);        
                
        List<RemoteXBeeDevice> devices = new ArrayList<>();
        devices.add(rxb);
        devices.add(rxb);
        devices.add(rxb);
        
        StringBuilder builder = new StringBuilder();
        builder.append("{\"devices\": [");
        builder.append("\"1234567812345678\",");
        builder.append("\"1234567812345678\",");
        builder.append("\"1234567812345678\"]");
        builder.append("}");
        String expectedMessage = builder.toString();
        
        MqttDiscoveryMessage message = new MqttDiscoveryMessage(xbmq);
        message.send(devices, MqttDiscoveryMessage.Format.JSON);

        ArgumentCaptor<MqttMessage> argument = ArgumentCaptor.forClass(MqttMessage.class);
        verify(xbmq).publishMqtt(
                eq(topics.pubDiscovery()),
                argument.capture());
        assertTrue("invalid message payload", Arrays.equals(
                expectedMessage.getBytes(), 
                argument.getValue().getPayload()));
    }

}
