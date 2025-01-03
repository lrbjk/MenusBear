package app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.Random;

/**
 * GifPlayer 类负责在指定的 ImageView 中随机播放 GIF 动画。
 * 它从提供的 GIF 路径数组中随机选择一个 GIF，并在指定的持续时间后执行回调操作。
 */
public class GifPlayer {
    private ImageView imageView; // 显示 GIF 动画的 ImageView
    private String[] gifPaths;    // 存储 GIF 路径的数组
    private Random random;        // 用于生成随机数的 Random 实例

    /**
     * 构造方法，初始化 GifPlayer。
     *
     * @param imageView 要显示 GIF 动画的 ImageView
     * @param gifPaths  存储 GIF 路径的数组
     */
    public GifPlayer(ImageView imageView, String[] gifPaths) {
        this.imageView = imageView;
        this.gifPaths = gifPaths;
        this.random = new Random();
    }

    /**
     * 播放一个随机选择的 GIF 动画，并在指定的持续时间后执行回调操作。
     *
     * @param duration 动画播放的持续时间（秒）
     */
    public void playRandomGif(double duration) {
        if (gifPaths == null || gifPaths.length == 0) {
            System.out.println("没有可用的 GIF 路径。");
            return;
        }

        // 从 gifPaths 数组中随机选择一个 GIF 路径
        String selectedGifPath = gifPaths[random.nextInt(gifPaths.length)];
        Image gif;
        try {
            gif = new Image(getClass().getResourceAsStream(selectedGifPath));
            if (gif.isError()) {
                throw new IllegalArgumentException("无法加载 GIF: " + selectedGifPath);
            }
        } catch (Exception e) {
            System.err.println("加载 GIF 失败: " + selectedGifPath);
            e.printStackTrace();
            return;
        }

        // 设置 ImageView 显示选中的 GIF
        imageView.setImage(gif);

        // 创建一个 Timeline，在指定时间后执行回调操作
        new Timeline(new KeyFrame(Duration.seconds(duration), event -> {
            // 在这里添加 GIF 播放完成后的操作
            // 例如：隐藏 ImageView、切换回默认图像等
            // imageView.setVisible(false);
            // 或者
            // imageView.setImage(defaultImage);
        })).play();
    }

    /**
     * 获取当前显示的 ImageView。
     *
     * @return 当前的 ImageView
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 设置 ImageView。
     *
     * @param imageView 要设置的 ImageView
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * 获取 GIF 路径数组。
     *
     * @return GIF 路径数组
     */
    public String[] getGifPaths() {
        return gifPaths;
    }

    /**
     * 设置 GIF 路径数组。
     *
     * @param gifPaths 要设置的 GIF 路径数组
     */
    public void setGifPaths(String[] gifPaths) {
        this.gifPaths = gifPaths;
    }

    /**
     * 获取 Random 实例。
     *
     * @return Random 实例
     */
    public Random getRandom() {
        return random;
    }

    /**
     * 设置 Random 实例。
     *
     * @param random 要设置的 Random 实例
     */
    public void setRandom(Random random) {
        this.random = random;
    }
}
