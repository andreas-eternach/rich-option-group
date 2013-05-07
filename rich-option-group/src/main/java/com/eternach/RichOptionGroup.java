/**
 * (C) Andreas Eternach 2013.
 */
package com.eternach;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.ui.OptionGroup;

// This is the server-side UI component that provides public API 
// for RichOptionGroup
public class RichOptionGroup extends OptionGroup {
  public static final String DESCRIPTION_ICON = "description-icon";
  public static final String DESCRIPTION_TEXT = "description-text";

  public RichOptionGroup(final String caption) {
    super(caption);
  }

  @Override
  protected void paintItem(final PaintTarget target, final Object itemId) throws PaintException {
    super.paintItem(target, itemId);
    final Object value = getItem(itemId).getItemProperty(DESCRIPTION_TEXT).getValue();
    if (value != null) {
      target.addAttribute(DESCRIPTION_TEXT, value.toString());
    }
    final Resource descriptionIcon = (Resource) getItem(itemId).getItemProperty(DESCRIPTION_ICON).getValue();
    if (descriptionIcon != null) {
      target.addAttribute(DESCRIPTION_ICON, descriptionIcon);
    }

  }
}
