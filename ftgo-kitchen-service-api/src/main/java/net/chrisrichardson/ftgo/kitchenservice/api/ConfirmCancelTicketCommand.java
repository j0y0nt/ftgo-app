package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

public record ConfirmCancelTicketCommand(long restaurantId, long orderId)  implements Command{

}
