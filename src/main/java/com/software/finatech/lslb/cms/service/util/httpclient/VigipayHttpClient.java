package com.software.finatech.lslb.cms.service.util.httpclient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.finatech.lslb.cms.service.dto.sso.SSOToken;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayCreateCustomer;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayCreateInvoice;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Component
public class VigipayHttpClient {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger logger = LoggerFactory.getLogger(VigipayHttpClient.class);

    @Value("${vigipay.client-secret}")
    private String vigipayClientSecret;
    @Value("${vigipay.client-id}")
    private String vigipayClientId;
    @Value("${vigipay.scope}")
    private String vigipayScope;
    @Value("${vigipay.grant-type}")
    private String grantType;
    @Value("${vigipay.username}")
    private String vigipayUsername;
    @Value("${vigipay.password}")
    private String vigipayPassword;
    @Value("${vigipay.token-url}")
    private String vigipayTokenUrl;

    @Value("${vigipay.loop-base-url}")
    private String vigipayBaseUrl;


    private String getAccessToken() {
        logger.info(vigipayTokenUrl);
        try {
            // RestTemplate restTemplate = new RestTemplate();
            RestTemplate restTemplate = getUnsecureRestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("grant_type", grantType);
            map.add("username", vigipayUsername);
            map.add("password", vigipayPassword);
            map.add("scope", vigipayScope);
            map.add("client_id", vigipayClientId);
            map.add("client_secret", vigipayClientSecret);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(vigipayTokenUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                SSOToken ssoToken = OBJECT_MAPPER.readValue(responseBody, SSOToken.class);
                return ssoToken.getAccess_token();
            }
            return null;
        } catch (IOException e) {
            logger.error("An error occurred while parsing the response body", e);
            return null;
        } catch (Exception e) {
            logger.error("An error occurred while creating customer with vg pay", e);
            return null;
        }
    }

    public String createCustomerCode(VigipayCreateCustomer vigipayCreateCustomer) {
        String url = vigipayBaseUrl + "/Customers/New";
        try {
            String accessToken = getAccessToken();
            if (!StringUtils.isEmpty(accessToken)) {
                //   RestTemplate restTemplate = new RestTemplate();
                RestTemplate restTemplate = getUnsecureRestTemplate();
                restTemplate.setErrorHandler(new ErrorHandler());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                String requestJson = OBJECT_MAPPER.writeValueAsString(vigipayCreateCustomer);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);

                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    String responseBody = responseEntity.getBody();
                    JSONObject responseJson = new JSONObject(responseBody);
                    return responseJson.getString("Code");
                }
                logger.info(responseEntity.getBody());
                return null;
            }
            return null;
        } catch (IOException e) {
            logger.error("An error occurred while parsing the response body", e);
            return null;
        } catch (Exception e) {
            logger.error("An error occurred while creating customer with vg pay", e);
            return null;
        }
    }

    public String createInvoice(VigipayCreateInvoice vigipayCreateInvoice) {
        String url = vigipayBaseUrl + "/Invoices/New";
        try {
            String accessToken = getAccessToken();
            if (!StringUtils.isEmpty(accessToken)) {
                //    RestTemplate restTemplate = new RestTemplate();
                RestTemplate restTemplate = getUnsecureRestTemplate();
                restTemplate.setErrorHandler(new ErrorHandler());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                String requestJson = OBJECT_MAPPER.writeValueAsString(vigipayCreateInvoice);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    return responseEntity.getBody();
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("An error occurred while making the call", e);
            return null;
        }
    }


    private RestTemplate getUnsecureRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }
}
