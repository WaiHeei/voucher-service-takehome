package com.prizm.campaign;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Test
    void redeemActiveVoucherSucceeds() {
        RedeemResponse res = voucherService.redeem("RAYA-0001", "user-1");
        assertEquals("OK", res.getResult());
    }

    @Test
    void redeemAlreadyRedeemedVoucherFails() {
        RedeemResponse res = voucherService.redeem("RAYA-0004", "user-2");
        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemUnknownCodeFails() {
        RedeemResponse res = voucherService.redeem("NOPE-9999", "user-3");
        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemFailsWhenUserHasReachedCampaignLimit(){
        RedeemResponse res1 = voucherService.redeem("RAYA-0002", "user-4");
        RedeemResponse res2 = voucherService.redeem("RAYA-0003", "user-4");
        RedeemResponse res3 = voucherService.redeem("RAYA-0006", "user-4");

        assertEquals("OK", res1.getResult());
        assertEquals("OK", res2.getResult());
        assertEquals("FAILED", res3.getResult());
    }

    @Test
    void redeemSucceedWhenUserHasNotReachedCampaignLimit(){
        RedeemResponse res = voucherService.redeem("RAYA-0006", "user-5");
        assertEquals("OK", res.getResult());
    }

    @Test
    void redeemSucceedWhenUserRedeemForOtherCampaign(){
        RedeemResponse res = voucherService.redeem("MRDK-0001", "user-4");
        assertEquals("OK", res.getResult());
    }

}
