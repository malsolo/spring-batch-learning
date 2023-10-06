drop table if exists video_game_sales;
create table if not exists video_game_sales
(
    rank         int,
    name         VARCHAR(150),
    platform     VARCHAR(50),
    year         VARCHAR(10),
    genre        VARCHAR(50),
    publisher    VARCHAR(100),
    na_sales     VARCHAR(20),
    eu_sales     VARCHAR(20),
    jp_sales     VARCHAR(20),
    other_sales  VARCHAR(20),
    global_sales VARCHAR(20),
    unique (name, platform, year, genre)

);
