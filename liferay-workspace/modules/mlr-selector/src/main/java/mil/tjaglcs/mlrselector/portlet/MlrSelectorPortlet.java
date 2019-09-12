package mil.tjaglcs.mlrselector.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import mil.tjaglcs.mlrselector.constants.MlrSelectorPortletKeys;
import mil.tjaglcs.mlrselector.model.Publication;


/**
 * @author Jag
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=TJAGLCS",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/main.js",
		"com.liferay.portlet.header-portlet-javascript=/js/nouislider.min.js",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=MLR Selector",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.init-param.config-template=/config.jsp",
		"javax.portlet.name=" + MlrSelectorPortletKeys.MLRSELECTOR,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class MlrSelectorPortlet extends MVCPortlet {
	
	public Publication fetchPublication(RenderRequest request) throws Exception {
		
		PortletPreferences portletPreferences = request.getPreferences();
		//String pubName = GetterUtil.getString(portletPreferences.getValue("publicationName", ""));
		String pubName = "Military Law Review";
		
		Publication pub = new Publication(pubName, request);
		
		System.out.println("pub: " + pub);
		
		return pub;
	}
	
	
}