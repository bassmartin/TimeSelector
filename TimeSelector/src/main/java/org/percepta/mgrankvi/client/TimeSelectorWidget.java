package org.percepta.mgrankvi.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class TimeSelectorWidget extends Composite implements SelectionHandler {//} implements CircleSelectCallback {

    public static final String CLASS_NAME = "TimeSelector";

    private SelectionHandler selectionHandler;

    TextBox content;

    TimeSelectorPopupWidget selector;
    private boolean twentyFour = true;

    public TimeSelectorWidget() {

        content = new TextBox();
        content.setMaxLength(5);
        content.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                selector.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = (Window.getClientWidth() - offsetWidth) / 2;
                        int top = (Window.getClientHeight() - offsetHeight) / 2;
                        selector.setPopupPosition(left, top);
                    }
                });
            }
        });
        content.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent blurEvent) {
                handleTextInField();
            }
        });
        content.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent keyDownEvent) {
                boolean enterPressed = KeyCodes.KEY_ENTER == keyDownEvent.getNativeEvent().getKeyCode();
                boolean escapePressed = KeyCodes.KEY_ESCAPE == keyDownEvent.getNativeEvent().getKeyCode();
                if (enterPressed) {
                    handleTextInField();
                } else if (escapePressed) {
                    selector.setHourSelection();
                    selector.hide();
                }
            }
        });
        selector = new TimeSelectorPopupWidget(this);
        SimplePanel baseContent = new SimplePanel();
        baseContent.add(content);
//        baseContent.getElement().getStyle().setBackgroundColor("white");
        initWidget(baseContent);
        // CSS class-name should not be v- prefixed

    }

    public void addSelectionHandler(SelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    private void handleTextInField() {
        String[] timeElements = content.getText().split("\\:");
        timeSelection(Integer.parseInt(timeElements[0]), Integer.parseInt(timeElements[1]));
    }

    @Override
    public void timeSelection(int hour, int minute) {
        setTime(hour, minute);
        selectionHandler.timeSelection(hour, minute);
    }

    public void setTime(int hour, int minute) {
        NumberFormat formatter = NumberFormat.getFormat("00");
        content.setText(formatter.format(hour) + ":" + formatter.format(minute) + (twentyFour ? "" : " " + selector.getHalf()));
        selector.setTimeSelection(hour, minute);
    }

    /**
     * Set clock to 24h or 12h mode
     *
     * @param twentyFour true for 24h mode
     */
    public void setClockMode(boolean twentyFour) {
        this.twentyFour = twentyFour;
        selector.setClockMode(twentyFour);
        if (!twentyFour) {
            if (selector.getHourSelection() > 12) {
                setTime(selector.getHourSelection() - 12, selector.getMinuteSelection());
                selector.setHalf(TimeSelectorPopupWidget.Target.PM);
            } else {
                setTime(selector.getHourSelection(), selector.getMinuteSelection());
                selector.setHalf(TimeSelectorPopupWidget.Target.AM);
            }
        } else if (selector.getHalf().equals(TimeSelectorPopupWidget.Target.PM) && selector.getHourSelection() != 0) {
            setTime(selector.getHourSelection() + 12, selector.getMinuteSelection());
        } else {
            setTime(selector.getHourSelection(), selector.getMinuteSelection());
        }
    }

    public void setVisibleMinutes(Integer[] visibleMinutes) {
        selector.setVisibleMinutes(visibleMinutes);
    }

    public void setMinuteSectors(int minuteSectors) {
        selector.setMinuteSectors(minuteSectors);
    }
}