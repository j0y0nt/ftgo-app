package net.chrisrichardson.ftgo.accountservice.api;

import io.eventuate.tram.commands.common.Command;
import net.chrisrichardson.ftgo.common.Money;

public record ReviseAuthorization(long consumerId, Long orderId, Money orderTotal) implements Command{

}
