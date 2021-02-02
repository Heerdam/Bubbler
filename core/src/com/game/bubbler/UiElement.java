package com.game.bubbler;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.game.bubbler.MenuScreen.clickStruct;
import com.game.bubbler.Options.RunnableInterface;

public class UiElement {

	public static final Color background = new Color(0.0824f, 0.0824f, 0.0824f, 1);
	public static final Color backgroundBorder = new Color(0.3294f, 0.3294f, 0.3294f, 1);
	public static final Color checked = new Color(0.6078f, 0.6078f, 0.6078f, 1f);
	
	public static float color(float input){
		return input/255;
	}
	
	public static class Kill{
		public Game game;
		public boolean kill;
		public Kill(Game game, boolean kill) {
			this.game = game;
			this.kill = kill;
		}
	}
	
	public static Table createErrorWindow(String input, boolean killApp, Game game){
		Label label = createLabel(input, 18, game);
		label.setWrap(true);
		label.setAlignment(Align.center);

		TextButton exit = UiElement.createButton(150, 75, "Close", 35, true, game);
		exit.setUserObject(new Kill(game, killApp));
		exit.addListener(new ClickListener(){

			@Override
			public void clicked (InputEvent event, float x, float y) {
				Gdx.input.vibrate(50);
				event.getListenerActor().getParent().remove();
				Kill kill = (Kill) event.getListenerActor().getUserObject();
				if((kill.kill) ){
					kill.game.exit = true;
					Gdx.app.exit();
				}
			}
			
		});
		
		Table window = new Table();
		//window.debug();
		window.setSize((int)game.scaleAndroid(600), (int)game.scaleAndroid(400));
		window.setBackground(createDrawble((int)game.scaleAndroid(600), (int)game.scaleAndroid(400), background, 5, backgroundBorder, true));
		window.add(label).width((int)game.scaleAndroid(600)).height((int)game.scaleAndroid(325)).pad((int)game.scaleAndroid(10)).expand().align(Align.center).row();
		window.add(exit).align(Align.bottomRight).pad((int)game.scaleAndroid(10));
		window.setPosition(Gdx.graphics.getWidth()/2 - window.getWidth()/2, Gdx.graphics.getHeight()/2 - window.getHeight()/2);
		return window;
	}

	public static Table createGameUi(Game game){
		Label points = createLabel("Points: " + game.options.getPoints(), (int)game.scaleAndroid(50), game);
		points.setName("points");
		

		int barw = (int)game.scaleAndroid(350);
		int barh = (int)game.scaleAndroid(60);
		
		Slider.SliderStyle barStyle = new Slider.SliderStyle();
		int border = (int)game.scaleAndroid(4);
		int space = (int)game.scaleAndroid(10);
		barStyle.background = createDrawble(barw, barh, new Color(color(30), color(30), color(30), 1), border, backgroundBorder, false);
		barStyle.knob = createDrawble(space, barh - space, Color.BLACK, border, Color.BLACK, false);
		barStyle.knobBefore = createDrawble(space, barh-space, checked, border, backgroundBorder, false);
		
		Slider bar = new Slider(0, 1000, 1, false, barStyle);
		bar.setSize(barw, barh);
		bar.setName("bar");
		bar.setTouchable(Touchable.disabled);
		bar.setValue(1000);
		
		Slider.SliderStyle sideBarStyle = new Slider.SliderStyle();
		sideBarStyle.background = createDrawble(barh, barw, new Color(color(30), color(30), color(30), 1), border, backgroundBorder, false);
		//sideBarStyle.knob = createDrawble(barh - space, space, Color.BLACK, border, Color.BLACK, false);
		sideBarStyle.knobBefore = createDrawble(barh-space, space, Color.YELLOW, border, backgroundBorder, false);
		
		Slider sideBar = new Slider(0, 15, 1, true, sideBarStyle);
		sideBar.setSize(barw, barh);
		sideBar.setName("bar");
		sideBar.setTouchable(Touchable.disabled);
		sideBar.setValue(0);
		sideBar.setName("sideBar");
		
		Table sideBarTable = new Table();
		sideBarTable.add(sideBar).width(barh).height(barw);
		sideBarTable.setName("sideBarTable");

		TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
		style.font = requestFont(Color.LIGHT_GRAY, (int)game.scaleAndroid(40), game);
		style.overFontColor = Color.WHITE;
		style.fontColor = Color.LIGHT_GRAY;
		style.up = createDrawble((int)game.scaleAndroid(135), (int)game.scaleAndroid(55), background, border, backgroundBorder, true);
		
		TextButton menu = new TextButton("menu", style);
		menu.setName("menu");

		TextButton.TextButtonStyle stylePause = new TextButton.TextButtonStyle();
		stylePause.font = style.font;
		stylePause.overFontColor = Color.WHITE;
		stylePause.fontColor = Color.LIGHT_GRAY;
		stylePause.up = style.up;
		stylePause.checkedFontColor = Color.RED;
		stylePause.checkedOverFontColor = Color.RED;
		
		TextButton pause = new TextButton("pause", stylePause);
		pause.setName("pause");

		TextButton plus = new TextButton("+", style);
		plus.setName("plus");

		Table buttons = new Table();
		buttons.setName("buttons");
		buttons.add(plus);
		buttons.add(pause);
		buttons.add(menu);
		
		Table difbar = new Table();
		difbar.setName("difBar");
		difbar.setSize(barw, barh);
		difbar.add(bar).height(barh).width(barw);
		
		Label min = createLabel("-" + game.options.minPoints, (int)game.scaleAndroid(25), game);
		min.setName("min");
		min.setColor(Color.RED);
		Label max = createLabel(game.options.maxPoints + "", (int)game.scaleAndroid(25), game);
		max.setName("max");
		max.setColor(Color.GREEN);
		Table minMax = new Table();
		minMax.setName("minMax");
		minMax.add(max).row();
		minMax.add(min);
		//minMax.debug();
		
		int pad = (int)game.scaleAndroid(10);
		Table table = new Table();
		//table.debugAll();
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		table.setPosition(0, 0);
		table.align(Align.top);

		table.add(points).width((int)game.scaleAndroid(500)).pad(pad, pad, 0, 0).align(Align.topLeft);
		table.add(minMax).align(Align.left).pad(0, pad, 0, 0);
		table.add(difbar).align(Align.bottomRight).expandX().pad(0, pad, 0, pad).row();
		table.add(sideBarTable).padLeft(pad).align(Align.left).expand().colspan(3).row();
		table.add(buttons).expandX().align(Align.center).colspan(3);
		table.layout();
		game.stage.addActor(table);
		return table;	
	}
	
