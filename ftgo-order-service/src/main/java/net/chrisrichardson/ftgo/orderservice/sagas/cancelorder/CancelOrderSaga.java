package net.chrisrichardson.ftgo.orderservice.sagas.cancelorder;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

import org.springframework.util.Assert;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import jakarta.annotation.PostConstruct;
import net.chrisrichardson.ftgo.accountservice.api.AccountingServiceChannels;
import net.chrisrichardson.ftgo.accountservice.api.ReverseAuthorizationCommand;
import net.chrisrichardson.ftgo.kitchenservice.api.BeginCancelTicketCommand;
import net.chrisrichardson.ftgo.kitchenservice.api.ConfirmCancelTicketCommand;
import net.chrisrichardson.ftgo.kitchenservice.api.KitchenServiceChannels;
import net.chrisrichardson.ftgo.kitchenservice.api.UndoBeginCancelTicketCommand;
import net.chrisrichardson.ftgo.orderservice.api.OrderServiceChannels;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.BeginCancelCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.ConfirmCancelOrderCommand;
import net.chrisrichardson.ftgo.orderservice.sagaparticipants.UndoBeginCancelCommand;
import org.springframework.util.Assert;
public class CancelOrderSaga implements SimpleSaga<CancelOrderSagaData>{

	private SagaDefinition<CancelOrderSagaData> sagaDefinition;
	
	@PostConstruct
	  public void initializeSagaDefinition() {
	    sagaDefinition = step()
	            .invokeParticipant(this::beginCancel)
	            .withCompensation(this::undoBeginCancel)
	            .step()
	            .invokeParticipant(this::beginCancelTicket)
	            .withCompensation(this::undoBeginCancelTicket)
	            .step()
	            .invokeParticipant(this::reverseAuthorization)
	            .step()
	            .invokeParticipant(this::confirmTicketCancel)
	            .step()
	            .invokeParticipant(this::confirmOrderCancel)
	            .build();
	}

	private CommandWithDestination beginCancel(CancelOrderSagaData data) {
		return send(new BeginCancelCommand(data.orderId())).to(OrderServiceChannels.COMMAND_CHANNEL).build();
	}

	private CommandWithDestination undoBeginCancel(CancelOrderSagaData data) {
		return send(new UndoBeginCancelCommand(data.orderId())).to(OrderServiceChannels.COMMAND_CHANNEL).build();
	}

	private CommandWithDestination beginCancelTicket(CancelOrderSagaData data) {
		return send(new BeginCancelTicketCommand(data.restaurantId(), (long) data.orderId()))
				.to(KitchenServiceChannels.COMMAND_CHANNEL).build();
	}

	private CommandWithDestination undoBeginCancelTicket(CancelOrderSagaData data) {
		return send(new UndoBeginCancelTicketCommand(data.restaurantId(), data.orderId()))
				.to(KitchenServiceChannels.COMMAND_CHANNEL).build();
	}

	private CommandWithDestination reverseAuthorization(CancelOrderSagaData data) {
		return send(new ReverseAuthorizationCommand(data.consumerId(), data.orderId(), data.orderTotal()))
				.to(AccountingServiceChannels.accountingServiceChannel).build();
	}
	
	private CommandWithDestination confirmTicketCancel(CancelOrderSagaData data) {
	    return send(new ConfirmCancelTicketCommand(data.restaurantId(), data.orderId()))
	            .to(KitchenServiceChannels.COMMAND_CHANNEL)
	            .build();
	}
	
	private CommandWithDestination confirmOrderCancel(CancelOrderSagaData data) {
	    return send(new ConfirmCancelOrderCommand(data.orderId()))
	            .to(OrderServiceChannels.COMMAND_CHANNEL)
	            .build();
	}

	@Override
	public SagaDefinition<CancelOrderSagaData> getSagaDefinition() {
		Assert.notNull(sagaDefinition, CancelOrderSaga.class.getName() + " saga cannot be null.");
	    return sagaDefinition;
	}
}
