package com.chain.chainscrape.controllers;

import com.chain.chainscrape.AppConfig;
import com.chain.chainscrape.model.EthData;
import com.chain.chainscrape.services.BlockDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Controller
public class EthDataViewController {
    String appTitle;
    private final BlockDataService blockDataService;

    public EthDataViewController(AppConfig appConfig,BlockDataService blockDataService) {
        this.blockDataService = blockDataService;
        this.appTitle = appConfig.getAppTitle();
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        EthData ethData = blockDataService.getLatestEthData();

        model.addAttribute("homePageTitle", appTitle);
        if(ethData!=null) {
            long timestamp = ethData.getBlock().getTimestamp().longValue();
            String dateReadable = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()).toString();
            model.addAttribute("blockNum", ethData.getBlock().getNumber());
            model.addAttribute("blockHash", ethData.getBlock().getHash());
            model.addAttribute("blockTime", timestamp + " (" + dateReadable + ")");
            model.addAttribute("blockMiner", ethData.getBlock().getMiner());
            model.addAttribute("blockTrans", ethData.getBlock().getTransactions().size());
            double gasFeeGwei = ethData.getBlock().getBaseFeePerGas().doubleValue() / Math.pow(10.0, 9.0);
            double roundedGasFeeGwei = Math.round(gasFeeGwei * 10.0) / 10.0;
            model.addAttribute("gasGwei", roundedGasFeeGwei);
            model.addAttribute("usdPrice", ethData.getAssetPrice().price());
        }

        return "homePage";
    }
}