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
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=MlrSelector",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + MlrSelectorPortletKeys.MLRSELECTOR,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class MlrSelectorPortlet extends MVCPortlet {
	
	private Publication publication;
	
	
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
	    throws PortletException, IOException {
		
		try {
			this.publication = fetchPublication(renderRequest);
			System.out.println(this.publication);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("caught!");
			e.printStackTrace();
		}

		
		
		renderRequest.setAttribute("publication", this.publication);

		//System.out.println("super: render");
	    super.render(renderRequest, renderResponse);
	}
	
	public Publication fetchPublication(RenderRequest request) throws Exception {
		//System.out.println("trying to get pub");
		
		PortletPreferences portletPreferences = request.getPreferences();
		
		//TODO: reenable
		//String pubName = GetterUtil.getString(portletPreferences.getValue("publicationName", ""));
		String pubName = "Military Law Review";
		
		Publication pub = new Publication(pubName, request);
		
		return pub;
	}
	
	
}