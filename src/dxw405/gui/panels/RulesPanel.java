package dxw405.gui.panels;

import dxw405.Mailbox;
import dxw405.gui.TextFieldPlaceholder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class RulesPanel extends JPanel
{
	private static final EmptyBorder BORDER = new EmptyBorder(2, 2, 2, 2);

	private Mailbox mailbox;
	private JPanel ruleList, controlPanel;
	private Map<Rule, JPanel> rules;

	private GridBagConstraints constraints;

	public RulesPanel(Mailbox mailbox)
	{
		this.mailbox = mailbox;
		this.rules = new LinkedHashMap<>();

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
		JScrollPane scrollPane = new JScrollPane(ruleContainer);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
		add(scrollPane);

		// default rule
		addRule("Spam", "lucky winner");

		addRuleSpace();
		addRuleControl();
	}

	private void addRule(String flag, String keyword)
	{
		Rule r = new Rule(flag, keyword);

		JPanel rulePanel = createRulePanel(r);
		addToList(rulePanel);

		if (r.isValid())
			rules.put(r, rulePanel);

		rulePanel.repaint();
	}

	private void addRuleSpace()
	{
		addRule(null, null);
	}

	private void addRuleControl()
	{
		ActionListener addRemoveListener = e -> {
			boolean remove = e.getActionCommand().equals("-");
			int count = ruleList.getComponentCount();

			if (remove)
			{
				count -= 2; // - 2 for +/- and placeholder

				// empty
				if (count <= 0)
					return;

				int compIndex = count - 1;

				Rule rule = ((RulePanel) ruleList.getComponent(compIndex)).rule;
				rules.remove(rule);

				ruleList.remove(compIndex);
				ruleList.revalidate();
			} else
			{
				// remove control and re-add
				ruleList.remove(count - 1);

				addRule("", "");

				addToList(controlPanel);

				revalidate();
			}

		};

		controlPanel = new JPanel();

		JButton plus = new JButton("+");
		plus.setActionCommand("+");
		plus.addActionListener(addRemoveListener);
		controlPanel.add(plus);

		JButton minus = new JButton("-");
		minus.setActionCommand("-");
		minus.addActionListener(addRemoveListener);
		controlPanel.add(minus);

		addToList(controlPanel);
	}

	private JPanel createRulePanel(Rule rule)
	{
		JPanel rulePanel = new RulePanel(new GridLayout(1, 3), rule);
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

	/**
	 * Helper method to add the given component to the end of the rules list
	 *
	 * @param component The component to add
	 */
	private void addToList(Component component)
	{
		ruleList.add(component, constraints);
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

		@Override
		public String toString()
		{
			return "Rule{" +
					"flag='" + flag + '\'' +
					", keyword='" + keyword + '\'' +
					'}';
		}
	}

	private class RulePanel extends JPanel
	{
		private Rule rule;

		public RulePanel(LayoutManager layout, Rule rule)
		{
			super(layout);
			this.rule = rule;
		}
	}


}

