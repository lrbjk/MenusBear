package app;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
public class EmojiManager {
    private ImageView emojiView;
    private Pane parentPane;

    public EmojiManager(Pane parentPane) {
        this.parentPane = parentPane;
        emojiView = new ImageView();
        emojiView.setVisible(false); // 初始状态不可见
        parentPane.getChildren().add(emojiView);
    }
    

    public void showEmoji(String imagePath, double x, double y, double duration) {
        Image emojiImage = new Image(this.getClass().getResourceAsStream(imagePath));
        emojiView.setImage(emojiImage);
        emojiView.setFitWidth(100); // 设置表情的宽度
        emojiView.setFitHeight(100); // 设置表情的高度
        emojiView.setLayoutX(x);
        emojiView.setLayoutY(y);
        emojiView.setVisible(true);
        emojiView.toFront(); // 确保表情包在最前面
        // 在指定时间后隐藏表情
        new Timeline(new KeyFrame(Duration.seconds(duration), ae -> {
            emojiView.setVisible(false);
        })).play();
    }
}
