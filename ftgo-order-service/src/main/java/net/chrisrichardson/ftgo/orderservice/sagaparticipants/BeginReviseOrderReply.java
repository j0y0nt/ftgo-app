package net.chrisrichardson.ftgo.orderservice.sagaparticipants;

import net.chrisrichardson.ftgo.common.Money;

public record BeginReviseOrderReply(Money revisedOrderTotal) {

}
