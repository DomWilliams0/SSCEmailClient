package dxw405.gui;

import javax.swing.*;
import java.awt.*;

public class TextFieldPlaceholder extends JTextField
{
	private String placeholder;

	public TextFieldPlaceholder(String placeholder) {this.placeholder = placeholder;}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (getText().length() == 0)
		{
			int h = getHeight();
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Insets ins = getInsets();
			FontMetrics fm = g.getFontMetrics();
			int c0 = getBackground().getRGB();
			int c1 = getForeground().getRGB();
			int m = 0xfefefefe;
			int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
			g.setColor(new Color(c2, true));
			g.drawString(placeholder, ins.left, h / 2 + fm.getAscent() / 2 - 2);
		}
	}
}
