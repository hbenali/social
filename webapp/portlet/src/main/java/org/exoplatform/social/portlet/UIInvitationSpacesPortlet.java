/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.portlet;

import org.exoplatform.social.webui.space.UIManageInvitationSpaces;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * {@link UIInvitationSpacesPortlet} used as a porltet for containing {@link UIManageInvitationSpaces}.
 * @author hoatle
 *
 */
@ComponentConfig(
  lifecycle = UIApplicationLifecycle.class,
  template = "war:/groovy/social/portlet/UIInvitationSpacesPortlet.gtmpl"
)
public class UIInvitationSpacesPortlet extends UIPortletApplication {
  /**
   * constructor
   * @throws Exception
   */
  public UIInvitationSpacesPortlet() throws Exception {
    addChild(UIManageInvitationSpaces.class, null, null);
  }
}