package net.chrisrichardson.ftgo.orderservice.sagas.reviseorder;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import jakarta.annotation.PostConstruct;
import net.chrisrichardson.ftgo.accountservice.api.ReviseAuthorization;
import net.chrisrichardson.ftgo.kitchenservice.api.BeginReviseTicketCommand;
import net.chrisrichardson.ftgo.kitchenservice.api.ConfirmReviseTicketCommand;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginReviseOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginReviseOrderReply;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConfirmReviseOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.UndoBeginReviseOrderCommand;
import net.chrisrichardson.ftgo.kitchenservice.api.KitchenServiceChannels;
import net.chrisrichardson.ftgo.kitchenservice.api.UndoBeginReviseTicketCommand;

public class ReviseOrderSaga implements SimpleSaga<ReviseOrderSagaData> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private SagaDefinition<ReviseOrderSagaData> sagaDefinition;

	@PostConstruct
	public void initializeSagaDefinition() {
		sagaDefinition = step()
				.invokeParticipant(this::beginReviseOrder)
				.onReply(BeginReviseOrderReply.class, this::handleBeginReviseOrderReply)
				.withCompensation(this::undoBeginReviseOrder)
				.step().invokeParticipant(this::beginReviseTicket)
				.withCompensation(this::undoBeginReviseTicket)
				.step()
				.invokeParticipant(this::reviseAuthorization)
				.step()
				.invokeParticipant(this::confirmTicketRevision)
				.step()
				.invokeParticipant(this::confirmOrderRevision)
				.build();
	}

	private CommandWithDestination beginReviseOrder(ReviseOrderSagaData data) {
		return send(new BeginReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
				.to(OrderServiceChannels.COMMAND_CHANNEL).build();
	}

	private void handleBeginReviseOrderReply(ReviseOrderSagaData data, BeginReviseOrderReply reply) {
		logger.info("ƒ order total: {}", reply.revisedOrderTotal());
		data.setRevisedOrderTotal(reply.revisedOrderTotal());
	}

	private CommandWithDestination undoBeginReviseOrder(ReviseOrderSagaData data) {
		return send(new UndoBeginReviseOrderCommand(data.getOrderId())).to(OrderServiceChannels.COMMAND_CHANNEL)
				.build();
	}

	private CommandWithDestination beginReviseTicket(ReviseOrderSagaData data) {
		return send(new BeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId(),
				data.getOrderRevision().revisedOrderLineItems())).to(KitchenServiceChannels.COMMAND_CHANNEL).build();
	}

	private CommandWithDestination undoBeginReviseTicket(ReviseOrderSagaData data) {
		return send(new UndoBeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId()))
				.to(KitchenServiceChannels.COMMAND_CHANNEL).build();
	}

	private CommandWithDestination reviseAuthorization(ReviseOrderSagaData data) {
		return send(new ReviseAuthorization(data.getConsumerId(), data.getOrderId(), data.getRevisedOrderTotal()))
				.to(AccountingServiceChannels.accountingServiceChannel).build();
	}

	private CommandWithDestination confirmTicketRevision(ReviseOrderSagaData data) {
		return send(new ConfirmReviseTicketCommand(data.getRestaurantId(), data.getOrderId(),
				data.getOrderRevision().revisedOrderLineItems())).to(KitchenServiceChannels.COMMAND_CHANNEL).build();
	}
	
	private CommandWithDestination confirmOrderRevision(ReviseOrderSagaData data) {
	    return send(new ConfirmReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
	            .to(OrderServiceChannels.COMMAND_CHANNEL)
	            .build();
	  }

	@Override
	public SagaDefinition<ReviseOrderSagaData> getSagaDefinition() {
		return sagaDefinition;
	}

}
