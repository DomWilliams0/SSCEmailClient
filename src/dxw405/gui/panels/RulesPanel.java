package dxw405.gui.panels;

import dxw405.Mailbox;
import dxw405.gui.TextFieldPlaceholder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RulesPanel extends JPanel
{
	private static final EmptyBorder BORDER = new EmptyBorder(2, 2, 2, 2);

	private Mailbox mailbox;
	private JPanel ruleList;
	private List<Rule> rules;

	private GridBagConstraints constraints;

	public RulesPanel(Mailbox mailbox)
	{
		this.mailbox = mailbox;
		this.rules = new ArrayList<>();

		// create list panel
		ruleList = new JPanel(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.weightx = 1;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// put rule list in a container anchored to the top
		JPanel ruleContainer = new JPanel(new BorderLayout());
		ruleContainer.add(ruleList, BorderLayout.NORTH);
		add(new JScrollPane(ruleContainer));

		// default rule
		addRule("Spam", "lucky winner");
		addRuleSpace();
	}

	private void addRule(String flag, String keyword)
	{
		Rule r = new Rule(flag, keyword);
		if (r.isValid())
			rules.add(r);

		JPanel rulePanel = createRulePanel(r);
		ruleList.add(rulePanel, constraints);

		rulePanel.repaint();
	}

	private void addRuleSpace()
	{
		addRule("", "");
	}

	private JPanel createRulePanel(Rule rule)
	{
		JPanel rulePanel = new JPanel(new GridLayout(1, 3));
		rulePanel.setBorder(BORDER);

		JTextField flag = new TextFieldPlaceholder("Flag Name");
		flag.setText(rule.flag);

		JLabel sep = new JLabel(":", SwingConstants.CENTER);

		JTextField keyword = new TextFieldPlaceholder("Keywords");
		keyword.setText(rule.keyword);

		GridBagConstraints c = new GridBagConstraints();
		c.insets.set(5, 5, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridy = 0;

		rulePanel.add(flag, c);
		rulePanel.add(sep, c);
		rulePanel.add(keyword, c);

		return rulePanel;
	}

	private class Rule
	{
		String flag;
		String keyword;

		public Rule(String flag, String keyword)
		{
			this.flag = flag;
			this.keyword = keyword;
		}

		public boolean isValid()
		{
			return flag != null && keyword != null;
		}
	}

}

