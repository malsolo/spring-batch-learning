drop table if exists video_game_sales;
create table if not exists video_game_sales
(
    rank         int,
    name         text,
    platform     text,
    year         int,
    genre        text,
    publisher    text,
    na_sales     text,
    eu_sales     text,
    jp_sales     text,
    other_sales  text,
    global_sales text,
    unique (name, platform, year, genre)

);
