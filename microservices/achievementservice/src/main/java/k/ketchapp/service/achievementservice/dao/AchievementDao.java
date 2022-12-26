package k.ketchapp.service.achievementservice.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import k.ketchapp.proto.Achievement;
import k.ketchapp.proto.AchievementProgress;
import k.ketchapp.proto.Event;

public class AchievementDao {

  private final List<Achievement> achievements = new ArrayList<>();

  private final Map<Integer, AchievementProgress> achievementProgressMap = new HashMap<>();

  public AchievementDao() {
    initialize();
  }

  public List<Achievement> getAchievements() {
    return achievements;
  }

  public AchievementProgress getAchievementProgress(Achievement achievement) {
    return achievementProgressMap.get(achievement.getId());
  }

  public void updateAchievement(Event event) {
    for (Map.Entry<Integer, AchievementProgress> entry : achievementProgressMap.entrySet()) {

      AchievementProgress oldProgress = entry.getValue();

      if (!oldProgress.getCompleted()) {

        int newProgressValue = oldProgress.getProgress() + 1;
        boolean isCompleted = oldProgress.getCompleted() || newProgressValue >= 100;

        AchievementProgress newProgress = AchievementProgress.newBuilder(oldProgress)
            .setProgress(newProgressValue)
            .setCompleted(isCompleted)
            .build();

        entry.setValue(newProgress);
      }
    }
  }

  private void initialize() {
    achievements.addAll(
        List.of(
            Achievement.newBuilder()
                .setId(1)
                .setNameId(1) // "7 days in a row"
                .build(),

            Achievement.newBuilder()
                .setId(2)
                .setNameId(2) // "30 days in a row"
                .build(),

            Achievement.newBuilder()
                .setId(3)
                .setNameId(3) // "Multiple events in a day"
                .build()
        )
    );

    achievementProgressMap.putAll(
        Map.of(1, AchievementProgress.newBuilder()
                .setCompleted(false)
                .setProgress(60)
                .build(),

            2, AchievementProgress.newBuilder()
                .setCompleted(true)
                .setProgress(100)
                .build(),

            3, AchievementProgress.newBuilder()
                .setCompleted(false)
                .setProgress(0)
                .build()
        )
    );
  }
}