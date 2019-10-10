package mil.tjaglcs.mlrselector.portlet;

//import static com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet._log;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import mil.tjaglcs.mlrselector.constants.MlrSelectorPortletKeys;
import mil.tjaglcs.mlrselector.model.Publication;


/**
 * @author Jag
 */
@Component(
	configurationPid = "mil.tjaglcs.mlrselector.portlet.MlrSelectorConfiguration",
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=TJAGLCS",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-css=/css/nouislider.css",
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
		
		MlrSelectorConfiguration mlrSelectorConfiguration = 
				(MlrSelectorConfiguration)
				request.getAttribute(MlrSelectorConfiguration.class.getName());

			String pubName = StringPool.BLANK;
			String issueDisplay = StringPool.BLANK;

			if (Validator.isNotNull(mlrSelectorConfiguration)) {
				pubName = portletPreferences.getValue("publicationName", mlrSelectorConfiguration.publicationName());
				issueDisplay = portletPreferences.getValue("issueDisplay", mlrSelectorConfiguration.issueDisplay());
			}
		
		
		//String pubName = GetterUtil.getString(portletPreferences.getValue("publicationName", ""));
		//String pubName = "Military Law Review";
		request.setAttribute("issueDisplay", issueDisplay);
	
		Publication pub = new Publication(pubName, request);
		
		//System.out.println("pub: " + pub);
		
		return pub;
	}
	
	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		/*if (_log.isInfoEnabled()) {
			_log.info("Blade Message Portlet render");
		}*/

		renderRequest.setAttribute(
			MlrSelectorConfiguration.class.getName(),
			_mlrDisplayConfiguration);

		super.doView(renderRequest, renderResponse);
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_mlrDisplayConfiguration = ConfigurableUtil.createConfigurable(
				MlrSelectorConfiguration.class, properties);
	}
	
	private volatile MlrSelectorConfiguration _mlrDisplayConfiguration;
	
	
}