package kechengsheji;

import javafx.animation.FadeTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class Player extends ImageView {
	private GameHelper pane;

	// 子弹的类型及数量
	private int life = 10;
	private int lineShoot = 20;
	private int superShoot = 20;
	private int clearShoot = 5;
	// 给判断是否激活了清场攻击
	private boolean clear = false;

	// 受到攻击后的效果
	private FadeTransition fadeTransition;

	public Player(GameHelper pane) {
		super("player.png");
		this.pane = pane;

		setPreserveRatio(true);
		setFitWidth(50);
		setFitHeight(50);

		// 受到伤害的效果设置
		fadeTransition = new FadeTransition(Duration.millis(300), this);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.setAutoReverse(true);
		fadeTransition.setCycleCount(4);
	}

	// 播放效果
	public void fadeEffcet() {
		fadeTransition.play();
	}

	// 玩家移动设置
	public void MoveSetting() {
		setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.W && getY() > 0)
				setY(getY() - 20);
			else if (e.getCode() == KeyCode.S && getY() + getFitHeight() < pane.getHeight())
				setY(getY() + 20);
			else if (e.getCode() == KeyCode.A && getX() > 0)
				setX(getX() - 20);
			else if (e.getCode() == KeyCode.D && getX() + getFitWidth() < pane.getWidth())
				setX(getX() + 20);
		});
	}

	public void ShootAndSoundSetting() {
		setOnKeyReleased(e -> {
			// 普通射击
			if (e.getCode() == KeyCode.J) {
				Bullet bullet = new Bullet(pane);
				pane.getChildren().add(bullet);
				bullet.setX(getX() + getFitWidth() / 2);
				bullet.setY(getY() - 10);

				pane.getBullets().add(bullet);
				// 连射
			} else if (e.getCode() == KeyCode.K && superShoot != 0)
				supershoot();
			// 控制背景音乐及音效
			else if (e.getCode() == KeyCode.SPACE) {
				Sound sound = pane.getSound();
				if (sound.play) {
					sound.mp.pause();
					sound.play = false;
				} else {
					sound.mp.play();
					sound.play = true;
				}

				pane.labelChange();
				// 清场攻击
			} else if (e.getCode() == KeyCode.O) {
				if (clearShoot > 0) {
					clear = true;
					clearShoot--;
					pane.labelChange();
				}
				// 激光攻击
			} else if (e.getCode() == KeyCode.B) {
				if (lineShoot > 0) {
					Lineshoot lineshoot = new Lineshoot(pane);
					pane.getChildren().add(lineshoot);

					lineshoot.setX(getX() + getFitWidth() / 2);
					lineshoot.setY(getY() - 50);
					pane.getBullets().add(lineshoot);
					lineShoot--;

					pane.labelChange();
				}
			}
		});
	}

	// 连发攻击
	private void supershoot() {
		Bullet bullet1 = new Bullet(pane);
		bullet1.setX(getX() - 60);
		bullet1.setY(getY() - 10);

		Bullet bullet2 = new Bullet(pane);
		bullet2.setX(getX() - 30);
		bullet2.setY(getY() - 10);

		Bullet bullet3 = new Bullet(pane);
		bullet3.setX(getX());
		bullet3.setY(getY() - 10);

		Bullet bullet4 = new Bullet(pane);
		bullet4.setX(getX() + 30);
		bullet4.setY(getY() - 10);

		Bullet bullet5 = new Bullet(pane);
		bullet5.setX(getX() + 60);
		bullet5.setY(getY() - 10);

		Bullet bullet6 = new Bullet(pane);
		bullet6.setX(getX() + 90);
		bullet6.setY(getY() - 10);

		pane.getBullets().add(bullet1);
		pane.getBullets().add(bullet2);
		pane.getBullets().add(bullet3);
		pane.getBullets().add(bullet4);
		pane.getBullets().add(bullet5);
		pane.getBullets().add(bullet6);
		pane.getChildren().addAll(bullet1, bullet2, bullet3, bullet4, bullet5, bullet6);

		superShoot--;
		pane.labelChange();
	}

	// 清场攻击
	public void clearPane() {
		pane.getChildren().removeAll(pane.getEnemies());
		pane.getChildren().removeAll(pane.getBullets());

		pane.getBullets().clear();
		pane.getEnemies().clear();
		clear = false;
	}

	public boolean isClear() {
		return clear;
	}

	public int getLife() {
		return life;
	}

	public void getHurt() {
		life--;
	}

	public boolean isAlive() {
		return life > 0;
	}

	public int getClearShoot() {
		return clearShoot;
	}

	public int getLineShoot() {
		return lineShoot;
	}

	public int getThreeShoot() {
		return superShoot;
	}
}
