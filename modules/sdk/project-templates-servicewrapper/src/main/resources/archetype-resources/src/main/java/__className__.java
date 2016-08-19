package ${package};

import _SERVICE_FULL_;

import com.liferay.portal.kernel.service.ServiceWrapper;

import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	property = {
	},
	service = ServiceWrapper.class
)
public class ${className} extends _SERVICE_SHORT_ {

	public ${className}() {
		super(null);
	}

}