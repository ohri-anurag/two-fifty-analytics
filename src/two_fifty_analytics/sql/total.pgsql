select
  players.player_name,
  sum(
    case
      when teams.id = bidding_team and bid <= score then bid
      when teams.id = bidding_team then 0
      when teams.id = anti_team and bid <= score then 0
      when teams.id = anti_team and 250 - score >= 100 then bid
      else 250 - score
    end
  ) as score,
  count(bid) as total_games,
  sum(
    case
      when players.id = bidder then 1
      else 0
    end
  ) as total_bids,
  sum(
    case
      when players.id = bidder and bid <= score then 1
      else 0
    end
  ) as successful_bids
from ((games
inner join teams on (teams.id = bidding_team or teams.id = anti_team))
inner join players on teams.p1 = players.id or teams.p2 = players.id or teams.p3 = players.id or teams.p4 = players.id or teams.p5 = players.id)
group by players.id
order by score desc
