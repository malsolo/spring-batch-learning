package com.malsolo.springframework.batch.batchfiledbprocessing;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VideoGameSaleRowMapper implements RowMapper<VideoGameSale> {
    @Override
    public VideoGameSale mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new VideoGameSale(
                rs.getInt("rank"),
                rs.getString("name"),
                rs.getString("platform"),
                rs.getString("year"),
                rs.getString("genre"),
                rs.getString("publisher"),
                rs.getString("na_sales"),
                rs.getString("eu_sales"),
                rs.getString("jp_sales"),
                rs.getString("other_sales"),
                rs.getString("global_sales")
        );
    }
}
