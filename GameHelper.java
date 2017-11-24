package kechengsheji;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameHelper extends Pane {
	// 使用两个arrayList 分别管理子弹和敌人
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<Bullet> bullets = new ArrayList<>();

	private ImageView backGround;// 游戏背景
	private Label label;// 玩家状况信息栏，包括生命值，分数，子弹数量
	private int score;// 玩家得分

	private Timeline timeline;// 游戏时间线
	private int creatEnemyTime;// 创建敌人的时间间隔，每次创建敌人后再随机生成
	private int enemyAppearInterval = 200;// 初始默认敌人间隔
	private int enemyShootInterval = 150;// 默认敌人射击间隔
	private int creatBulletOfEnemy = 0;// 距离上一次射击时间，每次射击后置为0

	private Sound backgroundMusic;// 背景音乐设置
	private Player player;// 玩家
	private Main main;// 控制游戏流程，在开始游戏，结束游戏，重新开始游戏之间来回切换
	private Stage stage;// 游戏界面

	public GameHelper(Main main, Stage stage, int width, int height) {
		this.main = main;
		this.stage = stage;

		setWidth(width);
		setHeight(height);

		backgroundSetting(width, height);// 放置背景画面
		soundSetting();// 设置音乐
		playerSetting();// 设置玩家
		labelSetting();// 设置游戏状态栏

		// 设置游戏时间线并且开始游戏
		System.out.println("Game Start!");
		timeline = new Timeline(new KeyFrame(Duration.millis(18), e -> gameStart()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	// 背景画面设置
	private void backgroundSetting(int width, int height) {
		backGround = new ImageView("background4.jpg");
		backGround.setFitHeight(height);
		backGround.setFitWidth(width);
		getChildren().add(backGround);
	}

	// 声音设置
	private void soundSetting() {
		backgroundMusic = new Sound();
	}

	// 玩家设置
	private void playerSetting() {
		player = new Player(this);
		getChildren().add(player);
		player.setY(getHeight() - player.getFitHeight());
		player.setX(getWidth() / 3 + 40);

		player.MoveSetting();// 玩家移动设置
		player.ShootAndSoundSetting();// 玩家射击设置
	}

	// 玩家状态栏设置
	private void labelSetting() {
		getChildren().add(label = new Label());
		labelChange();
		Font lbFont = Font.font("Times New Roman", FontWeight.BOLD, 15);
		label.setFont(lbFont);
	}

	// 游戏主流程
	// 控制敌人的出现，敌人的射击，子弹和子弹，子弹和玩家和敌人之间撞击的判断
	private void gameStart() {
		// 如果玩家存活，则继续游戏流程
		// 否则直接停止时间线，进入游戏结果画面
		if (player.isAlive()) {
			// 判断是否需要进行清场攻击
			if (player.isClear())
				player.clearPane();
			// 根据时间间隔创建敌人
			if (creatEnemyTime == enemyAppearInterval) {
				Enemy enemy = new Enemy(this);
				getChildren().add(enemy);
				enemies.add(enemy);
				enemy.setX(((Math.random() * (getWidth() - enemy.getFitWidth()))));
				creatEnemyTime = 0;
				enemyAppearInterval = (int) (Math.random() * 300);// 随机生成创建敌人的时间间隔（0～5秒）
			} else
				creatEnemyTime++;// 未到创建敌人的时间，继续累加

			// 对子弹的修改：包括移动，判断是否离开界面
			// 检查子弹是否离开游戏界面。如果是，则将子弹从管理子弹数组 移除
			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).bulletMove();// 移动子弹
				// 检查子弹是否存在
				if (isExistOfBullet(i)) {
					i--;
					continue;
				}

				// 判断子弹和子弹之间有没有发生碰撞
				// 有，则移除子弹
				boolean flagOfbulletCollision = false;// 用于标志是否发生碰撞
				for (int j = 0; j < bullets.size() && bullets.get(i) != bullets.get(j); j++) {
					if (collision(bullets.get(i), bullets.get(j))) {
						i--;
						flagOfbulletCollision = true;
						break;
					}
				}
				if (flagOfbulletCollision)
					continue;

				// 判断玩家和子弹之间有没有碰撞
				if (collision(bullets.get(i), player)) {
					i--;
					continue;
				}

				// 判断敌人和子弹之间有没有碰撞
				for (int j = 0; j < enemies.size(); j++)
					if (collision(bullets.get(i), enemies.get(j))) {
						j--;
						i--;
						break;
					}
			}

			// 对敌人的修改：包括移动，碰撞判断（和子弹或者玩家）
			for (int i = 0; i < enemies.size(); i++) {
				enemies.get(i).enemyMove();// 敌人移动
				enemyShootSetting(enemies.get(i));// 敌人射击
				// 检查敌人是否离开界面，是则将敌人移除敌人数组
				if (isExistOfEnemy(i)) {
					i--;
					continue;
				}
				// 判断敌人和玩家有没有碰撞，有则玩家生命值-1，敌人消失
				if (collision(player, enemies.get(i))) {
					i--;
					player.getHurt();
					player.fadeEffcet();
					continue;
				}
			}
		}

		// 玩家死亡，游戏结束，更换到结果画面
		else {
			timeline.pause();
			main.resultSceneSetting(stage, this);
			backgroundMusic.mp.pause();
		}
	}

	// 敌人射击设置，随机设置一个射击的间隔时间，并不断计时
	// 达到间隔时间就射击，并将计时置为0
	private void enemyShootSetting(Enemy enemy) {
		// create enemy's bullet
		if (creatBulletOfEnemy == enemyShootInterval) {
			enemy.enemyShoot();
			creatBulletOfEnemy = 0;
			enemyShootInterval = 150;
		} else
			creatBulletOfEnemy++;
	}

	// 判断子弹是否还在游戏界面内，否则移除出数组
	private boolean isExistOfBullet(int index) {
		if (bullets.get(index).isOutOfBound()) {
			bullets.remove(bullets.get(index));
			return true;
		}
		return false;
	}

	// 判断敌人是否还在游戏界面内，否则移除出数组
	private boolean isExistOfEnemy(int index) {
		if (enemies.get(index).isOutOfBound()) {
			enemies.remove(enemies.get(index));
			return true;
		}
		return false;
	}

	/*
	 * 碰撞判断使用坐标重叠法 根据子弹和另一个物体计算出不发生碰撞的区间范围，得出一个矩形的范围 如果物体处于改矩形范围内，则判断为碰撞
	 */
	private boolean collision(Bullet bullet, ImageView enemyOrPlayer) {
		// 计算判断为碰撞的矩形范围
		double xMin = bullet.getX() - enemyOrPlayer.getFitWidth() / 2;
		double xMax = bullet.getX() + bullet.getWidth() + enemyOrPlayer.getFitWidth() / 2;
		double yMin = bullet.getY() - enemyOrPlayer.getFitHeight() / 2;
		double yMax = bullet.getY() + bullet.getHeight() - enemyOrPlayer.getFitHeight() / 2;

		// 计算中心坐标作为判断碰撞的点
		double enemyCentreX = enemyOrPlayer.getX() + enemyOrPlayer.getFitWidth() / 2;
		double enemyCentreY = enemyOrPlayer.getY() + enemyOrPlayer.getFitHeight() / 2;

		// 中心点在碰撞范围内
		if (enemyCentreX >= xMin && enemyCentreX <= xMax && enemyCentreY >= yMin && enemyCentreY <= yMax) {
			// 如果是玩家的射击且是激光射击，则激光不消失，敌人消失
			// 发出射击中的音效，得分+10
			if (!bullet.isEnemyShoot() && enemyOrPlayer != player && bullet.isLineShoot) {
				enemies.remove(enemyOrPlayer);
				getChildren().remove(enemyOrPlayer);
				backgroundMusic.mBulletSound();
				score += 10;
				// 如果是玩家的射击且不是激光攻击
			} else if (!bullet.isEnemyShoot() && enemyOrPlayer != player) {
				bullets.remove(bullet);
				getChildren().remove(bullet);

				// 如果敌人生命值为零，则移除，否则，生命值-1
				Enemy enemy = (Enemy) enemyOrPlayer;
				if (!enemy.isAlive()) {
					enemies.remove(enemyOrPlayer);
					getChildren().remove(enemyOrPlayer);
					score += 10;
					System.out.println("kill!");
					backgroundMusic.mBulletSound();
				} else
					enemy.getHurt();
				// 如果是敌人的射击，子弹消失，玩家生命值-1，发出射击音效，产生玩家被射中的效果
			} else if (bullet.isEnemyShoot() && enemyOrPlayer == player && player.isAlive()) {
				bullets.remove(bullet);
				getChildren().remove(bullet);
				player.getHurt();
				backgroundMusic.hitMe();
				player.fadeEffcet();
				System.out.println("hit!lost a life!");
			}
			// 更新游戏状态栏
			labelChange();
			return true;
		}
		return false;
	}

	// 判断敌人和玩家的碰撞
	private boolean collision(ImageView player, ImageView enemy) {
		double xMin = player.getX() - enemy.getFitWidth() / 2;
		double xMax = player.getX() + player.getFitWidth() + enemy.getFitWidth() / 2;
		double yMin = player.getY() - enemy.getFitHeight() / 2;
		double yMax = player.getY() + player.getFitHeight() - enemy.getFitHeight() / 2;

		double enemyCentreX = enemy.getX() + enemy.getFitWidth() / 2;
		double enemyCentreY = enemy.getY() + enemy.getFitHeight() / 2;

		if (enemyCentreX >= xMin && enemyCentreX <= xMax && enemyCentreY >= yMin && enemyCentreY <= yMax) { // 刘德旺
			Enemy enemy2 = (Enemy) enemy;
			if (!enemy2.isAlive()) {
				enemies.remove(enemy);
				getChildren().remove(enemy);

				backgroundMusic.mBulletSound();
				System.out.println("crash!");
				labelChange();
			} else
				enemy2.getHurt();
			return true;
		}
		return false;
	}

	// 判断子弹和子弹的碰撞
	private boolean collision(Bullet bullet1, Bullet bullet2) {
		double xMin = bullet1.getX() - bullet2.getWidth() / 2;
		double xMax = bullet1.getX() + bullet1.getWidth() + bullet2.getWidth() / 2;
		double yMin = bullet1.getY() - bullet2.getHeight() / 2;
		double yMax = bullet1.getY() + bullet1.getHeight() - bullet2.getHeight() / 2;

		double enemyCentreX = bullet2.getX() + bullet2.getWidth() / 2;
		double enemyCentreY = bullet2.getY() + bullet2.getHeight() / 2;

		if (enemyCentreX >= xMin && enemyCentreX <= xMax && enemyCentreY >= yMin && enemyCentreY <= yMax) {
			// 如果两者都不是激光攻击，移除两者，否则，留下激光攻击
			if (!bullet1.isLineShoot && !bullet2.isLineShoot) {
				bullets.remove(bullet1);
				getChildren().remove(bullet1);

				bullets.remove(bullet2);
				getChildren().remove(bullet2);
				backgroundMusic.bulletToBullet();
				return true;
			} else if (bullet1.isLineShoot) {
				bullets.remove(bullet2);
				getChildren().remove(bullet2);
				backgroundMusic.bulletToBullet();
				return true;
			} else if (bullet2.isLineShoot) {
				bullets.remove(bullet1);
				getChildren().remove(bullet1);
				backgroundMusic.bulletToBullet();
				return true;
			}
		}
		return false;
	}

	// 更新游戏状态栏
	public void labelChange() {
		label.setText("Score:" + score + "\nLife:" + player.getLife() + "/10\nsuperShoot: " + player.getThreeShoot()
				+ "/20\nLineShoot:" + player.getLineShoot() + "/20\nclearPane:" + player.getClearShoot() + "/5\nSound:"
				+ backgroundMusic.display());
	}

	public ImageView getPlayer() {
		return player;
	}

	public int getScore() {
		return score;
	}

	public Sound getSound() {
		return backgroundMusic;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}

}
