package org.wilson.world.novel;

import java.util.List;

import org.wilson.world.manager.NovelTicketManager;
import org.wilson.world.model.Alert;
import org.wilson.world.model.NovelTicket;
import org.wilson.world.monitor.MonitorParticipant;

public class NovelTicketMonitor implements MonitorParticipant {
	private Alert alert;
	
	public NovelTicketMonitor() {
		this.alert = new Alert();
		this.alert.id = "Novel tickets found";
		this.alert.message = "Please resolve the novel tickets as soon as possible.";
		this.alert.canAck = true;
		this.alert.url = "novel_ticket_list.jsp";
	}
	
	@Override
	public boolean isOK() {
		List<NovelTicket> tickets = NovelTicketManager.getInstance().getNovelTickets();
		return tickets.isEmpty();
	}

	@Override
	public Alert getAlert() {
		return this.alert;
	}

}
