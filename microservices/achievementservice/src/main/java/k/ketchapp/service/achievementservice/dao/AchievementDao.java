package k.ketchapp.service.achievementservice.dao;

import java.util.List;
import k.ketchapp.proto.Achievement;
import k.ketchapp.proto.AchievementProgress;

public class AchievementDao {

  public List<Achievement> getAchievements() {
    return List.of(
        Achievement.newBuilder()
            .setId(1)
            .setName("7 days in a row")
            .build(),

        Achievement.newBuilder()
            .setId(2)
            .setName("30 days in a row")
            .build(),

        Achievement.newBuilder()
            .setId(3)
            .setName("Multiple events in a day")
            .build()
    );
  }

  public AchievementProgress getAchievementProgress(Achievement achievement) {
    return switch (achievement.getId()) {
      case 1 -> AchievementProgress.newBuilder()
          .setCompleted(false)
          .setProgress(60)
          .build();

      case 2 -> AchievementProgress.newBuilder()
          .setCompleted(true)
          .setProgress(100)
          .build();

      case 3 -> AchievementProgress.newBuilder()
          .setCompleted(false)
          .setProgress(0)
          .build();

      default -> null;
    };
  }

}
