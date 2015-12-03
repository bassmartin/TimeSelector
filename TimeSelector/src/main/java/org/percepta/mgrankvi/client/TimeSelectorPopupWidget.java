package org.percepta.mgrankvi.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.percepta.mgrankvi.client.circle.select.CircleSelect;
import org.percepta.mgrankvi.client.circle.select.CircleSelectCallback;

public class TimeSelectorPopupWidget extends DecoratedPopupPanel implements CircleSelectCallback {

    public static final String CLASS_NAME = "TimeSelector";

    private final SelectionHandler selectionHandler;

    private enum Target {HOURS, MINUTES, AM, PM}

    private String TOP_BG = "mediumaquamarine";
    private String NUMBER_SELECTED = "white";
    private String NUMBER_INACTIVE = "lightseagreen";
    private String DAY_TIME_SELECTED = "lavender";
    private String DAY_TIME_INACTIVE = "darkseagreen";

    private NumberFormat format = NumberFormat.getFormat("00");

    private static RegExp sizePattern = RegExp.compile("^(-?\\d+(\\.\\d+)?)(%|px|em|ex|in|cm|mm|pt|pc)?$");

    private Button am, pm, cancel, ok;
    private Label hour, minute;

    private int width, height;

    private final VerticalPanel content;
    private HorizontalPanel top;

    private Target select = Target.HOURS;
    private Target half = Target.AM;

    private int hourSelection = 12;
    private int minuteSelection = 0;

    CircleSelect clock;

