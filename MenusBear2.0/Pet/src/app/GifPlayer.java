package app;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import java.util.Random;

public class GifPlayer {
    private ImageView imageView;
    private String[] gifPaths; // 存储GIF路径的数组
    private Random random;

    public GifPlayer(ImageView imageView, String[] gifPaths) {
        this.imageView = imageView;
        this.gifPaths = gifPaths;
        this.random = new Random();
    }

    public void playRandomGif(double duration) {
        // 从gifPaths数组中随机选择一个GIF路径
        String selectedGifPath = gifPaths[random.nextInt(gifPaths.length)];
        Image gif = new Image(getClass().getResourceAsStream(selectedGifPath));
        imageView.setImage(gif);

        // 在指定时间后可以选择是否需要清除或回到某个特定状态
        new Timeline(new KeyFrame(Duration.seconds(duration), event -> {
            // 如果需要在GIF播放完后做些操作，如隐藏imageView或切换回其他状态，可以在这里添加
        })).play();
    }
}


