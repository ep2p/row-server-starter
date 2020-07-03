package labs.psychogen.row.config;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

public class ClassPathScanner extends ClassPathScanningCandidateComponentProvider {

    public ClassPathScanner(final boolean useDefaultFilters) {
        super(useDefaultFilters);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }

}
