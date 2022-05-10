package co.com.meli.clima.restclimatico.infrastructure.repository;

import co.com.meli.clima.restclimatico.domain.entity.Pronostico;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class PronosticoRepository  implements CommonRepository<Pronostico> {
    private static final String SQL_INSERT = "insert into pronostico (dia, clima) values (:dia,:clima)";
    private static final String SQL_QUERY_FIND_ALL = "select dia, clima from pronostico";
    private static final String SQL_QUERY_FIND_BY_ID = SQL_QUERY_FIND_ALL + " where dia = :dia";
    private static final String SQL_UPDATE = "update pronostico set clima = :clima where dia = :dia";
    private static final String SQL_DELETE = "delete from pronostico where dia = :dia";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PronosticoRepository(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Pronostico> toDoRowMapper = (ResultSet rs, int rowNum) -> {
        Pronostico domain = new Pronostico();
        domain.setDia(rs.getInt("dia"));
        domain.setClima(rs.getString("clima"));
        return domain;
    };

    @Override
    public Pronostico save(final Pronostico domain) {
        Pronostico result = findById(domain.getDia());
        if(result != null){
            result.setClima(domain.getClima());
            return upsert(result, SQL_UPDATE);
        }
        return upsert(domain,SQL_INSERT);
    }

    private Pronostico upsert(final Pronostico domain, final String sql){
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("dia",domain.getDia());
        namedParameters.put("clima",domain.getClima());

        this.jdbcTemplate.update(sql,namedParameters);
        return findById(domain.getDia());
    }

    @Override
    public Iterable<Pronostico> save(Collection<Pronostico> domains) {
        domains.forEach( this::save);
        return findAll();
    }

    @Override
    public void delete(final Pronostico domain) {
        Map<String, Integer> namedParameters = Collections.singletonMap("dia", domain.getDia());
        this.jdbcTemplate.update(SQL_DELETE,namedParameters);
    }

    @Override
    public Pronostico findById(Integer id) {
        try {
            Map<String, Integer> namedParameters = Collections.singletonMap("dia", id);
            return this.jdbcTemplate.queryForObject(SQL_QUERY_FIND_BY_ID, namedParameters, toDoRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Iterable<Pronostico> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_FIND_ALL, toDoRowMapper);
    }
}
