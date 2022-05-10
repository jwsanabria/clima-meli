package co.com.meli.clima.restclimatico.infrastructure.repository;

import co.com.meli.clima.restclimatico.domain.entity.Planeta;
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
public class PlanetaRepository implements CommonRepository<Planeta> {
    private static final String SQL_INSERT = "insert into planeta (id, nombre, radio, velocidad, horario) values (:id,:nombre,:radio,:velocidad, :horario)";
    private static final String SQL_QUERY_FIND_ALL = "select id, nombre, radio, velocidad, horario from planeta";
    private static final String SQL_QUERY_FIND_BY_ID = SQL_QUERY_FIND_ALL + " where id = :id";
    private static final String SQL_UPDATE = "update planeta set nombre = :nombre, radio = :radio, velocidad = :velocidad, horario = :horario where id = :id";
    private static final String SQL_DELETE = "delete from planeta where id = :id";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlanetaRepository(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Planeta> toDoRowMapper = (ResultSet rs, int rowNum) -> {
        Planeta domain = new Planeta();
        domain.setId(rs.getInt("id"));
        domain.setNombre(rs.getString("nombre"));
        domain.setRadio(rs.getInt("radio"));
        domain.setVelocidad(rs.getInt("velocidad"));
        domain.setHorario(rs.getBoolean("horario"));
        return domain;
    };

    @Override
    public Planeta save(final Planeta domain) {
        Planeta result = findById(domain.getId());
        if(result != null){
            result.setNombre(domain.getNombre());
            result.setRadio(domain.getRadio());
            result.setVelocidad(domain.getVelocidad());
            result.setHorario(domain.getHorario());
            return upsert(result, SQL_UPDATE);
        }
        return upsert(domain,SQL_INSERT);
    }

    private Planeta upsert(final Planeta domain, final String sql){
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("id",domain.getId());
        namedParameters.put("nombre",domain.getNombre());
        namedParameters.put("radio",domain.getRadio());
        namedParameters.put("velocidad",domain.getVelocidad());
        namedParameters.put("horario", domain.getHorario());

        this.jdbcTemplate.update(sql,namedParameters);
        return findById(domain.getId());
    }

    @Override
    public Iterable<Planeta> save(Collection<Planeta> domains) {
        domains.forEach( this::save);
        return findAll();
    }

    @Override
    public void delete(final Planeta domain) {
        Map<String, Integer> namedParameters = Collections.singletonMap("id", domain.getId());
        this.jdbcTemplate.update(SQL_DELETE,namedParameters);
    }

    @Override
    public Planeta findById(Integer id) {
        try {
            Map<String, Integer> namedParameters = Collections.singletonMap("id", id);
            return this.jdbcTemplate.queryForObject(SQL_QUERY_FIND_BY_ID, namedParameters, toDoRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Iterable<Planeta> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_FIND_ALL, toDoRowMapper);
    }
}
