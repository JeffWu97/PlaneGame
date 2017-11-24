package kechengsheji;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {
	// 创建stage 设置宽度，高度，坐标位置
	private int width = 520;
	private int height = 650;
	private double x;
	private double y;
	Pane mainPane;
	Pane resultPane;

	public void start(Stage primaryStage) {
		mainSceneSetting(primaryStage);
		primaryStage.setTitle("暴走大作战");
		primaryStage.show();
		x = primaryStage.getX();
		y = primaryStage.getY();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	// 游戏界面
	public void gameSceneSetting(Main main, Stage primaryStage) {
		primaryStage.setX(400);
		primaryStage.setY(30);

		GameHelper gameHelper = new GameHelper(this, primaryStage, width, height);
		primaryStage.setScene(new Scene(gameHelper, width, height));
		gameHelper.getPlayer().requestFocus();
	}

	// 游戏说明界面
	public void mainSceneSetting(Stage primaryStage) {
		Pane pane = new Pane();
		primaryStage.setScene(new Scene(pane, 200, 175));

		Label label = new Label("游戏说明：\nWSAD控制上下左右\nJ为普通子弹，K为连击子弹\nB为激光束攻击\nO为清场技能\n" + "空格键开关音效\n生命值为10，每击败一个敌人+10");
		Button start = new Button("开始游戏");
		Button end = new Button("退出游戏");

		start.setLayoutY(125);
		start.setLayoutX(15);
		end.setLayoutY(125);
		end.setLayoutX(pane.getWidth() - 90);

		pane.getChildren().addAll(label, start, end);
		// 绑定按钮
		start.setOnAction(e -> {
			gameSceneSetting(this, primaryStage);
			mainPane = null;
		});
		end.setOnAction(e -> {
			primaryStage.close();
		});
	}

	// 游戏结束界面
	public void resultSceneSetting(Stage primaryStage, GameHelper gameHelper) {
		primaryStage.setX(x);
		primaryStage.setY(y);

		Pane pane = new Pane();
		primaryStage.setScene(new Scene(pane, 200, 150));

		Label label = new Label("得分：" + gameHelper.getScore() + "\n\n胜败乃兵家常事\n请大侠重新再来");
		Font lbFont = Font.font("Times New Roman", FontWeight.BOLD, 20);
		label.setFont(lbFont);

		Button ReStart = new Button("重新游戏");
		Button quit = new Button("退出游戏");

		ReStart.setLayoutY(100);
		ReStart.setLayoutX(15);
		quit.setLayoutY(100);
		quit.setLayoutX(pane.getWidth() - 90);

		pane.getChildren().addAll(label, ReStart, quit);
		// gameHelper=null;//释放空间

		ReStart.setOnAction(e -> {
			gameSceneSetting(this, primaryStage);
			mainPane = null;
		});

		quit.setOnAction(e -> {
			primaryStage.close();
		});

	}
}