	public static Table createPicker(int width, int height, Array<clickStruct> inputs, MenuScreen screen, Game game){
		
		Table main = new Table();
		main.setSize((int)game.scaleAndroid(width), (int)game.scaleAndroid(height));

		Button.ButtonStyle style = new Button.ButtonStyle();
		int w = (int)game.scaleAndroid(50);
		int radius = (int)game.scaleAndroid(20); 
		int border = (int)game.scaleAndroid(3); 
		
		style.up = createDrawbleCircle(w, w, radius, background, backgroundBorder, border);
		style.over = createDrawbleCircle(w, w, radius, background, Color.LIGHT_GRAY, border);
		style.checked = createDrawbleCircle(w, w, radius, checked, backgroundBorder, border);
		style.checkedOver = createDrawbleCircle(w, w, radius, checked, Color.LIGHT_GRAY, border);
		
		for(clickStruct c : inputs){
			Table table = new Table();
			
			Button button = new Button(style);
			screen.buttons.add(button);
			button.setUserObject(c.run);
			button.addListener(new ClickListener(){
				
				protected RunnableInterface run;
				protected boolean loaded;
				protected Button button;
				
				@Override
				public void clicked (InputEvent event, float x, float y) {
					if(!loaded){
						run = (RunnableInterface) event.getListenerActor().getUserObject();
						button = (Button) event.getListenerActor();
						loaded = true;
					}
					Gdx.input.vibrate(50);
					run.runBoolean(button.isChecked());
				}
				
			});
			
			if(c.down){
				button.toggle();
			}
			
			Label label = createLabel(c.input, (int)game.scaleAndroid(25), game);
			
			int pad1 = (int)game.scaleAndroid(10);
			int pad2 = (int)game.scaleAndroid(5);
			table.add(button).pad(pad1, pad2, pad2, 0).align(Align.left);
			table.add(label).pad(pad1, pad2, pad2, pad1).align(Align.left);
			main.add(table).align(Align.left).row();
		}
				
		//main.background(createDrawble((int)main.getWidth(), (int)main.getHeight(), background, 5, backgroundBorder, true));
		
		game.stage.addActor(main);
		return main;
	}

	public static Label createLabel(String name, int fontSize, Game game){
		Label.LabelStyle style = new Label.LabelStyle();
		//style.background = createDrawble(width, height, Color.DARK_GRAY, 2, Color.WHITE);
		style.font = requestFont(Color.LIGHT_GRAY, fontSize, game);
		
		Label label = new Label(name, style);
		//label.setSize(width, height);
		return label;
	}
	
