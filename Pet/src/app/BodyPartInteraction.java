package app;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
public class BodyPartInteraction {
    private String bodyPartName;
    private int clickCount = 0;
    private EmojiManager emojiManager;
    private ImageView imageView;
    private Pane parentPane;

    public BodyPartInteraction(String bodyPartName, ImageView imageView, Pane parentPane) {
        this.bodyPartName = bodyPartName;
        this.imageView = imageView;
        this.parentPane = parentPane;
        this.emojiManager = new EmojiManager(parentPane);
    }

    public void handleClick(double time) {
        clickCount++;
        int count = clickCount;
        // 在动画播放完成后显示表情包
        new Timeline(new KeyFrame(Duration.seconds(time), ae -> {
            showEmoji(count);
        })).play();
    }

    private void showEmoji(int count) {
        String emojiPath = null;
        if (count == 1) {
            emojiPath = "/lxh/MB3.gif"; // 第一次点击：微笑表情包
        } else if (count == 2) {
            emojiPath = "/lxh/MB4.gif"; // 第二次点击：流汗表情包
        } else if (count == 3) {
            emojiPath ="/lxh/MB6.gif"; // 第三次点击：红温表情包
            // 重置计数器，如果需要循环
            clickCount = 0;
        }

        if (emojiPath != null) {
            // 显示表情包，持续 2 秒
        	double emojiX = imageView.getLayoutX() + 50; // 这里调整表情包横轴
        	double emojiY = imageView.getLayoutY() - 60; // 这里调整表情包纵轴
        	emojiManager.showEmoji(emojiPath, emojiX, emojiY, 2);
            System.out.println(emojiPath);
        }
    }
}
