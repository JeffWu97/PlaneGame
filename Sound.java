package kechengsheji;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Sound extends MediaView {
	MediaPlayer mp;
	boolean play = true;

	public Sound() {
		String music = GameHelper.class.getResource("/music.mp3").toString();

		mp = new MediaPlayer(new Media(music));
		mp.setAutoPlay(true);
		mp.setCycleCount(20);
	}

	// 控制音效的判断
	public String display() {
		String result = "ON";
		if (!play)
			result = "OFF";
		return result;
	}

	// 子弹击中的音效
	public void mBulletSound() {
		if (play) {
			String hitenemysound = GameHelper.class.getResource("/achievement.wav").toString();
			MediaPlayer mp1 = new MediaPlayer(new Media(hitenemysound));
			mp1.setAutoPlay(true);
			mp1.play();
		}
	}

	// 玩家受到攻击的音效
	public void hitMe() {
		if (play) {
			String hitmesound = GameHelper.class.getResource("/game_over.wav").toString();
			MediaPlayer mp2 = new MediaPlayer(new Media(hitmesound));
			mp2.setAutoPlay(true);
			mp2.play();
		}
	}

	// 子弹与子弹撞击的音效
	public void bulletToBullet() {
		if (play) {
			String btb = GameHelper.class.getResource("/use_bomb.wav").toString();
			MediaPlayer mp4 = new MediaPlayer(new Media(btb));
			mp4.setAutoPlay(true);
			mp4.play();
		}
	}

}
