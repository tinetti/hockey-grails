insert into game_new
(
SELECT
        game.id                   AS id                       ,
        game.version              AS version                  ,
        away_stats.hits           AS away_stats_hits          ,
        away_stats.score          AS away_stats_score         ,
        away_stats.shots          AS away_stats_shots         ,
        away_stats.team           AS away_stats_team          ,
        away_stats.time_on_attack AS away_stats_time_on_attack,
        game.date                 AS DATE                     ,
        game.game_length          AS game_length              ,
        game.game_version         AS game_version             ,
        home_stats.hits           AS home_stats_hits          ,
        home_stats.score          AS home_stats_score         ,
        home_stats.shots          AS home_stats_shots         ,
        home_stats.team           AS home_stats_team          ,
        home_stats.time_on_attack AS home_stats_time_on_attack,
        game.notes                AS notes
   FROM
        game                 ,
        team_stats away_stats,
        team_stats home_stats
  WHERE
        game.away_stats_id     = away_stats.id
        AND game.home_stats_id = home_stats.id
)