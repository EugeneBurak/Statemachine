package org.hys.workshop.statemachine.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.hys.workshop.statemachine.persister.RedisRuntimePersister;
import org.hys.workshop.statemachine.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.*;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;


@Slf4j
@EnableStateMachineFactory
public class PaymentStateMachineFactory extends StateMachineConfigurerAdapter<PaymentStateMachineFactory.States, PaymentStateMachineFactory.Events> {


    @Autowired private PaymentService paymentService;

    @Autowired private RedisRuntimePersister<States, Events, String> redisRuntimePersister;

    public enum States  {

        SUBMITTED,
        PAID,
        FULFILLED,
        CANCELED
    }

    public enum Events  {

        PAY,
        FULFILL,
        CANCEL
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {

        states.withStates()
                .initial(States.SUBMITTED)
                .state(States.PAID)
                .end(States.FULFILLED)
                .end(States.CANCELED);

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitionConfigurer) throws Exception {

        transitionConfigurer
                .withExternal().source(States.SUBMITTED).target(States.PAID).event(Events.PAY).action(pay()).guard(paymentGuard())
                .and()
                .withExternal().source(States.PAID).target(States.FULFILLED).event(Events.FULFILL).action(fulfill())
                .and()
                .withExternal().source(States.SUBMITTED).target(States.CANCELED).event(Events.CANCEL).action(cancel())
                .and()
                .withExternal().source(States.PAID).target(States.CANCELED).event(Events.CANCEL).action(cancel());
    }

    Action<States, Events> pay()    {
        return context -> {
            paymentService.pay(context.getStateMachine().getId());
        };
    }

    Action<States, Events> fulfill()    {
        return context -> {
            paymentService.fulfill(context.getStateMachine().getId());
        };
    }

    Action<States, Events> cancel()    {
        return context -> {
            paymentService.cancel(context.getStateMachine().getId());
        };
    }

    Guard<States, Events> paymentGuard()   {
        return context -> context.getStateMachine().getExtendedState().getVariables().get("status").equals("registered");
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> configurer) throws Exception {

        configurer.withConfiguration().listener(listener()).autoStartup(true
        );
        configurer.withPersistence().runtimePersister(redisRuntimePersister);
    }

    private StateMachineListener<States, Events> listener() {

        return new StateMachineListenerAdapter<States, Events>()  {

            @Override
            public void stateChanged(State from, State to)  {
                log.info(String.format("State was changed from %s to %s", (from != null ? from.getId() : from), to.getId()));
            }

        };
    }


}
