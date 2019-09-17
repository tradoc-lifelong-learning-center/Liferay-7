package mil.tjaglcs.mlrselector.portlet;

//import static com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction._log;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import mil.tjaglcs.mlrselector.constants.MlrSelectorPortletKeys;

@Component(
		configurationPid = "mil.tjaglcs.mlrselector.portlet.MlrSelectorConfiguration",
		configurationPolicy = ConfigurationPolicy.OPTIONAL, 
		immediate = true,
		property = "javax.portlet.name=" + MlrSelectorPortletKeys.MLRSELECTOR,
		service = ConfigurationAction.class
	)

public class MlrSelectorConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		/*if (_log.isInfoEnabled()) {
			_log.info("Blade Message Portlet configuration include");
		}*/

		httpServletRequest.setAttribute(
			MlrSelectorConfiguration.class.getName(),
			_mlrDisplayConfiguration);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}
	
	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		/*if (_log.isInfoEnabled()) {
			_log.info("Blade Message Portlet configuration action");
		}*/

		String publicationName = ParamUtil.getString(actionRequest, "publicationName");

		/*if (_log.isInfoEnabled()) {
			_log.info(
				"Message Display Configuration: Font Family: " + fontFamily);

			_log.info("Message Display Configuration: Font Size: " + fontSize);

			_log.info(
				"Message Display Configuration: Font Color: " + fontColor);
		}*/

		setPreference(actionRequest, "publicationName", publicationName);
		
		//setPreference(actionRequest, "publicationName", "military law review");

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_mlrDisplayConfiguration = ConfigurableUtil.createConfigurable(
				MlrSelectorConfiguration.class, properties);
	}
	
	private volatile MlrSelectorConfiguration _mlrDisplayConfiguration;
}
