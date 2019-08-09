package org.hys.workshop.statemachine.shell;


import lombok.extern.slf4j.Slf4j;
import org.hys.workshop.statemachine.persister.RedisRuntimePersister;
import org.hys.workshop.statemachine.statemachine.PaymentStateMachineFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;

import javax.annotation.PostConstruct;

@ShellComponent
@Slf4j
public class StateMachineComands {

    @Autowired
    private StateMachineFactory<PaymentStateMachineFactory.States, PaymentStateMachineFactory.Events> factory;
    private StateMachine<PaymentStateMachineFactory.States, PaymentStateMachineFactory.Events> stateMachine;

    @Autowired
    private RedisRuntimePersister redisRuntimePersister;

    @PostConstruct
    public void init()  {
        String machineId = "123";
        stateMachine = factory.getStateMachine(machineId);
        restore(machineId);
        stateMachine.getExtendedState().getVariables().put("status", "registered");
    }

    private void restore(String machineId)  {
        StateMachineContext<PaymentStateMachineFactory.States, PaymentStateMachineFactory.Events> context =
        redisRuntimePersister.read(machineId);
        if (context != null)    {
            stateMachine.stop();
            stateMachine.getStateMachineAccessor().doWithAllRegions(function -> function.resetStateMachine(context));
            stateMachine.start();
        }
    }

    @ShellMethod(value = "event", key = "sendEvent")
    public void sendEvent(PaymentStateMachineFactory.Events event) {
        stateMachine.sendEvent(event);
    }

}
