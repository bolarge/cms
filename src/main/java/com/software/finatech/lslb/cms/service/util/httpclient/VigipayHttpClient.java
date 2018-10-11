package com.software.finatech.lslb.cms.service.util.httpclient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.software.finatech.lslb.cms.service.dto.sso.SSOToken;
import com.software.finatech.lslb.cms.service.exception.VigiPayServiceException;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayCreateCustomer;
import com.software.finatech.lslb.cms.service.model.vigipay.VigipayCreateInvoice;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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
        try {
            RestTemplate restTemplate = new RestTemplate();
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
            logger.error("An error occurred while getting access token on vg pay", e);
            return null;
        }
    }

    public String createCustomerCode(VigipayCreateCustomer vigipayCreateCustomer) {
        String url = vigipayBaseUrl + "/Customers/New";
        try {
            String accessToken = getAccessToken();
            if (!StringUtils.isEmpty(accessToken)) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setErrorHandler(new ErrorHandler());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                String requestJson = OBJECT_MAPPER.writeValueAsString(vigipayCreateCustomer);
                logger.info("Creating customer on Vigipay \n Customer Name -> {} \n\n Request -> \n {}", vigipayCreateCustomer.getName(), requestJson);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
                logger.info(" response from Vigipay -> {}", responseEntity.getBody());
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
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setErrorHandler(new ErrorHandler());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                String requestJson = OBJECT_MAPPER.writeValueAsString(vigipayCreateInvoice);
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
                logger.info(" response from Vigipay -> {}", responseEntity.getBody());
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    String invoiceNumber = responseEntity.getBody();
                    return invoiceNumber.replace("\"", "");
                }
                return null;
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("An error occurred while creating invoice with Vigipay", e);
            return null;
        }
    }

    public boolean validateInvoicePaid(String invoiceNumber) throws VigiPayServiceException {
        String url = vigipayBaseUrl + "/Invoices/LookUpRef?invoiceReference=" + invoiceNumber;
        try {
            String accessToken = getAccessToken();
            if (!StringUtils.isEmpty(accessToken)) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setErrorHandler(new ErrorHandler());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + accessToken);
                HttpEntity<?> httpEntity = new HttpEntity<>(headers);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
                logger.info(" response from Vigipay -> {}", responseEntity.getBody());
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    String responseBody = responseEntity.getBody();
                    JSONObject responseJson = new JSONObject(responseBody);
                    return responseJson.getInt("PaymentStatus") == 1;
                }
                logger.info(responseEntity.getBody());
                return false;
            }
            return false;
        } catch (Exception e) {
            String errorMessage = "An error occurred while getting invoice payment status from vigipay";
            logger.error(errorMessage, e);
            throw new VigiPayServiceException(errorMessage);
        }
    }
}