	public static TextButton createButton(int width, int height, String name, int fontSize, boolean hasBorder, Game game){	
		TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
		style.font = requestFont(Color.LIGHT_GRAY, fontSize, game);
		style.checkedOverFontColor = Color.LIGHT_GRAY;
		style.overFontColor = Color.WHITE;
		style.fontColor = Color.LIGHT_GRAY;
		style.up = createDrawble(width, height, background, 5, backgroundBorder, hasBorder);
		style.disabledFontColor = Color.DARK_GRAY;
		
		TextButton button = new TextButton(name, style);
		//button.setColor(Color.DARK_GRAY);
		button.setSize((int)game.scaleAndroid(width), (int)game.scaleAndroid(height));

		return button;
	}
	
	public static SpriteDrawable createDrawbleCircle(int width, int height, int radius, Color color, Color colorBorder, int border){
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(colorBorder);
		pix.fillCircle(width/2, height/2, radius);
		
		pix.setColor(color);
		pix.fillCircle(width/2, height/2, radius - border);
	
		SpriteDrawable temp = new SpriteDrawable(new Sprite(new Texture(pix)));
		pix.dispose();
		
		return temp;
	}

	public static SpriteDrawable createDrawble(int width, int height, Color color, int border, Color borderColor, boolean hasBorder){
		Pixmap pix = new Pixmap(width, height, Format.RGBA8888);
		pix.setColor(color);
		pix.fill();
		if(hasBorder){
			pix.setColor(borderColor);
			pix.drawRectangle(0, 0, width, height);
			pix.drawRectangle(0 + border, 0 + border, width - 2*border, height - 2*border);
		}
		
		SpriteDrawable temp = new SpriteDrawable(new Sprite(new Texture(pix)));
		pix.dispose();
		
		return temp;
	}
	
	public static BitmapFont requestFont(Color color, int size, Game game){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.color = color;
		parameter.borderWidth = (int)game.scaleAndroid(2);
		parameter.borderStraight = false;
		parameter.borderColor = Color.DARK_GRAY;
		BitmapFont temp = generator.generateFont(parameter);
		generator.dispose();
		return temp;
	}
	
	public static Table constructTableArrow(int width, int height, double value, String text, float lowerLimit, float upperLimit, boolean hasLimits, RunnableInterface run, Label.LabelStyle lStyle, TextButton.TextButtonStyle buttonStyle, Game game){
		Label label = new Label(value + "", lStyle);
		label.setAlignment(Align.center);
		label.pack();
		int size = (int)game.scaleAndroid(50);
		UserObject object = new UserObject(value, label);
		if(hasLimits){
			object.lowerLimit = lowerLimit;
			object.upperLimit = upperLimit;
			object.hasLimits = hasLimits;
		}
		
		object.entity = new Entity();
		game.engine.addEntity(object.entity);
		LabelComponent comp = new LabelComponent();
		comp.object = object;
		object.comp = comp;
		object.run = run;
		
		TextButton up = new TextButton("+", buttonStyle);
		up.setSize(size, size);
		up.setUserObject(object);
		up.addListener(new InputListener(){	

			protected boolean loaded;
			protected Entity entity;
			protected LabelComponent comp;
			
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(!loaded){					
					UserObject object = (UserObject) event.getListenerActor().getUserObject();
					entity = object.entity;
					comp = object.comp;
					loaded = true;
				}
				Gdx.input.vibrate(50);
				comp.object.isUp = true;
				comp.object.firstRun = true;
				comp.object.clickStart = System.currentTimeMillis();
				entity.add(comp);
				return true;
			}
			
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				entity.remove(comp.getClass());
			}	
		});
		
		TextButton down = new TextButton("-", buttonStyle);
		up.setSize(size, size);
		down.setUserObject(object);
		down.addListener(new InputListener(){	

			protected boolean loaded;
			protected Entity entity;
			protected LabelComponent comp;
			
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(!loaded){
					UserObject object = (UserObject) event.getListenerActor().getUserObject();
					entity = object.entity;
					comp = object.comp;
					loaded = true;
				}
				Gdx.input.vibrate(50);
				comp.object.isUp = false;
				comp.object.firstRun = true;
				comp.object.clickStart = System.currentTimeMillis();
				entity.add(comp);
				return true;
			}
			
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				entity.remove(comp.getClass());
			}	
		});
		
		Label textLabel = new Label(text, lStyle);
		
		Table table = new Table();
		//table.setDebug(true);
		table.setSize(width, height);
		//table.pack();
		//table.setBackground(bg);
		
		table.add(up).width(size).height(size).align(Align.center);
		table.add();
		table.row();
		
		table.add(label).width(size).height(size).align(Align.center);
		table.add(textLabel).width(textLabel.getWidth()).align(Align.left);
		table.row();
		
		table.add(down).width(size).height(size).align(Align.center);
		table.add();
		table.row();
		
		return table;
	}
}
