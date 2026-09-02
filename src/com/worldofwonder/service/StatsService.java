package com.worldofwonder.service;

import com.worldofwonder.model.GameSession;
import com.worldofwonder.model.UserStats;
import com.worldofwonder.repository.StatsRepository;

public class StatsService {

    private final StatsRepository statsRepository = new StatsRepository();

    public UserStats getUserStats(int userId) {
        return statsRepository.getUserStats(userId);
    }

    public boolean recordSession(GameSession session) {
        if (session.getUserId() <= 0 || session.getGameType() == null) {
            return false;
        }
        return statsRepository.recordSession(session);
    }
}