    public TimeSelectorPopupWidget(final SelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
        content = new VerticalPanel();

        getElement().getStyle().setBackgroundColor("white");
        // CSS class-name should not be v- prefixed
        setStyleName("c-" + CLASS_NAME);
        add(content);
        top = new HorizontalPanel();
        top.setWidth("100%");
        top.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        content.add(top);
        content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        top.getElement().getStyle().setBackgroundColor(TOP_BG);

        int fontSize = 40;
        hour = new Label("12");
        hour.setHeight("100%");
        hour.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
        hour.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        hour.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                select = Target.HOURS;
                hour.getElement().getStyle().setColor(NUMBER_SELECTED);
                minute.getElement().getStyle().setColor(NUMBER_INACTIVE);
                setHourSelection();
            }
        });
        minute = new Label("00");
        minute.setHeight("100%");
        minute.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
        minute.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        minute.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                setMinuteSelection();
            }
        });
        Label divider = new Label(":");
        divider.setHeight("100%");
        divider.getElement().getStyle().setFontSize(fontSize, Style.Unit.PX);
        divider.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);

        // Default selection colors.
        hour.getElement().getStyle().setColor(NUMBER_SELECTED);
        minute.getElement().getStyle().setColor(NUMBER_INACTIVE);

        top.add(hour);
        top.add(divider);
        top.add(minute);

        VerticalPanel amToPm = new VerticalPanel();
        amToPm.setWidth("55px");
        amToPm.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        amToPm.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        am = new Button("AM");
        clearButtonStyle(am.getElement());
        am.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (half.equals(Target.PM)) {
                    hourSelection -= 12;
                    setTimeSelection();
                }
                half = Target.AM;
                am.getElement().getStyle().setColor(DAY_TIME_SELECTED);
                pm.getElement().getStyle().setColor(DAY_TIME_INACTIVE);
                if (select.equals(Target.HOURS))
                    setHourSelection();
            }
        });
        pm = new Button("PM");
        clearButtonStyle(pm.getElement());
        pm.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (half.equals(Target.AM)) {
                    hourSelection += 12;
                    setTimeSelection();
                }
                half = Target.PM;
                pm.getElement().getStyle().setColor(DAY_TIME_SELECTED);
                am.getElement().getStyle().setColor(DAY_TIME_INACTIVE);
                if (select.equals(Target.HOURS))
                    setHourSelection();
            }
        });

        am.getElement().getStyle().setColor(DAY_TIME_SELECTED);
        pm.getElement().getStyle().setColor(DAY_TIME_INACTIVE);

        amToPm.add(am);
        amToPm.add(pm);

        top.add(amToPm);
        top.setCellWidth(amToPm, "55px");

        clock = new CircleSelect(this, 250, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        content.add(clock);

        HorizontalPanel control = new HorizontalPanel();
        control.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        cancel = new Button("Cancel");
        ok = new Button("Ok");
        ok.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                selectionHandler.timeSelection(hourSelection, minuteSelection);
                select = Target.HOURS;
                hide();
            }
        });

        control.add(cancel);
        control.add(ok);

        content.add(control);
        content.setCellHorizontalAlignment(control, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private void setHourSelection() {
        setHours();
    }

    private void setHours() {
        clock.setValues(getHourValues());
        clock.setSelection(hourSelection);
        clock.setSectors(12);
    }

    private void setMinuteSelection() {
        select = Target.MINUTES;
        hour.getElement().getStyle().setColor(NUMBER_INACTIVE);
        minute.getElement().getStyle().setColor(NUMBER_SELECTED);

        clock.setValues(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);
        clock.setSelection(minuteSelection);
        clock.setSectors(60);
    }

    private Integer[] getHourValues() {
        if (half.equals(Target.PM)) {
            return new Integer[]{13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
        }
        return new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    }

    private void clearButtonStyle(Element element) {
        element.getStyle().setProperty("background", "none");
        element.getStyle().setProperty("border", "none");
        element.getStyle().setProperty("outline", "none");
        element.getStyle().setFontWeight(Style.FontWeight.BOLD);
    }

    // Set width for time selection component
    public void setWidths(String width) {
        content.setWidth(width);
        this.width = parseStringSize(width).size;
    }

    public void setHeights(String height) {
        content.setHeight(height);
        this.height = parseStringSize(height).size;
    }

    @Override
    public void valueSelection(Integer timeValue) {
        if (select.equals(Target.HOURS)) {
            if (half.equals(Target.PM)) {
                timeValue += 12;
            }
            hourSelection = timeValue;
            hour.setText(format.format(timeValue));
            setMinuteSelection();
        } else {
            minuteSelection = timeValue;
            minute.setText(format.format(timeValue % 60));
        }
    }

    @Override
    public void valueHover(Integer timeValue) {
        if (select.equals(Target.HOURS)) {
            if (half.equals(Target.PM)) {
                timeValue += 12;
            }
            hour.setText(format.format(timeValue));
        } else {
            minute.setText(format.format(timeValue % 60));
        }
    }

    @Override
    public void mouseOutEvent() {
        setTimeSelection();
        updateClock();
    }

    private void updateClock() {
        if (select.equals(Target.HOURS)) {
            clock.setSelection(hourSelection);
        } else {
            clock.setSelection(minuteSelection);
        }
    }

    private void setTimeSelection() {
        hour.setText(NumberFormat.getFormat("00").format(hourSelection));
        minute.setText(NumberFormat.getFormat("00").format(minuteSelection));
    }

    public void setTimeSelection(int hour, int minute) {
        hourSelection = hour;
        minuteSelection = minute;
        if(hour > 12) {
            half = Target.PM;
        }
        setTimeSelection();
        updateClock();
    }
    /*
     * Returns array with size in index 0 unit in index 1. Null or empty string
     * will produce Size(-1, "px");
     */
    private static Size parseStringSize(String s) {
        Size size = new Size(-1, "px");
        if (s == null) {
            return size;
        }
        s = s.trim();
        if ("".equals(s)) {
            return size;
        }

        MatchResult matcher = sizePattern.exec(s);
        if (matcher != null) {
            size.size = Integer.parseInt(matcher.getGroup(1));
            if (size.size < 0) {
                size.size = -1;
            } else {
                String unit = matcher.getGroup(3);
                if (unit == null) {
                    size.unit = "px";
                } else if (unit.equals("px")) {
                    size.unit = "px";
                } else if (unit.equals("%")) {
                    size.unit = "%";
                } else if (unit.equals("em")) {
                    size.unit = "em";
                } else if (unit.equals("ex")) {
                    size.unit = "ex";
                } else if (unit.equals("in")) {
                    size.unit = "in";
                } else if (unit.equals("cm")) {
                    size.unit = "cm";
                } else if (unit.equals("mm")) {
                    size.unit = "mm";
                } else if (unit.equals("pt")) {
                    size.unit = "pt";
                } else if (unit.equals("pc")) {
                    size.unit = "pc";
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid size argument: \"" + s
                    + "\" (should match " + sizePattern.getSource() + ")");
        }
        return size;
    }


}