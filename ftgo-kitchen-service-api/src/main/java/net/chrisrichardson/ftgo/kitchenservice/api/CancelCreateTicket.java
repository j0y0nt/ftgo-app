package net.chrisrichardson.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

public record CancelCreateTicket(long ticketId)  implements Command {

}
