package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Command;

public class UndoBeginReviseOrderCommand extends OrderCommand {

	protected UndoBeginReviseOrderCommand() {
	}

	public UndoBeginReviseOrderCommand(long orderId) {
		super(orderId);
	}
}
