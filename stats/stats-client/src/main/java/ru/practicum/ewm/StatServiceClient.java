package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class StatServiceClient extends BaseClient { // тестовый класс для проверки клиента

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

    public ResponseEntity<Object> getStats(Map<String, Object> parameters) {
        return get(parameters);
    }
}
