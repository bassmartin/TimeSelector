package org.percepta.mgrankvi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import org.percepta.mgrankvi.TimeSelector;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(TimeSelector.class)
public class TimeSelectorConnector extends AbstractComponentConnector {

    // ServerRpc is used to send events to server. Communication implementation
    // is automatically created here
    TimeSelectorServerRpc rpc = RpcProxy.create(TimeSelectorServerRpc.class, this);

    public TimeSelectorConnector() {

        // To receive RPC events from server, we register ClientRpc implementation
        registerRpc(TimeSelectorClientRpc.class, new TimeSelectorClientRpc() {
            public void alert(String message) {
                Window.alert(message);
            }
        });

        // We choose listed for mouse clicks for the widget
//		getWidget().addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				final MouseEventDetails mouseDetails = MouseEventDetailsBuilder
//						.buildMouseEventDetails(event.getNativeEvent(),
//								getWidget().getElement());
//
//				// When the widget is clicked, the event is sent to server with ServerRpc
//				rpc.clicked(mouseDetails);
//			}
//		});

    }

    // We must implement createWidget() to create correct type of widget
    @Override
    protected Widget createWidget() {
        return GWT.create(TimeSelectorWidget.class);
    }


    // We must implement getWidget() to cast to correct type
    @Override
    public TimeSelectorWidget getWidget() {
        return (TimeSelectorWidget) super.getWidget();
    }

    // We must implement getState() to cast to correct type
    @Override
    public TimeSelectorState getState() {
        return (TimeSelectorState) super.getState();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // State is directly readable in the client after it is set in server
//		final String text = getState().text;
//		getWidget().setText(text);
//        getWidget().setWidth(getState().width);
//        getWidget().setHeight(getState().height);
    }


}
