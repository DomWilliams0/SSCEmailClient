package dxw405.gui.panels;

import dxw405.Email;
import dxw405.Mailbox;
import dxw405.gui.TextFieldPlaceholder;
import dxw405.gui.workers.RuleApplierWorker;

import javax.mail.search.BodyTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RulesPanel extends JPanel
{
	private static final EmptyBorder BORDER = new EmptyBorder(2, 2, 2, 2);

	private Mailbox mailbox;
	private JPanel ruleList, controlPanel;
	private Map<Rule, RulePanel> rules;

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

		addPlaceholder();
		addRuleControl();
	}

	private void addRule(String flag, String keyword)
	{
		Rule r = new Rule(flag, keyword);

		RulePanel rulePanel = createRulePanel(r);
		addToList(rulePanel);

		rules.put(r, rulePanel);

		rulePanel.repaint();
	}

	private void addPlaceholder()
	{
		addRule("", "");
	}

	private void addRuleControl()
	{
		ActionListener addRemoveListener = e -> {
			boolean remove = e.getActionCommand().equals("-");

			if (remove)
			{
				// empty
				if (rules.isEmpty())
					return;

				int compIndex = rules.size() - 1;

				Rule rule = ((RulePanel) ruleList.getComponent(compIndex)).rule;
				rules.remove(rule);

				ruleList.remove(compIndex);
				ruleList.revalidate();
			} else
			{
				// remove control
				ruleList.remove(rules.size());

				// re-add with new placeholder
				addPlaceholder();
				addToList(controlPanel);

				revalidate();
			}

		};

		controlPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 10;
		c.ipady = 2;

		// +/-
		JButton plus = new JButton("+");
		plus.setActionCommand("+");
		plus.addActionListener(addRemoveListener);
		controlPanel.add(plus, c);

		JButton minus = new JButton("-");
		minus.setActionCommand("-");
		minus.addActionListener(addRemoveListener);
		controlPanel.add(minus, c);

		// apply button
		JButton apply = new JButton("Apply");
		apply.addActionListener(e -> {
			RuleApplierWorker worker = new RuleApplierWorker(mailbox, gatherRules());
			worker.setToggleComponent(apply);
			worker.run(this);
		});
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		c.insets.top = c.insets.bottom = 5;
		controlPanel.add(apply, c);

		addToList(controlPanel);
	}

	private Set<Rule> gatherRules()
	{
		Set<Map.Entry<Rule, RulePanel>> entries = rules.entrySet();
		Set<Rule> rules = new HashSet<>();

		for (Map.Entry<Rule, RulePanel> entry : entries)
		{
			entry.getValue().updateRule();
			rules.add(entry.getKey());
		}

		return rules;
	}

	private RulePanel createRulePanel(Rule rule)
	{
		RulePanel rulePanel = new RulePanel(new GridLayout(1, 3), rule);
		rulePanel.setBorder(BORDER);

		// flag
		JTextField flag = new TextFieldPlaceholder("Flag Name");
		rulePanel.setFlagField(flag);

		// colon separator
		JLabel sep = new JLabel(":", SwingConstants.CENTER);

		// keyword
		JTextField keyword = new TextFieldPlaceholder("Keywords");
		rulePanel.setKeywordField(keyword);

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

	public class Rule
	{
		private String flag;
		private String keyword;

		public Rule(String flag, String keyword)
		{
			this.flag = flag;
			this.keyword = keyword;
		}

		public boolean isValid()
		{
			return flag != null && !flag.isEmpty() && keyword != null && !keyword.isEmpty();
		}

		@Override
		public String toString()
		{
			return "Rule{" +
					"flag='" + flag + '\'' +
					", keyword='" + keyword + '\'' +
					'}';
		}

		public boolean satisfiedBy(Email email)
		{
			SearchTerm st = new OrTerm(new SubjectTerm(keyword), new BodyTerm(keyword));
			return st.match(email.getMailboxReference());
		}

		public String getFlag()
		{
			return flag;
		}
	}

	private class RulePanel extends JPanel
	{
		private Rule rule;
		private JTextField flagField;
		private JTextField keywordField;

		public RulePanel(LayoutManager layout, Rule rule)
		{
			super(layout);
			this.rule = rule;
			this.flagField = null;
			this.keywordField = null;
		}


		public void setFlagField(JTextField flagField)
		{
			this.flagField = flagField;
			this.flagField.setText(rule.flag);
		}


		public void setKeywordField(JTextField keywordField)
		{
			this.keywordField = keywordField;
			this.keywordField.setText(rule.keyword);
		}

		public void updateRule()
		{
			rule.flag = flagField.getText();
			rule.keyword = keywordField.getText();
		}
	}


}

