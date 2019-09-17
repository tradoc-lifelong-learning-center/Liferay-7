package mil.tjaglcs.mlrselector.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

import java.util.Map;

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

	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_mlrDisplayConfiguration = ConfigurableUtil.createConfigurable(
				MlrSelectorConfiguration.class, properties);
	}
	
	private volatile MlrSelectorConfiguration _mlrDisplayConfiguration;
}
