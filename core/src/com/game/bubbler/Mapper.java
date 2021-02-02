package com.game.bubbler;

import com.badlogic.ashley.core.ComponentMapper;

public class Mapper {
	public static final ComponentMapper<LabelComponent> labels = ComponentMapper.getFor(LabelComponent.class);
	public static final ComponentMapper<ScreenTextComponent> text = ComponentMapper.getFor(ScreenTextComponent.class);
}
