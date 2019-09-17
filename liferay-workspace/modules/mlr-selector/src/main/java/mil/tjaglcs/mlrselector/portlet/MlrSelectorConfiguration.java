package mil.tjaglcs.mlrselector.portlet;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;


@ExtendedObjectClassDefinition(
	    category = "Content and Data",
	    scope = ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE
	)

@Meta.OCD(
	    id = "mil.tjaglcs.mlrselector.portlet.MlrSelectorConfiguration",
	    name="military-law-review"
	)

public interface MlrSelectorConfiguration {
	
	@Meta.AD(
			deflt="Military Law Review",
			required = false
	)
	public String publicationName();
	
	/*@Meta.AD(
			deflt="multi",
			required = true
	)
	public String issueDisplay();*/

}
