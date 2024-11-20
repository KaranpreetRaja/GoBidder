package com.gobidder.auctionservice.bidder;

import com.gobidder.auctionservice.TestUtils;
import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BidderIntegrationTest {
    private final MockMvc mockMvc;

    @Autowired
    public BidderIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void winForwardAuction() throws Exception {
        AuctionCreateRequestDto requestBody = new AuctionCreateRequestDto();
        requestBody.setName("Test Auction");
        requestBody.setDescription("Test description");
        requestBody.setType(AuctionTypeEnum.FORWARD);
        requestBody.setAuctionOwnerId(1L);
        requestBody.setInitialPrice(100.0);
        requestBody.setStartTime(LocalDateTime.now());
        requestBody.setDuration(10);

        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/auction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(requestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        Auction auction = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Auction.class
        );

        Assertions.assertEquals(AuctionStatusEnum.NOT_STARTED, auction.getStatus());
        Assertions.assertNotNull(auction.getId());
        Long id = auction.getId();

        result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/auction/" + id + "/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(requestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        auction = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Auction.class
        );

        Assertions.assertEquals(AuctionStatusEnum.ACTIVE, auction.getStatus());

        HighestBidderDto bidderRequestBody = new HighestBidderDto();
        bidderRequestBody.setUserId(123L);
        bidderRequestBody.setBid(100.0);

        result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/auction/" + id + "/bid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(bidderRequestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        Bidder bidder = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Bidder.class
        );

        Assertions.assertEquals(bidderRequestBody.getUserId(), bidder.getUserId());

        // Wait for auction to end
        Thread.sleep(11 * 1000);

        result = this.mockMvc.perform(
                MockMvcRequestBuilders.get("/auction/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(requestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        auction = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Auction.class
        );

        Assertions.assertEquals(AuctionStatusEnum.WON, auction.getStatus());
    }

    @Test
    public void winDutchAuction() throws Exception {
        AuctionCreateRequestDto requestBody = new AuctionCreateRequestDto();
        requestBody.setName("Test Auction");
        requestBody.setDescription("Test description");
        requestBody.setType(AuctionTypeEnum.DUTCH);
        requestBody.setAuctionOwnerId(1L);
        requestBody.setInitialPrice(100.0);
        requestBody.setStartTime(LocalDateTime.now());
        requestBody.setMinimumPrice(10.0);

        MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/auction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(requestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        Auction auction = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Auction.class
        );

        Assertions.assertEquals(AuctionStatusEnum.NOT_STARTED, auction.getStatus());
        Assertions.assertNotNull(auction.getId());
        Long id = auction.getId();

        result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/auction/" + id + "/start")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(requestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        auction = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Auction.class
        );

        Assertions.assertEquals(AuctionStatusEnum.ACTIVE, auction.getStatus());

        HighestBidderDto bidderRequestBody = new HighestBidderDto();
        bidderRequestBody.setUserId(123L);
        bidderRequestBody.setBid(100.0);

        result = this.mockMvc.perform(
                MockMvcRequestBuilders.post("/auction/" + id + "/bid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(bidderRequestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        Bidder bidder = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Bidder.class
        );

        Assertions.assertEquals(bidderRequestBody.getUserId(), bidder.getUserId());

        result = this.mockMvc.perform(
                MockMvcRequestBuilders.get("/auction/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.generateJson(requestBody)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        auction = TestUtils.parseJson(
            result.getResponse().getContentAsString(),
            Auction.class
        );

        Assertions.assertEquals(AuctionStatusEnum.WON, auction.getStatus());
    }
}
