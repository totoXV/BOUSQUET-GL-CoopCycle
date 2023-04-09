package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Membre;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Membre}, with proper type conversions.
 */
@Service
public class MembreRowMapper implements BiFunction<Row, String, Membre> {

    private final ColumnConverter converter;

    public MembreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Membre} stored in the database.
     */
    @Override
    public Membre apply(Row row, String prefix) {
        Membre entity = new Membre();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
