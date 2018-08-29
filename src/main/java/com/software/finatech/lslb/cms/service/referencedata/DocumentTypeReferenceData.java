package com.software.finatech.lslb.cms.service.referencedata;

import com.software.finatech.lslb.cms.service.domain.DocumentType;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;

import java.util.HashSet;
import java.util.Set;

public class DocumentTypeReferenceData {

    private static String applicationFormDocumentPurposeId = DocumentPurposeReferenceData.APPLICATION_FORM_DOCUMENT_PURPOSE_ID;

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
        documentType6.setRequired(true);
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
        documentType7.setRequired(true);
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
        documentType8.setRequired(true);

        mongoRepositoryReactive.saveOrUpdate(documentType1);
        mongoRepositoryReactive.saveOrUpdate(documentType2);
        mongoRepositoryReactive.saveOrUpdate(documentType3);
        mongoRepositoryReactive.saveOrUpdate(documentType4);
        mongoRepositoryReactive.saveOrUpdate(documentType5);
        mongoRepositoryReactive.saveOrUpdate(documentType6);
        mongoRepositoryReactive.saveOrUpdate(documentType7);
        mongoRepositoryReactive.saveOrUpdate(documentType8);

    }

    private static Set<String> getOSbAndPolGameTypeIdSet() {
        Set<String> gameTypeIds = new HashSet<>();
        gameTypeIds.add(GameTypeReferenceData.OSB_GAME_TYPE_ID);
        gameTypeIds.add(GameTypeReferenceData.POL_GAME_TYPE_ID);
        return gameTypeIds;
    }
}
