package kechengsheji;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy extends ImageView {
	private GameHelper pane;

	private Image[] images = { new Image("enemy1.png"), new Image("enemy2.png"), new Image("enemy3.png"),
			new Image("enemy4.png") };// 存储敌人的图片

	private boolean isOutOfBound;// 标志是否超出游戏界面
	private int enemyType;// 敌人的类型
	private double size = 150;// 敌人的默认大小
	private int life;// 敌人的生命，均为enemyType+1

	public Enemy(GameHelper pane) {
		super();
		this.pane = pane;

		enemyType = (int) (Math.random() * 4);// 随机生成敌人的类型
		life = enemyType + 1;// 设置生命值
		setImage(images[enemyType]);// 放置敌人图片素材
		changeEnemySize();// 改变形状大小
	}

	// 修改敌人的大小
	private void changeEnemySize() {
		setPreserveRatio(true);
		if (enemyType == 0)
			size = 30;
		else if (enemyType == 1)
			size = 60;
		else if (enemyType == 2)
			size = 90;

		setFitWidth(size);
		setFitHeight(size);
	}

	public boolean isOutOfBound() {
		return isOutOfBound;
	}

	// 敌人移动
	// 如果没离开游戏界面，则向下走
	// 否则，将敌人移除游戏界面并且修改isOutOfBound为true
	// 并等待gameHelper将敌人移除
	public void enemyMove() {
		if (getY() < pane.getHeight() - getFitHeight()) {
			setY(getY() + 2);
		} else {
			isOutOfBound = true;
			pane.getChildren().remove(this);
		}
	}

	// 敌人射击
	public void enemyShoot() {
		Bullet bullet = new Bullet(1, pane);
		pane.getChildren().add(bullet);
		pane.getBullets().add(bullet);

		bullet.setX(getX() + getFitWidth() / 2);
		bullet.setY(getY() + getFitHeight());
	}

	public int getEnemyType() {
		return enemyType;
	}

	public boolean isAlive() {
		return life > 0;
	}

	public void getHurt() {
		life--;
	}
}
