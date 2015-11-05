package dxw405.gui.workers;

import dxw405.Mailbox;
import dxw405.gui.panels.RulesPanel;

import java.util.Set;

public class RuleApplierWorker extends Worker
{
	private Mailbox mailbox;
	private Set<RulesPanel.Rule> rules;

	public RuleApplierWorker(Mailbox mailbox, Set<RulesPanel.Rule> rules) {this.mailbox = mailbox;
		this.rules = rules;
	}

	@Override
	protected void work(OptionalProgressMonitor monitor)
	{
		mailbox.applyRules(rules, monitor);
	}
}
