package com.eternach.client;

import java.util.ArrayList;

import com.eternach.RichOptionGroup;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.optiongroup.OptionGroupConstants;

/**
 * (C) Andreas Eternach 2013.
 */
@Connect(RichOptionGroup.class)
public class RichOptionGroupConnector extends OptionGroupBaseConnector {

  @Override
  public TooltipInfo getTooltipInfo(final Element element) {
    final TooltipInfo tooltipInfo = getWidget().getTooltipInfo(element);
    if (tooltipInfo == null) {
      return super.getTooltipInfo(element);
    }
    return tooltipInfo;
  }

  @Override
  public RichOptionGroupWidget getWidget() {
    return (RichOptionGroupWidget) super.getWidget();
  }

  @Override
  public void updateFromUIDL(final UIDL uidl, final ApplicationConnection client) {
    getWidget().htmlContentAllowed = uidl.hasAttribute(OptionGroupConstants.HTML_CONTENT_ALLOWED);

    super.updateFromUIDL(uidl, client);

    getWidget().sendFocusEvents = client.hasEventListeners(this, EventId.FOCUS);
    getWidget().sendBlurEvents = client.hasEventListeners(this, EventId.BLUR);

    if (getWidget().focusHandlers != null) {
      for (final HandlerRegistration reg : getWidget().focusHandlers) {
        reg.removeHandler();
      }
      getWidget().focusHandlers.clear();
      getWidget().focusHandlers = null;

      for (final HandlerRegistration reg : getWidget().blurHandlers) {
        reg.removeHandler();
      }
      getWidget().blurHandlers.clear();
      getWidget().blurHandlers = null;
    }

    if (getWidget().sendFocusEvents || getWidget().sendBlurEvents) {
      getWidget().focusHandlers = new ArrayList<HandlerRegistration>();
      getWidget().blurHandlers = new ArrayList<HandlerRegistration>();

      // add focus and blur handlers to checkboxes / radio buttons
      for (final Widget wid : getWidget().panel) {
        if (wid instanceof CheckBox) {
          getWidget().focusHandlers.add(((CheckBox) wid).addFocusHandler(getWidget()));
          getWidget().blurHandlers.add(((CheckBox) wid).addBlurHandler(getWidget()));
        }
      }
    }
  }
}
