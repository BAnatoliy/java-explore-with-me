package ru.practicum.ewm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.StatsDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "stat_hits")
@NamedNativeQueries({
        @NamedNativeQuery(name = "getStatsDtoWithUniqueIp",
                query = "select h.app_name as app, h.uri_name as uri, count(distinct(h.ip_address)) as hits " +
                        "from stat_hits as h " +
                        "where (h.create_time between :start and :end) and (h.uri_name in :uris) " +
                        "group by h.app_name, h.uri_name " +
                        "order by 3 desc", resultSetMapping = "StatsDtoMapping"),
        @NamedNativeQuery(name = "getStatsDto",
                query = "select h.app_name as app, h.uri_name as uri, count(h.ip_address) as hits " +
                        "from stat_hits as h " +
                        "where (h.create_time between :start and :end) and (h.uri_name in :uris) " +
                        "group by h.app_name, h.uri_name " +
                        "order by 3 desc", resultSetMapping = "StatsDtoMapping")
})
@SqlResultSetMapping(name = "StatsDtoMapping", classes = {
        @ConstructorResult(
                columns = {
                        @ColumnResult(name = "app"),
                        @ColumnResult(name = "uri"),
                        @ColumnResult(name = "hits", type = Long.class)
                }, targetClass = StatsDto.class
        )
})
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app_name")
    private String app;
    @Column(name = "uri_name")
    private String uri;
    @Column(name = "ip_address")
    private String ip;
    @Column(name = "create_time")
    private LocalDateTime timestamp;
}
