package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

public record UndoBeginCancelTicketCommand(long restaurantId, long orderId) implements Command{

}
