package com.malsolo.springframework.batch.batchfiledbprocessing;

public record VideoGameSale(int rank, String name, String platform, String year, String genre, String publisher,
                            String naSales, String euSales, String jpSales, String otherSales, String globalSales) {
}
