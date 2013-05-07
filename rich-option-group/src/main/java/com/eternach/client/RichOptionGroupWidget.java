package com.eternach.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.FocusableFlexTable;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VCheckBox;
import com.vaadin.client.ui.VOptionGroupBase;
import com.vaadin.shared.EventId;
import com.vaadin.shared.ui.optiongroup.OptionGroupConstants;
/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Extended by Andreas Eternach 2013.
 */

public class RichOptionGroupWidget extends VOptionGroupBase implements FocusHandler, BlurHandler {

  public static final String CLASSNAME = "v-select-optiongroup";

  /** For internal use only. May be removed or replaced in the future. */
  public final Panel panel;

  private final Map<CheckBox, String> optionsToKeys;
  private final Map<Element, String> elementsToDescription;

  private final List<Boolean> optionsEnabled;

  /** For internal use only. May be removed or replaced in the future. */
  public boolean sendFocusEvents = false;
  /** For internal use only. May be removed or replaced in the future. */
  public boolean sendBlurEvents = false;
  /** For internal use only. May be removed or replaced in the future. */
  public List<HandlerRegistration> focusHandlers = null;
  /** For internal use only. May be removed or replaced in the future. */
  public List<HandlerRegistration> blurHandlers = null;

  private final LoadHandler iconLoadHandler = new LoadHandler() {
    @Override
    public void onLoad(final LoadEvent event) {
      Util.notifyParentOfSizeChange(RichOptionGroupWidget.this, true);
    }
  };

  /**
   * used to check whether a blur really was a blur of the complete optiongroup: if a control inside this optiongroup
   * gains focus right after blur of another control inside this optiongroup (meaning: if onFocus fires after onBlur has
   * fired), the blur and focus won't be sent to the server side as only a focus change inside this optiongroup occured
   */
  private boolean blurOccured = false;

  /** For internal use only. May be removed or replaced in the future. */
  public boolean htmlContentAllowed = false;

  public RichOptionGroupWidget() {
    super(CLASSNAME);
    panel = (Panel) optionsContainer;
    optionsToKeys = new HashMap<CheckBox, String>();
    elementsToDescription = new HashMap<Element, String>();
    optionsEnabled = new ArrayList<Boolean>();
  }

  /*
   * Return true if no elements were changed, false otherwise.
   */
  @Override
  public void buildOptions(final UIDL uidl) {
    panel.clear();
    optionsEnabled.clear();
    for (final Iterator<?> it = uidl.getChildIterator(); it.hasNext();) {
      boolean iconDisplayed = false;
      final UIDL opUidl = (UIDL) it.next();
      CheckBox op;

      String itemHtml = opUidl.getStringAttribute("caption");
      if (!htmlContentAllowed) {
        itemHtml = Util.escapeHTML(itemHtml);
      }

      final String icon = opUidl.getStringAttribute("icon");
      if (icon != null && icon.length() != 0) {
        final String iconUrl = client.translateVaadinUri(icon);
        itemHtml = "<span style=\"white-space: normal;\">" + itemHtml + "</span><br/><img src=\"" + iconUrl
            + "\" class=\"" + Icon.CLASSNAME + "\" alt=\"\" />";
        iconDisplayed = true;
      }

      if (isMultiselect()) {
        op = new VCheckBox();
      } else {
        op = new RadioButton(paintableId, null, true);
        op.setStyleName("v-radiobutton");
      }

      op.addStyleName(CLASSNAME_OPTION);
      op.setValue(opUidl.getBooleanAttribute("selected"));
      final boolean optionEnabled = !opUidl.getBooleanAttribute(OptionGroupConstants.ATTRIBUTE_OPTION_DISABLED);
      final boolean enabled = optionEnabled && !isReadonly() && isEnabled();
      op.setEnabled(enabled);
      optionsEnabled.add(optionEnabled);
      setStyleName(op.getElement(), ApplicationConnection.DISABLED_CLASSNAME, !(optionEnabled && isEnabled()));
      op.addClickHandler(this);
      optionsToKeys.put(op, opUidl.getStringAttribute("key"));
      String description = opUidl.getStringAttribute("description-text");
      if (description == null) {
        description = "";
      }
      if (opUidl.getStringAttribute("description-icon") != null) {
        description += "<br/><img src=\"" + client.translateVaadinUri(opUidl.getStringAttribute("description-icon"))
            + "\"\\>";
      }

      final FocusableFlexTable table = new FocusableFlexTable();
      table.setWidget(0, 0, op);
      table.setHTML(0, 1, itemHtml);
      table.setCellPadding(0);
      table.setCellSpacing(0);
      panel.add(table);

      if (description != null && description.length() != 0) {
        elementsToDescription.put(table.getElement(), description);
      }
      if (iconDisplayed) {
        Util.sinkOnloadForImages(table.getElement());
        table.addHandler(iconLoadHandler, LoadEvent.getType());
      }
    }

  }

