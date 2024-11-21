package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.TestUtils;
import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
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
public class AuctionIntegrationTest {
    private final MockMvc mockMvc;

    @Autowired
    public AuctionIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void createAndStartForwardAuctionAndWaitTimeout() throws Exception {
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

        Assertions.assertEquals(AuctionStatusEnum.CANCELLED, auction.getStatus());
    }

    @Test
    public void createAndStartDutchAuctionAndWaitPriceDecrease() throws Exception {
        AuctionCreateRequestDto requestBody = new AuctionCreateRequestDto();
        requestBody.setName("Test Dutch Auction");
        requestBody.setDescription("Test description");
        requestBody.setType(AuctionTypeEnum.DUTCH);
        requestBody.setAuctionOwnerId(1L);
        requestBody.setInitialPrice(100.0);
        requestBody.setMinimumPrice(90.0);
        requestBody.setStartTime(LocalDateTime.now());

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

        Double oldPrice = auction.getCurrentPrice();

        // Wait for auction price to decrease
        Thread.sleep(5 * 1000);

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

        Double newPrice = auction.getCurrentPrice();

        Assertions.assertEquals(AuctionStatusEnum.ACTIVE, auction.getStatus());
        Assertions.assertTrue(newPrice < oldPrice);

        // Wait for auction price to decrease to the point of completion
        Thread.sleep(7 * 1000);

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

        Double newerPrice = auction.getCurrentPrice();

        Assertions.assertEquals(AuctionStatusEnum.CANCELLED, auction.getStatus());
        Assertions.assertTrue(newerPrice < newPrice);
    }
}
