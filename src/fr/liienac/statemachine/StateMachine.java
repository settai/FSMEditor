/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 * Modified by Nicolas Saporito - ENAC (06/04/2017): 
 *     added generics reflection
 *     added getCurrentState()
 *     added initialize() to fix leaking this in constructor
 *     modified goTo() to leave() and enter() only on actual state changes
 */
package fr.liienac.statemachine;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StateMachine {

    protected State current = null;
    protected State first = null;

    public State getCurrentState() {
        return current;
    }

    public <Event> void handleEvent(Event evt) {
        current.handleEvent(evt);
    }

    private void goTo(State s) {
        if (current != s) {
            current.leave();
            current = s;
            current.enter();
        }
    }

    public class State {

        Map<Object, ArrayList<Transition>> transitionsPerType = new HashMap<>(); // with static type checking

        public State() {
            initialize();
        }

        private void initialize() {
            // first state is the initial state
            if (first == null) {
                first = this;
                current = first;
            }
        }

        protected void enter() {
        }

        protected void leave() {
        }

        // Hack for generics reflection: Type arguments to generic classes 
        // are not available for reflection at runtime, unless... 
        // they come from a generic superclass, so here it is
        private class MotherOfAllTransitions<EventT> {
        }

        public class Transition<EventT> extends MotherOfAllTransitions<EventT> {

            public Transition() {
                // generics reflection to get the specific event type from the generic type specified in this class
                ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
                Class clazz = (Class) parameterizedType.getActualTypeArguments()[0];

                // register transition in state
                Transition t = this;
                ArrayList<Transition> ts = transitionsPerType.get(clazz);
                if (ts == null) {
                    ts = new ArrayList<>();
                    ts.add(t);
                    transitionsPerType.put(clazz, ts);
                } else {
                    ts.add(t);
                }
            }

            protected EventT evt;

            protected boolean guard() {
                return true;
            }

            protected void action() {
            }

            protected State goTo() {
                return current;
            }
        }

        protected <EventT> void handleEvent(EventT evt) {
            ArrayList<Transition> ts = transitionsPerType.get(evt.getClass());
            if (ts == null) {
                return;
            }
            for (Transition t : ts) {
                t.evt = evt;
                if (t.guard()) {
                    t.action();
                    StateMachine.this.goTo(t.goTo());
                    break;
                }
            }
        }
    }
}
