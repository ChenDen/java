package com.pubnub.api.endpoints;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.models.consumer.history.PNDeleteMessagesResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertNotNull;


public class DeleteMessagesEndpointTest extends TestHarness {

    private DeleteMessages partialHistory;
    private PubNub pubnub;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    @Before
    public void beforeEach() throws IOException {
        pubnub = this.createPubNubInstance(8080);
        partialHistory = pubnub.deleteMessages();
        wireMockRule.start();
    }


    @Test
    public void testSyncSuccess() throws PubNubException {
        stubFor(delete(urlPathEqualTo("/v3/history/sub-key/mySubscribeKey/channel/mychannel,my_channel"))
                .willReturn(aResponse().withBody("{\"status\": 200, \"error\": False, \"error_message\": \"\"}")));

        PNDeleteMessagesResult response = partialHistory.channels(Arrays.asList("mychannel,my_channel")).sync();

        assertNotNull(response);
    }

    @Test
    public void testSyncAuthSuccess() throws PubNubException {
        stubFor(delete(urlPathEqualTo("/v3/history/sub-key/mySubscribeKey/channel/mychannel,my_channel"))
                .willReturn(aResponse().withBody("{\"status\": 200, \"error\": False, \"error_message\": \"\"}")));

        pubnub.getConfiguration().setAuthKey("authKey");

        PNDeleteMessagesResult response = partialHistory.channels(Arrays.asList("mychannel,my_channel")).sync();

        assertNotNull(response);
    }

    @Test
    public void testFailure() throws PubNubException {
        stubFor(delete(urlPathEqualTo("/v3/history/sub-key/mySubscribeKey/channel/mychannel,my_channel"))
                .willReturn(aResponse().withBody("{\"status\": 403, \"error\": False, \"error_message\": \"wut\"}")));

        pubnub.getConfiguration().setAuthKey("authKey");

        try {
            partialHistory.channels(Arrays.asList("mychannel,my_channel")).sync();
        } catch (PubNubException ex) {
            assert(ex.getErrormsg().equals("wut"));
        }
    }

}
