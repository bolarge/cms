package com.software.finatech.lslb.cms.service.util.data_updater;

import com.software.finatech.lslb.cms.service.domain.Agent;
import com.software.finatech.lslb.cms.service.domain.GameType;
import com.software.finatech.lslb.cms.service.domain.Institution;
import com.software.finatech.lslb.cms.service.service.EmailService;
import com.software.finatech.lslb.cms.service.service.MailContentBuilderService;
import com.software.finatech.lslb.cms.service.service.contracts.AgentService;
import com.software.finatech.lslb.cms.service.service.contracts.GameTypeService;
import com.software.finatech.lslb.cms.service.service.contracts.InstitutionService;
import com.software.finatech.lslb.cms.service.util.CSVUtils;
import com.software.finatech.lslb.cms.service.util.adapters.LslbGamingTerminalAdapter;
import com.software.finatech.lslb.cms.service.util.adapters.model.LslbGamingTerminal;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Component
public class ExistingGamingTerminalLoader {
    @Autowired
    private LslbGamingTerminalAdapter gamingTerminalAdapter;
    @Autowired
    private AgentService agentService;
    @Autowired
    private MailContentBuilderService mailContentBuilderService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private GameTypeService gameTypeService;
    private static final Logger logger = LoggerFactory.getLogger(ExistingGamingTerminalLoader.class);

    public void loadMachinesFromFile(String institutionId, String gameTypeId, MultipartFile multipartFile) {
        Institution institution = institutionService.findByInstitutionId(institutionId);
        if (institution == null) {
            return;
        }
        GameType gameType = gameTypeService.findById(gameTypeId);
        if (gameType == null) {
            return;
        }

        List<LslbGamingTerminal> toBeSavedTerminals = new ArrayList<>();
        List<LslbGamingTerminal> invalidTerminals = new ArrayList<>();
        try {
            byte[] bytes = multipartFile.getBytes();
            String completeData = new String(bytes);
            String[] rows = completeData.split("\\r?\\n");
            for (int i = 1; i < rows.length; i++) {
                String[] columns = rows[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (columns.length < 9) {
                    /// add to invalid terminals
                } else {
                    LslbGamingTerminal gamingTerminal = new LslbGamingTerminal();
                    String bvn = columns[7].trim();
                    Agent agent = agentService.findAgentByBvn(bvn);
                    gamingTerminal.setAgentTelephone(columns[2].trim());
                    gamingTerminal.setDeviceMagicMobile(columns[2].trim());
                    gamingTerminal.setBvn(bvn);
                    gamingTerminal.setLassra(columns[8].trim());
                    gamingTerminal.setAgentFullName(columns[1].trim());
                    gamingTerminal.setAgentAddress(String.format("%s %s", columns[4].trim(), columns[5].trim()));
                    //  gamingTerminal.setMachineCount(columns[6].trim());
                    gamingTerminal.setMachineId(columns[6].trim());
                    if (agent != null) {
                        gamingTerminal.setAgent(agent);
                        gamingTerminal.setInstitution(institution);
                        gamingTerminal.setGameType(gameType);
                        toBeSavedTerminals.add(gamingTerminal);
                    } else {
                        gamingTerminal.setFailReason(String.format("Agent with bvn %s is not not found", bvn));
                        invalidTerminals.add(gamingTerminal);
                    }
                }
            }
            for (LslbGamingTerminal lslbGamingTerminal : toBeSavedTerminals) {
                gamingTerminalAdapter.saveLslbGamingTerminalToDb(lslbGamingTerminal);
            }
            if (!invalidTerminals.isEmpty()) {
                sendMailOfFailureToPeople(invalidTerminals, institution, gameType);
            }

        } catch (Exception e) {
            logger.error("An error occurred while reading terminal", e);
            //   return logAndReturnError(logger, "An error occurred while parsing the file", e);
        }
    }

    private void sendMailOfFailureToPeople(List<LslbGamingTerminal> lslbGamingTerminals, Institution institution, GameType gameType) {
        if (lslbGamingTerminals.isEmpty()) {
            return;
        }
        FileWriter fileWriter = null;
        try {
            File file = new File("Failed-Machines.csv");
            fileWriter = new FileWriter(file);
            CSVUtils.writeLine(fileWriter, Arrays.asList("MachineId", "Agent Full name", "Agent BVN", "Agent Address", "Failure Reason"));
            for (LslbGamingTerminal gamingTerminal : lslbGamingTerminals) {
                CSVUtils.writeLine(fileWriter, Arrays.asList(gamingTerminal.getMachineId(), gamingTerminal.getAgentFullName(), gamingTerminal.getBvn(), gamingTerminal.getAgentAddress(), gamingTerminal.getFailReason()));
            }
            fileWriter.flush();
            fileWriter.close();
            Map<String, Object> model = new HashMap<>();
            model.put("date", LocalDate.now().toString());
            model.put("operatorName", institution.toString());
            model.put("gameType", String.valueOf(gameType));
            String mailContent = mailContentBuilderService.build(model, "failed-terminal/FailedMachineNotification");

            List<String> emails = Arrays.asList("adeboludeyi@gmail.com", "kunmi.akinlawon@finatechng.com");
            //, "david.jaiyeola@gmail.com");

            for (String email : emails) {
                emailService.sendEmailWithAttachment(mailContent, "Gaming Terminal Upload Notification", email, file);
            }
        } catch (Exception e) {
            logger.error("An error occurred while sending failed machine mail", e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                logger.error("An error occurred while closing file writer", e);
            }
        }
    }
}
