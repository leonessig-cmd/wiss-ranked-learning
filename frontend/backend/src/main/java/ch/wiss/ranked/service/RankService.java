package ch.wiss.ranked.service;

import org.springframework.stereotype.Service;

@Service
public class RankService {

    public RankInfo calculateRank(int rankPoints, int leaderboardPlace) {

        if (leaderboardPlace > 0 && leaderboardPlace <= 100 && rankPoints >= 7200) {
            return new RankInfo("Legende", 0, leaderboardPlace);
        }

        if (rankPoints < 900) {
            return new RankInfo("Bronze", calculateDivision(rankPoints, 0, 900), null);
        }

        if (rankPoints < 1800) {
            return new RankInfo("Silber", calculateDivision(rankPoints, 900, 1800), null);
        }

        if (rankPoints < 2700) {
            return new RankInfo("Gold", calculateDivision(rankPoints, 1800, 2700), null);
        }

        if (rankPoints < 3900) {
            return new RankInfo("Platin", calculateDivision(rankPoints, 2700, 3900), null);
        }

        if (rankPoints < 5400) {
            return new RankInfo("Diamant", calculateDivision(rankPoints, 3900, 5400), null);
        }

        return new RankInfo("Champion", calculateDivision(rankPoints, 5400, 7200), null);
    }

    private int calculateDivision(int points, int min, int max) {
        int range = max - min;
        int step = range / 3;
        int progress = points - min;

        if (progress < step) {
            return 3;
        }

        if (progress < step * 2) {
            return 2;
        }

        return 1;
    }

    public record RankInfo(String rank, int division, Integer legendPlace) {
    }
}