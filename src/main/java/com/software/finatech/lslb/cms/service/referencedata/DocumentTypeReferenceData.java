package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.HashSet;
import java.util.Set;

public class DocumentTypeReferenceData {

    private static String applicationFormDocumentPurposeId = DocumentPurposeReferenceData.APPLICATION_FORM_DOCUMENT_PURPOSE_ID;
    private static String AIPDocumentPurposeId = DocumentPurposeReferenceData.AIP_LICENSE_ID;
    private static String AIPReportDocumentPurposeId = DocumentPurposeReferenceData.AIP_REPORT_ID;

    private static String RENEWALDocumentPurposeId = DocumentPurposeReferenceData.RENEWAL_LICENSE_ID;
    private static String INSPECTION_ID = DocumentPurposeReferenceData.INSPECTION_ID;

    public static final String AGENT_PASSPORT_ID = "9";


    public static void load(MongoRepositoryReactiveImpl mongoRepositoryReactive) {

        DocumentType documentType1 = (DocumentType) mongoRepositoryReactive.findById("1", DocumentType.class).block();
        if (documentType1 == null) {
            documentType1 = new DocumentType();
            documentType1.setId("1");
        }
        documentType1.setName("Certificate of Incorporation");
        documentType1.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType1.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType1.setActive(true);
        documentType1.setRequired(true);


        DocumentType documentType2 = (DocumentType) mongoRepositoryReactive.findById("2", DocumentType.class).block();
        if (documentType2 == null) {
            documentType2 = new DocumentType();
            documentType2.setId("2");
        }
        documentType2.setName("CAC Form CO 2");
        documentType2.setDescription("CAC Form CO 2 reflecting a minimum share capital of N20,000,000");
        documentType2.setActive(true);
        documentType2.setRequired(true);
        documentType2.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType2.setDocumentPurposeId(applicationFormDocumentPurposeId);

        DocumentType documentType3 = (DocumentType) mongoRepositoryReactive.findById("3", DocumentType.class).block();
        if (documentType3 == null) {
            documentType3 = new DocumentType();
            documentType3.setId("3");
        }
        documentType3.setName("CAC Form CO 7");
        documentType3.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType3.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType3.setActive(true);
        documentType3.setRequired(true);


        DocumentType documentType4 = (DocumentType) mongoRepositoryReactive.findById("4", DocumentType.class).block();
        if (documentType4 == null) {
            documentType4 = new DocumentType();
            documentType4.setId("4");
        }
        documentType4.setName("Detailed Business Plan");
        documentType4.setDescription("Detailed business plan/proposal to include \n1) Address of registered office, branches and planned locations. \n2) Name and profile of directors. \n3) Tax clearance of all directors in the preceding 3 years. \n4) Description of operations and management structure. \n5)Online sports betting industry analysis in Lagos state.");
        documentType4.setActive(true);
        documentType4.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType4.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType4.setRequired(true);

        DocumentType documentType5 = (DocumentType) mongoRepositoryReactive.findById("5", DocumentType.class).block();
        if (documentType5 == null) {
            documentType5 = new DocumentType();
            documentType5.setId("5");
        }
        documentType5.setName("Details of Planned Games ");
        documentType5.setDescription("Details of planned games to include \n1) Relevant games/sport activity. \n2) Approximate odds to be used. \n3) Prizes and price structure. \n4) Number and frequency of games/sports. \n5) Operator game rules and participants code of practise");
        documentType5.setActive(true);
        documentType5.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType5.setRequired(true);
        documentType5.setGameTypeIds(getOSbAndPolGameTypeIdSet());

        DocumentType documentType6 = (DocumentType) mongoRepositoryReactive.findById("6", DocumentType.class).block();
        if (documentType6 == null) {
            documentType6 = new DocumentType();
            documentType6.setId("6");
        }
        documentType6.setName("Financial Information/Projections");
        documentType6.setDescription("Financial details including \n1) Company's bank statement for the preceding 12months(for existing companies) or evidence of financial capability to operate business (for new companies). \n2) Capital budgets.\n3) Business plan. \n4) Financing plan and marketing plan. \n5) 5 year financial projection and the assumption made in considering the figures. \n6) Management accounts.");
        documentType6.setActive(true);
        documentType6.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType6.setRequired(false);
        documentType6.setGameTypeIds(getOSbAndPolGameTypeIdSet());

        DocumentType documentType7 = (DocumentType) mongoRepositoryReactive.findById("7", DocumentType.class).block();
        if (documentType7 == null) {
            documentType7 = new DocumentType();
            documentType7.setId("7");
        }
        documentType7.setName("Information on Proposed Technical Topography");
        documentType7.setDescription("Detailed information on proposed technical topography including \n1) Architectural  illustration of the platform. \n2) Detailed information abput all the technical solution providers.");
        documentType7.setActive(true);
        documentType7.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType7.setRequired(false);
        documentType7.setGameTypeIds(getOSbAndPolGameTypeIdSet());

        DocumentType documentType8 = (DocumentType) mongoRepositoryReactive.findById("8", DocumentType.class).block();
        if (documentType8 == null) {
            documentType8 = new DocumentType();
            documentType8.setId("8");
        }
        documentType8.setName("Process and Systems Quality Assurance");
        documentType8.setDocumentPurposeId(applicationFormDocumentPurposeId);
        documentType8.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType8.setActive(true);
        documentType8.setRequired(false);

        DocumentType documentType9 = (DocumentType) mongoRepositoryReactive.findById(AGENT_PASSPORT_ID, DocumentType.class).block();
        if (documentType9 == null) {
            documentType9 = new DocumentType();
            documentType9.setId(AGENT_PASSPORT_ID);
        }
        documentType9.setName("Agent Passport");
        documentType9.setDocumentPurposeId(DocumentPurposeReferenceData.AGENT_REGISTRATION_ID);
        documentType9.setActive(true);
        documentType9.setRequired(true);


        DocumentType documentType10 = (DocumentType) mongoRepositoryReactive.findById("10", DocumentType.class).block();
        if (documentType10 == null) {
            documentType10 = new DocumentType();
            documentType10.setId("10");
        }
        documentType10.setName("Certificate of Registration obtained from Special Control Unit on Money Laundering of the Federal Ministry of Trade & Investment");
        documentType10.setDocumentPurposeId(AIPDocumentPurposeId);
        documentType10.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType10.setActive(true);
        documentType10.setRequired(true);

        DocumentType documentType11 = (DocumentType) mongoRepositoryReactive.findById("11", DocumentType.class).block();
        if (documentType11 == null) {
            documentType11 = new DocumentType();
            documentType11.setId("11");
        }
        documentType11.setName("Photo frame of your brand");
        documentType11.setDocumentPurposeId(AIPDocumentPurposeId);
        documentType11.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType11.setActive(true);
        documentType11.setRequired(true);

        String[] documentNames = {
                "Certificate of Incorporation",
                "Form CAC 2",
                "Form CAC 7",
                "Memorandum and Articles of Association",
                "Directors tax clearance certificate for the preceding year",
                "Copies of Anti-Money Laundering Training Certificate for key staff",
                "Copy of audited account and management account for the previous year",
                "Details of key management staff",
                "Details of casino staff"
        };
        //documentNames.
        for (int i = 0; i < documentNames.length; i++) {
            int id = i + 12;
            DocumentType documentType13 = (DocumentType) mongoRepositoryReactive.findById(String.valueOf(id), DocumentType.class).block();
            if (documentType13 == null) {
                documentType13 = new DocumentType();
                documentType13.setId(String.valueOf(id));
            }
            documentType13.setName(documentNames[i]);
            documentType13.setDocumentPurposeId(RENEWALDocumentPurposeId);
            documentType13.setGameTypeIds(getOSbAndPolGameTypeIdSet());
            documentType13.setActive(true);
            documentType13.setRequired(true);
            mongoRepositoryReactive.saveOrUpdate(documentType13);

        }
        DocumentType documentType22 = (DocumentType) mongoRepositoryReactive.findById("22", DocumentType.class).block();
        if (documentType22 == null) {
            documentType22 = new DocumentType();
            documentType22.setId("22");
        }
        documentType22.setName("IMAGE FROM AGENT");
        documentType22.setDocumentPurposeId(DocumentPurposeReferenceData.AGENT_UPLOADS);
        documentType22.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType22.setActive(true);
        documentType22.setRequired(true);


        DocumentType documentType23 = (DocumentType) mongoRepositoryReactive.findById("23", DocumentType.class).block();
        if (documentType23 == null) {
            documentType23 = new DocumentType();
            documentType23.setId("23");
        }
        documentType23.setName("INSPECTION FORM");
        documentType23.setDocumentPurposeId(DocumentPurposeReferenceData.INSPECTION_ID);
        documentType23.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType23.setActive(true);
        documentType23.setRequired(true);

        DocumentType documentType24 = (DocumentType) mongoRepositoryReactive.findById("24", DocumentType.class).block();
        if (documentType24 == null) {
            documentType24 = new DocumentType();
            documentType24.setId("24");
        }
        documentType24.setName("Customer complain document");
        documentType24.setDocumentPurposeId(DocumentPurposeReferenceData.CUSTOMER_COMPLAIN_ID);
        documentType24.setActive(true);
        documentType24.setRequired(true);

        DocumentType documentType25 = (DocumentType) mongoRepositoryReactive.findById("25", DocumentType.class).block();
        if (documentType25 == null) {
            documentType25 = new DocumentType();
            documentType25.setId("25");
        }
        documentType25.setName("Logged case document");
        documentType25.setDocumentPurposeId(DocumentPurposeReferenceData.LOGGED_CASE_ID);
        documentType25.setActive(true);
        documentType25.setRequired(true);

        DocumentType documentType26 = (DocumentType) mongoRepositoryReactive.findById("26", DocumentType.class).block();
        if (documentType26 == null) {
            documentType26 = new DocumentType();
            documentType26.setId("26");
        }
        documentType26.setName("AIP Assessment Document Type");
        documentType26.setDocumentPurposeId(AIPReportDocumentPurposeId);
        documentType26.setGameTypeIds(getOSbAndPolGameTypeIdSet());
        documentType26.setActive(true);
        documentType26.setRequired(false);


        mongoRepositoryReactive.saveOrUpdate(documentType1);
        mongoRepositoryReactive.saveOrUpdate(documentType2);
        mongoRepositoryReactive.saveOrUpdate(documentType3);
        mongoRepositoryReactive.saveOrUpdate(documentType4);
        mongoRepositoryReactive.saveOrUpdate(documentType5);
        mongoRepositoryReactive.saveOrUpdate(documentType6);
        mongoRepositoryReactive.saveOrUpdate(documentType7);
        mongoRepositoryReactive.saveOrUpdate(documentType8);
        mongoRepositoryReactive.saveOrUpdate(documentType9);
        mongoRepositoryReactive.saveOrUpdate(documentType10);
        mongoRepositoryReactive.saveOrUpdate(documentType11);
        mongoRepositoryReactive.saveOrUpdate(documentType22);
        mongoRepositoryReactive.saveOrUpdate(documentType23);
        mongoRepositoryReactive.saveOrUpdate(documentType24);
        mongoRepositoryReactive.saveOrUpdate(documentType25);
        mongoRepositoryReactive.saveOrUpdate(documentType26);


    }

    private static Set<String> getOSbAndPolGameTypeIdSet() {
        Set<String> gameTypeIds = new HashSet<>();
        gameTypeIds.add(GameTypeReferenceData.OSB_GAME_TYPE_ID);
        gameTypeIds.add(GameTypeReferenceData.POL_GAME_TYPE_ID);
        return gameTypeIds;
    }
}
