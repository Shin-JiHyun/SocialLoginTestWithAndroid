package com.example.SocialLoginTest.controller;


import com.example.SocialLoginTest.ConfigUtils;
import com.example.SocialLoginTest.dto.GoogleLoginDTO;
import com.example.SocialLoginTest.dto.GoogleLoginRequest;
import com.example.SocialLoginTest.dto.GoogleLoginResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Controller
@RequestMapping(value = "/google")
public class GoogleController {
    private final ConfigUtils configUtils;

    GoogleController(ConfigUtils configUtils) {
        this.configUtils = configUtils;
    }
/*
    @GetMapping(value = "/login")
    public ResponseEntity<Object> moveGoogleInitUrl() {
        String authUrl = configUtils.googleInitUrl();
        URI redirectUri = null;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }
*/
    @PostMapping(value = "/login/redirect")
    public ResponseEntity<GoogleLoginDTO> redirectGoogleLogin(@RequestBody GoogleLoginResponse googleLoginResponse) {
        // HTTP 통신을 위해 RestTemplate 활용
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginResponse requestparam = GoogleLoginResponse.builder()
                .accessToken(googleLoginResponse.getAccessToken())
                /*
                .scope(googleLoginResponse.getScope())
                .expiresIn(googleLoginResponse.getExpiresIn())
                //.refreshToken(googleLoginResponse.getRefreshToken())
                .tokenType(googleLoginResponse.getTokenType())
                 */
                .idToken(googleLoginResponse.getIdToken())
                .build();
        /*
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(configUtils.getGoogleClientId())
                .clientSecret(configUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(configUtils.getGoogleRedirectUri())
                .grantType("authorization_code")
                .build();

*/
        try {
            /*
            // Http Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
*/
            //access token을 받는 부분
            //ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(configUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);
            // ObjectMapper를 통해 String to Object로 변환
            /*
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});
*/
            // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
            String jwtToken = googleLoginResponse.getIdToken();
            log.info("sucess");
            log.info("idtoken:{}",jwtToken);
            log.info("accessToken:{}",googleLoginResponse.getAccessToken());

            // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
            String requestUrl = UriComponentsBuilder.fromHttpUrl(configUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();

            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            ObjectMapper objectMapper = new ObjectMapper();

            if(resultJson != null) {
                GoogleLoginDTO userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDTO>() {});

                String aaaa ="test";
                log.info("sucess");
                log.info("token:{}",jwtToken);
                log.info("userInfoDto:{}",userInfoDto.toString());

                return ResponseEntity.ok().body(userInfoDto);
            }
            else {
                throw new Exception("Google OAuth failed!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);
    }


}