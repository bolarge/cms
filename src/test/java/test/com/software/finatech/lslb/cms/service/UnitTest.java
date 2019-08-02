package test.com.software.finatech.lslb.cms.service;

import com.software.finatech.lslb.cms.service.domain.AuthInfo;
import com.software.finatech.lslb.cms.service.domain.FactObject;
import com.software.finatech.lslb.cms.service.persistence.MongoRepositoryReactiveImpl;
import com.software.finatech.lslb.cms.service.util.DatabaseLoaderUtils;
import com.software.finatech.lslb.cms.service.util.GlobalApplicationContext;
import io.advantageous.boon.json.JsonFactory;
import io.advantageous.boon.json.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

//@ComponentScan("com.software.finatech.lslb")
//@Configuration
//@EnableAutoConfiguration
//@ActiveProfiles("unittest")
//@Test(suiteName="IntegrationTestConfigurations",priority=1,dependsOnGroups = {})
//@SpringBootTest(classes = {MongoRepositoryReactiveImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class UnitTest  extends AbstractTestNGSpringContextTests {
//    protected ObjectMapper mapper;
//    protected com.fasterxml.jackson.databind.ObjectMapper mapperJackson;
//    @Autowired
//    protected TestRestTemplate restTemplate;
//    @Autowired
//    protected MongoRepositoryReactiveImpl mongoRepositoryReactive;
//    @Autowired
//    protected MongoTemplate mongoTemplate;
//    @Autowired
//    protected ApplicationContext ctx;
//    @LocalServerPort
//    protected int port;
//
//    public UnitTest(){
//        mapper =  JsonFactory.createUseAnnotations(true);
//    }
//
//    @AfterMethod
//    public void afterMethod() {
//        logger.debug("AfterMethod method is run...");
//    }
//    @BeforeMethod
//    public void beforeMethod() {
//        logger.debug("BeforeMethod method is run...");
//    }
//
//    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextPrepareTestInstance")
//    public void setUp() throws Exception {
//        GlobalApplicationContext.ctx =  ctx;
//        //DRop the databases first
//        mongoRepositoryReactive.getReactiveMongoTemplate().getMongoDatabase().drop();
//        mongoTemplate.getCollectionNames().stream().forEach(collection->{
//            logger.info("Dropping Collection -> " + collection );
//            mongoRepositoryReactive.getReactiveMongoTemplate().dropCollection(collection).block();
//        });
//
//        DatabaseLoaderUtils databaseLoaderUtils = (DatabaseLoaderUtils) ctx.getBean("databaseLoaderUtils");
//        databaseLoaderUtils.runLoadData();
//
//        AuthInfo authInfo = new AuthInfo();
//        authInfo.setAuthRoleId("2");
//        authInfo.setId("112");
//        authInfo.setAccountExpirationTime(null);
//        authInfo.setAccountLocked(false);
//        authInfo.setCredentialsExpirationTime(null);
//        authInfo.setEnabled(true);
//        authInfo.setFullName("David Opeyemi");
//        authInfo.setEmailAddress("david.jaiyeola@venturegardengroup.com");
//        mongoRepositoryReactive.saveOrUpdate(authInfo);
//    }
//
//    @AfterClass
//    public void tearDown() throws Exception {
//        logger.info("Finished running all test cases...");
//    }
//    //testSave
//    @Test(groups="RepositoryTest",alwaysRun=true,description="Test save of facts :Should save the facts")
//    public void test_MongoReactive_Save_Object(){
//        AuthInfo authInfo = new AuthInfo();
//        authInfo.setAuthRoleId("2");
//        authInfo.setId("111");
//        authInfo.setAccountExpirationTime(null);
//        authInfo.setAccountLocked(false);
//        authInfo.setCredentialsExpirationTime(null);
//        authInfo.setEnabled(true);
//        authInfo.setFullName("David Jaiyeola");
//        authInfo.setEmailAddress("david.jaiyeola@gmail.com");
//        mongoRepositoryReactive.saveOrUpdate(authInfo);
//        AuthInfo authInfoFromDb = (AuthInfo) mongoRepositoryReactive.findById(authInfo.getId(), AuthInfo.class).block();
//        assertNotNull(authInfoFromDb);
//    }
//
//    //testFind
//    @Test(groups="RepositoryTest",dependsOnMethods = { "test_MongoReactive_Save_Object" },alwaysRun=true,description="Test Find By name :Should fetch the fact")
//    public void test_MongoReactive_Find_By_Name() {
//        AuthInfo authInfoFromDb = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("fullName").is("David Jaiyeola")), AuthInfo.class).block();
//        assertNotNull(authInfoFromDb);
//    }
//
//    //testFindAll
//    @Test(groups="RepositoryTest",dependsOnMethods = { "test_MongoReactive_Save_Object" },alwaysRun=true,description="Test query of facts : Should fetch the facts")
//    public void test_MongoReactive_FindAll() {
//        ArrayList<FactObject> authInfos = (ArrayList<FactObject>) mongoRepositoryReactive.findAll(AuthInfo.class).toStream().collect(Collectors.toList());
//        assertThat(authInfos, hasSize(2));
//    }
//
//    //testUpdate
//    @Test(groups="RepositoryTest",dependsOnMethods = { "test_MongoReactive_FindAll" },alwaysRun=true,description="Test updating of facts : Should update the facts")
//    public void test_MongoReactive_Update() {
//        AuthInfo authInfoFromDb = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("fullName").is("David Jaiyeola")), AuthInfo.class).block();
//        assertNotNull(authInfoFromDb);
//        authInfoFromDb.setLastName("Oluwabamise");
//        mongoRepositoryReactive.saveOrUpdate(authInfoFromDb);
//
//        AuthInfo authInfoFromDb1 = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("lastName").is("Oluwabamise")), AuthInfo.class).block();
//        assertNotNull(authInfoFromDb1);
//    }
//
//    /**
//     * Should delete the facts
//     */
//    @Test(groups="RepositoryTest",dependsOnMethods = {"test_MongoReactive_Update"},alwaysRun=true,description="Test deleting of facts : Should delete the facts")
//    public void test_MongoReactive_Delete() {
//        AuthInfo authInfoFromDb1 = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("lastName").is("Oluwabamise")), AuthInfo.class).block();
//        assertNotNull(authInfoFromDb1);
//        mongoRepositoryReactive.delete(authInfoFromDb1);
//
//        AuthInfo authInfoFromDb = (AuthInfo) mongoRepositoryReactive.find(new Query(Criteria.where("lastName").is("Oluwabamise")), AuthInfo.class).block();
//        assertNull(authInfoFromDb);
//    }
//
//
//    // make thread sleep a while, so that reduce effect to subsequence operation if any shared resource,
//    protected void delay(long milliseconds) {
//        try {
//            Thread.sleep(milliseconds);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Generic method to call rest services
//     * @param accessToken
//     * @param object
//     * @param apiPath
//     * @param httpMethod
//     * @return
//     */
//    public ResponseEntity restAPI(String accessToken, Object object, String apiPath, HttpMethod httpMethod) {
//        logger.info("Running..." + apiPath);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        if(accessToken != null) {
//            headers.add("Authorization", "Bearer " + accessToken);
//        }
//        /*Response response;
//        if(httpMethod==HttpMethod.POST){
//            response = RestAssured.given()
//                    .header("Authorization", "Bearer " + accessToken)
//                    .and().with().when()
//                    .post("http://localhost:" + port + apiPath);
//        }else {
//            response = RestAssured.given()
//                    .header("Authorization", "Bearer " + accessToken)
//                    .and().with().when()
//                    .get("http://localhost:" + port + apiPath);
//        }
//        logger.info(response.body().asString());
//        */
//
//        HttpEntity<String> entity = null;
//        if(httpMethod==HttpMethod.POST){
//            try {
//                String json = mapper.writeValueAsString(object);
//                logger.info(json);
//                entity = new HttpEntity<>(json,headers);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }else{
//            entity = new HttpEntity<>(headers);
//        }
//        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + apiPath, httpMethod, entity, String.class);
//        logger.info(response.getBody());
//        return response;
//    }
//}
