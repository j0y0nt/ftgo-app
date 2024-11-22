package net.chrisrichardson.ftgo.kitchenservice.api;

import java.util.List;

public record TicketDetails(List<TicketLineItem> lineItems) {

}
