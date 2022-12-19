package k.ketchapp.service.achievementservice.dao;

import java.util.List;
import k.ketchapp.proto.Achievement;
import k.ketchapp.proto.AchievementProgress;
import k.ketchapp.proto.Event;

public class AchievementDao {

  public List<Achievement> getAchievements() {
    return List.of(
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

  public void updateAchievement(Event event) {
    // TODO: implement something
  }

}