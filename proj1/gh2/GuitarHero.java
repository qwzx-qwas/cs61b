package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[37];

        // 初始化每个音符对应的 GuitarString（一根弦）
        for (int i = 0; i < 37; i++) {
            double frequency = 440 * Math.pow(2, (i - 24) / 12.0);
            strings[i] = new GuitarString(frequency);
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped(); // 读取按键字母
                int index = keyboard.indexOf(key); // 查找按键对应的位置
                if (index != -1) {
                    strings[index].pluck();
                }
            }

            double sample = 0.0;

            // 叠加所有弦的样本
            for (int i = 0; i < 37; i++) {
                sample += strings[i].sample();
            }

            // 播放合成样本
            StdAudio.play(sample);

            // 每根弦推进一次时间，模拟衰减
            for (int i = 0; i < 37; i++) {
                strings[i].tic();
            }
        }
    }
}

