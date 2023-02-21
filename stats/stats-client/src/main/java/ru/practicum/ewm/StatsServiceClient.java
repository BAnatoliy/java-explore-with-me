package ru.practicum.ewm;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class StatsServiceClient {
    private final RestTemplate rest;

    public StatsServiceClient(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<Object> post(HitRequestDto body) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    public ResponseEntity<Object> get(Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, "/stats", parameters, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable HitRequestDto body) {
        HttpEntity<?> requestEntity;
        if (body != null) {
            requestEntity = new HttpEntity<>(body);
        } else {
            requestEntity = HttpEntity.EMPTY;
        }
        ResponseEntity<Object> statsServerResponse;

        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statsServerResponse);
    }

    private ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            bodyBuilder.body(response.getBody());
        }

        return bodyBuilder.build();
    }
}
