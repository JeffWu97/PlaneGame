package kechengsheji;

import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Rectangle {
	private GameHelper pane;

	private boolean isOutOfBound;// 判断是否离开游戏界面
	private boolean isEnemyShoot;// 判断是否为敌人的子弹
	public boolean isLineShoot;// 判断是否为激光射击

	// 创建玩家的子弹
	public Bullet(GameHelper pane) {
		super(15, 5);
		this.pane = pane;

		// 修改颜色以及创建模糊效果
		setFill(Color.ORANGE);
		setEffect(new BoxBlur());

	}

	// 创建敌人的子弹
	public Bullet(int i, GameHelper pane) {
		super(10, 10);
		this.pane = pane;

		setFill(Color.RED);
		setEffect(new BoxBlur());

		isEnemyShoot = true;

	}

	// 判断子弹是否离开游戏界面边界
	public boolean isOutOfBound() {
		return isOutOfBound;
	}

	// 判断是否敌人的子弹
	public boolean isEnemyShoot() {
		return isEnemyShoot;
	}

	// 子弹移动
	// 先判断是敌人的子弹还是玩家的子弹
	// 当子弹移出游戏界面后，移除子弹并修改isOutOfBound为true
	public void bulletMove() {
		// 玩家的子弹
		if (!isEnemyShoot) {
			if (getY() > 0)
				setY(getY() - 5);
			else {
				isOutOfBound = true;
				pane.getChildren().remove(this);
			}
			// 敌人的子弹
		} else {
			if (getY() < pane.getHeight())
				setY(getY() + 4);
			else {
				isOutOfBound = true;
				pane.getChildren().remove(this);
			}
		}
	}
}
