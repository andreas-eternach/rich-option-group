/**
 * (C) Andreas Eternach 2013.
 */
package com.eternach.demo;

import com.eternach.RichOptionGroup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("RichOptionGroup Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

  private static final String CAPTION_ITEM_PROPERTY = "caption-item-property";

  @Override
  protected void init(final VaadinRequest request) {

    // Initialize our new UI component
    final RichOptionGroup richOptionGroup = new RichOptionGroup("Label of Option-Group");
    richOptionGroup.setItemCaptionPropertyId(CAPTION_ITEM_PROPERTY);
    richOptionGroup.setContainerDataSource(new IndexedContainer() {
      {
        addContainerProperty(CAPTION_ITEM_PROPERTY, String.class, null);
        addContainerProperty(RichOptionGroup.DESCRIPTION_TEXT, String.class, null);
        addContainerProperty(RichOptionGroup.DESCRIPTION_ICON, Resource.class, null);
        final Item value1 = addItem("value1");
        value1.getItemProperty(CAPTION_ITEM_PROPERTY).setValue("value1 Label");
        value1.getItemProperty(RichOptionGroup.DESCRIPTION_TEXT).setValue("value1 description");
        value1.getItemProperty(RichOptionGroup.DESCRIPTION_ICON).setValue(new ThemeResource("images/value1.jpg"));
        final Item value2 = addItem("value2");
        value2.getItemProperty(CAPTION_ITEM_PROPERTY).setValue("value2 Label");
        value2.getItemProperty(RichOptionGroup.DESCRIPTION_TEXT).setValue("value2 description");
        value2.getItemProperty(RichOptionGroup.DESCRIPTION_ICON).setValue(new ThemeResource("images/value2.jpg"));
      }
    });

    // Show it in the middle of the screen
    final VerticalLayout layout = new VerticalLayout();
    layout.setStyleName("demoContentLayout");
    layout.setSizeFull();
    layout.addComponent(richOptionGroup);
    layout.setComponentAlignment(richOptionGroup, Alignment.MIDDLE_CENTER);
    setContent(layout);

  }
}