  @Override
  public void focus() {
    final Iterator<Widget> iterator = panel.iterator();
    if (iterator.hasNext()) {
      ((FocusableFlexTable) iterator.next()).setFocus(true);
    }
  }

  public TooltipInfo getTooltipInfo(final Element element) {
    Element lookupElement = element;
    while (lookupElement != getWidget().getElement()) {
      if (elementsToDescription.containsKey(lookupElement)) {
        return new TooltipInfo(elementsToDescription.get(lookupElement));
      }
      lookupElement = lookupElement.getParentElement();
      VConsole.error(element.toString());
    }
    return null;
  }

  @Override
  public void onBlur(final BlurEvent arg0) {
    blurOccured = true;
    if (sendBlurEvents) {
      Scheduler.get().scheduleDeferred(new Command() {
        @Override
        public void execute() {
          // check whether blurOccured still is true and then send the
          // event out to the server
          if (blurOccured) {
            client.updateVariable(paintableId, EventId.BLUR, "", true);
            blurOccured = false;
          }
        }
      });
    }
  }

  @Override
  public void onClick(final ClickEvent event) {
    super.onClick(event);
    if (event.getSource() instanceof CheckBox) {
      final boolean selected = ((CheckBox) event.getSource()).getValue();
      final String key = optionsToKeys.get(event.getSource());
      if (!isMultiselect()) {
        selectedKeys.clear();
      }
      if (selected) {
        selectedKeys.add(key);
      } else {
        selectedKeys.remove(key);
      }
      client.updateVariable(paintableId, "selected", getSelectedItems(), isImmediate());
    }
  }

  @Override
  public void onFocus(final FocusEvent arg0) {
    if (!blurOccured) {
      // no blur occured before this focus event
      // panel was blurred => fire the event to the server side if
      // requested by server side
      if (sendFocusEvents) {
        client.updateVariable(paintableId, EventId.FOCUS, "", true);
      }
    } else {
      // blur occured before this focus event
      // another control inside the panel (checkbox / radio box) was
      // blurred => do not fire the focus and set blurOccured to false, so
      // blur will not be fired, too
      blurOccured = false;
    }
  }

  @Override
  public void setTabIndex(final int tabIndex) {
    for (final Iterator<Widget> iterator = panel.iterator(); iterator.hasNext();) {
      iterator.next().getElement().setTabIndex(tabIndex);
    }
  }

  @Override
  protected String[] getSelectedItems() {
    return selectedKeys.toArray(new String[selectedKeys.size()]);
  }

  @Override
  protected void updateEnabledState() {
    int i = 0;
    final boolean optionGroupEnabled = isEnabled() && !isReadonly();
    // sets options enabled according to the widget's enabled,
    // readonly and each options own enabled
    for (final Widget w : panel) {
      if (w instanceof HasEnabled) {
        ((HasEnabled) w).setEnabled(optionsEnabled.get(i) && optionGroupEnabled);
        setStyleName(w.getElement(), ApplicationConnection.DISABLED_CLASSNAME, !(optionsEnabled.get(i) && isEnabled()));
      }
      i++;
    }
  }
}
