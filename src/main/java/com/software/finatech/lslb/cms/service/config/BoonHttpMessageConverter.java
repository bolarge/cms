package com.software.finatech.lslb.cms.userservice.config;

import com.software.finatech.lslb.cms.userservice.dto.LoginDto;
import com.software.finatech.lslb.cms.userservice.dto.sso.SSOChangePasswordModel;
import com.software.finatech.lslb.cms.userservice.dto.sso.SSOPasswordResetModel;
import io.advantageous.boon.json.JsonFactory;
import io.advantageous.boon.json.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import springfox.documentation.swagger.web.UiConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class BoonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    ObjectMapper mapper;
    com.fasterxml.jackson.databind.ObjectMapper mapperJackson;
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public BoonHttpMessageConverter(){
        super(new MediaType("application", "json", DEFAULT_CHARSET));
        mapper =  JsonFactory.createUseAnnotations(true);
        mapperJackson = new com.fasterxml.jackson.databind.ObjectMapper();
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try{
            Long startTime5 = System.nanoTime();
            String result = IOUtils.toString(inputMessage.getBody(), StandardCharsets.UTF_8);
            Object object =  mapper.readValue(result, clazz );
            Long endTime5 = System.nanoTime() - startTime5;
            Double timeMills5 = (double) (Double.valueOf(endTime5)/Double.valueOf(1000000));
            logger.info("Time taken to parse from http"+" ->"+ endTime5 + "ns" + " >>> " + timeMills5 +"ms");
            if(object instanceof LoginDto || object instanceof SSOChangePasswordModel || object instanceof SSOPasswordResetModel){

            }else{
                logger.info("Inbound Message ::: " + result);
            }
            return object;
        }catch(Throwable e){
            throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Long startTime5 = System.nanoTime();
        String json = "";
        //Boon does not parse this well so we use Jackson
        if(obj instanceof UiConfiguration || obj instanceof springfox.documentation.spring.web.json.Json){
            json = mapperJackson.writeValueAsString(obj);
        }else{
            json = mapper.writeValueAsString(obj);
        }

        Long endTime5 = System.nanoTime() - startTime5;
        Double timeMills5 = (double) (Double.valueOf(endTime5)/Double.valueOf(1000000));
        logger.info("Time taken to write to http"+" ->"+ endTime5 + "ns" + " >>> " + timeMills5 +"ms");
        logger.info("Outbound Message ::: " + json);
        outputMessage.getBody().write(json.getBytes());
    }

    //TODO: move this to a more appropriated utils class
    public String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the Reader.read(char[]
         * buffer) method. We iterate until the Reader return -1 which means
         * there's no more data to read. We use the StringWriter class to
         * produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
