package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatServiceClient extends BaseClient {

    public StatServiceClient(@Value("${hits-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> sendHit(HitRequestDto hitDto) {
        return post(hitDto);
    }

    public ResponseEntity<Object> getStats(String startTime, String endTime,
                                           List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", startTime);
        parameters.put("end", endTime);
        parameters.put("uris", uris.toString().replace("[", "").replace("]", ""));
        parameters.put("unique", unique);
        return get(parameters);
    }
}
